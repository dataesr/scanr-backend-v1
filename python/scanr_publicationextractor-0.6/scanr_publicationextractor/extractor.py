import re

import lxml
from companies_plugin.utils import add_logger
from pyquery import PyQuery as pq
from scanr_publicationextractor.line_tokenizer import get_lines
from unidecode import unidecode

match_years = re.compile(".*[^0-9](?:19|20)[0-9]{2}[^0-9].*")
match_doi = re.compile("doi(?:[^a-z0-9]+|\\.[a-z]{2,3}/)(1[0-9\\.]+/[0-9a-z\\.\\-]+)")


def _normalize(txt):
    striped = unidecode(txt).lower()
    striped = re.sub("[^a-z0-9]+", " ", striped)
    return striped


def _from_line(line):
    doi = match_doi.search(line.lower())
    if doi:
        return True, doi.group(1)
    return False, line


def _get_dois(txt):
    for match in match_doi.finditer(txt.lower()):
        yield match.group(1)


def _get_publications(url, page):
    """
    Get a list of potential publications included in a page

    :param url: The url of the page
    :param page: The pyquery content of the page
    :return: A list of potential publication (empty list if not eligible or none found)
    """
    relevant_lines = [r for r in get_lines(page) if 100 <= len(r) < 750]
    potential_lines = [r for r in relevant_lines if match_years.match(" " + r + " ")]

    if len(potential_lines) < 5 or len(potential_lines)/len(relevant_lines) < 0.8:
        return []

    is_publication = False
    all_relevant = "\n".join(potential_lines)

    if match_doi.search(all_relevant):
        is_publication = True
    elif "publications" in _normalize(page("title").text()):
        is_publication = True
    elif "publications" in _normalize(page("h1,h2,h3").text()):
        is_publication = True
    elif "publication" in _normalize(url):
        is_publication = True

    return [_from_line(line) for line in potential_lines] if is_publication else []


HTML_PARSER = lxml.html.HTMLParser(encoding='utf-8')


def _pq_from_html(content):
    """
    Builds a PyQuery element from the html string (useful to avoid encoding declaration and ValueError exceptions)

    :param content:
    :return:
    """
    return pq(lxml.html.fromstring(content.encode('utf-8'), parser=HTML_PARSER))


@add_logger
class PubExtractor:
    def __init__(self, crawl_store_conf):
        from cstore_api.crawl_store import CrawlStore
        self.cs = CrawlStore(crawl_store_conf)

    def extract_publications(self, url):
        self.logger.info("Extracting publications for website "+url)
        pages = self.cs.get_crawl_by_url(url, 4, 300)
        if not pages:
            return [], []

        all_dois = set()
        all_text = set()
        for r in pages:
            page = _pq_from_html(r.content)
            for is_doi, pub in _get_publications(r.url, page):
                if is_doi:
                    all_dois.add(pub)
                else:
                    all_text.add(pub)
            for doi in _get_dois(r.content):
                all_dois.add(doi)
        self.logger.info("Found %d dois and %d unknown publications in website %s" % (len(all_dois), len(all_text), url))
        return list(all_dois), list(all_text)

