/*
 * MIT License
 *
 * Copyright (c) 2018 Animesh Pandey
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.vader.sentiment.processor;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.analysis.miscellaneous.LengthFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

/**
 * This class defines a Lucene analyzer that is applied on the input string in
 * {@link com.vader.sentiment.analyzer.SentimentAnalyzer}.
 *
 * @author Animesh Pandey
 */
class InputAnalyzer implements InputAnalyzerInterface {
    /**
     * This function applies a Lucene analyzer that splits a string into a tokens.
     * Here we are using two types of Lucene {@link Tokenizer}s:
     * 1. {@link WhitespaceTokenizer} which tokenizes from the white spaces
     * 2. {@link StandardTokenizer} which tokenizes from white space as well as removed any punctuations
     *
     * @param inputString       The input string to be pre-processed with Lucene tokenizer
     * @param removePunctuation Flag which specifies if punctuations have to be removed from the string.
     * @return tokens
     * @throws IOException if Lucene's analyzer encounters any error
     */
    private List<String> tokenize(String inputString, boolean removePunctuation) throws IOException {
        final StringReader reader = new StringReader(inputString);
        final Tokenizer currentTokenizer;
        if (removePunctuation) {
            currentTokenizer = new StandardTokenizer();
        } else {
            currentTokenizer = new WhitespaceTokenizer();
        }
        currentTokenizer.setReader(reader);

        final TokenStream tokenStream = new LengthFilter(currentTokenizer, 2, Integer.MAX_VALUE);
        final CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
        tokenStream.reset();

        final List<String> tokenizedString = new ArrayList<>();
        while (tokenStream.incrementToken()) {
            tokenizedString.add(charTermAttribute.toString());
        }

        tokenStream.end();
        tokenStream.close();

        return tokenizedString;
    }

    /**
     * Implementation of {@link InputAnalyzerInterface#defaultSplit(String)}.
     *
     * {@inheritDoc}
     */
    @Override
    public List<String> defaultSplit(String inputString) throws IOException {
        return tokenize(inputString, false);
    }

    /**
     * Implementation of {@link InputAnalyzerInterface#removePunctuation(String)}.
     *
     * {@inheritDoc}
     */
    @Override
    public List<String> removePunctuation(String inputString) throws IOException {
        return tokenize(inputString, true);
    }
}
