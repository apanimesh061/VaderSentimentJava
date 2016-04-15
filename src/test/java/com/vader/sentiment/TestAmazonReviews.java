package com.vader.sentiment;

import com.vader.SentimentAnalyzer;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;

/**
 * @author Animesh Pandey
 *         Created on 4/14/2016.
 */
public class TestAmazonReviews {

    private static final ClassLoader loader = TestAmazonReviews.class.getClassLoader();

    private HashMap<String, HashMap<String, Object>> readGroundTruth() {
        URL gtFile = loader.getResource("amazonReviewSnippets_GroundTruth.txt");
        HashMap<String, HashMap<String, Object>> groundTruth = new HashMap<>();

        if (gtFile != null) {
            try (BufferedReader br = new BufferedReader(new FileReader(gtFile.getFile()))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] gtFileData = line.split("\\t");
                    String docId = gtFileData[0];
                    final Float meanSentimentScore = Float.parseFloat(gtFileData[1]);
                    final String inputText = gtFileData[2];
                    groundTruth.put(
                            docId,
                            new HashMap<String, Object>() {{
                                put("meanSentiment", meanSentimentScore);
                                put("inputText", inputText);
                            }}
                    );
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return groundTruth;
    }

    @Test
    public void testVaderSentiment() throws IOException {
        final HashMap<String, HashMap<String, Object>> groundTruthMap = readGroundTruth();
        for(HashMap.Entry<String, HashMap<String, Object>> entry: groundTruthMap.entrySet()) {
            String docId = entry.getKey();
            Float meanSentimentScore = (Float) entry.getValue().get("meanSentiment");
            String inputText = (String) entry.getValue().get("inputText");

            SentimentAnalyzer sentimentAnalyzer = new SentimentAnalyzer(inputText);
            sentimentAnalyzer.analyse();

            System.out.println(docId);
            System.out.println(meanSentimentScore);
            System.out.println(inputText);
            System.out.println(sentimentAnalyzer.getPolarity());
            System.out.println();
        }
    }

}
