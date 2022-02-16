from oaifetcher.fetcher import Fetcher
from oaifetcher.main import Extractor

# fetcher = Fetcher("https://api.archives-ouvertes.fr/oai/hal/", "xml-tei")
# fetcher = Fetcher("http://oai.prodinra.inra.fr/ft", "oai_inra")

# for r in fetcher.fetch("2016-05-10"):
#     if r["content"] is not None:
#         print(r)
#     pass
import re

# print(Extractor.scheduling_parameters("2016-06-11T23:00:42Z"))

fetcher = Fetcher("https://irsteadoc.irstea.fr/exl-php/oai/serveur/oai2.php", "oai_dcrnsr")

for r in fetcher.fetch("2004-01-01", "2004-02-01"):
    print(r)

print(fetcher.latest_entry_date)
# from lxml import etree, objectify
#
# from pyquery import PyQuery as pq
#
#
# def _value(self, fun=None, default=None):
#     if not self:
#         return default
#     txt = self.text()
#     if fun is not None:
#         return fun(txt)
#     return txt
#
# pq.value = _value
#
#
# def as_simple_pq(xml):
#     for elem in xml.getiterator():
#         if not hasattr(elem.tag, 'find'):
#             continue
#         replace = []
#         for k in elem.attrib.keys():
#             i = k.find('}')
#             if i >= 0:
#                 replace.append([k[i+1:], k])
#
#         for key_after, key_before in replace:
#             elem.attrib[key_after] = elem.attrib[key_before]
#             del elem.attrib[key_before]
#         i = elem.tag.find('}')
#         if i >= 0:
#             elem.tag = elem.tag[i+1:]
#     objectify.deannotate(xml, cleanup_namespaces=True)
#     etree.strip_attributes(xml, '{http://www.w3.org/2001/XMLSchema-instance}*')
#     etree.cleanup_namespaces(xml)
#     return pq(xml, parser='xml')
#
# xml = etree.fromstring(open("/tmp/test.xml", 'rb').read())
# tree = as_simple_pq(xml)
#
# print(tree.find("GetRecord"))