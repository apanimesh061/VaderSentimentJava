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
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.vader.sentiment.processor.TextProperties;
import com.vader.sentiment.util.Constants;
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
//CHECKSTYLE.OFF: CyclomaticComplexity
public final class SentimentAnalyzer {
    /**
     * Logger for current class.
     */
    private static Logger logger = LoggerFactory.getLogger(SentimentAnalyzer.class);

    /**
     * All functions is this class are static. So, this class should have a private constructor.
     */
    private SentimentAnalyzer() {
    }

    /**
     * This method returns the polarity scores for a given input string.
     *
     * @param inputString the string to be analyzed.
     * @return an object of {@link SentimentPolarities} which will hold all the sentiment scores.
     */
    public static SentimentPolarities getScoresFor(String inputString) {
        return computeSentimentPolaritiesFor(inputString);
    }

    /**
     * Adjust valence if a token is in {@link Utils#BoosterDictionary} or is a yelling word (all caps).
     *
     * @param precedingToken  token
     * @param currentValence  valence to be adjusted
     * @param inputHasYelling true if the input string has any yelling words.
     * @return adjusted valence
     */
    private static float adjustValenceIfCapital(final String precedingToken, final float currentValence,
                                                final boolean inputHasYelling) {
        float scalar = 0.0F;
        final String precedingTokenLower = precedingToken.toLowerCase();
        if (Utils.getBoosterDictionary().containsKey(precedingTokenLower)) {
            scalar = Utils.getBoosterDictionary().get(precedingTokenLower);
            if (currentValence < 0.0F) {
                scalar = -scalar;
            }
            if (Utils.isUpper(precedingToken) && inputHasYelling) {
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
     * @param distance            gram window size
     * @param currentItemPosition position of the current token
     * @param wordsAndEmoticons   tokenized version of the input text
     * @return true if any of the above phrases are found.
     */
    private static boolean areNeverPhrasesPresent(final int distance, final int currentItemPosition,
                                                  final List<String> wordsAndEmoticons) {
        if (distance == 1) {
            final String wordAtDistanceTwoLeft =
                wordsAndEmoticons.get(currentItemPosition - Constants.PRECEDING_BIGRAM_WINDOW);
            final String wordAtDistanceOneLeft =
                wordsAndEmoticons.get(currentItemPosition - Constants.PRECEDING_UNIGRAM_WINDOW);
            return (wordAtDistanceTwoLeft.equals(SentimentModifyingTokens.NEVER.getValue()))
                && (wordAtDistanceOneLeft.equals(SentimentModifyingTokens.SO.getValue())
                || (wordAtDistanceOneLeft.equals(SentimentModifyingTokens.NEVER.getValue())));
        } else if (distance == 2) {
            final String wordAtDistanceThreeLeft = wordsAndEmoticons.get(currentItemPosition
                - Constants.PRECEDING_TRIGRAM_WINDOW);
            final String wordAtDistanceTwoLeft =
                wordsAndEmoticons.get(currentItemPosition - Constants.PRECEDING_BIGRAM_WINDOW);
            final String wordAtDistanceOneLeft =
                wordsAndEmoticons.get(currentItemPosition - Constants.PRECEDING_UNIGRAM_WINDOW);
            return (wordAtDistanceThreeLeft.equals(SentimentModifyingTokens.NEVER.getValue()))
                && (wordAtDistanceTwoLeft.equals(SentimentModifyingTokens.SO.getValue())
                || wordAtDistanceTwoLeft.equals(SentimentModifyingTokens.THIS.getValue()))
                || (wordAtDistanceOneLeft.equals(SentimentModifyingTokens.SO.getValue())
                || wordAtDistanceOneLeft.equals(SentimentModifyingTokens.THIS.getValue()));
        }
        return false;
    }

    /**
     * Adjust the valence is there tokens contain any token which is a negative token or the bigrams and trigrams
     * around a token are phrases that have "never" in them.
     *
     * @param currentValence      valence before
     * @param distance            gram window size
     * @param currentItemPosition position of the current token
     * @param closeTokenIndex     token at the distance position from current item
     * @param wordsAndEmoticons   tokenized version of the input text
     * @return adjusted valence.
     */
    private static float dampValenceIfNegativeTokensFound(final float currentValence, final int distance,
                                                          final int currentItemPosition, final int closeTokenIndex,
                                                          final List<String> wordsAndEmoticons) {
        float newValence = currentValence;
        final boolean anyNeverPhrase = areNeverPhrasesPresent(distance, currentItemPosition, wordsAndEmoticons);

        if (!anyNeverPhrase) {
            if (isNegative(wordsAndEmoticons.get(closeTokenIndex))) {
                newValence *= Valence.NEGATIVE_WORD_DAMPING_FACTOR.getValue();
            }
        } else {
            final float neverPhraseAdjustment = (distance == 1)
                ? Valence.PRECEDING_BIGRAM_HAVING_NEVER_DAMPING_FACTOR.getValue()
                : Valence.PRECEDING_TRIGRAM_HAVING_NEVER_DAMPING_FACTOR.getValue();
            newValence *= neverPhraseAdjustment;
        }

        return newValence;
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
    private static List<String> getFirstRightGrams(final List<String> tokenList, final int minGramLength,
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
        float newValence = currentValence;
        for (String leftGramSequence : leftGramSequences) {
            if (Utils.getSentimentLadenIdiomsValenceDictionary().containsKey(leftGramSequence)) {
                newValence = Utils.getSentimentLadenIdiomsValenceDictionary().get(leftGramSequence);
                break;
            }
        }

        // Based on how getLeftGrams calculates grams, the bi-grams are at the all the even indices.
        // VADER only deals with the 2 left most bi-grams in leftGramSequences.
        for (int i = leftGramSequences.size() - 1; i <= 2; i--) {
            if (Utils.getBoosterDictionary().containsKey(leftGramSequences.get(i))) {
                newValence += Valence.DEFAULT_DAMPING.getValue();
                break;
            }
        }

        return newValence;
    }

    /**
     * Search if the any bi-gram/tri-grams around the currentItemPosition contains any idioms defined
     * in {@link Utils#SentimentLadenIdiomsValenceDictionary}. Adjust the current valence if there are
     * any idioms found.
     *
     * @param currentValence      valence to be adjusted
     * @param currentItemPosition current tokens position
     * @param wordsAndEmoticons   tokenized version of the input text
     * @param distance            max distance from the end of the current gram and the startPosition.
     * @return adjusted valence
     */
    private static float adjustValenceIfIdiomsFound(final float currentValence, final int currentItemPosition,
                                                    final List<String> wordsAndEmoticons, final int distance) {
        float newValence;

        final List<String> leftGramSequences = getLeftGrams(wordsAndEmoticons, 2,
            Constants.MAX_GRAM_WINDOW_SIZE, currentItemPosition, distance);
        newValence = adjustValenceIfLeftGramsHaveIdioms(currentValence, leftGramSequences);

        final List<String> rightGramSequences = getFirstRightGrams(wordsAndEmoticons, 2,
            Constants.MAX_GRAM_WINDOW_SIZE, currentItemPosition);
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
     * @param textProperties This objects holds the tokenized version of a string.
     * @return the valence of each token as a list
     */
    private static List<Float> getTokenWiseSentiment(final TextProperties textProperties) {
        List<Float> sentiments = new ArrayList<>();
        final List<String> wordsAndEmoticons = textProperties.getWordsAndEmoticons();

        for (String currentItem : wordsAndEmoticons) {
            float currentValence = 0.0F;
            final int currentItemPosition = wordsAndEmoticons.indexOf(currentItem);
            final String currentItemLower = currentItem.toLowerCase();

            logger.debug("Current token, \"" + currentItem + "\" with index, i = " + currentItemPosition);
            logger.debug("Sentiment State before \"kind of\" processing: " + sentiments);

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

            logger.debug("Sentiment State after \"kind of\" processing: " + sentiments);
            logger.debug(String.format("Current Valence is %f for \"%s\"", currentValence, currentItem));

            /*
             * If current item in lowercase is in {@link Utils#WordValenceDictionary}...
             */
            if (Utils.getWordValenceDictionary().containsKey(currentItemLower)) {
                currentValence = Utils.getWordValenceDictionary().get(currentItemLower);

                logger.debug("Current currentItem isUpper(): " + Utils.isUpper(currentItem));
                logger.debug("Current currentItem isYelling(): " + textProperties.isYelling());

                /*
                 * If current item is all in uppercase and the input string has yelling words,
                 * accordingly adjust currentValence.
                 */
                if (Utils.isUpper(currentItem) && textProperties.isYelling()) {
                    if (currentValence > 0.0) {
                        currentValence += Valence.ALL_CAPS_FACTOR.getValue();
                    } else {
                        currentValence -= Valence.ALL_CAPS_FACTOR.getValue();
                    }
                }

                logger.debug(String.format("Current Valence post all CAPS checks: %f", currentValence));

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

                        logger.debug(String.format("Current Valence pre gramBasedValence: %f", currentValence));
                        float gramBasedValence = adjustValenceIfCapital(wordsAndEmoticons.get(closeTokenIndex),
                            currentValence, textProperties.isYelling());
                        logger.debug(String.format("Current Valence post gramBasedValence: %f", currentValence));
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

                        logger.debug(String.format("Current Valence post gramBasedValence and "
                            + "distance based damping: %f", currentValence));

                        currentValence = dampValenceIfNegativeTokensFound(currentValence, distance,
                            currentItemPosition, closeTokenIndex, wordsAndEmoticons);

                        logger.debug(String.format("Current Valence post \"never\" check: %f", currentValence));

                        /*
                         * At a distance of 2, we check for idioms in bi-grams and tri-grams around currentItemPosition.
                         */
                        if (distance == 2) {
                            currentValence = adjustValenceIfIdiomsFound(currentValence, currentItemPosition,
                                wordsAndEmoticons, distance);
                            logger.debug(String.format("Current Valence post Idiom check: %f", currentValence));
                        }
                    }

                    distance++;
                }
                currentValence = adjustValenceIfHasAtLeast(currentItemPosition, wordsAndEmoticons, currentValence);
            }

            sentiments.add(currentValence);
        }
        logger.debug("Sentiment state after first pass through tokens: " + sentiments);

        sentiments = adjustValenceIfHasConjunction(wordsAndEmoticons, sentiments);
        logger.debug("Sentiment state after checking conjunctions: " + sentiments);

        return sentiments;
    }

    /**
     * This methods calculates the positive, negative and neutral sentiment from the sentiment values of the input
     * string.
     *
     * @param tokenWiseSentimentStateParam valence of the each token in input string
     * @param punctuationAmplifier         valence adjustment factor for punctuations
     * @return an object of the non-normalized scores as {@link RawSentimentScores}.
     */
    private static RawSentimentScores computeRawSentimentScores(final List<Float> tokenWiseSentimentStateParam,
                                                                final float punctuationAmplifier) {
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

        if (positiveSentimentScore > Math.abs(negativeSentimentScore)) {
            positiveSentimentScore += punctuationAmplifier;
        } else if (positiveSentimentScore < Math.abs(negativeSentimentScore)) {
            negativeSentimentScore -= punctuationAmplifier;
        }

        return new RawSentimentScores(positiveSentimentScore, negativeSentimentScore, (float) neutralSentimentCount);
    }

    /**
     * The compound score is computed by summing the valence scores of each word in the lexicon, adjusted
     * according to the rules, and then normalized to be between -1 (most extreme negative) and +1
     * (most extreme positive). This is the most useful metric if you want a single uni-dimensional measure
     * of sentiment for a given sentence. Calling it a 'normalized, weighted composite score' is accurate.
     *
     * @param tokenWiseSentimentState valence for each token
     * @param punctuationAmplifier    valence adjustment factor for punctuations
     * @return raw compound polarity
     */
    private static float computeCompoundPolarityScore(final List<Float> tokenWiseSentimentState,
                                                      final float punctuationAmplifier) {
        /*
         * Compute the total valence.
         */
        float totalValence = tokenWiseSentimentState.stream()
            .reduce(0.0F, (firstVal, secondVal) -> firstVal + secondVal);
        logger.debug("Total valence: " + totalValence);

        if (totalValence > 0.0F) {
            totalValence += punctuationAmplifier;
        } else if (totalValence < 0.0F) {
            totalValence -= punctuationAmplifier;
        }

        return totalValence;
    }

    /**
     * Normalize the compound score and the other three raw sentiment scores.
     *
     * @param rawSentimentScores    multi-dimensional sentiment scores.
     * @param compoundPolarityScore uni-dimensional sentiment score.
     * @return normalized values of all the type of the sentiment scores in a object of {@link SentimentPolarities}.
     */
    private static SentimentPolarities normalizeAllScores(final RawSentimentScores rawSentimentScores,
                                                          final float compoundPolarityScore) {
        final float positiveSentimentScore = rawSentimentScores.getPositiveScore();
        final float negativeSentimentScore = rawSentimentScores.getNegativeScore();
        final int neutralSentimentCount = Math.round(rawSentimentScores.getNeutralScore());

        final float normalizationFactor = positiveSentimentScore + Math.abs(negativeSentimentScore)
            + neutralSentimentCount;

        logger.debug("Normalization Factor: " + normalizationFactor);

        logger.debug(String.format("Pre-Normalized Scores: %s %s %s %s",
            Math.abs(positiveSentimentScore),
            Math.abs(negativeSentimentScore),
            Math.abs(neutralSentimentCount),
            compoundPolarityScore
        ));

        final float absolutePositivePolarity = Math.abs(positiveSentimentScore / normalizationFactor);
        final float absoluteNegativePolarity = Math.abs(negativeSentimentScore / normalizationFactor);
        final float absoluteNeutralPolarity = Math.abs(neutralSentimentCount / normalizationFactor);

        logger.debug(String.format("Pre-Round Scores: %s %s %s %s",
            absolutePositivePolarity,
            absoluteNegativePolarity,
            absoluteNeutralPolarity,
            compoundPolarityScore
        ));

        final float normalizedPositivePolarity = roundDecimal(absolutePositivePolarity, 3);
        final float normalizedNegativePolarity = roundDecimal(absoluteNegativePolarity, 3);
        final float normalizedNeutralPolarity = roundDecimal(absoluteNeutralPolarity, 3);

        // Normalizing the compound score.
        final float normalizedCompoundPolarity = roundDecimal(normalizeCompoundScore(compoundPolarityScore), 4);

        return new SentimentPolarities(normalizedPositivePolarity, normalizedNegativePolarity,
            normalizedNeutralPolarity, normalizedCompoundPolarity);
    }

    /**
     * Convert the lower level token wise valence to a higher level polarity scores.
     *
     * @param tokenWiseSentimentStateParam the token wise scores of the input string
     * @param punctuationAmplifier         valence adjustment factor for punctuations
     * @return the positive, negative, neutral and compound polarity scores as a map
     */
    private static SentimentPolarities getPolarityScores(final List<Float> tokenWiseSentimentStateParam,
                                                         final float punctuationAmplifier) {
        final List<Float> tokenWiseSentimentState = Collections.unmodifiableList(tokenWiseSentimentStateParam);
        logger.debug("Final token-wise sentiment state: " + tokenWiseSentimentState);

        final float compoundPolarity = computeCompoundPolarityScore(tokenWiseSentimentState, punctuationAmplifier);
        final RawSentimentScores rawSentimentScores = computeRawSentimentScores(tokenWiseSentimentState,
            punctuationAmplifier);

        return normalizeAllScores(rawSentimentScores, compoundPolarity);
    }

    /**
     * This function jointly performs the boosting if input string contains
     * '!'s and/or '?'s and then returns the sum of the boosted scores from
     * {@link SentimentAnalyzer#boostByExclamation(String)} and {@link SentimentAnalyzer#boostByQuestionMark(String)}.
     *
     * @param input the input string that needs to be processed.
     * @return joint boosted score
     */
    private static float boostByPunctuation(String input) {
        return boostByExclamation(input) + boostByQuestionMark(input);
    }

    /**
     * Valence boosting when '!' is found in the input string.
     *
     * @param input the input string that needs to be processed.
     * @return boosting score
     */
    private static float boostByExclamation(String input) {
        final int exclamationCount =
            StringUtils.countMatches(input, SentimentModifyingTokens.EXCLAMATION_MARK.getValue());
        return Math.min(exclamationCount, Constants.MAX_EXCLAMATION_MARKS)
            * Valence.EXCLAMATION_BOOSTING.getValue();
    }

    /**
     * Valence boosting when '?' is found in the input string.
     *
     * @param input the input string that needs to be processed.
     * @return boosting score
     */
    private static float boostByQuestionMark(String input) {
        final int questionMarkCount =
            StringUtils.countMatches(input, SentimentModifyingTokens.QUESTION_MARK.getValue());
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
    private static List<Float> adjustValenceIfHasConjunction(final List<String> inputTokensParam,
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
    private static float adjustValenceIfHasAtLeast(final int currentItemPosition,
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
    private static boolean hasContraction(final String token) {
        return token.endsWith(SentimentModifyingTokens.CONTRACTION.getValue());
    }

    /**
     * Check if token belongs to a pre-defined list of negative words. e.g. {@link Utils#NEGATIVE_WORDS}.
     *
     * @param token            current token
     * @param newNegWordsParam set of negative words
     * @return true if token belongs to newNegWords
     */
    private static boolean hasNegativeWord(final String token, final Set<String> newNegWordsParam) {
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
    private static boolean isNegative(final String token, final boolean checkContractions) {
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
    private static boolean isNegative(final String token) {
        return isNegative(token, true);
    }

    /**
     * Normalize the total valence of the input string, where alpha is the estimated maximum value of valence.
     *
     * @param score score
     * @param alpha estimated max value
     * @return normalized value of score
     */
    private static float normalizeCompoundScore(final float score, final float alpha) {
        final double normalizedScore = score / Math.sqrt((score * score) + alpha);
        return (float) normalizedScore;
    }

    /**
     * Default version of {@link SentimentAnalyzer#normalizeCompoundScore(float, float)} where alpha is 15.0.
     *
     * @param score score
     * @return normalized value of score
     */
    private static float normalizeCompoundScore(final float score) {
        return normalizeCompoundScore(score, Constants.DEFAULT_ALPHA);
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
     * @param inputString string that is to be processed.
     * @return the positive, negative, neutral and compound polarity scores as {@link SentimentPolarities}
     */
    private static SentimentPolarities computeSentimentPolaritiesFor(String inputString) {
        // Parse the string using Lucene and get the text tokens.
        final TextProperties inputStringProperties;
        try {
            inputStringProperties = new TextProperties(inputString);
        } catch (IOException excp) {
            logger.error("There was an issue while pre-processing the inputString.", excp);
            return SentimentPolarities.emptySentimentState();
        }

        // Calculate the per-token valence.
        final List<Float> tokenWiseSentiments = getTokenWiseSentiment(inputStringProperties);
        if (tokenWiseSentiments.isEmpty()) {
            return SentimentPolarities.emptySentimentState();
        }
        // Adjust the total valence score on the basis of the punctuations in the input string.
        final float punctuationAmplifier = boostByPunctuation(inputString);
        return getPolarityScores(tokenWiseSentiments, punctuationAmplifier);
    }
}
//CHECKSTYLE.ON: ExecutableStatementCount
//CHECKSTYLE.ON: JavaNCSS
//CHECKSTYLE.ON: CyclomaticComplexity
