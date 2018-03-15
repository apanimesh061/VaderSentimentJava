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

package com.vader.sentiment.analyzer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.vader.sentiment.processor.TextProperties;
import com.vader.sentiment.util.Constants;
import com.vader.sentiment.util.ScoreType;
import com.vader.sentiment.util.SentimentModifyingTokens;
import com.vader.sentiment.util.Utils;
import com.vader.sentiment.util.Valence;

/**
 * The SentimentAnalyzer class is the main class for VADER Sentiment analysis.
 *
 * @author Animesh Pandey
 * @see <a href="http://comp.social.gatech.edu/papers/icwsm14.vader.hutto.pdf">VADER: A Parsimonious Rule-based Model
 * for Sentiment Analysis of Social Media Text</a>
 */
//CHECKSTYLE.OFF: ExecutableStatementCount
//CHECKSTYLE.OFF: JavaNCSS
//CHECKSTYLE.OFF: NPath
//CHECKSTYLE.OFF: CyclomaticComplexity
public final class SentimentAnalyzer {
    /**
     * Logger for current class.
     */
    private static Logger logger = LoggerFactory.getLogger(SentimentAnalyzer.class);

    /**
     * This is the input string that will be analyzed.
     */
    private String inputString;

    /**
     * There are the properties that are associated with {@link SentimentAnalyzer#inputString}.
     *
     * @see TextProperties
     */
    private TextProperties inputStringProperties;

    /**
     * Sentiment scores for the current {@link SentimentAnalyzer#inputString}.
     */
    private Map<String, Float> polarity;

    /**
     * Empty constructor for current class.
     * This helps in lazy initialization of {@link SentimentAnalyzer#inputString}.
     */
    public SentimentAnalyzer() {
    }

    /**
     * Parameterized constructor for current class.
     *
     * @param inputString This is the input string.
     * @throws IOException if there was an error while executing {@link SentimentAnalyzer#setInputStringProperties()}.
     */
    public SentimentAnalyzer(String inputString) throws IOException {
        this.inputString = inputString;
        setInputStringProperties();
    }

    public void setInputString(String inputString) {
        this.inputString = inputString;
    }

    /**
     * Computes text properties required for current string.
     *
     * @throws IOException if there was an issue in getting {@link TextProperties} of the input string.
     */
    public void setInputStringProperties() throws IOException {
        inputStringProperties = new TextProperties(inputString);
    }

    public Map<String, Float> getPolarity() {
        return polarity;
    }

    /**
     * This is the main function.
     * This triggers all the sentiment scoring on the {@link SentimentAnalyzer#inputString}.
     */
    public void analyze() {
        polarity = getSentiment();
    }

    /**
     * Adjust valence if a token is in {@link Utils#BoosterDictionary} or is a yelling word (all caps).
     *
     * @param precedingToken token
     * @param currentValence valence to be adjusted
     * @return adjusted valence
     */
    private float valenceModifier(final String precedingToken, final float currentValence) {
        float scalar = 0.0F;
        final String precedingTokenLower = precedingToken.toLowerCase();
        if (Utils.getBoosterDictionary().containsKey(precedingTokenLower)) {
            scalar = Utils.getBoosterDictionary().get(precedingTokenLower);
            if (currentValence < 0.0F) {
                scalar = -scalar;
            }
            if (Utils.isUpper(precedingToken) && inputStringProperties.isYelling()) {
                if (currentValence > 0.0F) {
                    scalar += Valence.ALL_CAPS_FACTOR.getValue();
                } else {
                    scalar -= Valence.ALL_CAPS_FACTOR.getValue();
                }
            }
        }
        return scalar;
    }

