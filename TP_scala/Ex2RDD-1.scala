package uga.tpspark.flickr

import scala.collection.mutable
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

/**
 * Practical Lab work
 * Part 3 : Processing data using RDDs
 *
 * @author Trimech Abdellatif & Bazarbacha Hassen 
 * @version 1.0
 */


object Ex2RDD {
  def main(args: Array[String]): Unit = {
    println("hello")
    var spark: SparkSession = null
    try {
      /*
      *SparkSession affords a single entry point inorder to perform many operations and it includes a builder factory design pattern so that we can create 
      *a new sesion.
      *We set our master to local so that it will run locally within our single JVM that run this code
      */
      spark = SparkSession.builder().master("local").appName("Flickr using dataframes").getOrCreate()
      val originalFlickrMeta: RDD[String] = spark.sparkContext.textFile("flickrSample.txt")
      
    //************************ Partie II *************************
    //Question 1
    /*
    *Displaying the 5 lines of our RDD and the number of elements in it.
    */
      println("\n \n Question 1 : \n");
      originalFlickrMeta.take(5).foreach(println)
      println("The number of elements",originalFlickrMeta.count())
    
    //Question 2
    /*
    *To asnwer the second question, we proceeded as follow : First of all we mapped each line and splitted each field in order to load the picture Data 
    *set as RDD[Picture]  into the a Picture class constructor. Once finished from loading, the next step was to filter our RDD to keep only interesting pictures 
    *with valid country and tag. Eventually we displayed our results.
    */
      println("\n \n Question 2 : \n");
      val Q2: RDD[Picture] = originalFlickrMeta.map( line => new Picture(line.split("\t")))
      val toto = Q2.filter(line => line.hasValidCountry).filter(line => line.hasTags)
      println("\n \n Question 2 - non filtered : \n");
      Q2.take(50).foreach{println}
      println("\n \n Question 2 - filtered : \n");
      toto.take(50).foreach{println}
     
    //Question 3
    /*We were able to group the images by country thanks to the groupby() command since it splitted the data into groups based their countries
     *and then output the following couple (The Country name, a CompactBuffer that contains the pictures taken in that country).
     *The type of our RDD is a ShuffledRDD because the applied command (grouby), after splitting the date, it combines the output using a 
     *common key for each partition before executing the shuffling task.
    */
      println("\n \n Question 3 : \n");
      val Q3 = toto.groupBy(c => c)
      Q3.take(50).foreach{println}

      print("groupedby: ",Q3.count(),"titi", Q2.count())
      //Q3._jrdd.classTag().runtimeClass()
      //org.apache.spark.rdd.RDD[(String, Int)]
    //Question 4
      println("\n \n Question 4 : \n");
      
      
      
      
      
          //Question 4
      
      
      
          //Question 5
          /*
          *The goal of the fifth question is to build an RDD[(Country, Map[String, Int])] where each user tag has to be associated to its number of occurences.
          *We thought about using the groupby() method since it enables us to construct a Key/Value couple and then proceeding to mapping this couple, keeping 
          *in mind that the key does not change. We did this thanks to the mapValue method and we managed to extract the size of our input using size().
          *
          */
      println("\n \n Question 5 : \n");
    
      val Q5 = Q2.map(line => (line.c, line.userTags.map(ut => (ut,1)).toMap)).reduceByKey(_ ++ _)
      print("without groupBy ",Q5.count(),"groupBy ",Q5.groupBy(c => c).count() )
      Q5.take(50).foreach{println}
      
    } catch {
            case e: Exception => throw e
    } finally {
      spark.stop()
    }
    println("done")
  }
}

