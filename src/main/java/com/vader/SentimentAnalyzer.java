package com.vader;

import com.vader.analyzer.TextProperties;
import com.vader.util.Utils;

import org.apache.commons.lang.StringUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

/**
 * @author Animesh Pandey
 *         Created on 4/11/2016.
 */
public class SentimentAnalyzer {

    private String inputString;
    private TextProperties inputStringProperties;
    private HashMap<String, Float> polarity;

    private SentimentAnalyzer(String inputString) throws IOException {
        this.inputString = inputString;
        setInputStringProperties();
    }

    private void setInputStringProperties() throws IOException {
        inputStringProperties = new TextProperties(inputString);
    }

    private HashMap<String, Float> getPolarity() {
        return polarity;
    }

    private void analyse() {
        polarity = getSentiment();
    }

    private float valenceModifier(String precedingWord, float currentValence) {
        float scalar = 0.0f;
        String precedingWordLower = precedingWord.toLowerCase();
        if (Utils.BOOSTER_DICTIONARY.containsKey(precedingWordLower)) {
            scalar = Utils.BOOSTER_DICTIONARY.get(precedingWordLower);
            if (currentValence < 0.0)
                scalar *= -1.0;
            if (precedingWord.matches(Utils.ALL_CAPS_REGEXP) && inputStringProperties.isCapDIff()) {
                scalar = (currentValence > 0.0) ? scalar + Utils.ALL_CAPS_BOOSTER_SCORE : scalar - Utils.ALL_CAPS_BOOSTER_SCORE;
            }
        }
        return scalar;
    }

