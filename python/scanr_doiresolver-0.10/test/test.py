# Read the documentation here:
#  https://nose.readthedocs.org/en/latest/testing.html

# Modify the import path to find our package
import sys
import os.path

sys.path = [os.path.abspath("../packagename")] + sys.path
sys.path = [os.path.abspath("..")] + sys.path

# Import our package
from scanr_doiresolver.resolver import _window


# test.* functions will be executed
def test__window():
    assert [[1, 2], [3, 4]] == list(_window([1, 2, 3, 4], 2))
    assert [[1, 2], [3, 4], [5]] == list(_window([1, 2, 3, 4, 5], 2))
    assert [] == list(_window([], 2))


# To skip a test:
from nose.plugins.skip import SkipTest


def test_something_else():
    raise SkipTest()
    assert False
