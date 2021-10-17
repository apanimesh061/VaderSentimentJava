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
 * This class object will store the normalized scores that we get from {@link RawSentimentScores}.
 * The positivePolarity, neutralPolarity, and negativePolarity scores are ratios for proportions
 * of text that fall in each category (so these should all add up to be 1... or close to it with
 * float operation). These are the most useful metrics if you want multidimensional measures of
 * sentiment for a given sentence.
 * The compoundPolarity score is computed by summing the valence scores of each word in the lexicon,
 * adjusted according to the rules, and then normalized to be between -1 (most extreme negative) and +1
 * (most extreme positive). This is the most useful metric if you want a single uni-dimensional measure of
 * sentiment for a given sentence. Calling it a "normalized, weighted composite score" is accurate.
 *
 * @author Animesh Pandey
 */
public final class SentimentPolarities {
    /**
     * This represents proportion of text that is positive.
     */
    private final float positivePolarity;

    /**
     * This represents proportion of text that is negative.
     */
    private final float negativePolarity;

    /**
     * This represents proportion of text that is neutral.
     */
    private final float neutralPolarity;

    /**
     * This represents compound score.
     */
    private final float compoundPolarity;

    /**
     * Creates an object of this class and sets all the fields.
     *
     * @param positivePolarity proportion of text that is positive.
     * @param negativePolarity proportion of text that is negative.
     * @param neutralPolarity  proportion of text that is neutral.
     * @param compoundPolarity compound score.
     */
    public SentimentPolarities(float positivePolarity, float negativePolarity, float neutralPolarity,
                               float compoundPolarity) {
        this.positivePolarity = positivePolarity;
        this.negativePolarity = negativePolarity;
        this.neutralPolarity = neutralPolarity;
        this.compoundPolarity = compoundPolarity;
    }

    /**
     * Sometimes, if the string that is to be processed, is either empty, null or has un-identified tokens.
     * In this case all the polarities are set to zero.
     *
     * @return an object of {@link SentimentPolarities} class with all polarities set to 0.0.
     */
    public static SentimentPolarities emptySentimentState() {
        return new SentimentPolarities(0.0F, 0.0F, 0.0F, 0.0F);
    }

    public float getPositivePolarity() {
        return positivePolarity;
    }

    public float getNegativePolarity() {
        return negativePolarity;
    }

    public float getNeutralPolarity() {
        return neutralPolarity;
    }

    public float getCompoundPolarity() {
        return compoundPolarity;
    }

    @Override
    public String toString() {
        return "SentimentPolarities{"
            + "positivePolarity=" + positivePolarity
            + ", negativePolarity=" + negativePolarity
            + ", neutralPolarity=" + neutralPolarity
            + ", compoundPolarity=" + compoundPolarity
            + '}';
    }
}
