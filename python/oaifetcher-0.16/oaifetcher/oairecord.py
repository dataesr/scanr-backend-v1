from sickle.models import OAIItem, Header
from oaifetcher.parsers import parsers

from lxml import etree, objectify
from pyquery import PyQuery as pq


def as_simple_pq(xml):
    for elem in xml.getiterator():
        if not hasattr(elem.tag, 'find'):
            continue
        replace = []
        for k in elem.attrib.keys():
            i = k.find('}')
            if i >= 0:
                replace.append([k[i+1:], k])

        for key_after, key_before in replace:
            elem.attrib[key_after] = elem.attrib[key_before]
            del elem.attrib[key_before]
        i = elem.tag.find('}')
        if i >= 0:
            elem.tag = elem.tag[i+1:]
    objectify.deannotate(xml, cleanup_namespaces=True)
    etree.strip_attributes(xml, '{http://www.w3.org/2001/XMLSchema-instance}*')
    etree.cleanup_namespaces(xml)
    return pq(xml, parser='xml')


def _value(self, fun=None, default=None):
    """
    PQ Hack for simpler parsing
     node.value() returns None if node is empty (instead of '' when node.text()). Else it returns the text.

     May be used with a parameter to parse the text content. For instance node.value(int) to have the result parsed as int

    :param self: self
    :param fun: The optional function to apply to text
    :param default: the default value (None if omitted)
    :return:
    """
    if not self:
        return None
    txt = self[0].text
    if txt is None:
        return None
    txt = txt.strip()
    if fun is not None:
        return fun(txt)
    return txt


pq.value = _value


class OAIRecord(OAIItem):
    def __init__(self, record_element, strip_ns=True):
        super(OAIRecord, self).__init__(record_element, strip_ns=strip_ns)
        self.header = Header(self.xml.find('.//' + self._oai_namespace + 'header'))
        # Ensure valid datestamp
        if self.header.datestamp is not None and (len(self.header.datestamp) == 0 or self.header.datestamp == "null"):
            self.header.datestamp = None
        self.deleted = self.header.deleted
        metadata = None
        if not self.deleted:
            root_record = self.xml.find('.//' + self._oai_namespace + 'metadata').getchildren()
            if len(root_record) == 0:
                self.data = None
                return
            root_record = root_record[0]
            namespace = root_record.nsmap[root_record.prefix]
            element = as_simple_pq(root_record)
            if namespace not in parsers:
                raise Exception("Unknown parser for namespace " + namespace)
            metadata = parsers[namespace](element, root_record, self.header.identifier)
            if metadata is None:
                # If metadata is none dont set anything
                self.data = None
                return
        self.data = {
            "id": self.header.identifier,
            "date": self.header.datestamp,
            "deleted": self.header.deleted,
            "content": metadata
        }

    def __repr__(self):
        if self.header.deleted:
            return '<Record %s [deleted]>' % self.header.identifier
        else:
            return '<Record %s>' % self.header.identifier
