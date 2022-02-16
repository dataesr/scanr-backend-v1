import json

from companies_plugin import extractor
from companies_plugin.utils import add_logger
from oaifetcher import LIB_PATH
from oaifetcher.fetcher import Fetcher

from datetime import datetime, timedelta, date


@add_logger
class Extractor(extractor.Extractor):
    def __init__(self, batch_name, wanted_fields, conf):
        super().__init__(batch_name, wanted_fields, conf)

    def extract_with_connection(self, connection, headers, properties, message):
        """
            {
                "id": "job_id",
                "body": {
                    "url": "http://oai.prodinra.inra.fr/ft",
                    "meta_prefix": "oai_inra"
                },
                "status": "2016-03-25"
            }
        :param msg:
        :return:
        """
        reply_to = properties["reply_to"]
        msg = json.loads(message)

        url = msg["body"]["url"]
        fetcher = Fetcher(url, msg["body"]["meta_prefix"])
        last_date = msg.get("status")
        self.logger.info("Fetching new entries in %s from date %s" % (url, last_date))

        from_date, to_date, force_status_date = self.scheduling_parameters(last_date)

        for r in fetcher.fetch(from_date, to_date):
            with connection.Producer(routing_key="OAI_ENTRIES") as producer:
                producer.publish(json.dumps(r), headers={"source-id": "OAI_FETCHER"},
                                 priority=properties.get("priority"))

        self.logger.info(
            "Correctly fetched %d entries, latest entry date was %s" % (fetcher.count, fetcher.latest_entry_date))
        result = {
            "id": msg["id"],
            "body": None,
            "status": force_status_date if force_status_date is not None else fetcher.latest_entry_date,
            "reschedule": force_status_date is not None
        }
        return json.dumps(result), reply_to

    @staticmethod
    def scheduling_parameters(last_date):
        from_date = None
        to_date = None
        force_status_date = None
        if last_date is not None:
            from_date = last_date[:10]
            last_date = datetime.strptime(from_date, "%Y-%m-%d").date()
            now = datetime.today().date()
            if (now - last_date) < timedelta(3):
                # Less than 3 days, we can do it in one shot
                pass
            elif last_date.year <= 2005:
                # Go month by month

                # Advance by one month
                next_month = date(last_date.year + int(last_date.month / 12), ((last_date.month % 12) + 1), 1)
                # Set the dates
                to_date = (next_month - timedelta(days=1)).isoformat()
                force_status_date = next_month.isoformat()
            else:
                # Go day by day
                force_status_date = (last_date + timedelta(days=1)).isoformat()
                to_date = from_date
        else:
            to_date = "1999-12-31"
            force_status_date = "2000-01-01"
        return from_date, to_date, force_status_date


if __name__ == "__main__":
    m = extractor.Main(batch_name="OAI_FETCHER",
                       queue_name="OAI_FETCHER",
                       extractor_class=Extractor,
                       mod_path=LIB_PATH)
    m.launch()
