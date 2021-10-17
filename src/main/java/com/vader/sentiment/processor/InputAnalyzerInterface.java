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
import java.util.function.Consumer;

/**
 * This interface defines methods that use two methods for splitting up a raw string.
 *
 * @author Animesh Pandey
 */
interface InputAnalyzerInterface {
    /**
     * This method performs tokenization without punctuation removal.
     *
     * @param inputString   The input string to be pre-processed with Lucene tokenizer
     * @param tokenConsumer The consumer of the tokens
     * @throws IOException if Lucene's analyzer encounters any error
     */
    void keepPunctuation(String inputString, Consumer<String> tokenConsumer) throws IOException;

    /**
     * This method performs tokenization with punctuation removal.
     *
     * @param inputString   The input string to be pre-processed with Lucene tokenizer
     * @param tokenConsumer The consumer of the tokens
     * @throws IOException if Lucene's analyzer encounters any error
     */
    void removePunctuation(String inputString, Consumer<String> tokenConsumer) throws IOException;
}