    private HashMap<String, Float> getSentiment() {
        ArrayList<Float> sentiments = new ArrayList<>();
        ArrayList<String> wordsAndEmoticons = inputStringProperties.getWordsAndEmoticons();
        for (String item : wordsAndEmoticons) {
            float currentValence = 0.0f;
            int i = wordsAndEmoticons.indexOf(item);
            System.out.println("i = " + i + " " + item);
            if (i < wordsAndEmoticons.size() - 1 &&
                    item.toLowerCase().equals("kind") &&
                    wordsAndEmoticons.get(i + 1).toLowerCase().equals("of") ||
                    Utils.BOOSTER_DICTIONARY.containsKey(item.toLowerCase())) {
                sentiments.add(currentValence);
                continue;
            }

            String currentItemLower = item.toLowerCase();
            if (Utils.WORD_VALENCE_DICTIONARY.containsKey(currentItemLower)) {
                currentValence = Utils.WORD_VALENCE_DICTIONARY.get(currentItemLower);
                if (item.matches(Utils.ALL_CAPS_REGEXP) && inputStringProperties.isCapDIff()) {
                    currentValence = (currentValence > 0.0) ? currentValence + Utils.ALL_CAPS_BOOSTER_SCORE : currentValence - Utils.ALL_CAPS_BOOSTER_SCORE;
                }

                int startI = 0;
                float gramBasedValence = 0.0f;
                while (startI < 3) {
                    System.out.println(i + "\t" + startI);
                    if (i > startI && Utils.WORD_VALENCE_DICTIONARY.containsKey(wordsAndEmoticons.get(i - (startI + 1)).toLowerCase())) {
                        gramBasedValence = valenceModifier(wordsAndEmoticons.get(i - (startI + 1)), currentValence);
                        if (startI == 1 && gramBasedValence != 0.0f)
                            gramBasedValence += 0.95f;
                        if (startI == 2 && gramBasedValence != 0.0f)
                            gramBasedValence += 0.9f;
                        currentValence += gramBasedValence;

                        if (startI == 0) {
                            if (isNegative(new ArrayList<>(Collections.singletonList(wordsAndEmoticons.get(i - 1))))) {
                                currentValence *= Utils.N_SCALAR;
                            }
                        }
                        if (startI == 1) {
                            String wordAtDistanceTwoLeft = wordsAndEmoticons.get(i - 2);
                            String wordAtDistanceOneLeft = wordsAndEmoticons.get(i - 1);
                            if ((wordAtDistanceTwoLeft.equals("never")) &&
                                    (wordAtDistanceOneLeft.equals("so") || (wordAtDistanceOneLeft.equals("this")))) {
                                currentValence *= 1.5f;
                            }
                            else if (isNegative(new ArrayList<>(Collections.singletonList(wordsAndEmoticons.get(i - (startI + 1)))))) {
                                currentValence *= Utils.N_SCALAR;
                            }
                        }
                        if (startI == 2) {
                            String wordAtDistanceThreeLeft = wordsAndEmoticons.get(i - 3);
                            String wordAtDistanceTwoLeft = wordsAndEmoticons.get(i - 2);
                            String wordAtDistanceOneLeft = wordsAndEmoticons.get(i - 1);
                            if ((wordAtDistanceThreeLeft.equals("never")) &&
                                    (wordAtDistanceTwoLeft.equals("so") || wordAtDistanceTwoLeft.equals("this")) ||
                                    (wordAtDistanceOneLeft.equals("so") || wordAtDistanceOneLeft.equals("this"))) {
                                currentValence *= 1.25f;
                            }
                            else if (isNegative(new ArrayList<>(Collections.singletonList(wordsAndEmoticons.get(i - (startI + 1)))))) {
                                currentValence *= Utils.N_SCALAR;
                            }
                        }
                    }

                    if (startI == 2) {
                        System.out.println(i);
                        final String leftBiGramFromCurrent = String.format("%s %s", wordsAndEmoticons.get(i - 1), wordsAndEmoticons.get(i));
                        final String leftTriGramFromCurrent = String.format("%s %s %s", wordsAndEmoticons.get(i - 2), wordsAndEmoticons.get(i - 1), wordsAndEmoticons.get(i));
                        final String leftBiGramFromOnePrevious = String.format("%s %s", wordsAndEmoticons.get(i - 2), wordsAndEmoticons.get(i - 1));
                        final String leftTriGramFromOnePrevious = String.format("%s %s %s", wordsAndEmoticons.get(i - 3), wordsAndEmoticons.get(i - 2), wordsAndEmoticons.get(i - 1));
                        final String leftBiGramFromTwoPrevious = String.format("%s %s", wordsAndEmoticons.get(i - 3), wordsAndEmoticons.get(i - 2));

                        ArrayList<String> leftGramSequences = new ArrayList<String>() {{
                            add(leftBiGramFromCurrent);
                            add(leftTriGramFromCurrent);
                            add(leftBiGramFromOnePrevious);
                            add(leftTriGramFromOnePrevious);
                            add(leftBiGramFromTwoPrevious);
                        }};

                        System.out.println(leftGramSequences);

                        for (String leftGramSequence : leftGramSequences) {
                            if (Utils.SENTIMENT_LADEN_IDIOMS.containsKey(leftGramSequence)) {
                                currentValence = Utils.SENTIMENT_LADEN_IDIOMS.get(leftGramSequence);
                                break;
                            }
                        }

                        if (wordsAndEmoticons.size() - 1 > i) {
                            final String rightBiGramFromCurrent = String.format("%s %s", wordsAndEmoticons.get(i), wordsAndEmoticons.get(i + 1));
                            if (Utils.SENTIMENT_LADEN_IDIOMS.containsKey(rightBiGramFromCurrent))
                                currentValence = Utils.SENTIMENT_LADEN_IDIOMS.get(rightBiGramFromCurrent);
                        }
                        if (wordsAndEmoticons.size() - 1 > i + 1) {
                            final String rightTriGramFromCurrent = String.format("%s %s %s", wordsAndEmoticons.get(i), wordsAndEmoticons.get(i + 1), wordsAndEmoticons.get(i + 2));
                            if (Utils.SENTIMENT_LADEN_IDIOMS.containsKey(rightTriGramFromCurrent))
                                currentValence = Utils.SENTIMENT_LADEN_IDIOMS.get(rightTriGramFromCurrent);
                        }

                        if (Utils.SENTIMENT_LADEN_IDIOMS.containsKey(leftBiGramFromOnePrevious))
                            currentValence += Utils.BOOSTER_DICTIONARY.get(leftBiGramFromCurrent);
                        if (Utils.SENTIMENT_LADEN_IDIOMS.containsKey(leftTriGramFromCurrent))
                            currentValence += Utils.BOOSTER_DICTIONARY.get(leftTriGramFromCurrent);
                    }
                    startI++;
                }

                if (i > 1 &&
                        !Utils.WORD_VALENCE_DICTIONARY.containsKey(wordsAndEmoticons.get(i - 1).toLowerCase()) &&
                        wordsAndEmoticons.get(i - 1).toLowerCase().equals("least")) {
                    if (!wordsAndEmoticons.get(i - 2).toLowerCase().equals("at") &&
                            !wordsAndEmoticons.get(i - 2).toLowerCase().equals("very"))
                        currentValence *= Utils.N_SCALAR;
                } else if (i > 0 &&
                        !Utils.WORD_VALENCE_DICTIONARY.containsKey(wordsAndEmoticons.get(i - 1).toLowerCase()) &&
                        wordsAndEmoticons.get(i - 1).equals("least")) {
                    currentValence *= Utils.N_SCALAR;
                }
            }
            sentiments.add(currentValence);
        }

        sentiments = checkConjuntionBut(wordsAndEmoticons, sentiments);

        return polarityScores(sentiments);
    }

