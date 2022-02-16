import re
from oaifetcher.patent import extract_patents

NAMESPACE = "http://record.prodinra.inra.fr"

INRA_TYPE_ASSOCIATION = {
    "ARTICLE": "ARTICLE",
    "ARTICLE_TRANSLATION": "OTHER_PUBLISHED",
    "CHAPTER": "CHAPTER",
    "CHAPTER_TRANSLATION": "OTHER_PUBLISHED",
    "BOOK": "BOOK",
    "BOOK_TRANSLATION": "OTHER_PUBLISHED",
    "PROCEEDINGS": "PROCEEDINGS",
    "PROCEEDING_PAPER": "ARTICLE",
    "PAPER": "ARTICLE",
    "PREFACE_PROCEEDINGS": "OTHER_PUBLISHED",
    "PAPER_TRANSLATION": "OTHER_PUBLISHED",
    "DISSERTATION": "DISSERTATION",
    "THESIS": "THESIS",
    "HDR": "HDR",
    "PEDAGOGICAL_DOCUMENT": "LECTURE",
    "ACTIVITY_REPORT": "REPORT",
    "RESEARCH_REPORT": "REPORT",
    "RESEARCH_REPORT_CHAPTER": "REPORT",
    "REPORT": "REPORT",
    "PATENT": "PATENT",
    "AUDIOVISUAL_DOCUMENT": "OTHER_PUBLISHED",
    "SOFTWARE": "OTHER_PUBLISHED",
    "MAP": "OTHER_PUBLISHED"
}


def parse_inra(element, xml_root, identifier):
    global INRA_TYPE_ASSOCIATION
    element = element.find("record")
    source = dict()

    result = {
        "title": element.find("title").value(),
        "summary": element.find("abstract").value(),
        "alternativeSummary": element.find("abstract:nth-child(2)").value(),
        "link": "http://prodinra.inra.fr/record/"+element.find("identifier").value(),
        "authors": [],
        "source": source,
        "type": INRA_TYPE_ASSOCIATION[element.find("itemType").value()],
        "thesisDirectors": [{"lastName": e.text()} for e in element.items("thesisDirector")],
        "thematics": [{"label": e.text()} for e in element.items("keywords keyword")]+
                        [{
                             "type": "INRA_CLASSIFICATION",
                             "code": e.parent().find("identifier").text()+"/"+e.find("inraClassificationIdentifier").text(),
                             "label": e.find("usedTerm").text()
                         } for e in element.items("inraClassification")],
        "identifiers": {
            "doi": element.find("doi").value(),
            "prodinra": element.find("identifier").value(),
            "oai": [identifier],
            "hal": element.find("halIdentifier").value(),
            "patent": extract_patents(element.find("patentNumber").text())
        },
        "publicationDate": element.find("year").value()
    }

    for author in element.items("creator author"):
        affiliations = []
        result['authors'].append({
            "firstName": author.find("firstName").value(),
            "lastName": author.find("publicationName").value(),
            "affiliations": affiliations
        })
        for inraAff in author.items("inraAffiliation"):
            unit = inraAff.find("unit")
            if not unit:
                continue
            code = None
            unit_type = unit.find('type')
            unit_code = unit.find('code')
            if unit_type and unit_type.value() is not None and unit_code and unit_code.value() is not None:
                code = unit_type.value() + " " + re.sub('^0*', '', unit_code.value())
            affiliations.append({
                "institution": {
                    "label": inraAff.find("name").value(),
                    "acronym": inraAff.find("acronym").value(),
                },
                "structure": {
                    "label": unit.find("name").value(),
                    "acronym": unit.find("acronym").value(),
                    "code": code
                },
                "city": unit.find('city').value(),
                "country": unit.find('country').value()
            })
        for extAff in author.items("externalAffiliation"):
            section = extAff.find('section')
            structure = None
            if section and section.value() is not None:
                search = re.search("(?:^|.*[^A-Z])([A-Z]{2,3})\\s*([0-9]+)\\s*,?:?\\s*(.*)", section.value().strip())
                if search is not None:
                    structure = {
                        "label": search.string,
                        "code": search.group(1) + " " + re.sub('^0*', '', search.group(2))
                    }
                else:
                    structure = {
                        "label": section.value()
                    }
            affiliations.append({
                "institution": {
                    "label": extAff.find("name").value(),
                    "acronym": extAff.find("acronym").value(),
                },
                "structure": structure,
                "city": extAff.find('city').value(),
                "country": extAff.find('country').value()
            })

            # if hasattr(element, 'contract'):
            #     # Need to do ID detection on this?
            #     pass
            #
    source['isbn'] = element.find("isbn").value()

    event = element.find("event")
    if event:
        source['title'] = event.find("name").value()
        source['type'] = 'EVENT'

    source['pagination'] = element.find("articleInfos pagination").value()
    article_number = element.find("articleInfos articleNumber").value()
    if article_number:
        source['articleNumber'] = article_number

    proceedings_title = element.find("proceedingsTitle").value()
    if proceedings_title:
        source['title'] = proceedings_title
        source['type'] = 'PROCEEDINGS'

    book_title = element.find("bookTitleInfos")
    if book_title:
        source['title'] = book_title.find("title").value()
        source['title'] = book_title.find("subtitle").value()
        source['type'] = 'BOOK'

    collection = element.find("collection")
    if collection:
        issue = collection.find("issue")
        if issue:
            issue_str = ""
            volume = issue.attr("volume")
            if volume:
                issue_str += volume
            number = issue.attr("number")
            if number:
                if issue_str != "":
                    issue_str += " (" + number + ")"
                else:
                    issue_str += number
            issue = issue_str if issue_str != "" else None
        else:
            issue = None
        source['collection'] = {
            "title": collection.find("title").value(),
            "issn": collection.find("issn").value(),
            "issue": issue,
        }
        if 'type' not in source:
            source['type'] = 'COLLECTION'

    return result
