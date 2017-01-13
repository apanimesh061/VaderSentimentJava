package com.vader.sentiment;

import com.vader.SentimentAnalyzer;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * @author Animesh Pandey
 *         Created on 4/14/2016.
 */
public class TestNLTKTweets {
    private static final ClassLoader loader = TestNLTKTweets.class.getClassLoader();
    private static List<String> testFiles = new ArrayList<>();

    static {
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
                while ((line = br.readLine()) != null) {
                    String[] gtFileData = line.split("\\t");

                    float expectedNegativeScore = Float.parseFloat(gtFileData[1]);
                    float expectedNeutralScore = Float.parseFloat(gtFileData[2]);
                    float expectedPositiveScore = Float.parseFloat(gtFileData[3]);
                    float expectedCompoundScore = Float.parseFloat(gtFileData[4]);
                    String inputString = gtFileData[5];

                    SentimentAnalyzer sentimentAnalyzer = new SentimentAnalyzer(inputString);
                    sentimentAnalyzer.analyse();

                    HashMap<String, Float> inputStringPolarity = sentimentAnalyzer.getPolarity();
                    float actualNegativeScore = inputStringPolarity.get("negative");
                    float actualPositiveScore = inputStringPolarity.get("positive");
                    float actualNeutralScore = inputStringPolarity.get("neutral");
                    float actualCompoundScore = inputStringPolarity.get("compound");

                    Assert.assertFalse(error(actualNegativeScore, expectedNegativeScore));
                    Assert.assertFalse(error(actualPositiveScore, expectedPositiveScore));
                    Assert.assertFalse(error(actualNeutralScore, expectedNeutralScore));
                    Assert.assertFalse(error(actualCompoundScore, expectedCompoundScore));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Test Passed for " + fileName);
        }
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