    private ArrayList<Float> siftSentimentScores(ArrayList<Float> currentSentimentState) {
        float positiveSentimentScore = 0.0f;
        float negativeSentimentScore = 0.0f;
        int neutralSentimentCount = 0;
        for (Float valence : currentSentimentState) {
            if (valence > 0.0f)
                positiveSentimentScore = positiveSentimentScore + valence + 1.0f;
            if (valence < 0.0f)
                negativeSentimentScore = negativeSentimentScore + valence - 1.0f;
            if (valence == 0)
                neutralSentimentCount += 1;
        }
        return new ArrayList<>(Arrays.asList(
                positiveSentimentScore, negativeSentimentScore, (float) neutralSentimentCount));
    }

    private HashMap<String, Float> polarityScores(ArrayList<Float> currentSentimentState) {

        if (!currentSentimentState.isEmpty()) {
            float totalValence = 0.0f;
            for (Float valence : currentSentimentState)
                totalValence += valence;

            float punctuationAmplifier = boostByPunctuation();
            if (totalValence > 0.0f)
                totalValence += boostByPunctuation();
            else if (totalValence < 0.0f)
                totalValence -= boostByPunctuation();

            final float compoundPolarity = normalizeScore(totalValence);

            ArrayList<Float> siftedScores = siftSentimentScores(currentSentimentState);
            float positiveSentimentScore = siftedScores.get(0);
            float negativeSentimentScore = siftedScores.get(1);
            int neutralSentimentCount = Math.round(siftedScores.get(2));

            if (positiveSentimentScore > Math.abs(negativeSentimentScore))
                positiveSentimentScore += punctuationAmplifier;
            else if (positiveSentimentScore < Math.abs(negativeSentimentScore))
                negativeSentimentScore -= punctuationAmplifier;

            float normalizationFactor = positiveSentimentScore + Math.abs(negativeSentimentScore)
                    + neutralSentimentCount;

            final float positivePolarity = Math.abs(positiveSentimentScore / normalizationFactor);
            final float negativePolarity = Math.abs(negativeSentimentScore / normalizationFactor);
            final float neutralPolarity = Math.abs(neutralSentimentCount / normalizationFactor);

            return new HashMap<String, Float>(){{
                put("compound", compoundPolarity);
                put("positive", positivePolarity);
                put("negative", negativePolarity);
                put("neutral", neutralPolarity);
            }};

        } else {
            return new HashMap<String, Float>(){{
                put("compound", 0.0f);
                put("positive", 0.0f);
                put("negative", 0.0f);
                put("neutral", 0.0f);
            }};
        }
    }

    private float boostByPunctuation() {
        return boostByExclamation() + boostByQuestionMark();
    }

    private float boostByExclamation() {
        int exclamationCount = StringUtils.countMatches(inputString, "!");
        if (exclamationCount > 4)
            exclamationCount = 4;
        return exclamationCount * Utils.EXCLAMATION_BOOST;
    }

    private float boostByQuestionMark() {
        int questionMarkCount = StringUtils.countMatches(inputString, "?");
        float questionMarkAmplifier = 0.0f;
        if (questionMarkCount > 1) {
            if (questionMarkCount <= 3)
                questionMarkAmplifier = questionMarkCount * Utils.QUESTION_BOOST_COUNT_3;
            else
                questionMarkAmplifier = Utils.QUESTION_BOOST;
        }
        return questionMarkAmplifier;
    }

