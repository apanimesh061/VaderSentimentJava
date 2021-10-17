/*
 * MIT License
 *
 * Copyright (c) 2021 Animesh Pandey
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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.vader.sentiment.util.Utils;

/**
 * The TextProperties class implements the pre-processing steps of the input string for sentiment analysis.
 * It utilizes the Lucene analyzer to perform processing on the input string.
 *
 * @author Animesh Pandey
 */
public final class TextProperties {
    /**
     * String whose properties will be extracted.
     */
    private final String inputText;

    /**
     * List of tokens and emoticons extracted from the {@link TextProperties#inputText}.
     */
    private List<String> wordsAndEmoticons;

    /**
     * Set of tokens extracted from the {@link TextProperties#inputText}.
     * Emoticons are removed here.
     */
    private Set<String> wordsOnly;

    /**
     * Flags that specifies if the current string has yelling words.
     */
    private boolean hasYellWords;

    /**
     * Parameterized constructor accepting the input string that will be processed.
     *
     * @param inputText the input string
     * @throws IOException if there is an issue with the lucene analyzers
     */
    public TextProperties(final String inputText) throws IOException {
        this.inputText = inputText;
        setWordsAndEmoticons();
        setHasYellWords(hasCapDifferential(getWordsAndEmoticons()));
    }

    /**
     * Tokenize the input text in two steps:
     * 1. Use Lucene analyzer to tokenize while preserving the punctuations, so that the emoticons are preserved.
     * 2. Remove punctuations from a token, if adjacent to it without a space and replace it with the original token.
     * e.g. going!!!! -> going OR !?!?there -> there
     *
     * @param unTokenizedText           original text to be analyzed.
     * @param tokensWithoutPunctuations tokenized version of the input which has no punctuations.
     * @return tokenized version which preserves all the punctuations so that emoticons are preserved.
     * @throws IOException if there was an issue while Lucene was processing unTokenizedText
     */
    private List<String> tokensAftersKeepingEmoticons(final String unTokenizedText,
                                                      final Set<String> tokensWithoutPunctuations) throws IOException {
        final List<String> wordsAndEmoticonsList = new ArrayList<>();
        new InputAnalyzer().keepPunctuation(unTokenizedText, wordsAndEmoticonsList::add);
        wordsAndEmoticonsList.replaceAll(t -> stripPunctuations(t, tokensWithoutPunctuations));
        return wordsAndEmoticonsList;
    }

    /**
     * Remove punctuations from a token, if adjacent to it without a space and replace it with the original token.
     * e.g. going!!!! -> going OR !?!?there -> there
     *
     * @param token                     token that potentially includes punctuations.
     * @param tokensWithoutPunctuations tokenized version of the input which has no punctuations.
     * @return the token with any such punctuation removed from it, or the original token otherwise
     */
    private String stripPunctuations(String token, Set<String> tokensWithoutPunctuations) {
        for (final String punct : Utils.PUNCTUATIONS) {
            if (token.startsWith(punct)) {
                final String strippedToken = token.substring(punct.length());
                if (tokensWithoutPunctuations.contains(strippedToken)) {
                    return strippedToken;
                }
            } else if (token.endsWith(punct)) {
                final String strippedToken = token.substring(0, token.length() - punct.length());
                if (tokensWithoutPunctuations.contains(strippedToken)) {
                    return strippedToken;
                }
            }
        }
        return token;
    }

    /**
     * This method tokenizes the input string, preserving the punctuation marks using a custom Lucene analyzer.
     *
     * @throws IOException if something goes wrong in the Lucene analyzer.
     * @see InputAnalyzer#tokenize(String, org.apache.lucene.analysis.Tokenizer, java.util.function.Consumer)
     */
    private void setWordsAndEmoticons() throws IOException {
        setWordsOnly();
        this.wordsAndEmoticons = tokensAftersKeepingEmoticons(inputText, wordsOnly);
    }

    /**
     * This method tokenizes the input string, removing the special characters as well.
     *
     * @throws IOException iff there is an error which using Lucene analyzers.
     * @see InputAnalyzer#removePunctuation(String, java.util.function.Consumer)
     */
    private void setWordsOnly() throws IOException {
        this.wordsOnly = new HashSet<>();
        new InputAnalyzer().removePunctuation(inputText, wordsOnly::add);
    }

    public List<String> getWordsAndEmoticons() {
        return wordsAndEmoticons;
    }

    @SuppressWarnings("unused")
    public Set<String> getWordsOnly() {
        return wordsOnly;
    }

    public boolean isYelling() {
        return hasYellWords;
    }

    private void setHasYellWords(boolean hasYellWords) {
        this.hasYellWords = hasYellWords;
    }

    /**
     * Return true iff the input has yelling words i.e. all caps in the tokens,
     * but all the token should not be in upper case.
     * e.g. [GET, THE, HELL, OUT] returns false
     * [GET, the, HELL, OUT] returns true
     * [get, the, hell, out] returns false
     *
     * @param tokenList a list of strings
     * @return boolean value
     */
    private boolean hasCapDifferential(List<String> tokenList) {
        int countAllCaps = 0;
        for (String token : tokenList) {
            if (Utils.isUpper(token)) {
                countAllCaps++;
            }
        }
        final int capDifferential = tokenList.size() - countAllCaps;
        return (capDifferential > 0) && (capDifferential < tokenList.size());
    }
}
