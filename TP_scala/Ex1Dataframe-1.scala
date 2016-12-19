package uga.tpspark.flickr

import org.apache.spark.sql.types.IntegerType
import org.apache.spark.sql.types.DateType
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.types.ByteType
import org.apache.spark.sql.types.StructField
import org.apache.spark.sql.types.FloatType
import org.apache.spark.sql.types.StringType
import org.apache.spark.sql.types.LongType
import java.net.URLDecoder
import org.apache.spark.sql.Row
import org.apache.spark.sql.Encoders
import org.apache.ivy.core.module.descriptor.License

/**
 * Practical Lab work
 * Part 2 : Processing data using the DataFrame API
 *
 * @author Trimech Abdellatif & Bazarbacha Hassen 
 * @version 1.0
 */
object Ex1Dataframe {
  def main(args: Array[String]): Unit = {
    println("hello")
    var spark: SparkSession = null
    try {
      spark = SparkSession.builder().master("local").appName("Flickr using dataframes").getOrCreate()

      //   * Photo/video identifier
      //   * User NSID
      //   * User nickname
      //   * Date taken
      //   * Date uploaded
      //   * Capture device
      //   * Title
      //   * Description
      //   * User tags (comma-separated)
      //   * Machine tags (comma-separated)
      //   * Longitude
      //   * Latitude
      //   * Accuracy
      //   * Photo/video page URL
      //   * Photo/video download URL
      //   * License name
      //   * License URL
      //   * Photo/video server identifier
      //   * Photo/video farm identifier
      //   * Photo/video secret
      //   * Photo/video secret original
      //   * Photo/video extension original
      //   * Photos/video marker (0 = photo, 1 = video)

      val customSchemaFlickrMeta = StructType(Array(
        StructField("photo_id", LongType, true),
        StructField("user_id", StringType, true),
        StructField("user_nickname", StringType, true),
        StructField("date_taken", StringType, true),
        StructField("date_uploaded", StringType, true),
        StructField("device", StringType, true),
        StructField("title", StringType, true),
        StructField("description", StringType, true),
        StructField("user_tags", StringType, true),
        StructField("machine_tags", StringType, true),
        StructField("longitude", FloatType, false),
        StructField("latitude", FloatType, false),
        StructField("accuracy", StringType, true),
        StructField("url", StringType, true),
        StructField("download_url", StringType, true),
        StructField("license", StringType, true),
        StructField("license_url", StringType, true),
        StructField("server_id", StringType, true),
        StructField("farm_id", StringType, true),
        StructField("secret", StringType, true),
        StructField("secret_original", StringType, true),
        StructField("extension_original", StringType, true),
        StructField("marker", ByteType, true)))


      /*
      * Since our text file flickrSample does not contain a header, Spark won't be able to use it automatically and discovers the type of each column by 
      looking at the data. Therefore  we will manually define the schema of the data.
      */

      val originalFlickrMeta = spark.sqlContext.read
        .format("csv")
        .option("delimiter", "\t")
        .option("header", "false")
        .schema(customSchemaFlickrMeta)
        .load("flickrSample.txt")
    //************************ Partie I *************************
     //Question 1
     /*
     *Our goal here is to select the fields containing user_id,latitude,longitude and license, to do so the DataFrame originalFlickrMeta is 
     *used to create a temporary view that will be used in a select SQL statements.
     */
    println("\n \n Question 1 : \n");
    originalFlickrMeta.createOrReplaceTempView("Flickr")
    val Q1 = spark.sql("select user_id,latitude,longitude,license from Flickr");
    Q1.show(5);
    //Question 2
    /*
    *In order to select only the interesting pictures we create a new temporary view and we apply the filter method to get the defined features 
    *of an interesting picture, after that we display the content of the DataFrame using the command show ()
    */
    println("\n \n Question 2 : \n");
    originalFlickrMeta.createOrReplaceTempView("FilteredFlickr")
    val latitude = originalFlickrMeta("latitude")
    val Q2 = originalFlickrMeta.filter("latitude != -1.0").filter("longitude != -1.0").filter("license != NULL");
    Q2.show()
    //Question 3
    // We use the command explain() to display the execution plan used by Spark to compute the content of our DataFrame
    println("\n \n Question 3 : \n");
    originalFlickrMeta.explain();
    //Question 4
    println("\n \n Question 4 : \n");
    //%%%%%%%%%%%%%%%%%17
    //Question 5
    /*
    *The first step to answer this question is to load the FlickrLicense file (That has already its own header) and then to save the DataFrame as an SQL 
    *temporary view. The content of License could be shown thanks to the command show(). We applied an SQL query to execute a join operation in order to
    *identify pictures that are both interesting and NonDerivative at the same time. Results are, as always shown thanks to show().
    */
    println("\n \n Question 5 : \n");

        val originalFlickrLicense = spark.sqlContext.read
        .format("csv")
        .option("delimiter", "\t")
        .option("header", "True")
        .load("FlickrLicense.txt")
        
        originalFlickrLicense.createOrReplaceTempView("FlickrLicense")
        Q2.createOrReplaceTempView("Q")
        originalFlickrLicense.show()
        val Q5 = spark.sql("select * from Q  b left join FlickrLicense fi where fi.Name = b.license and fi.NonDerivative = 1")
        Q5.show()
    //Question 6 
    /*
    *Using the cache () method  we can store data in cache and then retrieve it later for a future use without having to re-do all the tasks done before in order
    *to get again an interesting picture for example, this is done by simply scanning only required columns which would result minimizing memory usage.
    *In deed, we can easily spot the difference between the two versions : with & without cache; the execution tasks have a direct access in the memory table 
    *making us gaining time and memory since we do not have to re-executing the join operations.
    */
     println("\n \n Question 6 : \n");
        Q2.cache()  
        Q2.show()
        
        
    //Question 7 
    //Saving our DataFrame as a CSV file.
     println("\n \n Question 7 : \n");
        Q5.write.format("com.databricks.spark.csv").option("header","true").save("Question77.csv")
    } catch {
      case e: Exception => throw e
    } finally {
      spark.stop()
    }
    
    
    
    
    println("done")
  }
}