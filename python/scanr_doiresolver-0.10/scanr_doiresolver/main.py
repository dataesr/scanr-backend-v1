import json

from companies_plugin import extractor
from companies_plugin.utils import add_logger
from scanr_doiresolver import LIB_PATH
from scanr_doiresolver.resolver import resolve_publications


@add_logger
class Extractor(extractor.Extractor):
    def extract(self, headers, properties, message):
        """
            The message is only {"url": ""} as input
            Output is {"url": "", "publications": ["", ""]}
        """
        reply_to = properties["reply_to"]
        msg = json.loads(message)

        return json.dumps({
            "id": msg.get("id"),
            "url": msg.get("url"),
            "publications": resolve_publications(msg.get("dois", []), msg.get("references", []))
        }), reply_to


if __name__ == "__main__":
    m = extractor.Main(batch_name="PUBLICATION_RESOLVER",
                       queue_name="PUBLICATION_RESOLVER",
                       extractor_class=Extractor,
                       mod_path=LIB_PATH)
    m.launch()
