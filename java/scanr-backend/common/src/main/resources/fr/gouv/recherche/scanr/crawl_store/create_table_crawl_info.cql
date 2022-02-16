CREATE TABLE IF NOT EXISTS crawl_info (
    id uuid,
    url text,
    depth int,
    max_pages int,
    page_count int,
    start_date timestamp,
    end_date timestamp,
    status text,
    main_lang text,
    histogram map<text, int>,
    PRIMARY KEY(url, depth, max_pages)
);
