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
     * Class loader to get the path to the resources folder.
     */
    private static final ClassLoader CLASS_LOADER = Utils.class.getClassLoader();

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
    private static Map<String, Float> BoosterDictionary = new HashMap<>();

    //CHECKSTYLE.OFF: ExecutableStatementCount
    //CHECKSTYLE.OFF: JavaNCSS
    static {
        BoosterDictionary.put("decidedly", Valence.DEFAULT_BOOSTING.getValue());
        BoosterDictionary.put("uber", Valence.DEFAULT_BOOSTING.getValue());
        BoosterDictionary.put("barely", Valence.DEFAULT_DAMPING.getValue());
        BoosterDictionary.put("particularly", Valence.DEFAULT_BOOSTING.getValue());
        BoosterDictionary.put("enormously", Valence.DEFAULT_BOOSTING.getValue());
        BoosterDictionary.put("less", Valence.DEFAULT_DAMPING.getValue());
        BoosterDictionary.put("absolutely", Valence.DEFAULT_BOOSTING.getValue());
        BoosterDictionary.put("kinda", Valence.DEFAULT_DAMPING.getValue());
        BoosterDictionary.put("flipping", Valence.DEFAULT_BOOSTING.getValue());
        BoosterDictionary.put("awfully", Valence.DEFAULT_BOOSTING.getValue());
        BoosterDictionary.put("purely", Valence.DEFAULT_BOOSTING.getValue());
        BoosterDictionary.put("majorly", Valence.DEFAULT_BOOSTING.getValue());
        BoosterDictionary.put("substantially", Valence.DEFAULT_BOOSTING.getValue());
        BoosterDictionary.put("partly", Valence.DEFAULT_DAMPING.getValue());
        BoosterDictionary.put("remarkably", Valence.DEFAULT_BOOSTING.getValue());
        BoosterDictionary.put("really", Valence.DEFAULT_BOOSTING.getValue());
        BoosterDictionary.put("sort of", Valence.DEFAULT_DAMPING.getValue());
        BoosterDictionary.put("little", Valence.DEFAULT_DAMPING.getValue());
        BoosterDictionary.put("fricking", Valence.DEFAULT_BOOSTING.getValue());
        BoosterDictionary.put("sorta", Valence.DEFAULT_DAMPING.getValue());
        BoosterDictionary.put("amazingly", Valence.DEFAULT_BOOSTING.getValue());
        BoosterDictionary.put("kind of", Valence.DEFAULT_DAMPING.getValue());
        BoosterDictionary.put("just enough", Valence.DEFAULT_DAMPING.getValue());
        BoosterDictionary.put("fucking", Valence.DEFAULT_BOOSTING.getValue());
        BoosterDictionary.put("occasionally", Valence.DEFAULT_DAMPING.getValue());
        BoosterDictionary.put("somewhat", Valence.DEFAULT_DAMPING.getValue());
        BoosterDictionary.put("kindof", Valence.DEFAULT_DAMPING.getValue());
        BoosterDictionary.put("friggin", Valence.DEFAULT_BOOSTING.getValue());
        BoosterDictionary.put("incredibly", Valence.DEFAULT_BOOSTING.getValue());
        BoosterDictionary.put("totally", Valence.DEFAULT_BOOSTING.getValue());
        BoosterDictionary.put("marginally", Valence.DEFAULT_DAMPING.getValue());
        BoosterDictionary.put("more", Valence.DEFAULT_BOOSTING.getValue());
        BoosterDictionary.put("considerably", Valence.DEFAULT_BOOSTING.getValue());
        BoosterDictionary.put("fabulously", Valence.DEFAULT_BOOSTING.getValue());
        BoosterDictionary.put("hardly", Valence.DEFAULT_DAMPING.getValue());
        BoosterDictionary.put("very", Valence.DEFAULT_BOOSTING.getValue());
        BoosterDictionary.put("sortof", Valence.DEFAULT_DAMPING.getValue());
        BoosterDictionary.put("kind-of", Valence.DEFAULT_DAMPING.getValue());
        BoosterDictionary.put("scarcely", Valence.DEFAULT_DAMPING.getValue());
        BoosterDictionary.put("thoroughly", Valence.DEFAULT_BOOSTING.getValue());
        BoosterDictionary.put("quite", Valence.DEFAULT_BOOSTING.getValue());
        BoosterDictionary.put("most", Valence.DEFAULT_BOOSTING.getValue());
        BoosterDictionary.put("completely", Valence.DEFAULT_BOOSTING.getValue());
        BoosterDictionary.put("frigging", Valence.DEFAULT_BOOSTING.getValue());
        BoosterDictionary.put("intensely", Valence.DEFAULT_BOOSTING.getValue());
        BoosterDictionary.put("utterly", Valence.DEFAULT_BOOSTING.getValue());
        BoosterDictionary.put("highly", Valence.DEFAULT_BOOSTING.getValue());
        BoosterDictionary.put("extremely", Valence.DEFAULT_BOOSTING.getValue());
        BoosterDictionary.put("unbelievably", Valence.DEFAULT_BOOSTING.getValue());
        BoosterDictionary.put("almost", Valence.DEFAULT_DAMPING.getValue());
        BoosterDictionary.put("especially", Valence.DEFAULT_BOOSTING.getValue());
        BoosterDictionary.put("fully", Valence.DEFAULT_BOOSTING.getValue());
        BoosterDictionary.put("frickin", Valence.DEFAULT_BOOSTING.getValue());
        BoosterDictionary.put("tremendously", Valence.DEFAULT_BOOSTING.getValue());
        BoosterDictionary.put("exceptionally", Valence.DEFAULT_BOOSTING.getValue());
        BoosterDictionary.put("flippin", Valence.DEFAULT_BOOSTING.getValue());
        BoosterDictionary.put("hella", Valence.DEFAULT_BOOSTING.getValue());
        BoosterDictionary.put("so", Valence.DEFAULT_BOOSTING.getValue());
        BoosterDictionary.put("greatly", Valence.DEFAULT_BOOSTING.getValue());
        BoosterDictionary.put("hugely", Valence.DEFAULT_BOOSTING.getValue());
        BoosterDictionary.put("deeply", Valence.DEFAULT_BOOSTING.getValue());
        BoosterDictionary.put("unusually", Valence.DEFAULT_BOOSTING.getValue());
        BoosterDictionary.put("entirely", Valence.DEFAULT_BOOSTING.getValue());
        BoosterDictionary.put("slightly", Valence.DEFAULT_DAMPING.getValue());
        BoosterDictionary.put("effing", Valence.DEFAULT_BOOSTING.getValue());
    }
    //CHECKSTYLE.ON: ExecutableStatementCount
    //CHECKSTYLE.ON: JavaNCSS

    /**
     * Idioms with their respective valencies.
     */
    private static Map<String, Float> SentimentLadenIdioms = new HashMap<>();

    //CHECKSTYLE.OFF: MagicNumber
    static {
        SentimentLadenIdioms.put("cut the mustard", 2f);
        SentimentLadenIdioms.put("bad ass", 1.5f);
        SentimentLadenIdioms.put("kiss of death", -1.5f);
        SentimentLadenIdioms.put("yeah right", -2f);
        SentimentLadenIdioms.put("the bomb", 3f);
        SentimentLadenIdioms.put("hand to mouth", -2f);
        SentimentLadenIdioms.put("the shit", 3f);
    }
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
        final InputStream lexFile = CLASS_LOADER.getResourceAsStream("vader_sentiment_lexicon.txt");
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

    public static Map<String, Float> getSentimentLadenIdioms() {
        return SentimentLadenIdioms;
    }

    public static Map<String, Float> getBoosterDictionary() {
        return BoosterDictionary;
    }

    public static Map<String, Float> getWordValenceDictionary() {
        return WordValenceDictionary;
    }
}
