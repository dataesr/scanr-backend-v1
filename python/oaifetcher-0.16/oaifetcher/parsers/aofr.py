import re
from oaifetcher.patent import extract_patents

NAMESPACE = "http://www.tei-c.org/ns/1.0"
# https://api.archives-ouvertes.fr/documents/aofr.xsd

"""
ARTICLE("Article dans une revue"),
COMMUNICATION("Communication dans un congrès"),
POSTER("Poster"),
BOOK("Ouvrage (y compris édition critique et traduction)"),
CHAPTER("Chapitre d'ouvrage"),
PROCEEDINGS("Direction d'ouvrage, Proceedings"),
REPORT("Rapport de recherche"),
PATENT("Brevet"),
THESIS("Thèse"),
HDR("HDR"),
LECTURE("Cours et mémoire de cours"),
DISSERTATION("Mémoire d'étudiant"),
RESEARCH_DATA("Donnée de recherche"),
OTHER_PUBLISHED("Autre publication"),
OTHER_UNPUBLISHED("Pré-publication, Document de travail");
"""

AO_TYPE_ASSOCIATION = {
    "ART": "ARTICLE",  # Article dans une revue
    "COMM": "COMMUNICATION",  # Communication dans un congrès
    "POSTER": "POSTER",  # Poster
    "PRESCONF": "COMMUNICATION",  # Document associé à des manifestations scientifiques
    "OUV": "BOOK",  # Ouvrage (y compris édition critique et traduction)
    "COUV": "CHAPTER",  # Chapitre d'ouvrage
    "DOUV": "PROCEEDINGS",  # Direction d'ouvrage, Proceedings
    "PATENT": "PATENT",  # Brevet
    "OTHER": "OTHER_PUBLISHED",  # Autre publication
    "UNDEFINED": "OTHER_UNPUBLISHED",  # Pré-publication, Document de travail
    "REPORT": "REPORT",  # Rapport
    "THESE": "THESIS",  # Thèse
    "HDR": "HDR",  # HDR
    "MEM": "DISSERTATION",  # Mémoire d'étudiant
    "LECTURE": "LECTURE",  # Cours
    "IMG": "OTHER_PUBLISHED",  # Image
    "VIDEO": "OTHER_PUBLISHED",  # Vidéo
    "SON": "OTHER_PUBLISHED",  # Son
    "MAP": "OTHER_PUBLISHED",  # Carte
    "MINUTES": "OTHER_UNPUBLISHED",  # Compte rendu de table ronde
    "NOTE": "OTHER_UNPUBLISHED",  # Note de lecture
    "SYNTHESE": "OTHER_UNPUBLISHED",  # Notes de synthèse
    "OTHERREPORT": "OTHER_UNPUBLISHED",  # Autre rapport, séminaire, workshop
    "REPACT": "REPORT",  # Rapport d'activité
}

AOFR_SOURCE_TITLE = {
    "j": "COLLECTION",
    "m": "BOOK",
}


def parse_source(elt):
    global AOFR_SOURCE_TITLE
    # PROCEEDINGS, EVENT, BOOK, COLLECTION, ARTICLE
    monogr = elt.find("monogr")
    title = monogr.find("title")
    meeting = monogr.find("meeting")
    if meeting:
        return {
            "title": meeting.find("title").value(default=title.value()),
            "type": "EVENT"
        }
    if not title:
        return dict()  # Cant parse source
    source_type = AOFR_SOURCE_TITLE.get(title.attr("level"), "ARTICLE")
    result = {
        "type": source_type,
        "article": monogr.find("biblScope[unit=pp]").value()
    }
    if source_type == "COLLECTION":
        result["collection"] = {
            "title": title.value(),
            "issn": monogr.find("idno[type=issn]").value(),
            "issue": monogr.find("biblScope[unit=issue]").value()
        }
    else:
        result["title"] = title.value()
    return result


def parse_org(org):
    org_type = org.attr("type")
    org_name = org.find("orgName")
    if not org_name:
        return dict()
    label = org_name.value()

    result = {"country": org.find("address country").attr("key")}
    acronym = org.find("orgName[type=acronym]").value()
    if org_type == "laboratory" or org_type == "researchteam":
        codes = list(re.subn("\\s*([A-Z]+)(?:_[A-Z]*)?\\s*0*([1-9][0-9]*)\\s*", "\\1 \\2", r.attr("name"))
                     for r in org.items("relation") if r.attr("name") is not None)
        # now filter by matched patterns
        codes = [c[0] for c in codes if c[1] == 1]
        result["structure"] = {
            "id": org.find("idno[type=RNSR]").value(),
            "code": codes[0] if len(codes) > 0 else None,
            "label": label,
            "acronym": acronym
        }
    else:
        result["institution"] = {
            "label": label,
            "acronym": acronym
        }
    return result


def parse_authors(author, orgs):
    return {
        "firstName": author.find("forename").value(),
        "lastName": author.find("surname").value(),
        "affiliations": [orgs[affiliation.attr("ref")[1:]] for affiliation in author.items("affiliation") if affiliation.attr("ref") is not None and affiliation.attr("ref")[1:] in orgs]
    }


def parse_aofr(element, xml_root, identifier):
    elt = element.find("biblFull")
    title_stmt = elt.find("titleStmt")
    orgs = {o.attr("id"): parse_org(o) for o in element.items("back listOrg org")}
    #missing dates
    result = {
        "title": title_stmt.find("title").value(),  # get the first title elt
        "subtitle": title_stmt.find("title[type=sub]").value(),
        "link": elt.find("publicationStmt idno[type=halUri]").value(),
        "authors": [parse_authors(author, orgs) for author in title_stmt.items("author")],
        "source": parse_source(elt.find("sourceDesc biblStruct")),
        "summary": elt.find("abstract:first").value(),
        "alternativeSummary": elt.find("abstract:nth-child(2)").value(),
        "type": AO_TYPE_ASSOCIATION.get(elt.find("profileDesc textClass classCode[scheme=halTopology]").value(),
                                        "OTHER_UNPUBLISHED"),
        "thematics": [{"label": term.text()} for term in elt.items("profileDesc keywords term")]+
                     [{"type": "HAL_DOMAIN", "code": term.attr("n"), "label": term.text()} for term in elt.items("profileDesc classCode[scheme=halDomain]")],
        "identifiers": {
            "hal": elt.find("publicationStmt idno[type=halId]").value(),
            "doi": elt.find("sourceDesc biblStruct idno[type=doi]").value(),
            "oai": [identifier],
            "patent": extract_patents(elt.find("sourceDesc biblStruct idno[type=patentNumber]").text(), default_country=elt.find("sourceDesc biblStruct monogr country").attr("key"))
        },
        "publicationDate": elt.find("edition[type=current] date[type=whenProduced]").value()
    }

    return result
