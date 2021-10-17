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

package com.vader.sentiment.util;

/**
 * List of default values of valence modifiers.
 * This list has values as well as factors that modify the valence.
 *
 * @author Animesh Pandey
 */
public enum Valence {
    /**
     * This denotes the default valence for token that boost.
     */
    DEFAULT_BOOSTING(0.293F),

    /**
     * This denotes the default valence for token that damp.
     */
    DEFAULT_DAMPING(-0.293F),

    /**
     * Boosting factor for strings having a '?'.
     */
    ALL_CAPS_FACTOR(0.733F),

    /**
     * If a negative word is encountered, its valence is reduced by this factor.
     */
    NEGATIVE_WORD_DAMPING_FACTOR(-0.74F),

    /**
     * Boosting factor for strings having a '!'.
     */
    EXCLAMATION_BOOSTING(0.292F),

    /**
     * Boosting factor for strings having a '?'.
     */
    QUESTION_MARK_BOOSTING(0.96F),

    /**
     * Boosting factor for strings having 3 or more '?'s.
     */
    QUESTION_MARK_MAX_COUNT_BOOSTING(0.18F),

    /**
     * If the preceding trigram has a "never" type phrase, increase the negative valence by 25%.
     */
    PRECEDING_TRIGRAM_HAVING_NEVER_DAMPING_FACTOR(1.25F),

    /**
     * If the preceding bigram has a "never" type phrase, increase the negative valence by 50%.
     */
    PRECEDING_BIGRAM_HAVING_NEVER_DAMPING_FACTOR(1.5F),

    /**
     * At distance of 1 from current token, reduce current gram's valence by 5%.
     */
    ONE_WORD_DISTANCE_DAMPING_FACTOR(0.95F),

    /**
     * At distance of 2 from current token, reduce current gram's valence by 10%.
     */
    TWO_WORD_DISTANCE_DAMPING_FACTOR(0.9F),

    /**
     * If the conjunction is after the current token then reduce valence by 50%.
     */
    PRE_CONJUNCTION_ADJUSTMENT_FACTOR(0.5F),

    /**
     * If the conjunction is before the current token then increase valence by 50%.
     */
    POST_CONJUNCTION_ADJUSTMENT_FACTOR(1.5F);

    /**
     * Valence value.
     */
    private final float value;

    /**
     * Enum constructor.
     *
     * @param value valence value
     */
    Valence(float value) {
        this.value = value;
    }

    public float getValue() {
        return value;
    }
}
