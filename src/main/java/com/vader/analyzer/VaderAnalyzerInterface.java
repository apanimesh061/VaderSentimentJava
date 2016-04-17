package com.vader.analyzer;

import java.io.IOException;
import java.util.ArrayList;

/**
 * @author Animesh Pandey
 *         Created on 4/16/2016.
 */
interface VaderAnalyzerInterface {
    ArrayList<String> defaultSplit(String inputString) throws IOException;
    ArrayList<String> removePunctuation(String inputString) throws IOException;
}
