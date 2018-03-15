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
import java.util.Collections;
import java.util.List;

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
    private String inputText;

    /**
     * List of tokens and emoticons extracted from the {@link TextProperties#inputText}.
     */
    private List<String> wordsAndEmoticons;

    /**
     * List of tokens extracted from the {@link TextProperties#inputText}.
     * Emoticons are removed here.
     */
    private List<String> wordsOnly;

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
    private List<String> tokensAftersKeepingEmoticons(String unTokenizedText,
                                                      List<String> tokensWithoutPunctuations) throws IOException {
        final List<String> wordsAndEmoticonsList = new InputAnalyzer().keepPunctuation(unTokenizedText);
        return handleTokensWithAdjacentPunctuations(wordsAndEmoticonsList, tokensWithoutPunctuations);
    }

    /**
     * Remove punctuations from a token, if adjacent to it without a space and replace it with the original token.
     * e.g. going!!!! -> going OR !?!?there -> there
     *
     * @param tokensWithPunctuations    tokenized version of the input which has punctuations.
     * @param tokensWithoutPunctuations tokenized version of the input which has no punctuations.
     * @return a list of tokens.
     */
    private List<String> handleTokensWithAdjacentPunctuations(List<String> tokensWithPunctuations,
                                                              List<String> tokensWithoutPunctuations) {
        for (String currentWord : tokensWithoutPunctuations) {
            for (String currentPunctuation : Utils.PUNCTUATION_LIST) {
                final String wordPunct = currentWord + currentPunctuation;
                Integer wordPunctCount = Collections.frequency(tokensWithPunctuations, wordPunct);
                while (wordPunctCount > 0) {
                    final int index = tokensWithPunctuations.indexOf(wordPunct);
                    tokensWithPunctuations.set(index, currentWord);
                    wordPunctCount--;
                }

                final String punctWord = currentPunctuation + currentWord;
                Integer punctWordCount = Collections.frequency(tokensWithPunctuations, punctWord);
                while (punctWordCount > 0) {
                    final int index = tokensWithPunctuations.indexOf(punctWord);
                    tokensWithPunctuations.set(index, currentWord);
                    punctWordCount--;
                }
            }
        }
        return tokensWithPunctuations;
    }

    /**
     * This method tokenizes the input string, preserving the punctuation marks using
     *
     * @throws IOException if something goes wrong in the Lucene analyzer.
     * @see InputAnalyzer#tokenize(String, boolean)
     */
    private void setWordsAndEmoticons() throws IOException {
        setWordsOnly();
        this.wordsAndEmoticons = tokensAftersKeepingEmoticons(inputText, wordsOnly);
    }

    /**
     * This method tokenizes the input string, removing the special characters as well.
     *
     * @throws IOException iff there is an error which using Lucene analyzers.
     * @see InputAnalyzer#removePunctuation(String)
     */
    private void setWordsOnly() throws IOException {
        this.wordsOnly = new InputAnalyzer().removePunctuation(inputText);
    }

    public List<String> getWordsAndEmoticons() {
        return wordsAndEmoticons;
    }

    @SuppressWarnings("unused")
    public List<String> getWordsOnly() {
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