    /**
     * This method checks for phrases having
     * - "never so current_word"
     * - "never this current_word"
     * - "never so this" etc.
     *
     * @param currentValence      valence before
     * @param distance            gram window size
     * @param currentItemPosition position of the current token
     * @param closeTokenIndex     token at the distance position from current item
     * @param wordsAndEmoticons   tokenized version of the input text
     * @return adjusted valence
     */
    private float checkForNever(final float currentValence, final int distance,
                                final int currentItemPosition, final int closeTokenIndex,
                                final List<String> wordsAndEmoticons) {
        float tempValence = currentValence;

        if (distance == 0) {
            if (isNegative(wordsAndEmoticons.get(closeTokenIndex))) {
                tempValence *= Valence.NEGATIVE_WORD_DAMPING_FACTOR.getValue();
            }
        } else if (distance == 1) {
            final String wordAtDistanceTwoLeft =
                wordsAndEmoticons.get(currentItemPosition - Constants.PRECEDING_BIGRAM_WINDOW);
            final String wordAtDistanceOneLeft =
                wordsAndEmoticons.get(currentItemPosition - Constants.PRECEDING_UNIGRAM_WINDOW);
            if ((wordAtDistanceTwoLeft.equals(SentimentModifyingTokens.NEVER.getValue()))
                && (wordAtDistanceOneLeft.equals(SentimentModifyingTokens.SO.getValue())
                || (wordAtDistanceOneLeft.equals(SentimentModifyingTokens.NEVER.getValue())))) {
                tempValence *= Valence.PRECEDING_BIGRAM_HAVING_NEVER_DAMPING_FACTOR.getValue();
            } else if (isNegative(wordsAndEmoticons.get(closeTokenIndex))) {
                tempValence *= Valence.NEGATIVE_WORD_DAMPING_FACTOR.getValue();
            }
        } else if (distance == 2) {
            final String wordAtDistanceThreeLeft = wordsAndEmoticons.get(currentItemPosition
                - Constants.PRECEDING_TRIGRAM_WINDOW);
            final String wordAtDistanceTwoLeft =
                wordsAndEmoticons.get(currentItemPosition - Constants.PRECEDING_BIGRAM_WINDOW);
            final String wordAtDistanceOneLeft =
                wordsAndEmoticons.get(currentItemPosition - Constants.PRECEDING_UNIGRAM_WINDOW);
            if ((wordAtDistanceThreeLeft.equals(SentimentModifyingTokens.NEVER.getValue()))
                && (wordAtDistanceTwoLeft.equals(SentimentModifyingTokens.SO.getValue())
                || wordAtDistanceTwoLeft.equals(SentimentModifyingTokens.THIS.getValue()))
                || (wordAtDistanceOneLeft.equals(SentimentModifyingTokens.SO.getValue())
                || wordAtDistanceOneLeft.equals(SentimentModifyingTokens.THIS.getValue()))) {
                tempValence *= Valence.PRECEDING_TRIGRAM_HAVING_NEVER_DAMPING_FACTOR.getValue();
            } else if (isNegative(wordsAndEmoticons.get(closeTokenIndex))) {
                tempValence *= Valence.NEGATIVE_WORD_DAMPING_FACTOR.getValue();
            }
        }

        return tempValence;
    }

    /**
     * This method builds the possible to n-grams starting from last token in the token list.
     * VADER uses bi-grams and tri-grams only, so here minGramLength will be 2 and maxGramLength
     * will be 3.
     *
     * @param tokenList                    The list of tokens for which we want to compute the n-grams.
     * @param minGramLength                The minimum size of the possible n-grams.
     * @param maxGramLength                The maximum size of the possible n-grams.
     * @param startPosition                The position of the token from which we'll extract the tokens.
     * @param maxDistanceFromStartPosition The max distance from the end of the current gram and the startPosition.
     * @return list of all possible to minGramLength-grams and maxGramLength-grams starting from startPosition.
     */
    private static List<String> getLeftGrams(final List<String> tokenList, final int minGramLength,
                                             final int maxGramLength, final int startPosition,
                                             final int maxDistanceFromStartPosition) {
        Preconditions.checkArgument(minGramLength > 0 && maxGramLength > 0,
            "Left Gram lengths should not be negative or zero.");
        Preconditions.checkArgument(maxGramLength >= minGramLength,
            "Maximum left gram length should be at least equal to the minimum value.");
        Preconditions.checkArgument(tokenList != null);

        final int noOfTokens = tokenList.size();
        if (noOfTokens < minGramLength) {
            return Collections.emptyList();
        }

        final List<String> result = new ArrayList<>();
        for (int end = startPosition; end > 0; end--) {
            final int windowStart = end - minGramLength + 1;
            final int windowEnd = end - maxGramLength;
            String currentSuffix = tokenList.get(end);
            for (int start = windowStart; start >= ((windowEnd < 0) ? 0 : Math.max(0, windowEnd) + 1); start--) {
                currentSuffix = tokenList.get(start) + Constants.SPACE_SEPARATOR + currentSuffix;
                result.add(currentSuffix);
                if ((startPosition - end) == maxDistanceFromStartPosition) {
                    return result;
                }
            }
        }
        return result;
    }

