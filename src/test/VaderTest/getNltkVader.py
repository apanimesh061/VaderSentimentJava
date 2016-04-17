from nltk.sentiment.vader import SentimentIntensityAnalyzer

"""
This script uses the NLTK to get the sentiment polarities of 4000 Tweets from "tweets_GroundTruth.txt"
DATASET: http://comp.social.gatech.edu/papers/hutto_ICWSM_2014.tar.gz
PAPER: http://comp.social.gatech.edu/papers/icwsm14.vader.hutto.pdf

The file tweets_GroundTruth_Vader.tsv created using this script serves as the ground truth for comparing
results of the JAVA post of NLTK vader sentiment analyzer.
"""

sid = SentimentIntensityAnalyzer()

csv_file = open("tweets_GroundTruth_Vader.tsv", "wb")

with open('tweets_GroundTruth_Vader.tsv', 'wb') as fp:
    with open("tweets_GroundTruth.txt", "rb") as tweets:
        for line in tweets.readlines():
            tweet_id, _, tweet = line.split("\t")
            ss = sid.polarity_scores(tweet)
            csv_file.write("\t".join([tweet_id, str(ss["neg"]), str(ss["neu"]), str(ss["pos"]), str(ss["compound"]), tweet.strip()]) + "\n")

csv_file.close()
