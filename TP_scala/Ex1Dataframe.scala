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

/**
 * Introduction to Spark : Practical work
 * Part 2 : Data Frames
 *
 * @author Mouna LABIADH & Onss BOUSBIH
 * @version 1.0
 */
object Ex1Dataframe {
  def main(args: Array[String]): Unit = {
    println("hello")
    var spark: SparkSession = null
    try {
      
      /**
       * SparkSession follows builder factory design pattern. The below is the code to create a spark session 
       * and set the URL master to local
       */
      spark = SparkSession.builder().master("local").appName("Flickr using dataframes").getOrCreate()

      //provide a schema for the Flickr pictures data by providing the name of the filed and its type 
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

      /**
       * The text file flickrSample contains un-headed dataset
       * In order to load it and rename the column names we have to specify the schema that we're following 
       */
      val originalFlickrMeta = spark.sqlContext.read
        .format("csv")
        .option("delimiter", "\t")
        .option("header", "false")
        .schema(customSchemaFlickrMeta)
        .load("flickrSample.txt")
      
      // print the Data frame that we have just created
      originalFlickrMeta.show()
        
      // Register the DataFrame as a SQL temporary view
      originalFlickrMeta.createOrReplaceTempView("FlickrDF")

      println("QUESTION 1 :")
      //SQL statements can be run by using the sql methods provided by Spark
      //Run a SELECT sql command to get the photo_id, the longitude/latitude and the license of all data
      val resultsDF = spark.sql("SELECT photo_id, longitude, latitude, license from FlickrDF")
      resultsDF.show(5)
     
      println("QUESTION 2 & 3 & 4:")
      // Select the interesting pictures
      val FilteredFlickrDF = spark.sql("SELECT * FROM FlickrDF WHERE license IS NOT NULL AND longitude != -1.0 and latitude != -1.0")
      FilteredFlickrDF.explain()
      FilteredFlickrDF.show()
      
      
     println("QUESTION 5 :")
     // load the license file whuch already contains a header
     val licenseFlickrMeta = spark.sqlContext.read
        .format("csv")
        .option("delimiter", "\t")
        .option("header", "true")
        .load("FlickrLicense.txt")
        
     // print the content of licenses' dataframe
     licenseFlickrMeta.show()
     
     // Register the DataFrame as a SQL temporary view
      licenseFlickrMeta.createOrReplaceTempView("LicenseFlickrDF")
      
     // Select the non derivative and interesting licenses
     val joinLicenseFlickrDF = spark.sql("SELECT f.* FROM FlickrDF f LEFT JOIN LicenseFlickrDF lf ON f.license = lf.NAME WHERE license IS NOT NULL AND NonDerivative = 1")
     
     /** Hive provides an EXPLAIN command that shows the execution plan for a query. 
      *  The description of the stages shows a sequence of operators with the metadata associated with these operators.
      *  The metadata comprises in our case the select expression which contains the filter and the CSV file scan operators
      */
     joinLicenseFlickrDF.explain()
     joinLicenseFlickrDF.show()
     
     println("QUESTION 6 :")
     /**
      * dataframe.cache() enables us to cache a dataframe using an in-memory columnar format. 
      * By using this, Spark SQL will scan only the required columns and tune compression to minimize memory usage and Garbage Collector pressure   
      * By analyzing the output of dataframe explain methods before and after the cache operation, we can notice that in the cached version
      * the execution plan access directly the in memory table without re-executing the join operations
      */
     FilteredFlickrDF.cache()
     FilteredFlickrDF.explain()
      
     //write a dataframe to a CSV file
     joinLicenseFlickrDF.write.format("com.databricks.spark.csv").option("header", "true").save("nonDerivativeInterestingFlickr.csv")

     
    } catch {
      case e: Exception => throw e
    } finally {
      spark.stop()
    }
    println("done")
  }
}