    /**
     * This method builds the first possible n-grams starting from startPosition in the token list.
     * VADER uses bi-grams and tri-grams only, so here minGramLength will be 2 and maxGramLength
     * will be 3.
     *
     * @param tokenList     The list of tokens for which we want to compute the n-grams.
     * @param minGramLength The minimum size of the possible n-grams.
     * @param maxGramLength The maximum size of the possible n-grams.
     * @param startPosition The position of the token from which we'll extract the tokens.
     * @return list of the first to minGramLength-grams and maxGramLength-grams starting from
     */
    public static List<String> getFirstRightGrams(final List<String> tokenList, final int minGramLength,
                                                  final int maxGramLength, final int startPosition) {
        Preconditions.checkArgument(minGramLength > 0 && maxGramLength > 0,
            "Right Gram lengths should not be negative or zero.");
        Preconditions.checkArgument(maxGramLength >= minGramLength,
            "Maximum right gram length should be at least equal to the minimum value.");
        Preconditions.checkArgument(tokenList != null);

        final int noOfTokens = tokenList.size();
        if (noOfTokens < minGramLength) {
            return Collections.emptyList();
        }

        final List<String> result = new ArrayList<>();
        String currentGram = tokenList.get(startPosition);
        for (int i = minGramLength; i <= maxGramLength; i++) {
            final int endPosition = startPosition + i - 1;
            if (endPosition > tokenList.size() - 1) {
                break;
            }
            currentGram += Constants.SPACE_SEPARATOR + tokenList.get(endPosition);
            result.add(currentGram);
        }
        return result;
    }

    /**
     * We check if the idioms present in {@link Utils#getSentimentLadenIdiomsValenceDictionary()} are present in
     * left bi/tri-grams sequences.
     *
     * @param currentValence    current valence before checking for idioms.
     * @param leftGramSequences list of all the left bi/tri-grams.
     * @return adjusted valence.
     */
    private static float adjustValenceIfLeftGramsHaveIdioms(final float currentValence,
                                                            final List<String> leftGramSequences) {
        float tempValence = currentValence;
        for (String leftGramSequence : leftGramSequences) {
            if (Utils.getSentimentLadenIdiomsValenceDictionary().containsKey(leftGramSequence)) {
                tempValence = Utils.getSentimentLadenIdiomsValenceDictionary().get(leftGramSequence);
                break;
            }
        }

        // Based on how getLeftGrams calculates grams, the bi-grams are at the all the even indices.
        // VADER only deals with the 2 left most bi-grams in leftGramSequences.
        for (int i = leftGramSequences.size() - 1; i <= 2; i--) {
            if (Utils.getBoosterDictionary().containsKey(leftGramSequences.get(i))) {
                tempValence += Valence.DEFAULT_DAMPING.getValue();
                break;
            }
        }

        return tempValence;
    }

    /**
     * Search if the any bi-gram/tri-grams around the currentItemPosition contains any idioms defined
     * in {@link Utils#SentimentLadenIdiomsValenceDictionary}. Adjust the current valence if there are
     * any idioms found.
     *
     * @param currentValence      valence to be adjusted
     * @param currentItemPosition current tokens position
     * @param wordsAndEmoticons   tokenized version of the input text
     * @return adjusted valence
     */
    private float adjustValenceIfIdiomsFound(final float currentValence, final int currentItemPosition,
                                             final List<String> wordsAndEmoticons) {
        float newValence;

        final List<String> leftGramSequences = getLeftGrams(wordsAndEmoticons, 2, 3,
            currentItemPosition, 2);
        newValence = adjustValenceIfLeftGramsHaveIdioms(currentValence, leftGramSequences);

        final List<String> rightGramSequences = getFirstRightGrams(wordsAndEmoticons, 2, 3,
            currentItemPosition);
        for (String rightGramSequence : rightGramSequences) {
            if (Utils.getSentimentLadenIdiomsValenceDictionary().containsKey(rightGramSequence)) {
                newValence = Utils.getSentimentLadenIdiomsValenceDictionary().get(rightGramSequence);
            }
        }

        return newValence;
    }

