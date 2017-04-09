package com.vader.sentiment.util;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

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
public class Utils {
    private static final ClassLoader loader = Utils.class.getClassLoader();

    private static final String[] PUNCTUATION_LIST_array = {
            ".", "!", "?", ",", ";", ":", "-", "'", "\"", "!!", "!!!", "??", "???", "?!?", "!?!", "?!?!", "!?!?"
    };

    private static final String[] NEGATIVE_WORDS_array = {"aint", "arent", "cannot", "cant", "couldnt", "darent",
            "didnt", "doesnt", "ain't", "aren't", "can't", "couldn't", "daren't", "didn't", "doesn't",
            "dont", "hadnt", "hasnt", "havent", "isnt", "mightnt", "mustnt", "neither",
            "don't", "hadn't", "hasn't", "haven't", "isn't", "mightn't", "mustn't",
            "neednt", "needn't", "never", "none", "nope", "nor", "not", "nothing", "nowhere",
            "oughtnt", "shant", "shouldnt", "uhuh", "wasnt", "werent",
            "oughtn't", "shan't", "shouldn't", "uh-uh", "wasn't", "weren't",
            "without", "wont", "wouldnt", "won't", "wouldn't", "rarely", "seldom", "despite"
    };

    public static ArrayList<String> PUNCTUATION_LIST = new ArrayList<>(Arrays.asList(PUNCTUATION_LIST_array));
    public static ArrayList<String> NEGATIVE_WORDS = new ArrayList<>(Arrays.asList(NEGATIVE_WORDS_array));

    public static float BOOSTER_WORD_INCREMENT = 0.293f;
    public static float DAMPENER_WORD_DECREMENT = -0.293f;

    public static float ALL_CAPS_BOOSTER_SCORE = 0.733f;
    public static float N_SCALAR = -0.74f;
    public static float EXCLAMATION_BOOST = 0.292f;
    public static float QUESTION_BOOST_COUNT_3 = 0.18f;
    public static float QUESTION_BOOST = 0.96f;

    public static HashMap<String, Float> BOOSTER_DICTIONARY = new HashMap<>();

