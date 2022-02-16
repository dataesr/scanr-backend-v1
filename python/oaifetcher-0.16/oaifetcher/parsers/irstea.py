NAMESPACE = "http://www.openarchives.org/OAI/2.0/oai_dcrnsr/"

IRSTEA_TYPE_ASSOCIATION = {
    "ACTES DE CONGRES": "PROCEEDINGS",
    "ARTICLE DE REVUE DE VULGARISATION": "ARTICLE",
    "ARTICLE DE REVUE SCIENTIFIQUE A COMITE DE LECTURE": "ARTICLE",
    "ARTICLE DE REVUE TECHNIQUE A COMITE DE LECTURE": "ARTICLE",
    "ARTICLE DE REVUE TECHNIQUE SANS COMITE DE LECTURE": "ARTICLE",
    "AUTRES": "OTHER_PUBLISHED",
    "BREVET": "PATENT",
    "CHAPITRE D'OUVRAGE SCIENTIFIQUE": "CHAPTER",
    "CHAPITRE D'OUVRAGE TECHNIQUE": "CHAPTER",
    "COMMUNICATION A UN CONGRES": "COMMUNICATION",
    "COMMUNICATION SCIENTIFIQUE AVEC ACTES": "COMMUNICATION",
    "COMMUNICATION SCIENTIFIQUE SANS ACTES": "COMMUNICATION",
    "COMMUNICATION TECHNIQUE AVEC ACTES": "COMMUNICATION",
    "COMMUNICATION TECHNIQUE SANS ACTES": "COMMUNICATION",
    "CONFERENCE INVITEE": "COMMUNICATION",
    "DATA PAPER": "RESEARCH_DATA",
    "DIRECTION D'OUVRAGE": "PROCEEDINGS",
    "DOCUMENT CARTOGRAPHIQUE": "OTHER_PUBLISHED",
    "EXTRAIT DE DOCUMENT": "OTHER_PUBLISHED",
    "FICHE TECHNIQUE": "OTHER_PUBLISHED",
    "HDR": "HDR",
    "MEMOIRE D'ELEVE": "DISSERTATION",
    "NORME": "OTHER_PUBLISHED",
    "OUVRAGE": "BOOK",
    "OUVRAGE DE VULGARISATION": "BOOK",
    "OUVRAGE SCIENTIFIQUE": "BOOK",
    "OUVRAGE TECHNIQUE": "BOOK",
    "OUVRAGE TECHNIQUE/GUIDE TECHNIQUE": "BOOK",
    "POSTER": "POSTER",
    "PRODUIT MULTIMEDIA": "OTHER_PUBLISHED",
    "PRODUIT PEDAGOGIQUE": "LECTURE",
    "RAPPORT": "REPORT",
    "RAPPORT D'EXPERTISE/D'ETUDE": "REPORT",
    "RAPPORT SCIENTIFIQUE": "REPORT",
    "RAPPORT TECHNIQUE": "REPORT",
    "THESE": "THESIS",
}

# PROCEEDINGS, EVENT, BOOK, COLLECTION, ARTICLE
COMM_TYPE_TO_SOURCE = {
    "CHAPTER": "BOOK",
    "PROCEEDINGS": "PROCEEDINGS",
    "ARTICLE": "ARTICLE",
    "COMMUNICATION": "EVENT",
    "BOOK": "BOOK"
}


def parse_source(str, communication_type):
    # Transforms "International symposium on fruit nut and vegetable production engineering 5, Valencia Zaragoza, ESP, 22-26 March 1993"
    # Into "International symposium on fruit nut and vegetable production engineering 5"
    title = str.split(", ")[0]
    return {
        "type": COMM_TYPE_TO_SOURCE.get(communication_type, "ARTICLE"),
        "title": title
    }


def parse_irstea(element, xml_root, identifier):
    global IRSTEA_TYPE_ASSOCIATION
    doc_type = element.find("type").value().upper().replace("À", "A").replace("É", "E").replace("È", "E").replace("Ê", "E")
    comm_type = IRSTEA_TYPE_ASSOCIATION[doc_type]
    result = {
        "title": element.find("title").value(),
        "summary": element.find("description").value(),
        "link": "https://irsteadoc.irstea.fr/cemoa/"+element.find("identifier").value(),
        "authors": [],
        "type": comm_type,
        "thematics": [{"label": e.text()} for e in element.items("subject")],
        "identifiers": {
            "oai": [identifier]
        },
        "publicationDate": element.find("date").value()
    }
    doi = element.find("identifier[type='doi']").text().replace("doi:", "")
    if doi:
        result["identifiers"]["doi"] = doi

    source = element.find("source").text()
    if source:
        result["source"] = parse_source(source, comm_type)

    for author in element.items("creator"):
        affiliations = []
        name = author.find("name").value()
        if not name:
            continue
        name = name.split(", ")
        result['authors'].append({
            "firstName": ", ".join(name[1:]),
            "lastName": name[0],
            "affiliations": affiliations
        })
        for aff in author.items("affiliation"):
            value = aff.value()
            if not value or len(value) < 3:
                continue
            affiliations.append({
                "structure": {
                    "label": value,
                    "id": aff.attr("rnsr") or None,
                }
            })

    return result