    /**
     * Analyze each token/emoticon in the input string and calculate its valence.
     *
     * @return the valence of each token as a list
     */
    private List<Float> getTokenWiseSentiment() {
        List<Float> sentiments = new ArrayList<>();
        final List<String> wordsAndEmoticons = inputStringProperties.getWordsAndEmoticons();

        for (String currentItem : wordsAndEmoticons) {
            float currentValence = 0.0F;
            final int currentItemPosition = wordsAndEmoticons.indexOf(currentItem);
            final String currentItemLower = currentItem.toLowerCase();

            if (logger.isDebugEnabled()) {
                logger.debug("Current token, \"" + currentItem + "\" with index, i = " + currentItemPosition);
                logger.debug("Sentiment State before \"kind of\" processing: " + sentiments);
            }

            /*
             * This section performs the following evaluation:
             * If the term at currentItemPosition is followed by "kind of" or the it is present in
             * {@link Utils#BoosterDictionary}, add the currentValence to sentiment array and break
             * to the next loop.
             *
             * If currentValence was 0.0, then current word's valence will also be 0.0.
             */
            if ((currentItemPosition < wordsAndEmoticons.size() - 1
                && currentItemLower.equals(SentimentModifyingTokens.KIND.getValue())
                && wordsAndEmoticons.get(currentItemPosition + 1).toLowerCase()
                .equals(SentimentModifyingTokens.OF.getValue()))
                || Utils.getBoosterDictionary().containsKey(currentItemLower)) {
                sentiments.add(currentValence);
                continue;
            }

            if (logger.isDebugEnabled()) {
                logger.debug("Sentiment State after \"kind of\" processing: " + sentiments);
                logger.debug(String.format("Current Valence is %f for \"%s\"", currentValence, currentItem));
            }

            /*
             * If current item in lowercase is in {@link Utils#WordValenceDictionary}...
             */
            if (Utils.getWordValenceDictionary().containsKey(currentItemLower)) {
                currentValence = Utils.getWordValenceDictionary().get(currentItemLower);

                if (logger.isDebugEnabled()) {
                    logger.debug("Current currentItem isUpper(): " + Utils.isUpper(currentItem));
                    logger.debug("Current currentItem isYelling(): " + inputStringProperties.isYelling());
                }

                /*
                 * If current item is all in uppercase and the input string has yelling words,
                 * accordingly adjust currentValence.
                 */
                if (Utils.isUpper(currentItem) && inputStringProperties.isYelling()) {
                    if (currentValence > 0.0) {
                        currentValence += Valence.ALL_CAPS_FACTOR.getValue();
                    } else {
                        currentValence -= Valence.ALL_CAPS_FACTOR.getValue();
                    }
                }

                if (logger.isDebugEnabled()) {
                    logger.debug(String.format("Current Valence post all CAPS checks: %f", currentValence));
                }

                /*
                 * "distance" is the window size.
                 * e.g. "The plot was good, but the characters are uncompelling.",
                 * if the current item is "characters", then at:
                 *  - distance = 0, closeTokenIndex = 5
                 *  - distance = 1, closeTokenIndex = 4
                 *  - distance = 2, closeTokenIndex = 3
                 */
                int distance = 0;
                while (distance < Constants.MAX_GRAM_WINDOW_SIZE) {
                    int closeTokenIndex = currentItemPosition - (distance + 1);
                    if (closeTokenIndex < 0) {
                        closeTokenIndex = wordsAndEmoticons.size() - Math.abs(closeTokenIndex);
                    }

                    if ((currentItemPosition > distance)
                        && !Utils.getWordValenceDictionary().containsKey(wordsAndEmoticons.get(closeTokenIndex)
                        .toLowerCase())) {

                        if (logger.isDebugEnabled()) {
                            logger.debug(String.format("Current Valence pre gramBasedValence: %f", currentValence));
                        }
                        float gramBasedValence =
                            valenceModifier(wordsAndEmoticons.get(closeTokenIndex), currentValence);
                        if (logger.isDebugEnabled()) {
                            logger.debug(String.format("Current Valence post gramBasedValence: %f", currentValence));
                        }
                        /*
                         * At distance of 1, reduce current gram's valence by 5%.
                         * At distance of 2, reduce current gram's valence by 10%.
                         */
                        if (gramBasedValence != 0.0F) {
                            if (distance == 1) {
                                gramBasedValence *= Valence.ONE_WORD_DISTANCE_DAMPING_FACTOR.getValue();
                            } else if (distance == 2) {
                                gramBasedValence *= Valence.TWO_WORD_DISTANCE_DAMPING_FACTOR.getValue();
                            }
                        }
                        currentValence += gramBasedValence;

                        if (logger.isDebugEnabled()) {
                            logger.debug(String.format("Current Valence post gramBasedValence and "
                                + "distance based damping: %f", currentValence));
                        }

                        currentValence = checkForNever(currentValence, distance, currentItemPosition,
                            closeTokenIndex, wordsAndEmoticons);

                        if (logger.isDebugEnabled()) {
                            logger.debug(String.format("Current Valence post \"never\" check: %f", currentValence));
                        }

                        /*
                         * At a distance of 2, we check for idioms in bi-grams and tri-grams around currentItemPosition.
                         */
                        if (distance == 2) {
                            currentValence = adjustValenceIfIdiomsFound(currentValence, currentItemPosition,
                                wordsAndEmoticons);
                            if (logger.isDebugEnabled()) {
                                logger.debug(String.format("Current Valence post Idiom check: %f", currentValence));
                            }
                        }
                    }
                    distance++;
                }
                currentValence = adjustValenceIfHasAtLeast(currentItemPosition, wordsAndEmoticons, currentValence);
            }

            sentiments.add(currentValence);
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Sentiment state after first pass through tokens: " + sentiments);
        }
        sentiments = adjustValenceIfHasConjunction(wordsAndEmoticons, sentiments);
        if (logger.isDebugEnabled()) {
            logger.debug("Sentiment state after checking conjunctions: " + sentiments);
        }
        return sentiments;
    }

