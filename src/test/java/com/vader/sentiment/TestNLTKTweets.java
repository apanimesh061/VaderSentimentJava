package com.vader.sentiment;

import com.vader.SentimentAnalyzer;
import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

/**
 * @author Animesh Pandey
 *         Created on 4/14/2016.
 */
public class TestNLTKTweets {

    private static final ClassLoader loader = TestNLTKTweets.class.getClassLoader();

    @Test
    public void readGroundTruth() {
        URL gtFile = loader.getResource("tweets_GroundTruth_Vader.tsv");
        if (gtFile != null) {
            try (BufferedReader br = new BufferedReader(new FileReader(gtFile.getFile()))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] gtFileData = line.split("\\t");

                    float negativeScore = Float.parseFloat(gtFileData[1]);
                    float neutralScore = Float.parseFloat(gtFileData[2]);
                    float positiveScore = Float.parseFloat(gtFileData[3]);
                    float compoundScore = Float.parseFloat(gtFileData[4]);
                    String inputString = gtFileData[5];

                    SentimentAnalyzer sentimentAnalyzer = new SentimentAnalyzer(inputString);
                    sentimentAnalyzer.analyse();
                    HashMap<String, Float> inputStringPolarity = sentimentAnalyzer.getPolarity();

                    Assert.assertTrue(inputStringPolarity.get("negative") == negativeScore);
                    Assert.assertTrue(inputStringPolarity.get("positive") == positiveScore);
                    Assert.assertTrue(inputStringPolarity.get("neutral") == neutralScore);
                    Assert.assertTrue(inputStringPolarity.get("compound") == compoundScore);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
