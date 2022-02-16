import json

from companies_plugin import extractor
from companies_plugin.utils import add_logger
from scanr_publicationextractor import LIB_PATH
from scanr_publicationextractor.extractor import PubExtractor


@add_logger
class Extractor(extractor.Extractor):
    def __init__(self, batch_name, wanted_fields, conf):
        super().__init__(batch_name, wanted_fields, conf)
        self.ex = PubExtractor(conf.crawl_store)

    def extract(self, headers, properties, message):
        """
            The message is only {"url": ""} as input
            Output is {"url": "", "publications": ["", ""]}
        """
        reply_to = properties["reply_to"]
        msg = json.loads(message)

        dois, texts = self.ex.extract_publications(msg["url"])
        msg["dois"] = dois
        msg["references"] = texts

        return json.dumps(msg), reply_to


if __name__ == "__main__":
    m = extractor.Main(batch_name="PUBLICATION_EXTRACTOR",
                       queue_name="PUBLICATION_EXTRACTOR",
                       extractor_class=Extractor,
                       mod_path=LIB_PATH)
    m.launch()