    /**
     * This methods calculates the positive, negative and neutral sentiment from the sentiment values of the input
     * string.
     *
     * @param tokenWiseSentimentStateParam valence of the each token in input string
     * @return the positive, negative and neutral sentiment as an List
     */
    private List<Float> siftSentimentScores(final List<Float> tokenWiseSentimentStateParam) {
        final List<Float> tokenWiseSentimentState = Collections.unmodifiableList(tokenWiseSentimentStateParam);
        float positiveSentimentScore = 0.0F;
        float negativeSentimentScore = 0.0F;
        int neutralSentimentCount = 0;
        for (Float valence : tokenWiseSentimentState) {
            if (valence > 0.0F) {
                positiveSentimentScore += valence + 1.0F;
            } else if (valence < 0.0F) {
                negativeSentimentScore += valence - 1.0F;
            } else {
                neutralSentimentCount += 1;
            }
        }
        return new ArrayList<>(
            Arrays.asList(
                positiveSentimentScore,
                negativeSentimentScore,
                (float) neutralSentimentCount
            )
        );
    }

    /**
     * Convert the lower level token wise valence to a higher level polarity scores.
     *
     * @param tokenWiseSentimentStateParam the tokenwise scores of the input string
     * @return the positive, negative, neutral and compound polarity scores as a map
     */
    private Map<String, Float> getPolarityScores(List<Float> tokenWiseSentimentStateParam) {
        final List<Float> tokenWiseSentimentState = Collections.unmodifiableList(tokenWiseSentimentStateParam);
        final Map<String, Float> result = new HashMap<>();
        if (!tokenWiseSentimentState.isEmpty()) {
            /*
             * Compute the total valence.
             */
            float totalValence = 0.0F;
            for (Float valence : tokenWiseSentimentState) {
                totalValence += valence;
            }
            if (logger.isDebugEnabled()) {
                logger.debug("Total valence: " + totalValence);
            }

            /*
             * Adjust the total valence score on the basis of the punctuations in the input string.
             */
            final float punctuationAmplifier = boostByPunctuation();

            if (totalValence > 0.0F) {
                totalValence += punctuationAmplifier;
            } else if (totalValence < 0.0F) {
                totalValence -= punctuationAmplifier;
            }

            if (logger.isDebugEnabled()) {
                logger.debug("Total valence after boost/damp by punctuation: " + totalValence);
                logger.debug("Final token-wise sentiment state: " + tokenWiseSentimentState);
            }

            final List<Float> siftedScores = siftSentimentScores(tokenWiseSentimentState);
            float positiveSentimentScore = siftedScores.get(0);
            float negativeSentimentScore = siftedScores.get(1);
            final int neutralSentimentCount = Math.round(siftedScores.get(2));

            if (logger.isDebugEnabled()) {
                logger.debug(String.format("Post Sift Sentiment Scores: %s %s %s",
                    positiveSentimentScore,
                    negativeSentimentScore,
                    neutralSentimentCount
                ));
            }

            if (positiveSentimentScore > Math.abs(negativeSentimentScore)) {
                positiveSentimentScore += punctuationAmplifier;
            } else if (positiveSentimentScore < Math.abs(negativeSentimentScore)) {
                negativeSentimentScore -= punctuationAmplifier;
            }

            final float normalizationFactor = positiveSentimentScore + Math.abs(negativeSentimentScore)
                + neutralSentimentCount;

            if (logger.isDebugEnabled()) {
                logger.debug("Normalization Factor: " + normalizationFactor);
            }

            final float compoundPolarity = normalizeScore(totalValence);

            if (logger.isDebugEnabled()) {
                logger.debug(String.format("Pre-Normalized Scores: %s %s %s %s",
                    Math.abs(positiveSentimentScore),
                    Math.abs(negativeSentimentScore),
                    Math.abs(neutralSentimentCount),
                    compoundPolarity
                ));
            }

            final float absolutePositivePolarity = Math.abs(positiveSentimentScore / normalizationFactor);
            final float absoluteNegativePolarity = Math.abs(negativeSentimentScore / normalizationFactor);
            final float absoluteNeutralPolarity = Math.abs(neutralSentimentCount / normalizationFactor);

            if (logger.isDebugEnabled()) {
                logger.debug(String.format("Pre-Round Scores: %s %s %s %s",
                    absolutePositivePolarity,
                    absoluteNegativePolarity,
                    absoluteNeutralPolarity,
                    compoundPolarity
                ));
            }

            final float normalizedPositivePolarity = roundDecimal(absolutePositivePolarity, 3);
            final float normalizedNegativePolarity = roundDecimal(absoluteNegativePolarity, 3);
            final float normalizedNeutralPolarity = roundDecimal(absoluteNeutralPolarity, 3);
            final float normalizedCompoundPolarity = roundDecimal(compoundPolarity, 4);

            result.put(ScoreType.COMPOUND, normalizedCompoundPolarity);
            result.put(ScoreType.POSITIVE, normalizedPositivePolarity);
            result.put(ScoreType.NEGATIVE, normalizedNegativePolarity);
            result.put(ScoreType.NEUTRAL, normalizedNeutralPolarity);
        } else {
            result.put(ScoreType.COMPOUND, 0.0F);
            result.put(ScoreType.POSITIVE, 0.0F);
            result.put(ScoreType.NEGATIVE, 0.0F);
            result.put(ScoreType.NEUTRAL, 0.0F);
        }
        return result;
    }