    static {
        BOOSTER_DICTIONARY.put("decidedly", BOOSTER_WORD_INCREMENT);
        BOOSTER_DICTIONARY.put("uber", BOOSTER_WORD_INCREMENT);
        BOOSTER_DICTIONARY.put("barely", DAMPENER_WORD_DECREMENT);
        BOOSTER_DICTIONARY.put("particularly", BOOSTER_WORD_INCREMENT);
        BOOSTER_DICTIONARY.put("enormously", BOOSTER_WORD_INCREMENT);
        BOOSTER_DICTIONARY.put("less", DAMPENER_WORD_DECREMENT);
        BOOSTER_DICTIONARY.put("absolutely", BOOSTER_WORD_INCREMENT);
        BOOSTER_DICTIONARY.put("kinda", DAMPENER_WORD_DECREMENT);
        BOOSTER_DICTIONARY.put("flipping", BOOSTER_WORD_INCREMENT);
        BOOSTER_DICTIONARY.put("awfully", BOOSTER_WORD_INCREMENT);
        BOOSTER_DICTIONARY.put("purely", BOOSTER_WORD_INCREMENT);
        BOOSTER_DICTIONARY.put("majorly", BOOSTER_WORD_INCREMENT);
        BOOSTER_DICTIONARY.put("substantially", BOOSTER_WORD_INCREMENT);
        BOOSTER_DICTIONARY.put("partly", DAMPENER_WORD_DECREMENT);
        BOOSTER_DICTIONARY.put("remarkably", BOOSTER_WORD_INCREMENT);
        BOOSTER_DICTIONARY.put("really", BOOSTER_WORD_INCREMENT);
        BOOSTER_DICTIONARY.put("sort of", DAMPENER_WORD_DECREMENT);
        BOOSTER_DICTIONARY.put("little", DAMPENER_WORD_DECREMENT);
        BOOSTER_DICTIONARY.put("fricking", BOOSTER_WORD_INCREMENT);
        BOOSTER_DICTIONARY.put("sorta", DAMPENER_WORD_DECREMENT);
        BOOSTER_DICTIONARY.put("amazingly", BOOSTER_WORD_INCREMENT);
        BOOSTER_DICTIONARY.put("kind of", DAMPENER_WORD_DECREMENT);
        BOOSTER_DICTIONARY.put("just enough", DAMPENER_WORD_DECREMENT);
        BOOSTER_DICTIONARY.put("fucking", BOOSTER_WORD_INCREMENT);
        BOOSTER_DICTIONARY.put("occasionally", DAMPENER_WORD_DECREMENT);
        BOOSTER_DICTIONARY.put("somewhat", DAMPENER_WORD_DECREMENT);
        BOOSTER_DICTIONARY.put("kindof", DAMPENER_WORD_DECREMENT);
        BOOSTER_DICTIONARY.put("friggin", BOOSTER_WORD_INCREMENT);
        BOOSTER_DICTIONARY.put("incredibly", BOOSTER_WORD_INCREMENT);
        BOOSTER_DICTIONARY.put("totally", BOOSTER_WORD_INCREMENT);
        BOOSTER_DICTIONARY.put("marginally", DAMPENER_WORD_DECREMENT);
        BOOSTER_DICTIONARY.put("more", BOOSTER_WORD_INCREMENT);
        BOOSTER_DICTIONARY.put("considerably", BOOSTER_WORD_INCREMENT);
        BOOSTER_DICTIONARY.put("fabulously", BOOSTER_WORD_INCREMENT);
        BOOSTER_DICTIONARY.put("sort of", DAMPENER_WORD_DECREMENT);
        BOOSTER_DICTIONARY.put("hardly", DAMPENER_WORD_DECREMENT);
        BOOSTER_DICTIONARY.put("very", BOOSTER_WORD_INCREMENT);
        BOOSTER_DICTIONARY.put("sortof", DAMPENER_WORD_DECREMENT);
        BOOSTER_DICTIONARY.put("kind-of", DAMPENER_WORD_DECREMENT);
        BOOSTER_DICTIONARY.put("scarcely", DAMPENER_WORD_DECREMENT);
        BOOSTER_DICTIONARY.put("thoroughly", BOOSTER_WORD_INCREMENT);
        BOOSTER_DICTIONARY.put("quite", BOOSTER_WORD_INCREMENT);
        BOOSTER_DICTIONARY.put("most", BOOSTER_WORD_INCREMENT);
        BOOSTER_DICTIONARY.put("completely", BOOSTER_WORD_INCREMENT);
        BOOSTER_DICTIONARY.put("frigging", BOOSTER_WORD_INCREMENT);
        BOOSTER_DICTIONARY.put("intensely", BOOSTER_WORD_INCREMENT);
        BOOSTER_DICTIONARY.put("utterly", BOOSTER_WORD_INCREMENT);
        BOOSTER_DICTIONARY.put("highly", BOOSTER_WORD_INCREMENT);
        BOOSTER_DICTIONARY.put("extremely", BOOSTER_WORD_INCREMENT);
        BOOSTER_DICTIONARY.put("unbelievably", BOOSTER_WORD_INCREMENT);
        BOOSTER_DICTIONARY.put("almost", DAMPENER_WORD_DECREMENT);
        BOOSTER_DICTIONARY.put("especially", BOOSTER_WORD_INCREMENT);
        BOOSTER_DICTIONARY.put("fully", BOOSTER_WORD_INCREMENT);
        BOOSTER_DICTIONARY.put("frickin", BOOSTER_WORD_INCREMENT);
        BOOSTER_DICTIONARY.put("tremendously", BOOSTER_WORD_INCREMENT);
        BOOSTER_DICTIONARY.put("exceptionally", BOOSTER_WORD_INCREMENT);
        BOOSTER_DICTIONARY.put("flippin", BOOSTER_WORD_INCREMENT);
        BOOSTER_DICTIONARY.put("hella", BOOSTER_WORD_INCREMENT);
        BOOSTER_DICTIONARY.put("so", BOOSTER_WORD_INCREMENT);
        BOOSTER_DICTIONARY.put("greatly", BOOSTER_WORD_INCREMENT);
        BOOSTER_DICTIONARY.put("hugely", BOOSTER_WORD_INCREMENT);
        BOOSTER_DICTIONARY.put("deeply", BOOSTER_WORD_INCREMENT);
        BOOSTER_DICTIONARY.put("unusually", BOOSTER_WORD_INCREMENT);
        BOOSTER_DICTIONARY.put("entirely", BOOSTER_WORD_INCREMENT);
        BOOSTER_DICTIONARY.put("slightly", DAMPENER_WORD_DECREMENT);
        BOOSTER_DICTIONARY.put("effing", BOOSTER_WORD_INCREMENT);
    }

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

    public static HashMap<String, Float> WORD_VALENCE_DICTIONARY = getWordValenceDictionary("vader_sentiment_lexicon.txt");

    public static boolean isUpper(String token) {
        if (token.toLowerCase().startsWith("http://"))
            return false;
        if (!token.matches(".*[a-zA-Z]+.*"))
            return false;
        for (int i = 0; i < token.length(); i++) {
            if (Character.isLowerCase(token.charAt(i)))
                return false;
        }
        return true;
    }

    private static HashMap<String, Float> getWordValenceDictionary(String filename) {
        InputStream lexFile = loader.getResourceAsStream(filename);
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
}
