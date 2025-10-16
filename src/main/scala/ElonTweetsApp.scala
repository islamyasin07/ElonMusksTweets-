import org.apache.spark.sql.SparkSession
import scala.io.StdIn.readLine

object ElonTweetsApp {
  def main(args: Array[String]): Unit = {

    System.setProperty("hadoop.home.dir", "C:\\hadoop")
    System.setProperty("HADOOP_HOME", "C:\\hadoop")
    System.setProperty("hadoop.native.lib", "false")

    // Start Spark
    val spark = SparkSession.builder()
      .appName("ElonTweetsProfessional")
      .master("local[*]")
      .config("spark.executor.memory", "8g")
      .config("spark.network.timeout", "300s")
      .config("spark.executor.heartbeatInterval", "60s")
      .getOrCreate()

    // Ask for keywords
    println("Enter keywords to analyze (comma-separated): ")
    val userInput = readLine()
    val keywords = userInput.split(",").map(_.trim.toLowerCase).filter(_.nonEmpty).toList

    if (keywords.isEmpty) {
      println("No keywords entered..")
      spark.stop()
      System.exit(0)
    }

    val inputPath  = if (args.nonEmpty) args(0) else "elonmusk_tweets.csv"
    val outputPath = if (args.length > 1) args(1) else "output"

    // Load tweets
    val tweets = DataLoader.loadTweets(spark, inputPath)

    val (percent1, percent2) = KeywordAnalyzer.keywordStats(tweets, keywords)
    val (mean, std)          = KeywordAnalyzer.lengthStats(tweets)
    val dailyCounts          = KeywordAnalyzer.keywordDistribution(tweets, keywords)

    val keywordLengths = KeywordAnalyzer.lengthStatsByKeyword(tweets, keywords)

    println("\n Average Tweet Length per Keyword:")
    keywordLengths.collect().foreach {
      case (kw, meanVal, stdVal) =>
        println(f"  - $kw%-10s → Mean: ${meanVal}%.2f words | Std: ${stdVal}%.2f")
    }

    ReportGenerator.saveReport(outputPath, percent1, percent2, mean, std, dailyCounts)

    println(s"\n Report generated at: $outputPath\\report.html")

    spark.stop()
  }
}
