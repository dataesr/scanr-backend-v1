from scanr_doiresolver import crossref


def _window(iterable, size):
    """
    Simple buffer-based windowing function
    [7,6,5,4,3,2,1], 3 -> [[7, 6, 5], [4, 3, 2], [1]]

    :param iterable:
    :param size:
    :return:
    """

    buffer = []
    n = 0
    for el in iterable:
        buffer.append(el)
        n += 1
        if n == size:
            yield buffer
            buffer = []
            n = 0
    if n != 0:
        yield buffer


TYPE_MAPPING = {
    "book-section": "CHAPTER",
    "monograph": "BOOK",
    "report": "REPORT",
    "book-track": "CHAPTER",
    "journal-article": "ARTICLE",
    "book-part": "CHAPTER",
    "other": "OTHER_PUBLISHED",
    "book": "BOOK",
    "journal-volume": "BOOK",
    "book-set": "PROCEEDINGS",
    "reference-entry": "ARTICLE",
    "proceedings-article": "COMMUNICATION",
    "journal": "PROCEEDINGS",
    "component": "RESEARCH_DATA",
    "book-chapter": "CHAPTER",
    "report-series": "PROCEEDINGS",
    "proceedings": "PROCEEDINGS",
    "standard": "OTHER_PUBLISHED",
    "reference-book": "BOOK",
    "journal-issue": "ARTICLE",
    "dissertation": "DISSERTATION",
    "dataset": "RESEARCH_DATA",
    "book-series": "PROCEEDINGS",
    "edited-book": "BOOK"
}


def _first(lst):
    if lst is None or len(lst) == 0:
        return None
    return lst[0]


def _biggest(lst):
    if lst is None or len(lst) == 0:
        return None
    result = None
    max_size = 0
    for e in lst:
        if len(e) > max_size:
            result = e
            max_size = len(e)

    return result


def _date(date):
    if date is None:
        return None
    return "-".join(['%02d' % e for e in date["date-parts"][0]])


def as_publication(item):
    issn = _first(item.get("ISSN"))
    collection = None
    container_title = _biggest(item.get("container-title"))
    source_type = None
    if issn is not None:
        source_type = "COLLECTION"
        collection = {
            "issue": item.get("issue"),
            "issn": issn,
            "title": container_title
        }
        container_title = None
    return {
        "type": TYPE_MAPPING.get(item["type"], "OTHER_UNPUBLISHED"),
        "title": _first(item["title"]),
        "subtitle": _first(item["subtitle"]),
        "authors": [
            {
                "firstName": author.get("given"),
                "lastName": author.get("family"),
                "affiliations": [{"structure": {"label": aff.get("name")}} for aff in author.get("affiliation", []) if aff.get("name") is not None]
            }
            for author in item.get("author", [])
        ],
        "source": {
            "title": container_title,
            "type": source_type,
            "collection": collection,
            "pagination": item.get("page"),
            "articleNumber": item.get("article-number")
        },
        "identifiers": {
            "doi": item["DOI"]
        },
        "link": item.get("URL"),
        "lastSourceDate": _date(item.get("deposited")),
        "publicationDate": _date(item.get("published-print", item.get("published-online"))),
    }


def resolve_publications(dois, references):
    # ensure non duplicates
    dois = set(dois)
    result = list()
    for refs in _window(references, 10):
        dois |= crossref.resolve_refs(refs)

    for ref in dois:
        pub = crossref.resolve_dois(ref)
        if pub is not None:
            result.append(as_publication(pub))

    return result
