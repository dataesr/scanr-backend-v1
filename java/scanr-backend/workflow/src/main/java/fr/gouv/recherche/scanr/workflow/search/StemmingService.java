/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.workflow.search;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.fr.FrenchLightStemmer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * This service will compute the Mapping between stem and most frequent corresponding word in preperation for the word cloud.
 */
@Service
public class StemmingService {
    private static final Logger log = LoggerFactory.getLogger(StemmingService.class);

    private Analyzer analyzer;


    public Map<String, String> buildStemMapping(String text) {
        Map<String, String> result = new HashMap<>();

        StringReader tReader = new StringReader(text);
        try (TokenStream tStream = analyzer.tokenStream("contents", tReader)) {
            CharTermAttribute term = tStream.addAttribute(CharTermAttribute.class);
            tStream.reset();

            FrenchLightStemmer stemmer = new FrenchLightStemmer();

            Map<String, List<String>> mapping = new HashMap<>();

            while (tStream.incrementToken()) {
                String rawTerm = new String(term.buffer(), 0, term.length());
                int len = stemmer.stem(term.buffer(), term.length());
                String stem = new String(term.buffer(), 0, len);
                mapping.putIfAbsent(stem, new ArrayList<>());
                mapping.get(stem).add(rawTerm);
            }

            // compute the most frequent raw term
            mapping.forEach((stem, terms) -> {
                Map<String, Long> collect = terms.stream().collect(Collectors.groupingBy(w -> w, Collectors.counting()));
                Map.Entry<String, Long> mostCommonEntry = collect.entrySet().stream().max(Comparator.comparing(Map.Entry::getValue)).get();
                String mostCurrentTerm = mostCommonEntry.getKey();
                if (!stem.equals(mostCurrentTerm) && (stem.indexOf('.') == -1)) {
                    result.putIfAbsent(stem, mostCurrentTerm);
                }
            });

        } catch (IOException ioe) {
            log.error("Invalid stemming mapping generation ", ioe);
        }
        return result;
    }

//    public StemmingService() {
//        analyzer = new MyFrenchAnalyzer();
//    }


    public static void main(String[] args) throws IOException {
        String text = "Salut les gars, j'aime bien les études, la justice, le spatial, la justice (c-radar) \"c-radar\" spatiale, études urbaines, justices spatiales, c'est très urbain et C-Radar c'est bien";

        StemmingService stemmingService = new StemmingService();
        //   stemmingService.buildStemMapping(text).forEach((k, v) -> System.out.println(k + "->" + v));
        System.out.println("------- Mapping size:" + stemmingService.buildStemMapping(text).size());
        System.out.println("------- Stemming");
        stemmingService.displayWordCloud(text, true);
        System.out.println("------- No stemming");
        stemmingService.displayWordCloud(text, false);
    }


    private void displayWordCloud(String text, boolean stemming) throws IOException {
        List<String> terms = new ArrayList<>();

        StringReader tReader = new StringReader(text);
        try (TokenStream tStream = analyzer.tokenStream("contents", tReader)) {
            CharTermAttribute term = tStream.addAttribute(CharTermAttribute.class);
            tStream.reset();
            FrenchLightStemmer stemmer = new FrenchLightStemmer();


            while (tStream.incrementToken()) {
                String rawTerm = new String(term.buffer(), 0, term.length());
                if (stemming) {
                    int len = stemmer.stem(term.buffer(), term.length());
                    rawTerm = new String(term.buffer(), 0, len);
                }
                terms.add(rawTerm);
            }

        }
        Map<String, String> mapping = stemming ? buildStemMapping(text) : new HashMap<>();

        terms.stream().collect(Collectors.groupingBy(w -> w, Collectors.counting())).entrySet().stream().sorted(Comparator.comparing(i -> -i.getValue())).limit(20).
                forEach(
                        e -> System.out.println(e.getKey() + "|" + mapping.getOrDefault(e.getKey(), e.getKey()) + " " + e.getValue())
                );
    }


    /**
     * French Anlyser with no stemming and stop word from french and english
     */
//    public final static class MyFrenchAnalyzer extends StopwordAnalyzerBase {
//
//        /**
//         * Default set of articles for ElisionFilter
//         */
//        public static final CharArraySet DEFAULT_ARTICLES = CharArraySet.unmodifiableSet(
//                new CharArraySet(Arrays.asList(
//                        "l", "m", "t", "qu", "n", "s", "j", "d", "c", "jusqu", "quoiqu", "lorsqu", "puisqu"), true));
//
//        private static class DefaultSetHolder {
//            static final CharArraySet FRENCH_STOP;
//            static final CharArraySet ENGLISH_STOP;
//
//            static {
//                try {
//                    FRENCH_STOP = WordlistLoader.getSnowballWordSet(IOUtils.getDecodingReader(SnowballFilter.class,
//                            "french_stop.txt", StandardCharsets.UTF_8));
//                    ENGLISH_STOP = WordlistLoader.getSnowballWordSet(IOUtils.getDecodingReader(SnowballFilter.class,
//                            "english_stop.txt", StandardCharsets.UTF_8));
//                } catch (IOException ex) {
//                    // default set should always be present as it is part of the
//                    // distribution (JAR)
//                    throw new RuntimeException("Unable to load default stopword set");
//                }
//            }
//        }
//
//
//        public MyFrenchAnalyzer() {
//            super(DefaultSetHolder.FRENCH_STOP);
//        }
//
//
//        /**
//         * Creates
//         * {@link org.apache.lucene.analysis.Analyzer.TokenStreamComponents}
//         * used to tokenize all the text in the provided {@link Reader}.
//         *
//         * @return {@link org.apache.lucene.analysis.Analyzer.TokenStreamComponents}
//         * built from a {@link StandardTokenizer} filtered with
//         * {@link StandardFilter}, {@link ElisionFilter},
//         * {@link LowerCaseFilter}, {@link StopFilter},
//         * {@link SetKeywordMarkerFilter} if a stem exclusion set is
//         * provided, and {@link FrenchLightStemFilter}
//         */
//        @Override
//        protected TokenStreamComponents createComponents(String fieldName) {
////            final Tokenizer source = new WhitespaceTokenizer();
//            byte[] defaultWordDelimTable = WordDelimiterIterator.DEFAULT_WORD_DELIM_TABLE;
//            defaultWordDelimTable[0x2D] = WordDelimiterFilter.LOWER;
//
//            final Tokenizer source = new WhitespaceTokenizer();
//            TokenStream result = new StandardFilter(source);
//            result = new WordDelimiterFilter(result, defaultWordDelimTable, 0, null);
//
//            result = new ElisionFilter(result, DEFAULT_ARTICLES);
//            result = new LowerCaseFilter(result);
//            result = new StopFilter(result, stopwords);
//            result = new StopFilter(result, DefaultSetHolder.ENGLISH_STOP);
//            return new TokenStreamComponents(source, result);
//        }
//    }
}
