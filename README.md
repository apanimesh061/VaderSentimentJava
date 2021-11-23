## VADER-Sentiment-Analysis in Java

[![Build Status](https://travis-ci.org/apanimesh061/VaderSentimentJava.svg?branch=master)](https://travis-ci.org/apanimesh061/VaderSentimentJava)

VADER (Valence Aware Dictionary and sEntiment Reasoner) is a lexicon and rule-based sentiment analysis tool that is _specifically attuned to sentiments expressed in social media_. It is fully open-sourced under the [MIT License](http://choosealicense.com/) (we sincerely appreciate all attributions and readily accept most contributions, but please don't hold us liable).

This is a JAVA port of the NLTK VADER sentiment analysis originally written in Python.

 - The [Original](https://github.com/cjhutto/vaderSentiment) python module by the paper's author C.J. Hutto
 - The [NLTK](http://www.nltk.org/_modules/nltk/sentiment/vader.html) source

For the testing I have compared the results of the NLTK module with this Java port.

### Update (Oct 2021)
- - -
Releasing `v1.1.1`.

Thanks to @ArjohnKampman for helping is optimizing some parts of the code. Since I was touching this repo after a long time, I noticed that a lot of the Maven dependencies and plugins were outdated, so I have updated them. `mvn package` still works so it should be fine.

I also noticed a lot of comments on not being able to use the library from Maven. I did upload a Jar to Nexus a long time back and I was having trouble doing that again since I think I've lost the pass-phrases needed to sign and upload the Jar to the Nexus. Luckily, I found a new solution [here](https://stackoverflow.com/a/28483461) which suggests to use https://jitpack.io/ for public GitHub repositories. Turns out it is super simple to use it and get the pacakge from GitHub. I wanted to make sure I unblock anyone who wants to use this package.

I created a test Maven project `test-mvn-pkg1` locally and added the following to its `pom.xml`:

```
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>org.example</groupId>
    <artifactId>test-mvn-pkg1</artifactId>
    <version>1.0-SNAPSHOT</version>

    <repositories>
        <repository>
            <id>jitpack.io</id>
            <url>https://jitpack.io</url>
        </repository>
    </repositories>

    <dependencies>
        <dependency>
            <groupId>com.github.apanimesh061</groupId>
            <artifactId>VaderSentimentJava</artifactId>
            <version>v1.1.1</version>
        </dependency>
    </dependencies>

</project>
```
Once Maven downloads the dependencies, you can easily use it in your code like:

```
package org.example;

import com.vader.sentiment.analyzer.SentimentAnalyzer;
import com.vader.sentiment.analyzer.SentimentPolarities;

public class Test {
    public static void main(String[] args) {
        final SentimentPolarities sentimentPolarities =
            SentimentAnalyzer.getScoresFor("that's a rare and valuable feature.");
        System.out.println(sentimentPolarities);
	// SentimentPolarities{positivePolarity=0.437, negativePolarity=0.0, neutralPolarity=0.563, compoundPolarity=0.4767}
    }
}
```

I'll try the Nexus upload and figure out if I can create a new Maven repo all together. Meanwhile, `jitpack` should work for anyone wanting to use the package.


### Update (Jan 2018)

- - -
Based on a recommendation from @alexpetlenko, I uploaded the jar to Nexus as `vader-sentiment-analyzer-1.0`.

You can download the jar by adding the following to you `pom.xml`:
```xml
<dependency>
  <groupId>com.github.apanimesh061</groupId>
  <artifactId>vader-sentiment-analyzer</artifactId>
  <version>1.0</version>
</dependency>
```

Path to Jar: [vader-sentiment-analyzer-1.0.jar](https://oss.sonatype.org/service/local/repositories/releases/content/com/github/apanimesh061/vader-sentiment-analyzer/1.0/vader-sentiment-analyzer-1.0.jar)

### Update (May 2017)

- - -
Major design refactorings resulting from addition of `checkstyle` to the project.

Also added JavaDocs to the project.

### Update (Jan 2017)

- - -

I have corrected a few bugs that I encountered when I was adding more tests.

The details are [here](https://github.com/apanimesh061/VaderSentimentJava/commit/d1d30c4ceeb356ec838f8abac70514bd21a92b4b).

This project now includes tests on text from:

1. Amazon Reviews
2. Movie Reviews
3. NyTimes Editorial snippets

### Introduction
- - -

This README file describes the dataset of the paper:

  **VADER: A Parsimonious Rule-based Model for Sentiment Analysis of Social Media Text** <br />
  (by C.J. Hutto and Eric Gilbert) <br />
  Eighth International Conference on Weblogs and Social Media (ICWSM-14). Ann Arbor, MI, June 2014. <br />

For questions, please contact: <br />

C.J. Hutto <br />
Georgia Institute of Technology, Atlanta, GA 30032  <br />
cjhutto [at] gatech [dot] edu <br />

### Citation Information
- - -

If you use either the dataset or any of the VADER sentiment analysis tools (VADER sentiment lexicon or Python code for rule-based sentiment analysis engine) in your research, please cite the above paper. For example:  <br />

  > <small> **Hutto, C.J. & Gilbert, E.E. (2014). VADER: A Parsimonious Rule-based Model for Sentiment Analysis of Social Media Text. Eighth International Conference on Weblogs and Social Media (ICWSM-14). Ann Arbor, MI, June 2014.** </small><br />

### Resources and Dataset Descriptions
- - -

The compressed .tar.gz package includes **PRIMARY RESOURCES** (items 1-3) as well as additional **DATASETS AND TESTING RESOURCES** (items 4-12):

1. [VADER: A Parsimonious Rule-based Model for Sentiment Analysis of Social Media Text](http://comp.social.gatech.edu/papers/icwsm14.vader.hutto.pdf) <br />
    The original paper for the data set, see citation information (above).

2. vader_sentiment_lexicon.txt <br />
       Empirically validated by multiple independent human judges, VADER incorporates a "gold-standard" sentiment lexicon that is especially attuned to microblog-like contexts.  <br />
    The VADER sentiment lexicon is sensitive both the **polarity** and the **intensity** of sentiments
	expressed in social media contexts, and is also generally applicable to sentiment analysis
	in other domains. <br />
	   Manually creating (much less, validating) a comprehensive sentiment lexicon is
	a labor intensive and sometimes error prone process, so it is no wonder that many
	opinion mining researchers and practitioners rely so heavily on existing lexicons
	as primary resources. We are pleased to offer ours as a new resource. <br />
	   We begin by constructing a list inspired by examining existing well-established
	sentiment word-banks (LIWC, ANEW, and GI). To this, we next incorporate numerous
	lexical features common to sentiment expression in microblogs, including
	 - a full list of Western-style emoticons, for example, :-) denotes a smiley face
	   and generally indicates positive sentiment)
	 - sentiment-related acronyms and initialisms (e.g., LOL and WTF are both examples of
	   sentiment-laden initialisms)
	 - commonly used slang with sentiment value (e.g., nah, meh and giggly).

	This process provided us with over 9,000 lexical feature candidates. Next, we assessed
	the general applicability of each feature candidate to sentiment expressions. We
	used a wisdom-of-the-crowd13 (WotC) approach (Surowiecki, 2004) to acquire a valid
	point estimate for the sentiment valence (intensity) of each context-free candidate
	feature. We collected intensity ratings on each of our candidate lexical features
	from ten independent human raters (for a total of 90,000+ ratings). Features were
	rated on a scale from "[–4] Extremely Negative" to "[4] Extremely Positive", with
	allowance for "[0] Neutral (or Neither, N/A)".  <br />
	   We kept every lexical feature that had a non-zero mean rating, and whose standard
	deviation was less than 2.5 as determined by the aggregate of ten independent raters.
	This left us with just over 7,500 lexical features with validated valence scores that
	indicated both the sentiment polarity (positive/negative), and the sentiment intensity
	on a scale from –4 to +4. For example, the word "okay" has a positive valence of 0.9,
	"good" is 1.9, and "great" is 3.1, whereas "horrible" is –2.5, the frowning emoticon :(
	is –2.2, and "sucks" and it's slang derivative "sux" are both –1.5.

3. vaderSentiment.py <br />
    The Python code for the rule-based sentiment analysis engine. Implements the
	grammatical and syntactical rules described in the paper, incorporating empirically
	derived quantifications for the impact of each rule on the perceived intensity of
	sentiment in sentence-level text. Importantly, these heuristics go beyond what would
	normally be captured in a typical bag-of-words model. They incorporate **word-order
	sensitive relationships** between terms. For example, degree modifiers (also called
	intensifiers, booster words, or degree adverbs) impact sentiment intensity by either
	increasing or decreasing the intensity. Consider these examples: <br />
	   (a) "The service here is extremely good"  <br />
	   (b) "The service here is good" <br />
	   (c) "The service here is marginally good" <br />
	From Table 3 in the paper, we see that for 95% of the data, using a degree modifier
    increases the positive sentiment intensity of example (a) by 0.227 to 0.36, with a
	mean difference of 0.293 on a rating scale from 1 to 4. Likewise, example (c) reduces
	the perceived sentiment intensity by 0.293, on average.

4. tweets_GroundTruth.txt <br />
    **NOTE**: This java module uses this file for testing. <br />
	FORMAT: the file is tab delimited with ID, MEAN-SENTIMENT-RATING, and TWEET-TEXT <br />
    DESCRIPTION: includes "tweet-like" text as inspired by 4,000 tweets pulled from Twitter’s public timeline, plus 200 completely contrived tweet-like texts intended to specifically test syntactical and grammatical conventions of conveying differences in sentiment intensity. The "tweet-like" texts incorporate a fictitious username (@anonymous) in places where a username might typically appear, along with a fake URL ( http://url_removed ) in places where a URL might typically appear, as inspired by the original tweets. The ID and MEAN-SENTIMENT-RATING correspond to the raw sentiment rating data provided in 'tweets_anonDataRatings.txt' (described below).

5. tweets_anonDataRatings.txt <br />
    FORMAT: the file is tab delimited with ID, MEAN-SENTIMENT-RATING, STANDARD DEVIATION, and RAW-SENTIMENT-RATINGS <br />
	DESCRIPTION: Sentiment ratings from a minimum of 20 independent human raters (all pre-screened, trained, and quality checked for optimal inter-rater reliability).

6. nytEditorialSnippets_GroundTruth.txt <br />
	FORMAT: the file is tab delimited with ID, MEAN-SENTIMENT-RATING, and TEXT-SNIPPET <br />
    DESCRIPTION: includes 5,190 sentence-level snippets from 500 New York Times opinion news editorials/articles; we used the NLTK tokenizer to segment the articles into sentence phrases, and added sentiment intensity ratings. The ID and MEAN-SENTIMENT-RATING correspond to the raw sentiment rating data provided in 'nytEditorialSnippets_anonDataRatings.txt' (described below).

7. nytEditorialSnippets_anonDataRatings.txt <br />
	FORMAT: the file is tab delimited with ID, MEAN-SENTIMENT-RATING, STANDARD DEVIATION, and RAW-SENTIMENT-RATINGS <br />
    DESCRIPTION: Sentiment ratings from a minimum of 20 independent human raters (all pre-screened, trained, and quality checked for optimal inter-rater reliability).

8. movieReviewSnippets_GroundTruth.txt <br />
	FORMAT: the file is tab delimited with ID, MEAN-SENTIMENT-RATING, and TEXT-SNIPPET <br />
    DESCRIPTION: includes 10,605 sentence-level snippets from rotten.tomatoes.com. The snippets were derived from an original set of 2000 movie reviews (1000 positive and 1000 negative) in Pang & Lee (2004); we used the NLTK tokenizer to segment the reviews into sentence phrases, and added sentiment intensity ratings. The ID and MEAN-SENTIMENT-RATING correspond to the raw sentiment rating data provided in 'movieReviewSnippets_anonDataRatings.txt' (described below).

9. movieReviewSnippets_anonDataRatings.txt <br />
	FORMAT: the file is tab delimited with ID, MEAN-SENTIMENT-RATING, STANDARD DEVIATION, and RAW-SENTIMENT-RATINGS <br />
    DESCRIPTION: Sentiment ratings from a minimum of 20 independent human raters (all pre-screened, trained, and quality checked for optimal inter-rater reliability).

10. amazonReviewSnippets_GroundTruth.txt <br />
	 FORMAT: the file is tab delimited with ID, MEAN-SENTIMENT-RATING, and TEXT-SNIPPET <br />
     DESCRIPTION: includes 3,708 sentence-level snippets from 309 customer reviews on 5 different products. The reviews were originally used in Hu & Liu (2004); we added sentiment intensity ratings. The ID and MEAN-SENTIMENT-RATING correspond to the raw sentiment rating data provided in 'amazonReviewSnippets_anonDataRatings.txt' (described below).

11. amazonReviewSnippets_anonDataRatings.txt <br />
	 FORMAT: the file is tab delimited with ID, MEAN-SENTIMENT-RATING, STANDARD DEVIATION, and RAW-SENTIMENT-RATINGS <br />
     DESCRIPTION: Sentiment ratings from a minimum of 20 independent human raters (all pre-screened, trained, and quality checked for optimal inter-rater reliability).

12. Comp.Social website with more papers/research: [Comp.Social](http://comp.social.gatech.edu/papers/)
	 
13. vader_sentiment_comparison_online_weblink <br />
     A short-cut hyperlinked to the online (web-based) sentiment comparison using a "light" version of VADER. http://www.socialai.gatech.edu/apps/sentiment.html .


## Java Code EXAMPLE:

```
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

    for (String sentence : sentences) {
        System.out.println(sentence);
        final SentimentPolarities sentimentPolarities =
			SentimentAnalyzer.getScoresFor(sentence);
        System.out.println(sentimentPolarities);
    }
}
```

### Online (web-based) Sentiment Comparison using VADER

http://www.socialai.gatech.edu/apps/sentiment.html .
