package com.vader.analyzer;

import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.Tokenizer;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;


/**
 * @author Animesh Pandey
 *         Created on 4/9/2016.
 */
class VaderLuceneAnalyzer {

    static ArrayList<String> defaultSplit(String inputString) throws IOException {
        StringReader reader = new StringReader(inputString);
        Tokenizer whiteSpaceTokenizer = new WhitespaceTokenizer();
        whiteSpaceTokenizer.setReader(reader);
        final CharTermAttribute charTermAttribute = whiteSpaceTokenizer.addAttribute(CharTermAttribute.class);
        whiteSpaceTokenizer.reset();

        ArrayList<String> tokenizedString = new ArrayList<>();
        while (whiteSpaceTokenizer.incrementToken()) {
            tokenizedString.add(charTermAttribute.toString());
        }

        whiteSpaceTokenizer.end();
        whiteSpaceTokenizer.close();

        return tokenizedString;
    }

    static ArrayList<String> removePunctuation(String inputString) throws IOException {
        StringReader reader = new StringReader(inputString);
        Tokenizer removePunctuationTokenizer = new StandardTokenizer();
        removePunctuationTokenizer.setReader(reader);
        final CharTermAttribute charTermAttribute = removePunctuationTokenizer.addAttribute(CharTermAttribute.class);
        removePunctuationTokenizer.reset();

        ArrayList<String> tokenizedString = new ArrayList<>();
        while (removePunctuationTokenizer.incrementToken()) {
            tokenizedString.add(charTermAttribute.toString());
        }

        removePunctuationTokenizer.end();
        removePunctuationTokenizer.close();

        return tokenizedString;
    }

}