    /**
     * This function jointly performs the boosting if input string contains
     * '!'s and/or '?'s and then returns the sum of the boosted scores from
     * {@link SentimentAnalyzer#boostByExclamation()} and {@link SentimentAnalyzer#boostByQuestionMark()}.
     *
     * @return joint boosted score
     */
    private float boostByPunctuation() {
        return boostByExclamation() + boostByQuestionMark();
    }

    /**
     * Valence boosting when '!' is found in the input string.
     *
     * @return boosting score
     */
    private float boostByExclamation() {
        final int exclamationCount =
            StringUtils.countMatches(inputString, SentimentModifyingTokens.EXCLAMATION_MARK.getValue());
        return Math.min(exclamationCount, Constants.MAX_EXCLAMATION_MARKS)
            * Valence.EXCLAMATION_BOOSTING.getValue();
    }

    /**
     * Valence boosting when '?' is found in the input string.
     *
     * @return boosting score
     */
    private float boostByQuestionMark() {
        final int questionMarkCount =
            StringUtils.countMatches(inputString, SentimentModifyingTokens.QUESTION_MARK.getValue());
        float questionMarkAmplifier = 0.0F;
        if (questionMarkCount > 1) {
            if (questionMarkCount <= Constants.MAX_QUESTION_MARKS) {
                questionMarkAmplifier = questionMarkCount * Valence.QUESTION_MARK_MAX_COUNT_BOOSTING.getValue();
            } else {
                questionMarkAmplifier = Valence.QUESTION_MARK_BOOSTING.getValue();
            }
        }
        return questionMarkAmplifier;
    }

