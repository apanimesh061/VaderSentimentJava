package com.vader.analyzer;

import com.vader.util.Utils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * The TextProperties class implements the pre-processing steps of the input string for sentiment analysis.
 * It utilizes the Lucene analyzers
 *
 * @author Animesh Pandey
 *         Created on 4/10/2016.
 * @see com.vader.analyzer.VaderLuceneAnalyzer
 */
public class TextProperties {
    private static Logger logger = Logger.getLogger(TextProperties.class);

    private String inputText;
    private ArrayList<String> wordsAndEmoticons;
    private ArrayList<String> wordsOnly;
    private boolean isCapDIff;

    /**
     * This method tokenizes the input string, preserving the punctuation marks using
     *
     * @throws IOException
     * @see com.vader.analyzer.VaderLuceneAnalyzer#defaultSplit(String)
     */
    private void setWordsAndEmoticons() throws IOException {
        setWordsOnly();

        ArrayList<String> wordsAndEmoticonsList = new VaderLuceneAnalyzer().defaultSplit(inputText);
        for (String currentWord : wordsOnly) {
            for (String currentPunc : Utils.PUNCTUATION_LIST) {
                String pWord = currentWord + currentPunc;
                Integer pWordCount = Collections.frequency(wordsAndEmoticonsList, pWord);
                while (pWordCount > 0) {
                    int index = wordsAndEmoticonsList.indexOf(pWord);
                    wordsAndEmoticonsList.remove(pWord);
                    wordsAndEmoticonsList.add(index, currentWord);
                    pWordCount = Collections.frequency(wordsAndEmoticonsList, pWord);
                }

                String wordP = currentPunc + currentWord;
                Integer wordPCount = Collections.frequency(wordsAndEmoticonsList, wordP);
                while (wordPCount > 0) {
                    int index = wordsAndEmoticonsList.indexOf(wordP);
                    wordsAndEmoticonsList.remove(wordP);
                    wordsAndEmoticonsList.add(index, currentWord);
                    wordPCount = Collections.frequency(wordsAndEmoticonsList, wordP);
                }
            }
        }
        this.wordsAndEmoticons = wordsAndEmoticonsList;
    }

    /**
     * This method tokenizes the input string, removing the special characters as well
     *
     * @throws IOException
     * @see com.vader.analyzer.VaderLuceneAnalyzer#removePunctuation(String)
     * This helps get the actual number of tokens in the input string
     */
    private void setWordsOnly() throws IOException {
        this.wordsOnly = new VaderLuceneAnalyzer().removePunctuation(inputText);
    }

    private void setCapDIff(boolean capDIff) {
        isCapDIff = capDIff;
    }

    /**
     * @return True iff the the tokens have yelling words i.e. all caps in the tokens
     * e.g. [GET, THE, HELL, OUT] returns false
     * [GET, the, HELL, OUT] returns true
     * [get, the, hell, out] returns false
     */
    private boolean isAllCapDifferential() {
        int countAllCaps = 0;
        for (String s : wordsAndEmoticons) {
            logger.debug(s + "\t" + Utils.isUpper(s));
            if (Utils.isUpper(s))
                countAllCaps++;
        }
        int capDifferential = wordsAndEmoticons.size() - countAllCaps;
        logger.debug(wordsAndEmoticons.size() + "\t" + capDifferential + "\t" + countAllCaps);
        return (0 < capDifferential) && (capDifferential < wordsAndEmoticons.size());
    }

    public ArrayList<String> getWordsAndEmoticons() {
        return wordsAndEmoticons;
    }

    public ArrayList<String> getWordsOnly() {
        return wordsOnly;
    }

    public boolean isCapDIff() {
        return isCapDIff;
    }

    public TextProperties(String inputText) throws IOException {
        this.inputText = inputText;
        setWordsAndEmoticons();
        setCapDIff(isAllCapDifferential());
    }

    public static void main(String[] args) throws IOException {
        TextProperties t = new TextProperties("Going to brave the rainy Walt Disney World day to find some fun.  Hopefully some dry fun. LOL");
        System.out.println(t.isCapDIff());
    }
}
