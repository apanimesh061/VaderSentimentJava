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

package com.vader.sentiment.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableMap;

/**
 * This class contains the constants that are the used by the sentiment analyzer.
 * The constants are same as the ones used in the official python implementation
 *
 * @author Animesh Pandey
 * @see <a href="http://www.nltk.org/_modules/nltk/sentiment/vader.html">NLTK Source</a>
 * @see <a href="https://github.com/cjhutto/vaderSentiment/blob/master/vaderSentiment/vaderSentiment.py">
 * vaderSentiment Python module</a>
 */
public final class Utils {
    /**
     * List of possible punctuation marks.
     */
    private static final String[] PUNCTUATION_LIST_ARRAY = {
        ".", "!", "?", ",", ";", ":", "-", "'", "\"", "!!", "!!!", "??", "???", "?!?", "!?!", "?!?!", "!?!?",
    };

    /**
     * Array of negating words.
     */
    private static final String[] NEGATIVE_WORDS_ARRAY = {
        "aint", "arent", "cannot", "cant", "couldnt", "darent", "didnt", "doesnt", "ain't",
        "aren't", "can't", "couldn't", "daren't", "didn't", "doesn't", "dont", "hadnt",
        "hasnt", "havent", "isnt", "mightnt", "mustnt", "neither", "don't", "hadn't",
        "hasn't", "haven't", "isn't", "mightn't", "mustn't", "neednt", "needn't", "never",
        "none", "nope", "nor", "not", "nothing", "nowhere", "oughtnt", "shant", "shouldnt",
        "uhuh", "wasnt", "werent", "oughtn't", "shan't", "shouldn't", "uh-uh", "wasn't", "weren't",
        "without", "wont", "wouldnt", "won't", "wouldn't", "rarely", "seldom", "despite",
    };

    /**
     * List of {@link Utils#PUNCTUATION_LIST_ARRAY}.
     */
    public static final ArrayList<String> PUNCTUATION_LIST = new ArrayList<>(Arrays.asList(PUNCTUATION_LIST_ARRAY));

    /**
     * Converting {@link Utils#NEGATIVE_WORDS_ARRAY} to a set.
     */
    public static final Set<String> NEGATIVE_WORDS = new HashSet<>(Arrays.asList(NEGATIVE_WORDS_ARRAY));

    /**
     * This dictionary holds a token and its corresponding boosting/dampening
     * factor for sentiment scoring.
     */
    private static Map<String, Float> BoosterDictionary = ImmutableMap.<String, Float>builder()
        .put("decidedly", Valence.DEFAULT_BOOSTING.getValue())
        .put("uber", Valence.DEFAULT_BOOSTING.getValue())
        .put("barely", Valence.DEFAULT_DAMPING.getValue())
        .put("particularly", Valence.DEFAULT_BOOSTING.getValue())
        .put("enormously", Valence.DEFAULT_BOOSTING.getValue())
        .put("less", Valence.DEFAULT_DAMPING.getValue())
        .put("absolutely", Valence.DEFAULT_BOOSTING.getValue())
        .put("kinda", Valence.DEFAULT_DAMPING.getValue())
        .put("flipping", Valence.DEFAULT_BOOSTING.getValue())
        .put("awfully", Valence.DEFAULT_BOOSTING.getValue())
        .put("purely", Valence.DEFAULT_BOOSTING.getValue())
        .put("majorly", Valence.DEFAULT_BOOSTING.getValue())
        .put("substantially", Valence.DEFAULT_BOOSTING.getValue())
        .put("partly", Valence.DEFAULT_DAMPING.getValue())
        .put("remarkably", Valence.DEFAULT_BOOSTING.getValue())
        .put("really", Valence.DEFAULT_BOOSTING.getValue())
        .put("sort of", Valence.DEFAULT_DAMPING.getValue())
        .put("little", Valence.DEFAULT_DAMPING.getValue())
        .put("fricking", Valence.DEFAULT_BOOSTING.getValue())
        .put("sorta", Valence.DEFAULT_DAMPING.getValue())
        .put("amazingly", Valence.DEFAULT_BOOSTING.getValue())
        .put("kind of", Valence.DEFAULT_DAMPING.getValue())
        .put("just enough", Valence.DEFAULT_DAMPING.getValue())
        .put("fucking", Valence.DEFAULT_BOOSTING.getValue())
        .put("occasionally", Valence.DEFAULT_DAMPING.getValue())
        .put("somewhat", Valence.DEFAULT_DAMPING.getValue())
        .put("kindof", Valence.DEFAULT_DAMPING.getValue())
        .put("friggin", Valence.DEFAULT_BOOSTING.getValue())
        .put("incredibly", Valence.DEFAULT_BOOSTING.getValue())
        .put("totally", Valence.DEFAULT_BOOSTING.getValue())
        .put("marginally", Valence.DEFAULT_DAMPING.getValue())
        .put("more", Valence.DEFAULT_BOOSTING.getValue())
        .put("considerably", Valence.DEFAULT_BOOSTING.getValue())
        .put("fabulously", Valence.DEFAULT_BOOSTING.getValue())
        .put("hardly", Valence.DEFAULT_DAMPING.getValue())
        .put("very", Valence.DEFAULT_BOOSTING.getValue())
        .put("sortof", Valence.DEFAULT_DAMPING.getValue())
        .put("kind-of", Valence.DEFAULT_DAMPING.getValue())
        .put("scarcely", Valence.DEFAULT_DAMPING.getValue())
        .put("thoroughly", Valence.DEFAULT_BOOSTING.getValue())
        .put("quite", Valence.DEFAULT_BOOSTING.getValue())
        .put("most", Valence.DEFAULT_BOOSTING.getValue())
        .put("completely", Valence.DEFAULT_BOOSTING.getValue())
        .put("frigging", Valence.DEFAULT_BOOSTING.getValue())
        .put("intensely", Valence.DEFAULT_BOOSTING.getValue())
        .put("utterly", Valence.DEFAULT_BOOSTING.getValue())
        .put("highly", Valence.DEFAULT_BOOSTING.getValue())
        .put("extremely", Valence.DEFAULT_BOOSTING.getValue())
        .put("unbelievably", Valence.DEFAULT_BOOSTING.getValue())
        .put("almost", Valence.DEFAULT_DAMPING.getValue())
        .put("especially", Valence.DEFAULT_BOOSTING.getValue())
        .put("fully", Valence.DEFAULT_BOOSTING.getValue())
        .put("frickin", Valence.DEFAULT_BOOSTING.getValue())
        .put("tremendously", Valence.DEFAULT_BOOSTING.getValue())
        .put("exceptionally", Valence.DEFAULT_BOOSTING.getValue())
        .put("flippin", Valence.DEFAULT_BOOSTING.getValue())
        .put("hella", Valence.DEFAULT_BOOSTING.getValue())
        .put("so", Valence.DEFAULT_BOOSTING.getValue())
        .put("greatly", Valence.DEFAULT_BOOSTING.getValue())
        .put("hugely", Valence.DEFAULT_BOOSTING.getValue())
        .put("deeply", Valence.DEFAULT_BOOSTING.getValue())
        .put("unusually", Valence.DEFAULT_BOOSTING.getValue())
        .put("entirely", Valence.DEFAULT_BOOSTING.getValue())
        .put("slightly", Valence.DEFAULT_DAMPING.getValue())
        .put("effing", Valence.DEFAULT_BOOSTING.getValue())
        .build();

