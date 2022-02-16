import requests
import json
import re
import time

from companies_plugin.utils import add_logger
from pyquery import PyQuery as pq


@add_logger
def resolve_refs(lines, logger=None):
    lines = [line.replace('\\', ' ').replace('~', ' ') for line in lines]
    total_lines = len(lines)
    logger.info("Calling to resolve %d refs" % total_lines)
    if total_lines > 10:
        raise Exception("Too many refs given for resolution [%d]. A maximum of 10 is allowed" % total_lines)
    if total_lines == 0:
        return []
    result = requests.post("https://search.crossref.org/references", data={"references": "\n".join(lines)})
    # Soft recovery from doing a hardwork
    time.sleep(1)
    if result.status_code == 502:
        # The number of refs is too large, retry with a subset
        if total_lines >= 2:
            return resolve_refs(lines[:total_lines // 2]) | resolve_refs(lines[total_lines // 2:])
    if result.status_code != 200:
        raise Exception("Invalid status code %d from crossref search %s" % (result.status_code, result.text))
    html = pq(result.text)
    if "Match score" not in result.text:
        raise Exception("Invalid result content: "+html.text())
    result = set()
    for tr in html.items("tr"):
        tds = tr("td")
        href = tds.eq(0).find("a").attr("href")
        if not href:
            continue
        score = float(tds.eq(1).text())
        if score < 100:
            continue
        result.add(re.sub(".*/([^/]*/[^/]*)$", "\\1", href))
    return result


@add_logger
def resolve_dois(doi, logger=None):
    if not re.match("^10\\.[^/]+/[^/]+$", doi):
        return None
    logger.info("Calling to resolve doi %s" % doi)
    result = requests.get("http://api.crossref.org/works/" + doi)
    # Soft recovery from doing a hardwork
    time.sleep(0.5)
    if result.status_code == 404:
        # Unknown
        return None
    if result.status_code != 200:
        raise Exception("Invalid status code %d from crossref doi get %s" % (result.status_code, result.text))
    res = json.loads(result.text)
    if res.get("status") != "ok":
        raise Exception("Invalid json content %s" % result.text)
    return res["message"]