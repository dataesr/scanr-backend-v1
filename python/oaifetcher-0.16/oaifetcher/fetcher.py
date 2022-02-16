from oaifetcher.oairecord import OAIRecord
from sickle import Sickle
from sickle.oaiexceptions import NoRecordsMatch


class Fetcher:
    def __init__(self, url, meta_prefix):
        self.count = 0
        self.sickle = Sickle(url)
        self.sickle.class_mapping['ListRecords'] = OAIRecord
        self.meta_prefix = meta_prefix
        self.latest_entry_date = None

    def fetch(self, from_date, to_date):
        self.count = 0
        try:
            for r in self.sickle.ListRecords(**{'metadataPrefix': self.meta_prefix, 'from': from_date, 'until': to_date}):
                self.count += 1
                if r.header.datestamp is not None:
                    if self.latest_entry_date is None or r.header.datestamp >= self.latest_entry_date:
                        self.latest_entry_date = r.header.datestamp
                if r.data is not None:
                    yield r.data
        except NoRecordsMatch:
            # Finished harvesting or no record have been returned
            pass
        # Ensure that latest_entry_date is never none
        if self.latest_entry_date is None:
            self.latest_entry_date = from_date if to_date is None else to_date
