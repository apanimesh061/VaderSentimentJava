package com.vader.analyzer;

import com.vader.util.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Animesh Pandey
 *         Created on 4/10/2016.
 */
public class TextProperties {

    private String inputText;
    private ArrayList<String> wordsAndEmoticons;
    private ArrayList<String> wordsOnly;
    private boolean isCapDIff;

    private void setWordsAndEmoticons() throws IOException {
        setWordsOnly();

        ArrayList<String> wordsAndEmoticonsList = VaderLuceneAnalyzer.defaultSplit(inputText);
        for (int i = 0; i < wordsOnly.size(); i++)
            if (wordsOnly.get(i).length() <= 1)
                wordsOnly.remove(i);

        for (String currentWord : wordsOnly) {
            for (String currentPunc : Utils.PUNCTUATION_LIST) {
                String pWord = currentWord + currentPunc;
                Integer x1 = Collections.frequency(wordsAndEmoticonsList, pWord);
                while (x1 > 0) {
                    int index = wordsAndEmoticonsList.indexOf(pWord);
                    wordsAndEmoticonsList.remove(pWord);
                    wordsAndEmoticonsList.add(index, currentWord);
                    x1 = Collections.frequency(wordsAndEmoticonsList, pWord);
                }

                String wordP = currentPunc + currentWord;
                Integer x2 = Collections.frequency(wordsAndEmoticonsList, wordP);
                while (x2 > 0) {
                    int index = wordsAndEmoticonsList.indexOf(wordP);
                    wordsAndEmoticonsList.remove(wordP);
                    wordsAndEmoticonsList.add(index, currentWord);
                    x2 = Collections.frequency(wordsAndEmoticonsList, wordP);
                }
            }
        }
        this.wordsAndEmoticons = wordsAndEmoticonsList;
    }

    private void setWordsOnly() throws IOException {
        this.wordsOnly = VaderLuceneAnalyzer.removePunctuation(inputText);
    }

    private void setCapDIff(boolean capDIff) {
        isCapDIff = capDIff;
    }

    private boolean isAllCapDifferential() {
        int countAllCaps = 0;
        for (String s : wordsAndEmoticons)
            if (s.matches(Utils.ALL_CAPS_REGEXP))
                countAllCaps += 1;
        int capDifferential = wordsAndEmoticons.size() - countAllCaps;
        return (0 < capDifferential) && (0 < wordsAndEmoticons.size());
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
}
