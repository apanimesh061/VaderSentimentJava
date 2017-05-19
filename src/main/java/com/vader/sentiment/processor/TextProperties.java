/*
 * MIT License
 *
 * Copyright (c) 2017 Animesh Pandey
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

import com.vader.sentiment.util.Utils;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * The TextProperties class implements the pre-processing steps of the input string for sentiment analysis.
 * It utilizes the Lucene analyzers
 *
 * @author Animesh Pandey
 *         Created on 4/10/2016.
 */
public class TextProperties {
    private String inputText;
    private List<String> wordsAndEmoticons;
    private List<String> wordsOnly;
    private boolean isCapDIff;

    /**
     * This method tokenizes the input string, preserving the punctuation marks using
     *
     * @throws IOException if something goes wrong in the Lucene analyzer.
     * @see InputAnalyzer#tokenize(String, boolean)
     */
    private void setWordsAndEmoticons() throws IOException {
        setWordsOnly();

        List<String> wordsAndEmoticonsList = new InputAnalyzer().defaultSplit(inputText);
        for (String currentWord : wordsOnly) {
            for (String currentPunc : Utils.PUNCTUATION_LIST) {
                String pWord = currentWord + currentPunc;
                Integer pWordCount = Collections.frequency(wordsAndEmoticonsList, pWord);
                while (pWordCount > 0) {
                    int index = wordsAndEmoticonsList.indexOf(pWord);
                    wordsAndEmoticonsList.remove(pWord);
                    wordsAndEmoticonsList.add(index, currentWord);
                    pWordCount = Collections.frequency(wordsAndEmoticonsList, pWord);
                }

                String wordP = currentPunc + currentWord;
                Integer wordPCount = Collections.frequency(wordsAndEmoticonsList, wordP);
                while (wordPCount > 0) {
                    int index = wordsAndEmoticonsList.indexOf(wordP);
                    wordsAndEmoticonsList.remove(wordP);
                    wordsAndEmoticonsList.add(index, currentWord);
                    wordPCount = Collections.frequency(wordsAndEmoticonsList, wordP);
                }
            }
        }
        this.wordsAndEmoticons = wordsAndEmoticonsList;
    }

    /**
     * This method tokenizes the input string, removing the special characters as well
     *
     * @throws IOException
     * @see InputAnalyzer#removePunctuation(String)
     */
    private void setWordsOnly() throws IOException {
        this.wordsOnly = new InputAnalyzer().removePunctuation(inputText);
    }

    private void setCapDiff(boolean capDIff) {
        isCapDIff = capDIff;
    }

    /**
     * @return True iff the input has yelling words i.e. all caps in the tokens, but all the token should not be
     * in upper case.
     * e.g. [GET, THE, HELL, OUT] returns false
     * [GET, the, HELL, OUT] returns true
     * [get, the, hell, out] returns false
     */
    private boolean isAllCapDifferential() {
        int countAllCaps = 0;
        for (String s : wordsAndEmoticons) {
            if (Utils.isUpper(s)) {
                countAllCaps++;
            }
        }
        int capDifferential = wordsAndEmoticons.size() - countAllCaps;
        return (0 < capDifferential) && (capDifferential < wordsAndEmoticons.size());
    }

    public List<String> getWordsAndEmoticons() {
        return wordsAndEmoticons;
    }

    public List<String> getWordsOnly() {
        return wordsOnly;
    }

    public boolean isCapDiff() {
        return isCapDIff;
    }

    /**
     *
     * @param inputText
     * @throws IOException
     */
    public TextProperties(String inputText) throws IOException {
        this.inputText = inputText;
        setWordsAndEmoticons();
        setCapDiff(isAllCapDifferential());
    }

    public static void main(String[] args) throws IOException {
        String input = "The plot was good, but the characters are uncompelling and the dialog is not great. :( :(";
        TextProperties properties = new TextProperties(input);
        System.out.println(properties.getWordsOnly());
        System.out.println(properties.getWordsAndEmoticons());
        System.out.println(properties.isCapDiff());
    }
}
