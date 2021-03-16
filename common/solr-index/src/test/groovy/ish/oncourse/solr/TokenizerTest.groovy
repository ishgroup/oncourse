package ish.oncourse.solr


import com.carrotsearch.randomizedtesting.RandomizedRunner
import org.apache.lucene.analysis.BaseTokenStreamTestCase
import org.apache.lucene.analysis.core.LowerCaseFilterFactory
import org.apache.lucene.analysis.core.StopFilterFactory
import org.apache.lucene.analysis.core.WhitespaceTokenizerFactory
import org.apache.lucene.analysis.custom.CustomAnalyzer
import org.apache.lucene.analysis.hunspell.HunspellStemFilterFactory
import org.apache.lucene.analysis.miscellaneous.WordDelimiterGraphFilterFactory
import org.apache.lucene.analysis.standard.StandardTokenizerFactory
import org.apache.lucene.analysis.synonym.SynonymGraphFilterFactory
import org.hamcrest.SelfDescribing
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(RandomizedRunner.class)
class TokenizerTest {


    @Test
    void testWhitespaceTokenizer()  {
        SelfDescribing.class
        CustomAnalyzer analyzer = CustomAnalyzer.builder()
                .withTokenizer(WhitespaceTokenizerFactory.class)
                .addTokenFilter(SynonymGraphFilterFactory.class, [synonyms:'synonyms.txt'])
                .addTokenFilter(StopFilterFactory.class, [ignoreCase: "true", words:  "stopwords.txt"])
                .addTokenFilter(WordDelimiterGraphFilterFactory.class)
                .addTokenFilter(LowerCaseFilterFactory.class)
                .addTokenFilter(HunspellStemFilterFactory.class, [dictionary:'en_AU.dic', affix:'en_AU.aff', ignoreCase:'true'])
                .build()
        BaseTokenStreamTestCase.assertAnalyzesTo(analyzer, "contact", ['contact','tact'].toArray() as String[])
        BaseTokenStreamTestCase.assertAnalyzesTo(analyzer, "react", ['react','act'].toArray() as String[])
        BaseTokenStreamTestCase.assertAnalyzesTo(analyzer, "extraction", ['extraction'].toArray() as String[])

    }

    @Test
    void testStandardTokenizer()  {
        SelfDescribing.class
        CustomAnalyzer analyzer = CustomAnalyzer.builder()
                .withTokenizer(StandardTokenizerFactory.class)
                .addTokenFilter(SynonymGraphFilterFactory.class, [synonyms:'synonyms.txt'])
                .addTokenFilter(StopFilterFactory.class, [ignoreCase: "true", words:  "stopwords.txt"])
                .addTokenFilter(WordDelimiterGraphFilterFactory.class)
                .addTokenFilter(LowerCaseFilterFactory.class)
//                .addTokenFilter(HunspellStemFilterFactory.class, [dictionary:'en_AU.dic', affix:'en_AU.aff', ignoreCase:'true'])
                .build()
        BaseTokenStreamTestCase.assertAnalyzesTo(analyzer, "contact", ['contact'].toArray() as String[])
        BaseTokenStreamTestCase.assertAnalyzesTo(analyzer, "react", ['react'].toArray() as String[])
        BaseTokenStreamTestCase.assertAnalyzesTo(analyzer, "extraction", ['extraction'].toArray() as String[])

    }
}
