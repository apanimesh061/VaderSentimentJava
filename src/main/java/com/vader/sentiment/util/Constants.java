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

package com.vader.sentiment.util;

/**
 * @author Animesh Pandey
 *         Created on 5/14/2017.
 */
public final class Constants {
    /**
     *
     */
    public static final String POSITIVE_SCORE = "positive";

    /**
     *
     */
    public static final String NEGATIVE_SCORE = "negative";

    /**
     *
     */
    public static final String NEUTRAL_SCORE = "neutral";

    /**
     *
     */
    public static final String COMPOUND_SCORE = "compound";

    /**
     * Max allowed question marks in a string.
     * Beyond this value the affect of the Question marks will be considered the same.
     *
     * @see SentimentChangingTokens#QUESTION_MARK
     */
    public static final int MAX_QUESTION_MARKS = 3;

    /**
     * Window size for preceding trigram.
     */
    public static final int PRECEDING_TRIGRAM_WINDOW = 3;

    /**
     * Window size for preceding bigram.
     */
    public static final int PRECEDING_BIGRAM_WINDOW = 2;

    /**
     * Window size for preceding unigram.
     */
    public static final int PRECEDING_UNIGRAM_WINDOW = 1;

    /**
     * Private constructor for a utility class.
     */
    private Constants() {

    }
}