    /**
     * This methods manages the effect of contrastive conjunctions like "but" on the valence of a token.
     * "VADER" only support "but/BUT" as a conjunction that modifies the valence.
     *
     * @param inputTokensParam             list of token and/or emoticons in the input string
     * @param tokenWiseSentimentStateParam current token wise sentiment scores
     * @return adjusted token wise sentiment scores
     */
    private List<Float> adjustValenceIfHasConjunction(final List<String> inputTokensParam,
                                                      final List<Float> tokenWiseSentimentStateParam) {
        final List<String> inputTokens = Collections.unmodifiableList(inputTokensParam);
        final List<Float> tokenWiseSentimentState = new ArrayList<>(tokenWiseSentimentStateParam);

        int indexOfConjunction = inputTokens.indexOf(SentimentModifyingTokens.BUT.getValue());
        if (indexOfConjunction < 0) {
            indexOfConjunction = inputTokens.indexOf(SentimentModifyingTokens.BUT.getValue().toUpperCase());
        }
        if (indexOfConjunction >= 0) {
            for (int valenceIndex = 0; valenceIndex < tokenWiseSentimentState.size(); valenceIndex++) {
                float currentValence = tokenWiseSentimentState.get(valenceIndex);
                if (valenceIndex < indexOfConjunction) {
                    currentValence *= Valence.PRE_CONJUNCTION_ADJUSTMENT_FACTOR.getValue();
                } else if (valenceIndex > indexOfConjunction) {
                    currentValence *= Valence.POST_CONJUNCTION_ADJUSTMENT_FACTOR.getValue();
                }
                tokenWiseSentimentState.set(valenceIndex, currentValence);
            }
        }
        return tokenWiseSentimentState;
    }

    /**
     * Check for the cases where you have phrases having "least" in the words preceding the token at
     * currentItemPosition and accordingly adjust the valence.
     *
     * @param currentItemPosition    position of the token in wordsAndEmoticons around which we will search for "least"
     *                               type phrases
     * @param wordsAndEmoticonsParam list of token and/or emoticons in the input string
     * @param currentValence         valence of the token at currentItemPosition
     * @return adjusted currentValence
     */
    private float adjustValenceIfHasAtLeast(final int currentItemPosition,
                                            final List<String> wordsAndEmoticonsParam,
                                            final float currentValence) {
        final List<String> wordsAndEmoticons = Collections.unmodifiableList(wordsAndEmoticonsParam);
        float valence = currentValence;
        if (currentItemPosition > 1
            && !Utils.getWordValenceDictionary().containsKey(wordsAndEmoticons.get(currentItemPosition - 1)
            .toLowerCase())
            && wordsAndEmoticons.get(currentItemPosition - 1).toLowerCase()
            .equals(SentimentModifyingTokens.LEAST.getValue())) {
            if (!(wordsAndEmoticons.get(currentItemPosition - 2).toLowerCase()
                .equals(SentimentModifyingTokens.AT.getValue())
                || wordsAndEmoticons.get(currentItemPosition - 2).toLowerCase()
                .equals(SentimentModifyingTokens.VERY.getValue()))) {
                valence *= Valence.NEGATIVE_WORD_DAMPING_FACTOR.getValue();
            }
        } else if (currentItemPosition > 0 && !Utils.getWordValenceDictionary()
            .containsKey(wordsAndEmoticons.get(currentItemPosition - 1).toLowerCase())
            && wordsAndEmoticons.get(currentItemPosition - 1).equals(SentimentModifyingTokens.LEAST.getValue())) {
            valence *= Valence.NEGATIVE_WORD_DAMPING_FACTOR.getValue();
        }
        return valence;
    }

