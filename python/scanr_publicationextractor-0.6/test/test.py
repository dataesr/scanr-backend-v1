# Read the documentation here:
#  https://nose.readthedocs.org/en/latest/testing.html

# Modify the import path to find our package
import sys
import os.path
sys.path = [os.path.abspath("../scanr_publicationextractor")] + sys.path
sys.path = [os.path.abspath("..")] + sys.path


# Import our package
from scanr_publicationextractor.extractor import _from_line, _pq_from_html, _get_dois
from scanr_publicationextractor.line_tokenizer import get_lines


def test_get_doi():
    query = "FRADKINE Héloïse. 2015, Les mondes de la chasse. Contribution à une étude de rapports sociaux spatialisés en Seine-et-Marne et en Côte d'Or. http://dx.doi.org/10.2345/125 12"
    assert ["10.2345/125"] == list(_get_dois(query))


def test_from_line():
    query = "“Aminobisphosphonates synergize with HCMV to activate the antiviral activity of Vg9Vd2 cells“, C. Daguzan, M. Moulin, H. Kulyk-Barbier, C. Davrinche, S. Peyrottes, E. Champagne, Journal of Immunology (2016), in press. doi:10.4049/jimmunol.1501661 "
    assert (True, "10.4049/jimmunol.1501661") == _from_line(query)
    query = "FRADKINE Héloïse. 2015, Les mondes de la chasse. Contribution à une étude de rapports sociaux spatialisés en Seine-et-Marne et en Côte d'Or, Thèse pour le Doctorat en Sociologie sous la direction de Alain CHENU et Philippe COULANGEON, Sciences Po Paris, 07 juillet, 645 + XXXIV p."
    assert (False, query) == _from_line(query)
    query = "FRADKINE Héloïse. 2015, Les mondes de la chasse. Contribution à une étude de rapports sociaux spatialisés en Seine-et-Marne et en Côte d'Or. http://dx.doi.org/10.2345/125 12"
    assert (True, "10.2345/125") == _from_line(query)


def test_line_tokenizer():
    query = "<p>stuff</p><p>other \n stuff</p><p>third<br/>stuff</p><p>hey<img/><br>ho</p>"
    # ignore single br
    assert ["stuff", "other stuff", "third stuff", "hey ho"] == get_lines(_pq_from_html(query))
    query = "<p>hey<br>ho<br><br>heyo</p>"
    # split on double br
    assert ["hey ho", "heyo"] == get_lines(_pq_from_html(query))
    query = "<i>Nature</i> <a>doi:stuff</a>"
    # Natural tokenization of tags
    assert ["Nature doi:stuff"] == get_lines(_pq_from_html(query))
