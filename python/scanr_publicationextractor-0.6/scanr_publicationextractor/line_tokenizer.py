from lxml.html import HtmlComment
import re

newline_tags = {"br", "p", "div", "li", "ul", "ol", "table", "tr", "td", "h1", "h2", "h3", "h4", "h5", "blockquote"}
useless_tags = {"script", "style", "head", "img"}
spaces = re.compile(r'\s+')


def get_lines(node):
    """
    Merges text nodes inside a node. May be used for example
    to generate merged text inside a <p> tag.
    Useless tags as script, style, ... are ignored.
    <br>, <p> and such introduce a supplementary '\n'
    """

    def gettext(node):
        nltag = node.tag in newline_tags
        txt = ""
        if nltag:
            txt += "\n"
        if node.text is not None and not isinstance(node, HtmlComment):
            text = re.sub(spaces, ' ', node.text)
            txt += text
        for child in node.iterchildren():
            if child.tag not in useless_tags:
                txt += gettext(child)
        if nltag and len(txt) > 1:
            txt += "\n"
        if node.tail is not None:
            text = re.sub(spaces, ' ', node.tail)
            txt += text
        return txt

    text = "".join([gettext(n) for n in node])

    text = text.strip()

    # space normalization
    text = re.sub(r'\r', '', text)
    # Dont split if only one line
    text = re.sub(r'([^\n])\n([^\n])', '\\1 \\2', text)
    text = re.sub(r'\s*\n\s*', '\n', text)
    text = re.sub(r'[ \t]+', ' ', text)

    return text.split("\n")