package com.speech.grammarmatch

import java.text.NumberFormat
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Utilities for formatting and generally working with grammar results
 * User: Cliff
 * Date: 4/6/2016
 * Time: 10:29 AM
 */
object GrammarUtils {


  def time[R](block: => R): R = {
    val t0 = System.nanoTime()
    val result = block    // call-by-name
    val t1 = System.nanoTime()
    println("Elapsed time: " + (t1 - t0) + "ns")
    result
  }

  def wordCombinations( sentence: String, slideSize: Int ) = {
    val words = sentence.split(" ")
    words.sliding(slideSize).foldLeft( Vector[String]() ) { (vs,v) =>
      vs :+ v.mkString(" ")
    }
  }

  /**
   * format a date from the date grammar (yyyyMMdd format) into MMM dd yyyy format
   * @param inDate - String in yyyyMMdd format
   * @return a String in MMM dd yyyy format
   */
  def formatDate( inDate: String ) : String = {
    val dateFormat = DateTimeFormatter.ofPattern("yyyyMMdd")
    val outFormat = DateTimeFormatter.ofPattern("MMM dd yyyy")
    outFormat.format( dateFormat.parse( inDate ))
  }

  def formatTime( time: String ) : String = {
    val inTime = time.toUpperCase
    val timeFormat = DateTimeFormatter.ofPattern("hhmma")
    val outFormat = DateTimeFormatter.ofPattern("h:mma")
    outFormat.format( timeFormat.parse(inTime) )
  }

  def formatCurrency( currency:String ) : String = {
    val currencyFormatter = NumberFormat.getCurrencyInstance( Locale.US )
    currencyFormatter.format( currency.toDouble )
  }



  def grammarFormatter( grammarType:GrammarType, sentence:String ) = grammarType match {
    case GrammarType.CURRENCY => formatCurrency( sentence )
    case GrammarType.DATE => formatDate( sentence )
    case GrammarType.NUMBER => sentence
    case GrammarType.ORDINAL => sentence
    case GrammarType.TIME => formatTime( sentence )
  }


  def grammarMaxMin( grammarType:GrammarType ) = grammarType match {
    //returns (MAX,MIN) word counts for the different grammars
    case GrammarType.CURRENCY => ( 15,2 )
    case GrammarType.DATE => ( 8,2 )
    case GrammarType.NUMBER => ( 9,1 )
    case GrammarType.ORDINAL => ( 9,1 )
    case GrammarType.TIME => ( 4,2 )
  }

}
