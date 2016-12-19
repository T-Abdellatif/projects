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
import org.apache.spark.rdd.RDD

import uga.tpspark.flickr.Picture

/**
 * Introduction to Spark : Practical work
 * Part 3 : RDD
 *
 * @author Mouna LABIADH & Onss BOUSBIH
 * @version 1.0
 */
object Ex2RDD {
  def main(args: Array[String]): Unit = {
    println("hello")
    var spark: SparkSession = null
    try {
      
      /**
       * SparkSession follows builder factory design pattern. The below is the code to create a spark session 
       * and set the URL master to local
       */
      spark = SparkSession.builder().master("local").appName("Flickr using dataframes").getOrCreate()
      val originalFlickrMeta: RDD[String] = spark.sparkContext.textFile("flickrSample.txt")
      
      /**
       * Question 1
       * Printing the elements of RDD[String] and the number of lines
       */
      println("QUESTION 1 ")
      originalFlickrMeta.take(5).foreach {println}
      println(originalFlickrMeta.count())
      
      /**
       * Question 2
       * Loading the pictures dataset as RDD[Picture] by mapping each line, after splitting out the different fields, into the Picture class constructor
       * and keeping only interesting pictures with valid country and tags by filtering the RDD
       */
      println("QUESTION 2 ")
      val pictureFlickrMeta: RDD[Picture] = originalFlickrMeta.map { line => new Picture(line.split("\t")) }
      val pictureFlickrFilter = pictureFlickrMeta.filter(line => line.hasValidCountry).filter(line => line.hasTags)
      pictureFlickrFilter.take(20).foreach {println}
      
      
      
      /**
       * Question 3
       * Grouping the pictures present in the previous constructed RDD by their corresponding countries using the GroupBy method
       * This command will output the couple (Country name, CompactBuffer containing the pictures taken in this country)
       * Also, the GroupBy method enables us to combine the output with a common key on each partition before shuffling the data. 
       * This is why as an output we obtain a ShuffledRDD
       */
      println("Question 3 ")
      
      val pictureFlickrCountry = pictureFlickrFilter.groupBy(picture => picture.c)
      pictureFlickrCountry.take(50).foreach{println}
      
      
      println(" Type of the output ",pictureFlickrCountry.getClass)
      println("Testing: Groupedby data : ",pictureFlickrCountry.count()," | Initial data : ", pictureFlickrMeta.count())
      
      
      
      /**
       * Question 4
       * Flattening the list of usertags of the pictures taken in the same country in order to make a big list containing all these tags at the same level
       * line_1 : the country
       * line_2 : the CompactBuffer of pictures taken in this country       
       */
      
      println("Question 4 ")
      val pictureFlickrFlat = pictureFlickrCountry.map(line => (line._1 , line._2.flatMap { picture => picture.userTags })) 
      pictureFlickrFlat.take(50).foreach(println)
      
      
      
      /**
       * Question 5
       * Build a RDD[(Country, Map[String, Int])] where each user tag is associated to its number of occurences
       * This is done by building a Key/Value couple using the groupBy method
       * and then mapping these Key/Values without changing the key part by using the mapValue method and extracting the size of these values (frequency)
       * line_1 : the country
       * line_2 : the flattened list of user tags of the pictures taken in this country 
       */
      
      println("Question 5 ")
      val x : RDD[(Country, Map[String, Int])]= pictureFlickrFlat.map (line => (line._1, line._2.groupBy(identity).mapValues(_.size)))
      x.foreach(println)
      
      
      /**
       * Question 6
       * Proposing a different parallelizable version of the same work as before
       */
      
      println("Question 6 ")
      
    } catch {
      case e: Exception => throw e
    } finally {
      spark.stop()
    }
    println("done")
  }
}