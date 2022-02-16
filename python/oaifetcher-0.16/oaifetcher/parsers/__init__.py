from oaifetcher.parsers import prodinra, aofr, irstea

parsers = {
    prodinra.NAMESPACE: prodinra.parse_inra,
    aofr.NAMESPACE: aofr.parse_aofr,
    irstea.NAMESPACE: irstea.parse_irstea
}