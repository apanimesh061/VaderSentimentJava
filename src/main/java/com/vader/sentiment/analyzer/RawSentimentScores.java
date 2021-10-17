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

package com.vader.sentiment.analyzer;

/**
 * This class defines the three types of raw sentiment scores which are non-normalized.
 *
 * @author Animesh Pandey
 */
public final class RawSentimentScores {
    /**
     * This is the raw positive sentiment score.
     */
    private final float positiveScore;

    /**
     * This is the raw negative sentiment score.
     */
    private final float negativeScore;

    /**
     * This is the raw neutral sentiment score.
     */
    private final float neutralScore;

    /**
     * Creates an object of this class and sets all the fields.
     *
     * @param positiveScore positive score
     * @param negativeScore negative score
     * @param neutralScore  neutral score
     */
    public RawSentimentScores(float positiveScore, float negativeScore, float neutralScore) {
        this.positiveScore = positiveScore;
        this.negativeScore = negativeScore;
        this.neutralScore = neutralScore;
    }

    public float getPositiveScore() {
        return positiveScore;
    }

    public float getNegativeScore() {
        return negativeScore;
    }

    public float getNeutralScore() {
        return neutralScore;
    }
}