    /**
     * Idioms with their respective valencies.
     */
    //CHECKSTYLE.OFF: MagicNumber
    private static Map<String, Float> SentimentLadenIdiomsValenceDictionary = ImmutableMap.<String, Float>builder()
        .put("cut the mustard", 2f)
        .put("bad ass", 1.5f)
        .put("kiss of death", -1.5f)
        .put("yeah right", -2f)
        .put("the bomb", 3f)
        .put("hand to mouth", -2f)
        .put("the shit", 3f)
        .build();
    //CHECKSTYLE.ON: MagicNumber

    /**
     * Tokens with their respective valencies.
     */
    private static Map<String, Float> WordValenceDictionary = readLexiconFile();

    /**
     * Private constructor for utility class.
     */
    private Utils() {

    }

    /**
     * This function returns false if the input token:
     * 1. is a URL starting with "http://" or "HTTP://"
     * 2. is a number as string
     * 3. has one character in lower case
     *
     * @param token input token
     * @return true iff none of the above conditions occur
     */
    public static boolean isUpper(String token) {
        if (token.toLowerCase().startsWith(Constants.URL_PREFIX)) {
            return false;
        }
        if (!token.matches(Constants.NON_NUMERIC_STRING_REGEX)) {
            return false;
        }
        for (int i = 0; i < token.length(); i++) {
            if (Character.isLowerCase(token.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * This function reads in a file that stores lexicon and their corresponding valence intensity.
     * Each pair of lexicon and its valence is then stored as key-value pairs in a HashMap.
     *
     * @return map of lexicons with their corresponding valence
     */
    private static Map<String, Float> readLexiconFile() {
        final InputStream lexFile = Utils.class.getClassLoader()
            .getResourceAsStream("vader_sentiment_lexicon.txt");
        final Map<String, Float> lexDictionary = new HashMap<>();
        if (lexFile != null) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(lexFile))) {
                String line;
                while ((line = br.readLine()) != null) {
                    final String[] lexFileData = line.split("\\t");
                    final String currentText = lexFileData[0];
                    final Float currentTextValence = Float.parseFloat(lexFileData[1]);
                    lexDictionary.put(currentText, currentTextValence);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return lexDictionary;
    }

    public static Map<String, Float> getSentimentLadenIdiomsValenceDictionary() {
        return SentimentLadenIdiomsValenceDictionary;
    }

    public static Map<String, Float> getBoosterDictionary() {
        return BoosterDictionary;
    }

    public static Map<String, Float> getWordValenceDictionary() {
        return WordValenceDictionary;
    }
}
