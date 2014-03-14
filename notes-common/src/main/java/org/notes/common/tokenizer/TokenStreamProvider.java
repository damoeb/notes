package org.notes.common.tokenizer;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.de.GermanAnalyzer;
import org.apache.lucene.analysis.de.GermanLightStemFilter;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.util.Version;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import java.io.StringReader;

@LocalBean
@Stateless
@TransactionAttribute(TransactionAttributeType.NEVER)
public class TokenStreamProvider {

    public TokenStreamProvider() {

    }

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public TokenStream getTokenizer(String text, Language lang) {

        if (lang == null) {
            throw new IllegalArgumentException("lang is null");
        }

        TokenStream tokenStream = new StandardTokenizer(getLuceneVersion(), new StringReader(text));

        if (Language.GERMAN == lang) {
            tokenStream = new StopFilter(getLuceneVersion(), tokenStream, GermanAnalyzer.getDefaultStopSet());
            tokenStream = new GermanLightStemFilter(tokenStream);

        } else if (Language.ENGLISH == lang) {
            CharArraySet stop_word_set = new CharArraySet(getLuceneVersion(), StopAnalyzer.ENGLISH_STOP_WORDS_SET, true);

            tokenStream = new StopFilter(getLuceneVersion(), tokenStream, stop_word_set);
            tokenStream = new PorterStemFilter(tokenStream);
        }

        return tokenStream;
    }

    public Version getLuceneVersion() {
        return Version.LUCENE_46;
    }

}
