import org.apache.spark.rdd.RDD

object KeywordAnalyzer {
  def keywordDistribution(
      tweets: RDD[(String, String)],
      keywords: List[String]
  ): RDD[(String, String, Int)] = {
    val lowerKeywords = keywords.map(_.toLowerCase)

    tweets.flatMap { case (date, text) =>
      val lowerText = text.toLowerCase
      lowerKeywords
        .filter(lowerText.contains)
        .map(k => ((k, date), 1))
    }
    .reduceByKey(_ + _)
    .map { case ((k, d), c) => (k, d, c) }
  }

  def keywordStats(
      tweets: RDD[(String, String)],
      keywords: List[String]
  ): (Double, Double) = {
    val lowerKeywords = keywords.map(_.toLowerCase)
    val total = tweets.count()

    val one = tweets.filter { case (_, t) =>
      val txt = t.toLowerCase
      lowerKeywords.exists(txt.contains)
    }.count()

    val two = tweets.filter { case (_, t) =>
      val txt = t.toLowerCase
      lowerKeywords.count(txt.contains) == 2
    }.count()

    (one.toDouble / total * 100, two.toDouble / total * 100)
  }

  def lengthStats(tweets: RDD[(String, String)]): (Double, Double) = {
    val lengths = tweets.map { case (_, text) =>
      text.split("\\s+").length.toDouble
    }

    val mean = lengths.mean()
    val variance = lengths.map(l => math.pow(l - mean, 2)).mean()
    val std = math.sqrt(variance)

    (mean, std)
  }

  def lengthStatsByKeyword(
      tweets: RDD[(String, String)],
      keywords: List[String]
  ): RDD[(String, Double, Double)] = {
    val lowerKeywords = keywords.map(_.toLowerCase)
    val sc = tweets.sparkContext

    val stats = lowerKeywords.map { keyword =>
      val filteredTweets = tweets.filter { case (_, text) =>
        text.toLowerCase.contains(keyword)
      }

      val lengths = filteredTweets.map { case (_, text) =>
        text.split("\\s+").length.toDouble
      }

      val mean = if (lengths.isEmpty()) 0.0 else lengths.mean()
      val std =
        if (lengths.isEmpty()) 0.0
        else {
          val variance = lengths.map(l => math.pow(l - mean, 2)).mean()
          math.sqrt(variance)
        }

      (keyword, mean, std)
    }

    sc.parallelize(stats)
  }
}
