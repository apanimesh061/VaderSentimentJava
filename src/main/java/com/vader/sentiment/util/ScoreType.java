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
 * This class defines labels for the four types of sentiment scores.
 *
 * @author Animesh Pandey
 *         Created on 5/28/2017.
 */
public final class ScoreType {
    /**
     * Label for positive sentiment.
     */
    public static final String POSITIVE = "positive";

    /**
     * Label for negative sentiment.
     */
    public static final String NEGATIVE = "negative";

    /**
     * Label for neutral sentiment.
     */
    public static final String NEUTRAL = "neutral";

    /**
     * Label for compound sentiment.
     */
    public static final String COMPOUND = "compound";

    /**
     * Private constructor for utility class.
     */
    private ScoreType() {

    }
}