    /**
     * Check if token has "n't" in the end.
     *
     * @param token current token
     * @return true iff token has "n't" in the end
     */
    private boolean hasContraction(final String token) {
        return token.endsWith(SentimentModifyingTokens.CONTRACTION.getValue());
    }

    /**
     * Check if token belongs to a pre-defined list of negative words. e.g. {@link Utils#NEGATIVE_WORDS}.
     *
     * @param token            current token
     * @param newNegWordsParam set of negative words
     * @return true if token belongs to newNegWords
     */
    private boolean hasNegativeWord(final String token, final Set<String> newNegWordsParam) {
        final Set<String> newNegWords = Collections.unmodifiableSet(newNegWordsParam);
        return newNegWords.contains(token);
    }

    /**
     * Check if token belongs to a pre-defined list of negative words. e.g. {@link Utils#NEGATIVE_WORDS}
     * and also checks if the token has "n't" in the end.
     *
     * @param token             current token
     * @param checkContractions flag to check "n't" in end of token
     * @return true iff token is in newNegWords or if checkContractions is true, token should have "n't" in its end
     */
    private boolean isNegative(final String token, final boolean checkContractions) {
        final boolean result = hasNegativeWord(token, Utils.NEGATIVE_WORDS);
        if (!checkContractions) {
            return result;
        }
        return result || hasContraction(token);
    }

    /**
     * This is the default version of {@link SentimentAnalyzer#isNegative(String, boolean)}.
     *
     * @param token current token
     * @return true iff token is in {@link Utils#NEGATIVE_WORDS} or token has "n't" in its end
     */
    private boolean isNegative(final String token) {
        return isNegative(token, true);
    }

    /**
     * Normalize the total valence of the input string, where alpha is the estimated maximum value of valence.
     *
     * @param score score
     * @param alpha estimated max value
     * @return normalized value of score
     */
    private float normalizeScore(final float score, final float alpha) {
        final double normalizedScore = score / Math.sqrt((score * score) + alpha);
        return (float) normalizedScore;
    }

    /**
     * Default version of {@link SentimentAnalyzer#normalizeScore(float, float)} where alpha is 15.0.
     *
     * @param score score
     * @return normalized value of score
     */
    private float normalizeScore(final float score) {
        return normalizeScore(score, Constants.DEFAULT_ALPHA);
    }

    /**
     * This method rounds of a float value to defined no. of places.
     *
     * @param currentValue current float values
     * @param noOfPlaces   no. of decimal places
     * @return rounded float value
     */
    private static float roundDecimal(final float currentValue, final int noOfPlaces) {
        final float factor = (float) Math.pow(10.0, (double) noOfPlaces);
        final float number = Math.round(currentValue * factor);
        return number / factor;
    }

    /**
     * This is a composite function that computes token-wise sentiment scores and then converts that to
     * higher level scores.
     *
     * @return the positive, negative, neutral and compound polarity scores as a map
     */
    private Map<String, Float> getSentiment() {
        final List<Float> tokenWiseSentiments = getTokenWiseSentiment();
        return getPolarityScores(tokenWiseSentiments);
    }
}
//CHECKSTYLE.ON: ExecutableStatementCount
//CHECKSTYLE.ON: JavaNCSS
//CHECKSTYLE.ON: NPath
//CHECKSTYLE.ON: CyclomaticComplexity
