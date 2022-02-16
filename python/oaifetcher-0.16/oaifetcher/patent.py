import re


def extract_patents(line, default_country=None):
    """
    Extract a list of patent number strings
    Supports only standard patent publication number like WO2012066519

    Tolerance with spaces, punctuations, slashes

    Keep application numbers like PCT/IB2011055210

    If a iso2 country code is provided, then it will try match just a number

    :param line: The free text line
    :param default_country: The default country
    :return:
    """
    result = []
    line = ("" if default_country is None else " " + default_country) + " " + line + " "
    for m in re.findall("(?:[^a-z])((?:pct/?)?[a-z]{2})([0-9 ,/]{6,})", line.lower()):
        num = re.sub("[ ,-/]", "", m[1])
        country = m[0].upper()
        if len(num) < 5:
            continue
        result.append(country+num)
    return result
