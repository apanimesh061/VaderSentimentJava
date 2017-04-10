package com.vader.sentiment.analyzer;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * This tests confirms if the port from Python NLTK was correct.
 * The sentiment scores are pre-computed for Python and them compared
 * with same text input using the Java implementation.
 * The sentiment scores are supposed to be equal.
 * <p>
 * NOTE: There are some issues with floating point precision differences
 * between Python and Java.
 *
 * @author Animesh Pandey
 *         Created on 4/14/2016.
 * @see <a href=http://github.com/apanimesh061/VaderSentimentJava/commit/d1d30c4ceeb356ec838f8abac70514bd21a92b4b>
 * http://github.com/apanimesh061/VaderSentimentJava/commit/d1d30c4ceeb356ec838f8abac70514bd21a92b4b
 * </a>
 */
public class SentimentAnalyzerTest {
    private static final ClassLoader loader = SentimentAnalyzerTest.class.getClassLoader();
    private static List<String> testFiles = new ArrayList<>();
    private static Logger LOGGER = Logger.getLogger(SentimentAnalyzerTest.class);

    @BeforeClass
    public static void setUpTestFiles() {
        testFiles.add("amazonReviewSnippets_GroundTruth_vader.tsv");
        testFiles.add("movieReviewSnippets_GroundTruth_vader.tsv");
        testFiles.add("nytEditorialSnippets_GroundTruth_vader.tsv");
        testFiles.add("tweets_GroundTruth_vader.tsv");
    }

    @Test
    public void readGroundTruth() {
        for (String fileName : testFiles) {
            InputStream inputStream = loader.getResourceAsStream(fileName);
            try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                SentimentAnalyzer sentimentAnalyzer = new SentimentAnalyzer();
                while ((line = br.readLine()) != null) {
                    String[] gtFileData = line.split("\\t");

                    float expectedNegativeScore = Float.parseFloat(gtFileData[1]);
                    float expectedNeutralScore = Float.parseFloat(gtFileData[2]);
                    float expectedPositiveScore = Float.parseFloat(gtFileData[3]);
                    float expectedCompoundScore = Float.parseFloat(gtFileData[4]);
                    String inputString = gtFileData[5];

                    sentimentAnalyzer.setInputString(inputString);
                    sentimentAnalyzer.setInputStringProperties();
                    sentimentAnalyzer.analyse();

                    HashMap<String, Float> inputStringPolarity = sentimentAnalyzer.getPolarity();
                    float actualNegativeScore = inputStringPolarity.get("negative");
                    float actualPositiveScore = inputStringPolarity.get("positive");
                    float actualNeutralScore = inputStringPolarity.get("neutral");
                    float actualCompoundScore = inputStringPolarity.get("compound");

                    Assert.assertFalse(
                            getErrorMessage(inputString, actualNegativeScore, expectedNegativeScore, "Negative Score"),
                            error(actualNegativeScore, expectedNegativeScore)
                    );
                    Assert.assertFalse(
                            getErrorMessage(inputString, actualPositiveScore, expectedPositiveScore, "Positive Score"),
                            error(actualPositiveScore, expectedPositiveScore)
                    );
                    Assert.assertFalse(
                            getErrorMessage(inputString, actualNeutralScore, expectedNeutralScore, "Neutral Score"),
                            error(actualNeutralScore, expectedNeutralScore)
                    );
                    Assert.assertFalse(
                            getErrorMessage(inputString, actualCompoundScore, expectedCompoundScore, "Compound Score"),
                            error(actualCompoundScore, expectedCompoundScore)
                    );
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            LOGGER.info("Test passed for" + fileName);
        }
    }

    private String getErrorMessage(String message, float actual, float expected, String type) {
        return String.format("Test String: %s ==> %s (actual = %s, expectd = %s)", message, type, actual, expected);
    }

    private int noOfDecimalDigits(float value) {
        String text = Float.toString(Math.abs(value));
        return text.length() - text.indexOf('.') - 1;
    }

    /**
     * Due to Floating Point Precision errors results used to differ by 1
     * e.g. 0.0345 from NLTK might be 0.0344 or 0.0346 when calculated
     * in Java. This was mainly due to rounding off errors.
     * To handle this the difference between two values should not be
     * greater than 1.
     * <p>
     * error(0.0345, 0.0344) => false
     * error(0.0345, 0.0346) => false
     * error(0.0345, 0.0348) => true
     *
     * @param actual
     * @param experiment
     * @return true iff the difference between actual and experiment is
     * greater than 1.0
     */
    private boolean error(float actual, float experiment) {
        int maxPlaces = Math.max(noOfDecimalDigits(actual), noOfDecimalDigits(experiment));
        return ((Math.abs(Math.abs(actual * maxPlaces) - Math.abs(experiment * maxPlaces))) > 1.0);
    }
}
