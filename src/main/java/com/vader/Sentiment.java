package com.vader;

import com.vader.analyzer.VaderAnalyzer;
import com.vader.util.Utils;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * @author Animesh Pandey
 *         Created on 4/9/2016.
 */
public class Sentiment {
    private HashMap<String, Float> polarity;
    private boolean isCapDiff;

    private boolean isCapDiff() {
        return isCapDiff;
    }

    private void setCapDiff(boolean capDiff) {
        isCapDiff = capDiff;
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

    private boolean isAllCapDifferential(ArrayList<String> tokenList) {
        int countAllCaps = 0;
        for (String s : tokenList)
            if (s.matches(Utils.ALL_CAPS_REGEXP))
                countAllCaps += 1;
        int capDifferential = tokenList.size() - countAllCaps;
        return (0 < capDifferential) && (0 < tokenList.size());
    }

    private float valenceModifier(String precedingWord, float currentValence) {
        float scalar = 0.0f;
        String precedingWordLower = precedingWord.toLowerCase();
        if (Utils.BOOSTER_DICTIONARY.containsKey(precedingWordLower)) {
            scalar = Utils.BOOSTER_DICTIONARY.get(precedingWordLower);
            if (currentValence < 0.0)
                scalar *= -1.0;
            if (precedingWord.matches(Utils.ALL_CAPS_REGEXP) && isCapDiff()) {
                scalar = (currentValence > 0.0) ? scalar + Utils.ALL_CAPS_BOOSTER_SCORE : scalar - Utils.ALL_CAPS_BOOSTER_SCORE;
            }
        }
        return scalar;
    }

    private HashMap<String, Float> getSentiment(String text) throws IOException {
        ArrayList<String> wordsAndEmoticons = VaderAnalyzer.DefaultSplit(text);
        ArrayList<String> wordsOnly = VaderAnalyzer.RemovePunctuation(text);

        for (int i = 0; i < wordsOnly.size(); i++)
            if (wordsOnly.get(i).length() <= 1)
                wordsOnly.remove(i);

        for (String currentWord : wordsOnly) {
            for (String currentPunc : Utils.PUNCTUATION_LIST) {
                String pWord = currentWord + currentPunc;
                Integer x1 = Collections.frequency(wordsAndEmoticons, pWord);
                while (x1 > 0) {
                    int index = wordsAndEmoticons.indexOf(pWord);
                    wordsAndEmoticons.remove(pWord);
                    wordsAndEmoticons.add(index, currentWord);
                    x1 = Collections.frequency(wordsAndEmoticons, pWord);
                }

                String wordP = currentPunc + currentWord;
                Integer x2 = Collections.frequency(wordsAndEmoticons, wordP);
                while (x2 > 0) {
                    int index = wordsAndEmoticons.indexOf(wordP);
                    wordsAndEmoticons.remove(wordP);
                    wordsAndEmoticons.add(index, currentWord);
                    x2 = Collections.frequency(wordsAndEmoticons, wordP);
                }
            }
        }

        for (String word : wordsAndEmoticons)
            if (word.length() <= 1)
                wordsAndEmoticons.remove(word);

        setCapDiff(isAllCapDifferential(wordsAndEmoticons));

        ArrayList<Float> sentiments = new ArrayList<>();
        for (String wordsAndEmoticon : wordsAndEmoticons) {
            float currentValence = 0.0f;
            int index = wordsAndEmoticons.indexOf(wordsAndEmoticon);
            if (((index < (wordsAndEmoticons.size() - 1) && wordsAndEmoticon.toLowerCase().equals("kind")) && (wordsAndEmoticons.get(index + 1).toLowerCase().equals("of"))) && (Utils.BOOSTER_DICTIONARY.containsKey(wordsAndEmoticon.toLowerCase()))) {
                sentiments.add(currentValence);
                continue;
            }
            String wordsAndEmoticonLower = wordsAndEmoticon.toLowerCase();
            if (Utils.WORD_VALENCE_DICTIONARY.containsKey(wordsAndEmoticonLower)) {

                currentValence = Utils.WORD_VALENCE_DICTIONARY.get(wordsAndEmoticonLower);
                if (wordsAndEmoticon.matches(Utils.ALL_CAPS_REGEXP) && isCapDiff()) {
                    currentValence = (currentValence > 0.0) ? currentValence + Utils.ALL_CAPS_BOOSTER_SCORE : currentValence - Utils.ALL_CAPS_BOOSTER_SCORE;
                }

                float n_scalar = -0.74f;
                if (index > 0 && Utils.WORD_VALENCE_DICTIONARY.containsKey(wordsAndEmoticons.get(index - 1).toLowerCase())) {
                    float s1 = valenceModifier(wordsAndEmoticons.get(index - 1), currentValence);
                    currentValence += s1;
                    if (isNegative(new ArrayList<>(Collections.singletonList(wordsAndEmoticons.get(index - 1))))) {
                        currentValence *= n_scalar;
                    }
                }

                if (index > 1 && Utils.WORD_VALENCE_DICTIONARY.containsKey(wordsAndEmoticons.get(index - 2).toLowerCase())) {
                    float s2 = valenceModifier(wordsAndEmoticons.get(index - 2), currentValence);
                    if (s2 != 0.0)
                        s2 *= 0.9511f;
                    currentValence += s2;
                    if (wordsAndEmoticons.get(index - 2).equals("never") && (wordsAndEmoticons.get(index - 1).equals("so") || wordsAndEmoticons.get(index - 1).equals("this"))) {
                        currentValence *= 1.5f;
                    } else if (isNegative(new ArrayList<>(Collections.singletonList(wordsAndEmoticons.get(index - 2))))) {
                        currentValence *= n_scalar;
                    }
                }

                if (index > 2 && Utils.WORD_VALENCE_DICTIONARY.containsKey(wordsAndEmoticons.get(index - 3).toLowerCase())) {
                    float s3 = valenceModifier(wordsAndEmoticons.get(index - 3), currentValence);
                    if (s3 != 0.0)
                        s3 *= 0.9f;
                    currentValence += s3;
                    if (wordsAndEmoticons.get(index - 3).equals("never") &&
                            (wordsAndEmoticons.get(index - 2).equals("so") || wordsAndEmoticons.get(index - 2).equals("this") ||
                                    wordsAndEmoticons.get(index - 1).equals("so") || wordsAndEmoticons.get(index - 1).equals("this"))) {
                        currentValence *= 1.25f;
                    } else if (isNegative(new ArrayList<>(Collections.singletonList(wordsAndEmoticons.get(index - 3))))) {
                        currentValence *= n_scalar;
                    }

                    final String leftBiGramFromCurrent = String.format("%s %s", wordsAndEmoticons.get(index - 1), wordsAndEmoticon).toLowerCase();
                    final String leftTriGramFromCurrent = String.format("%s %s %s", wordsAndEmoticons.get(index - 2), wordsAndEmoticons.get(index - 1), wordsAndEmoticon).toLowerCase();
                    final String leftBiGramFromOnePrevious = String.format("%s %s", wordsAndEmoticons.get(index - 2), wordsAndEmoticons.get(index - 1)).toLowerCase();
                    final String leftTriGramFromOnePrevious = String.format("%s %s %s", wordsAndEmoticons.get(index - 3), wordsAndEmoticons.get(index - 2), wordsAndEmoticons.get(index - 1)).toLowerCase();
                    final String leftBiGramFromTwoPrevious = String.format("%s %s", wordsAndEmoticons.get(index - 3), wordsAndEmoticons.get(index - 2)).toLowerCase();

                    ArrayList<String> leftGramSequences = new ArrayList<String>() {{
                        add(leftBiGramFromCurrent);
                        add(leftTriGramFromCurrent);
                        add(leftBiGramFromOnePrevious);
                        add(leftTriGramFromOnePrevious);
                        add(leftBiGramFromTwoPrevious);
                    }};

                    for (String leftGramSequence : leftGramSequences) {
                        if (Utils.SENTIMENT_LADEN_IDIOMS.containsKey(leftGramSequence)) {
                            currentValence = Utils.SENTIMENT_LADEN_IDIOMS.get(leftGramSequence);
                            break;
                        }
                    }

                    if (wordsAndEmoticons.size() - 1 > index) {
                        final String rightBiGramFromCurrent = String.format("%s %s", wordsAndEmoticon, wordsAndEmoticons.get(index + 1)).toLowerCase();
                        if (Utils.SENTIMENT_LADEN_IDIOMS.containsKey(rightBiGramFromCurrent))
                            currentValence = Utils.SENTIMENT_LADEN_IDIOMS.get(rightBiGramFromCurrent);
                    }
                    if (wordsAndEmoticons.size() - 1 > index + 1) {
                        final String rightTriGramFromCurrent = String.format("%s %s %s", wordsAndEmoticon, wordsAndEmoticons.get(index + 1), wordsAndEmoticons.get(index + 2)).toLowerCase();
                        if (Utils.SENTIMENT_LADEN_IDIOMS.containsKey(rightTriGramFromCurrent))
                            currentValence = Utils.SENTIMENT_LADEN_IDIOMS.get(rightTriGramFromCurrent);
                    }

                    if (Utils.SENTIMENT_LADEN_IDIOMS.containsKey(leftBiGramFromOnePrevious))
                        currentValence += Utils.BOOSTER_DICTIONARY.get(leftBiGramFromCurrent);
                    if (Utils.SENTIMENT_LADEN_IDIOMS.containsKey(leftTriGramFromCurrent))
                        currentValence += Utils.BOOSTER_DICTIONARY.get(leftTriGramFromCurrent);
                }

                if (index > 1 && !Utils.WORD_VALENCE_DICTIONARY.containsKey(wordsAndEmoticons.get(index - 1).toLowerCase()) && wordsAndEmoticons.get(index - 1).equals("least")) {
                    if (!wordsAndEmoticons.get(index - 2).toLowerCase().equals("at") && !wordsAndEmoticons.get(index - 2).toLowerCase().equals("very"))
                        currentValence *= n_scalar;
                } else if (index > 0 && !Utils.WORD_VALENCE_DICTIONARY.containsKey(wordsAndEmoticons.get(index - 1).toLowerCase()) && wordsAndEmoticons.get(index - 1).equals("least")) {
                    currentValence *= n_scalar;
                }
            }
            sentiments.add(currentValence);
        }

        if (wordsAndEmoticons.contains("but") || wordsAndEmoticons.contains("BUT")) {
            int butIndex = (wordsAndEmoticons.indexOf("but") != -1) ? wordsAndEmoticons.indexOf("BUT"): wordsAndEmoticons.indexOf("but");
            for (int i = 0; i < sentiments.size(); i++) {
                if (i < butIndex) {
                    sentiments.remove(i);
                    sentiments.add(i, sentiments.get(i) * 0.5f);
                } else if (i > butIndex) {
                    sentiments.remove(i);
                    sentiments.add(i, sentiments.get(i) * 1.5f);
                }
            }
        }

        float compoundScore = 0.0f;
        float positiveScore = 0.0f;
        float negativeScore = 0.0f;
        float neutralScore = 0.0f;

        if (!sentiments.isEmpty()) {
            float sentimentSum = 0.0f;
            for (Float sentiment : sentiments)
                sentimentSum += sentiment;

            int exclamationCount = StringUtils.countMatches(text, "!");
            if (exclamationCount > 4)
                exclamationCount = 4;
            if (sentimentSum > 0.0) {
                sentimentSum += (exclamationCount * Utils.EXCLAMATION_MARK_SCORE_AMPLIFIER);
            } else if (sentimentSum < 0.0) {
                sentimentSum -= (exclamationCount * Utils.EXCLAMATION_MARK_SCORE_AMPLIFIER);
            }

            int questionCount = StringUtils.countMatches(text, "?");
            float questionMarkAmplifier = 0.0f;
            if (questionCount > 1) {
                if (questionCount <= 3) {
                    questionMarkAmplifier = questionCount * 0.18f;
                } else {
                    questionMarkAmplifier = 0.96f;
                }
            }
            if (sentimentSum > 0.0) {
                sentimentSum += (questionCount * Utils.QUESTION_MARK_SCORE_AMPLIFIER);
            } else if (sentimentSum < 0.0) {
                sentimentSum -= (questionCount * Utils.QUESTION_MARK_SCORE_AMPLIFIER);
            }

            compoundScore = normalizeScore(sentimentSum);
            for (Float sentimentScore : sentiments) {
                if (sentimentScore > 0.0)
                    positiveScore += sentimentScore + 1.0;
                if (sentimentScore < 0.0)
                    negativeScore += sentimentScore - 1.0;
                if (sentimentScore == 0.0)
                    neutralScore += 1.0;
            }

            float normalizationFactorSentimentScore = positiveScore + Math.abs(negativeScore) + neutralScore;
            positiveScore = Math.abs(positiveScore / normalizationFactorSentimentScore);
            negativeScore = Math.abs(negativeScore / normalizationFactorSentimentScore);
            neutralScore = Math.abs(neutralScore / normalizationFactorSentimentScore);

        }

        HashMap<String, Float> sentimentScores = new HashMap<>();
        sentimentScores.put("neg", negativeScore);
        sentimentScores.put("neu", neutralScore);
        sentimentScores.put("pos", positiveScore);
        sentimentScores.put("compound", compoundScore);

        return sentimentScores;
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

        Sentiment sentimentAnalyzer = new Sentiment();
        for (String sentence : sentences) {
            System.out.println(sentence + "\t\t" +sentimentAnalyzer.getSentiment(sentence));
        }
    }

}
