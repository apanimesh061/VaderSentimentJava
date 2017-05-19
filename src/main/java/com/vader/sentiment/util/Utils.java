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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * This class contains the constants that are the used by the sentiment analyzer.
 * The constants are same as the ones used in the official python implementation
 *
 * @author Animesh Pandey
 *         Created on 4/9/2016.
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
    public static HashMap<String, Float> BOOSTER_DICTIONARY = new HashMap<>();

    static {
        BOOSTER_DICTIONARY.put("decidedly", Valence.DEFAULT_BOOSTING.getValue());
        BOOSTER_DICTIONARY.put("uber", Valence.DEFAULT_BOOSTING.getValue());
        BOOSTER_DICTIONARY.put("barely", Valence.DEFAULT_DAMPING.getValue());
        BOOSTER_DICTIONARY.put("particularly", Valence.DEFAULT_BOOSTING.getValue());
        BOOSTER_DICTIONARY.put("enormously", Valence.DEFAULT_BOOSTING.getValue());
        BOOSTER_DICTIONARY.put("less", Valence.DEFAULT_DAMPING.getValue());
        BOOSTER_DICTIONARY.put("absolutely", Valence.DEFAULT_BOOSTING.getValue());
        BOOSTER_DICTIONARY.put("kinda", Valence.DEFAULT_DAMPING.getValue());
        BOOSTER_DICTIONARY.put("flipping", Valence.DEFAULT_BOOSTING.getValue());
        BOOSTER_DICTIONARY.put("awfully", Valence.DEFAULT_BOOSTING.getValue());
        BOOSTER_DICTIONARY.put("purely", Valence.DEFAULT_BOOSTING.getValue());
        BOOSTER_DICTIONARY.put("majorly", Valence.DEFAULT_BOOSTING.getValue());
        BOOSTER_DICTIONARY.put("substantially", Valence.DEFAULT_BOOSTING.getValue());
        BOOSTER_DICTIONARY.put("partly", Valence.DEFAULT_DAMPING.getValue());
        BOOSTER_DICTIONARY.put("remarkably", Valence.DEFAULT_BOOSTING.getValue());
        BOOSTER_DICTIONARY.put("really", Valence.DEFAULT_BOOSTING.getValue());
        BOOSTER_DICTIONARY.put("sort of", Valence.DEFAULT_DAMPING.getValue());
        BOOSTER_DICTIONARY.put("little", Valence.DEFAULT_DAMPING.getValue());
        BOOSTER_DICTIONARY.put("fricking", Valence.DEFAULT_BOOSTING.getValue());
        BOOSTER_DICTIONARY.put("sorta", Valence.DEFAULT_DAMPING.getValue());
        BOOSTER_DICTIONARY.put("amazingly", Valence.DEFAULT_BOOSTING.getValue());
        BOOSTER_DICTIONARY.put("kind of", Valence.DEFAULT_DAMPING.getValue());
        BOOSTER_DICTIONARY.put("just enough", Valence.DEFAULT_DAMPING.getValue());
        BOOSTER_DICTIONARY.put("fucking", Valence.DEFAULT_BOOSTING.getValue());
        BOOSTER_DICTIONARY.put("occasionally", Valence.DEFAULT_DAMPING.getValue());
        BOOSTER_DICTIONARY.put("somewhat", Valence.DEFAULT_DAMPING.getValue());
        BOOSTER_DICTIONARY.put("kindof", Valence.DEFAULT_DAMPING.getValue());
        BOOSTER_DICTIONARY.put("friggin", Valence.DEFAULT_BOOSTING.getValue());
        BOOSTER_DICTIONARY.put("incredibly", Valence.DEFAULT_BOOSTING.getValue());
        BOOSTER_DICTIONARY.put("totally", Valence.DEFAULT_BOOSTING.getValue());
        BOOSTER_DICTIONARY.put("marginally", Valence.DEFAULT_DAMPING.getValue());
        BOOSTER_DICTIONARY.put("more", Valence.DEFAULT_BOOSTING.getValue());
        BOOSTER_DICTIONARY.put("considerably", Valence.DEFAULT_BOOSTING.getValue());
        BOOSTER_DICTIONARY.put("fabulously", Valence.DEFAULT_BOOSTING.getValue());
        BOOSTER_DICTIONARY.put("sort of", Valence.DEFAULT_DAMPING.getValue());
        BOOSTER_DICTIONARY.put("hardly", Valence.DEFAULT_DAMPING.getValue());
        BOOSTER_DICTIONARY.put("very", Valence.DEFAULT_BOOSTING.getValue());
        BOOSTER_DICTIONARY.put("sortof", Valence.DEFAULT_DAMPING.getValue());
        BOOSTER_DICTIONARY.put("kind-of", Valence.DEFAULT_DAMPING.getValue());
        BOOSTER_DICTIONARY.put("scarcely", Valence.DEFAULT_DAMPING.getValue());
        BOOSTER_DICTIONARY.put("thoroughly", Valence.DEFAULT_BOOSTING.getValue());
        BOOSTER_DICTIONARY.put("quite", Valence.DEFAULT_BOOSTING.getValue());
        BOOSTER_DICTIONARY.put("most", Valence.DEFAULT_BOOSTING.getValue());
        BOOSTER_DICTIONARY.put("completely", Valence.DEFAULT_BOOSTING.getValue());
        BOOSTER_DICTIONARY.put("frigging", Valence.DEFAULT_BOOSTING.getValue());
        BOOSTER_DICTIONARY.put("intensely", Valence.DEFAULT_BOOSTING.getValue());
        BOOSTER_DICTIONARY.put("utterly", Valence.DEFAULT_BOOSTING.getValue());
        BOOSTER_DICTIONARY.put("highly", Valence.DEFAULT_BOOSTING.getValue());
        BOOSTER_DICTIONARY.put("extremely", Valence.DEFAULT_BOOSTING.getValue());
        BOOSTER_DICTIONARY.put("unbelievably", Valence.DEFAULT_BOOSTING.getValue());
        BOOSTER_DICTIONARY.put("almost", Valence.DEFAULT_DAMPING.getValue());
        BOOSTER_DICTIONARY.put("especially", Valence.DEFAULT_BOOSTING.getValue());
        BOOSTER_DICTIONARY.put("fully", Valence.DEFAULT_BOOSTING.getValue());
        BOOSTER_DICTIONARY.put("frickin", Valence.DEFAULT_BOOSTING.getValue());
        BOOSTER_DICTIONARY.put("tremendously", Valence.DEFAULT_BOOSTING.getValue());
        BOOSTER_DICTIONARY.put("exceptionally", Valence.DEFAULT_BOOSTING.getValue());
        BOOSTER_DICTIONARY.put("flippin", Valence.DEFAULT_BOOSTING.getValue());
        BOOSTER_DICTIONARY.put("hella", Valence.DEFAULT_BOOSTING.getValue());
        BOOSTER_DICTIONARY.put("so", Valence.DEFAULT_BOOSTING.getValue());
        BOOSTER_DICTIONARY.put("greatly", Valence.DEFAULT_BOOSTING.getValue());
        BOOSTER_DICTIONARY.put("hugely", Valence.DEFAULT_BOOSTING.getValue());
        BOOSTER_DICTIONARY.put("deeply", Valence.DEFAULT_BOOSTING.getValue());
        BOOSTER_DICTIONARY.put("unusually", Valence.DEFAULT_BOOSTING.getValue());
        BOOSTER_DICTIONARY.put("entirely", Valence.DEFAULT_BOOSTING.getValue());
        BOOSTER_DICTIONARY.put("slightly", Valence.DEFAULT_DAMPING.getValue());
        BOOSTER_DICTIONARY.put("effing", Valence.DEFAULT_BOOSTING.getValue());
    }

    /**
     *
     */
    public static HashMap<String, Float> SENTIMENT_LADEN_IDIOMS = new HashMap<>();

    static {
        SENTIMENT_LADEN_IDIOMS.put("cut the mustard", 2f);
        SENTIMENT_LADEN_IDIOMS.put("bad ass", 1.5f);
        SENTIMENT_LADEN_IDIOMS.put("kiss of death", -1.5f);
        SENTIMENT_LADEN_IDIOMS.put("yeah right", -2f);
        SENTIMENT_LADEN_IDIOMS.put("the bomb", 3f);
        SENTIMENT_LADEN_IDIOMS.put("hand to mouth", -2f);
        SENTIMENT_LADEN_IDIOMS.put("the shit", 3f);
    }

    /**
     *
     */
    public static HashMap<String, Float> WORD_VALENCE_DICTIONARY =
            getWordValenceDictionary("vader_sentiment_lexicon.txt");

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
        if (token.toLowerCase().startsWith("http://")) {
            return false;
        }
        if (!token.matches(".*[a-zA-Z]+.*")) {
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
     * @param filename name of the lexicon file
     * @return map of lexicons with their corresponding valence
     */
    private static HashMap<String, Float> getWordValenceDictionary(String filename) {
        InputStream lexFile = CLASS_LOADER.getResourceAsStream(filename);
        HashMap<String, Float> lexDictionary = new HashMap<>();
        if (lexFile != null) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(lexFile))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] lexFileData = line.split("\\t");
                    String currentText = lexFileData[0];
                    Float currentTextValence = Float.parseFloat(lexFileData[1]);
                    lexDictionary.put(currentText, currentTextValence);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return lexDictionary;
    }

    /**
     * Private constructor for utility class.
     */
    private Utils() {

    }
}
