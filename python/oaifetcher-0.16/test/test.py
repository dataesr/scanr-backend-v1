# Read the documentation here:
#  https://nose.readthedocs.org/en/latest/testing.html

# Modify the import path to find our package
import sys
import os.path
sys.path = [os.path.abspath("../oaifetcher")] + sys.path
sys.path = [os.path.abspath("..")] + sys.path


# Import our package
from oaifetcher.patent import extract_patents


def test_extract_patents():
    assert ["US8130258"] == extract_patents("US 8,130,258 B2")
    assert ["WO2007085623"] == extract_patents("WO 2007/085623 A1")
    assert [] == extract_patents("10/54780")
    assert ["FR1054780"] == extract_patents("FR 10/54780")
    assert ["FR1054780"] == extract_patents("10/54780", default_country="FR")
    assert ["FR1054780"] == extract_patents("10/54780.A1", default_country="FR")
    assert [] == extract_patents("Hey 10/54780", default_country="FR")
    assert [] == extract_patents("INPI N°10/00246", default_country="FR")
    assert ["EP11183034", "US61540085"] == extract_patents("EP11183034.5 & US61/540,085 ,")
    assert ["PCT/IB2011055210", "EP2640748", "WO2012066519"] == extract_patents("PCT/IB2011/055210; EP2640748; WO2012066519")
    assert ["WO2012001329"] == extract_patents("WO 2012/001329")
