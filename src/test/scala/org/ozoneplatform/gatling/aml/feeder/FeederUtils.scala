package org.ozoneplatform.gatling.aml.feeder

import scala.sys.process._

object FeederUtils {
	  val DICTIONARY_FILE_PATH:String = "dictionary.txt"
    val CORPUS_FILE_PATH:String = "wordDistro.txt"

    val getBaseUrl: String = {
        val url = System.getProperty("baseUrl");
        if(url != null) {
            if(url.endsWith("/")) url else { url + "/" }
        } else "https://localhost:8443/marketplace/"
    }

    val getAdminCount:Integer = {
        val count = Integer.getInteger("adminCount")
        if(count != null) count else 2
    }

    val getUserCount:Integer = {
        val count = Integer.getInteger("userCount")
        if(count != null) count else 2
    }

    val getItemCount:Integer = {
        val count = Integer.getInteger("itemCount")
        if(count != null) count else 1
    }

    val getTagCount:Integer = {
        val count = Integer.getInteger("tagCount")
        if(count != null) count else 1000
    }

    val getActionPercentage:Integer = {
        val count = Integer.getInteger("actionPercentage")
        if(count != null) count else 5
    }

    val getRampPeriod:Integer = {
        val count = Integer.getInteger("rampPeriod")
        if(count != null) count else 10
    }

    val getScenarioUserCount:Integer = {
        val count = Integer.getInteger("scenarioUserCount")
        if(count != null) count else 10
    }

    val getDictionaryWords:Array[String] = {
        scala.io.Source.fromFile(DICTIONARY_FILE_PATH, "utf-8").getLines.toArray
    }

    val getTextCorpus:String = {
        scala.io.Source.fromFile(CORPUS_FILE_PATH, "utf-8").getLines.mkString
    }

    val getWordsDistro:Array[String] = {
      scala.io.Source.fromFile(CORPUS_FILE_PATH, "utf-8").getLines.toArray
    }

    def getStoreItemsAsJson:String = {
        Seq("curl", "-k", "-XGET", "-utestAdmin1:password", "http://10.40.1.244:8080/marketplace/api/profile").!!
    }

    def getStoreProfilesAsJsonString:String = {
        Seq("curl", "-k", "-XGET", "-utestAdmin1:password", "http://10.40.1.244:8080/marketplace/api/profile").!!
    }
}
