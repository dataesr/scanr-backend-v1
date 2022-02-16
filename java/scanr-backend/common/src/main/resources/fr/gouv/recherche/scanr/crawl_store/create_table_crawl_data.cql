CREATE TABLE IF NOT EXISTS crawl_data (
    headers map<text, text>,
    content text,
    http_status int,
    title text,
    domain text,
    charset text,
    crawl_date timestamp,
    crawl_id uuid,
    content_type text,
    url text,
    depth int,
    relevant_txt text,
    lang text,
    PRIMARY KEY(crawl_id, url)
);
