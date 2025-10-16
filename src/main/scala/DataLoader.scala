import org.apache.spark.sql.SparkSession
import org.apache.spark.rdd.RDD

object DataLoader {
  def loadTweets(spark: SparkSession, path: String): RDD[(String, String)] = {
    val df = spark.read
      .option("header", "true")        
      .option("inferSchema", "false")  
      .option("quote", "\"")           
      .csv(path)

    df.select("created_at", "text")
      .rdd
      .map(row => (
        Option(row.getString(0)).getOrElse("").trim,
        Option(row.getString(1)).getOrElse("").trim.toLowerCase
      ))
      .filter { case (date, text) => date.nonEmpty && text.nonEmpty }
      .cache()
  }
}
