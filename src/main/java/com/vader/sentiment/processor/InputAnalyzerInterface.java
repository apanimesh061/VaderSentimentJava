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
import java.util.List;

/**
 * This interface defines methods that use two methods for splitting up a raw string.
 *
 * @author Animesh Pandey
 */
interface InputAnalyzerInterface {
    /**
     * This is {@link InputAnalyzer#tokenize(String, boolean)} with removePunctuation set as false. So, this
     * method performs tokenization without removing punctuations.
     *
     * @param inputString The input string to be pre-processed with Lucene tokenizer
     * @return tokens
     * @throws IOException if Lucene's analyzer encounters any error
     */
    List<String> keepPunctuation(String inputString) throws IOException;

    /**
     * This is {@link InputAnalyzer#tokenize(String, boolean)} with removePunctuation set as false. So, this
     * method performs tokenization without removing punctuations.
     *
     * @param inputString The input string to be pre-processed with Lucene tokenizer
     * @return tokens
     * @throws IOException if Lucene's analyzer encounters any error
     */
    List<String> removePunctuation(String inputString) throws IOException;
}
