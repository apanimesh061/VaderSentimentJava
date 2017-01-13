# -*- coding: utf-8 -*-

from nltk.sentiment.vader import SentimentIntensityAnalyzer
from unidecode import unidecode

"""
This script uses the NLTK to get the sentiment polarities of 4000 Tweets from "tweets_GroundTruth.txt"
DATASET: http://comp.social.gatech.edu/papers/hutto_ICWSM_2014.tar.gz
PAPER: http://comp.social.gatech.edu/papers/icwsm14.vader.hutto.pdf

The file tweets_GroundTruth_Vader.tsv created using this script serves as the ground truth for comparing
results of the JAVA post of NLTK vader sentiment analyzer.
"""

sid = SentimentIntensityAnalyzer()

ground_truth_file_list = [
    "GroundTruth/tweets_GroundTruth.txt",
    "GroundTruth/amazonReviewSnippets_GroundTruth.txt",
    "GroundTruth/movieReviewSnippets_GroundTruth.txt",
    "GroundTruth/nytEditorialSnippets_GroundTruth.txt"
]


def remove_non_ascii(text):
    return unidecode(unicode(text, encoding="utf-8"))


for test_file in ground_truth_file_list:
    current_file = test_file.split("/")[1].split(".")[0]
    output_filename = current_file + "_vader.tsv"
    with open(output_filename, "wb") as csv_file:
        with open(test_file, "rb") as tweets:
            for line in tweets.readlines():
                tweet_id, _, tweet = line.split("\t")
                tweet = remove_non_ascii(tweet.strip())
                ss = sid.polarity_scores(tweet)
                csv_file.write("\t".join([tweet_id, str(ss["neg"]), str(ss["neu"]), str(ss["pos"]), str(ss["compound"]),
                                          tweet.strip()]) + "\n")
        print "Created output for ", test_file, "as", output_filename
