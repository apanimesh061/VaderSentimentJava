package com.vader.util;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * @author Animesh Pandey
 *         Created on 4/9/2016.
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

    public static String ALL_CAPS_REGEXP = "\\b[A-Z\\s]+\\b";

    public static ArrayList<String> PUNCTUATION_LIST = new ArrayList<>(Arrays.asList(PUNCTUATION_LIST_array));
    public static ArrayList<String> NEGATIVE_WORDS = new ArrayList<>(Arrays.asList(NEGATIVE_WORDS_array));

    private static Float BOOSTER_WORD_INCREMENT = 0.293f;
    private static Float DAMPENER_WORD_DECREMENT = -0.293f;

    public static Float ALL_CAPS_BOOSTER_SCORE = 0.733f;
    public static Float EXCLAMATION_MARK_SCORE_AMPLIFIER = 0.292f;
    public static Float QUESTION_MARK_SCORE_AMPLIFIER = 0.18f;
    public static Float N_SCALAR = -0.74f;
    public static Float EXCLAMATION_BOOST = 0.292f;
    public static Float QUESTION_BOOST_COUNT_3 = 0.292f;
    public static Float QUESTION_BOOST = 0.96f;


    public static HashMap<String, Float> BOOSTER_DICTIONARY = new HashMap<String, Float>() {{
        put("decidedly", Utils.BOOSTER_WORD_INCREMENT);
        put("uber", Utils.BOOSTER_WORD_INCREMENT);
        put("barely", Utils.DAMPENER_WORD_DECREMENT);
        put("particularly", Utils.BOOSTER_WORD_INCREMENT);
        put("enormously", Utils.BOOSTER_WORD_INCREMENT);
        put("less", Utils.DAMPENER_WORD_DECREMENT);
        put("absolutely", Utils.BOOSTER_WORD_INCREMENT);
        put("kinda", Utils.DAMPENER_WORD_DECREMENT);
        put("flipping", Utils.BOOSTER_WORD_INCREMENT);
        put("awfully", Utils.BOOSTER_WORD_INCREMENT);
        put("purely", Utils.BOOSTER_WORD_INCREMENT);
        put("majorly", Utils.BOOSTER_WORD_INCREMENT);
        put("substantially", Utils.BOOSTER_WORD_INCREMENT);
        put("partly", Utils.DAMPENER_WORD_DECREMENT);
        put("remarkably", Utils.BOOSTER_WORD_INCREMENT);
        put("really", Utils.BOOSTER_WORD_INCREMENT);
        put("sort of", Utils.DAMPENER_WORD_DECREMENT);
        put("little", Utils.DAMPENER_WORD_DECREMENT);
        put("fricking", Utils.BOOSTER_WORD_INCREMENT);
        put("sorta", Utils.DAMPENER_WORD_DECREMENT);
        put("amazingly", Utils.BOOSTER_WORD_INCREMENT);
        put("kind of", Utils.DAMPENER_WORD_DECREMENT);
        put("just enough", Utils.DAMPENER_WORD_DECREMENT);
        put("fucking", Utils.BOOSTER_WORD_INCREMENT);
        put("occasionally", Utils.DAMPENER_WORD_DECREMENT);
        put("somewhat", Utils.DAMPENER_WORD_DECREMENT);
        put("kindof", Utils.DAMPENER_WORD_DECREMENT);
        put("friggin", Utils.BOOSTER_WORD_INCREMENT);
        put("incredibly", Utils.BOOSTER_WORD_INCREMENT);
        put("totally", Utils.BOOSTER_WORD_INCREMENT);
        put("marginally", Utils.DAMPENER_WORD_DECREMENT);
        put("more", Utils.BOOSTER_WORD_INCREMENT);
        put("considerably", Utils.BOOSTER_WORD_INCREMENT);
        put("fabulously", Utils.BOOSTER_WORD_INCREMENT);
        put("sort of", Utils.DAMPENER_WORD_DECREMENT);
        put("hardly", Utils.DAMPENER_WORD_DECREMENT);
        put("very", Utils.BOOSTER_WORD_INCREMENT);
        put("sortof", Utils.DAMPENER_WORD_DECREMENT);
        put("kind-of", Utils.DAMPENER_WORD_DECREMENT);
        put("scarcely", Utils.DAMPENER_WORD_DECREMENT);
        put("thoroughly", Utils.BOOSTER_WORD_INCREMENT);
        put("quite", Utils.BOOSTER_WORD_INCREMENT);
        put("most", Utils.BOOSTER_WORD_INCREMENT);
        put("completely", Utils.BOOSTER_WORD_INCREMENT);
        put("frigging", Utils.BOOSTER_WORD_INCREMENT);
        put("intensely", Utils.BOOSTER_WORD_INCREMENT);
        put("utterly", Utils.BOOSTER_WORD_INCREMENT);
        put("highly", Utils.BOOSTER_WORD_INCREMENT);
        put("extremely", Utils.BOOSTER_WORD_INCREMENT);
        put("unbelievably", Utils.BOOSTER_WORD_INCREMENT);
        put("almost", Utils.DAMPENER_WORD_DECREMENT);
        put("especially", Utils.BOOSTER_WORD_INCREMENT);
        put("fully", Utils.BOOSTER_WORD_INCREMENT);
        put("frickin", Utils.BOOSTER_WORD_INCREMENT);
        put("tremendously", Utils.BOOSTER_WORD_INCREMENT);
        put("exceptionally", Utils.BOOSTER_WORD_INCREMENT);
        put("flippin", Utils.BOOSTER_WORD_INCREMENT);
        put("hella", Utils.BOOSTER_WORD_INCREMENT);
        put("so", Utils.BOOSTER_WORD_INCREMENT);
        put("greatly", Utils.BOOSTER_WORD_INCREMENT);
        put("hugely", Utils.BOOSTER_WORD_INCREMENT);
        put("deeply", Utils.BOOSTER_WORD_INCREMENT);
        put("unusually", Utils.BOOSTER_WORD_INCREMENT);
        put("entirely", Utils.BOOSTER_WORD_INCREMENT);
        put("slightly", Utils.DAMPENER_WORD_DECREMENT);
        put("effing", Utils.BOOSTER_WORD_INCREMENT);
    }};

    public static HashMap<String, Float> SENTIMENT_LADEN_IDIOMS = new HashMap<String, Float>() {{
        put("cut the mustard", 2f);
        put("bad ass", 1.5f);
        put("kiss of death", -1.5f);
        put("yeah right", -2f);
        put("the bomb", 3f);
        put("hand to mouth", -2f);
        put("the shit", 3f);
    }};

    private static HashMap<String, Float> getWordValenceDictionary(String filename) {
        URL lexFile = loader.getResource(filename);
        HashMap<String, Float> lexDictionary = new HashMap<>();
        if (lexFile != null) {
            try(BufferedReader br = new BufferedReader(new FileReader(lexFile.getFile()))) {
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

    public static HashMap<String, Float> WORD_VALENCE_DICTIONARY = getWordValenceDictionary("vader_sentiment_lexicon.txt");

}
