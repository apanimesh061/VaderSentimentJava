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
    private boolean isCapDiff;

    /**
     * Parameterized constructor accepting the input string that will be processed.
     *
     * @param inputText the input string
     * @throws IOException if there is an issue with the lucene analyzers
     */
    public TextProperties(final String inputText) throws IOException {
        this.inputText = inputText;
        setWordsAndEmoticons();
        setCapDiff(isAllCapDifferential());
    }

    /**
     * This method tokenizes the input string, preserving the punctuation marks using
     *
     * @throws IOException if something goes wrong in the Lucene analyzer.
     * @see InputAnalyzer#tokenize(String, boolean)
     */
    private void setWordsAndEmoticons() throws IOException {
        setWordsOnly();

        final List<String> wordsAndEmoticonsList = new InputAnalyzer().defaultSplit(inputText);
        for (String currentWord : wordsOnly) {
            for (String currentPunc : Utils.PUNCTUATION_LIST) {
                final String wordPunct = currentWord + currentPunc;
                Integer wordPunctCount = Collections.frequency(wordsAndEmoticonsList, wordPunct);
                while (wordPunctCount > 0) {
                    final int index = wordsAndEmoticonsList.indexOf(wordPunct);
                    wordsAndEmoticonsList.remove(wordPunct);
                    wordsAndEmoticonsList.add(index, currentWord);
                    wordPunctCount = Collections.frequency(wordsAndEmoticonsList, wordPunct);
                }

                final String punctWord = currentPunc + currentWord;
                Integer punctWordCount = Collections.frequency(wordsAndEmoticonsList, punctWord);
                while (punctWordCount > 0) {
                    final int index = wordsAndEmoticonsList.indexOf(punctWord);
                    wordsAndEmoticonsList.remove(punctWord);
                    wordsAndEmoticonsList.add(index, currentWord);
                    punctWordCount = Collections.frequency(wordsAndEmoticonsList, punctWord);
                }
            }
        }
        this.wordsAndEmoticons = wordsAndEmoticonsList;
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

    private void setCapDiff(final boolean capDiff) {
        this.isCapDiff = capDiff;
    }

    public List<String> getWordsAndEmoticons() {
        return wordsAndEmoticons;
    }

    @SuppressWarnings("unused")
    public List<String> getWordsOnly() {
        return wordsOnly;
    }

    public boolean isCapDiff() {
        return isCapDiff;
    }

    /**
     * Return true iff the input has yelling words i.e. all caps in the tokens, but all the token should not be
     * in upper case.
     * e.g. [GET, THE, HELL, OUT] returns false
     * [GET, the, HELL, OUT] returns true
     * [get, the, hell, out] returns false
     *
     * @return boolean value
     */
    private boolean isAllCapDifferential() {
        int countAllCaps = 0;
        for (String token : wordsAndEmoticons) {
            if (Utils.isUpper(token)) {
                countAllCaps++;
            }
        }
        final int capDifferential = wordsAndEmoticons.size() - countAllCaps;
        return (0 < capDifferential) && (capDifferential < wordsAndEmoticons.size());
    }
}