    private ArrayList<Float> checkConjuntionBut(ArrayList<String> inputTokens, ArrayList<Float> currentSentimentState) {
        int index = inputTokens.indexOf("but");
        if (index == -1)
            index = inputTokens.indexOf("BUT");
        for (Float valence : currentSentimentState) {
            int currentValenceIndex = currentSentimentState.indexOf(valence);
            if (currentValenceIndex < index) {
                currentSentimentState.remove(currentValenceIndex);
                currentSentimentState.add(currentValenceIndex, valence * 0.5f);
            } else if (currentValenceIndex > index) {
                currentSentimentState.remove(currentValenceIndex);
                currentSentimentState.add(currentValenceIndex, valence * 1.5f);
            }
        }
        return currentSentimentState;
    }

    private boolean hasAtLeast(ArrayList<String> tokenList) {
        if (tokenList.contains("least")) {
            int index = tokenList.indexOf("least");
            if (index > 0 && tokenList.get(index - 1).equals("at")) {
                return true;
            }
        }
        return false;
    }

    private boolean hasContraction(ArrayList<String> tokenList) {
        for (String s : tokenList) {
            if (s.endsWith("n't")) {
                return true;
            }
        }
        return false;
    }

    private boolean hasNegativeWord(ArrayList<String> tokenList, ArrayList<String> newNegWords) {
        for (String newNegWord : newNegWords) {
            if (tokenList.contains(newNegWord)) {
                return true;
            }
        }
        return false;
    }

    private boolean isNegative(ArrayList<String> tokenList, ArrayList<String> newNegWords, boolean checkContractions) {
        newNegWords.addAll(Utils.NEGATIVE_WORDS);
        if (checkContractions)
            return hasNegativeWord(tokenList, newNegWords) || hasAtLeast(tokenList);
        else
            return hasNegativeWord(tokenList, newNegWords) || hasAtLeast(tokenList) || hasContraction(tokenList);
    }

    private boolean isNegative(ArrayList<String> tokenList, ArrayList<String> newNegWords) {
        newNegWords.addAll(Utils.NEGATIVE_WORDS);
        return hasNegativeWord(tokenList, newNegWords) || hasAtLeast(tokenList) || hasContraction(tokenList);
    }

    private boolean isNegative(ArrayList<String> tokenList) {
        return hasNegativeWord(tokenList, Utils.NEGATIVE_WORDS) || hasAtLeast(tokenList) || hasContraction(tokenList);
    }

    private Float normalizeScore(Float score, Float alpha) {
        double normalizedScore = score / Math.sqrt((score.doubleValue() * score.doubleValue()) + alpha.doubleValue());
        return (float) normalizedScore;
    }

    private Float normalizeScore(Float score) {
        double normalizedScore = score / Math.sqrt((score.doubleValue() * score.doubleValue()) + 15.0);
        return (float) normalizedScore;
    }

    public static void main(String[] args) throws IOException {
        ArrayList<String> sentences = new ArrayList<String>() {{
            add("VADER is smart, handsome, and funny.");
            add("VADER is smart, handsome, and funny!");
            add("VADER is very smart, handsome, and funny.");
            add("VADER is VERY SMART, handsome, and FUNNY.");
            add("VADER is VERY SMART, handsome, and FUNNY!!!");
            add("VADER is VERY SMART, really handsome, and INCREDIBLY FUNNY!!!");
            add("The book was good.");
            add("The book was kind of good.");
            add("The plot was good, but the characters are uncompelling and the dialog is not great.");
            add("A really bad, horrible book.");
            add("At least it isn't a horrible book.");
            add(":) and :D");
            add("");
            add("Today sux");
            add("Today sux!");
            add("Today SUX!");
            add("Today kinda sux! But I'll get by, lol");
        }};

        for (String sentence : sentences) {
            System.out.println(sentence);
            SentimentAnalyzer sentimentAnalyzer = new SentimentAnalyzer(sentence);
            sentimentAnalyzer.analyse();
            System.out.println(sentence + "\t\t" + sentimentAnalyzer.getPolarity());
        }
    }

}
