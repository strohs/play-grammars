package controllers

import java.text.NumberFormat
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.regex.{Pattern, Matcher}
import javax.inject.Inject

import com.speech.grammarmatch.{JsgfGrammarMatcher, GrammarMatcherService, GrammarType}
import play.api.Logger
import play.api.mvc.{Action, Controller}

import scala.collection.mutable.{ArrayBuffer, ListBuffer}

import play.api.libs.concurrent.Execution.Implicits.defaultContext

import scala.concurrent.Future
import scala.util.Either


/**
 * Created with IntelliJ IDEA.
 * User: Cliff
 * Date: 2/24/2016
 * Time: 1:51 PM
 */
class GrammarController @Inject() ( gmService: GrammarMatcherService ) extends Controller {


  def date( sentence: String ) = Action.async {
    val futureMatch: Future[Either[String,String]] = scala.concurrent.Future { matchGrammar( GrammarType.DATE, sentence) }
    futureMatch map {
      case Left(m) => Ok( m )
      case Right(m) => Ok( formatDate( m ) )
    }
  }


  def currency( sentence: String ) = Action.async {
    val futureMatch: Future[Either[String,String]] = scala.concurrent.Future { matchGrammar( GrammarType.CURRENCY, sentence) }
    futureMatch map {
      case Left(m) => Ok( m )
      case Right(m) => Ok( formatCurrency( m ) )
    }
  }

  def time( sentence: String ) = Action.async {
    val futureMatch: Future[Either[String,String]] = scala.concurrent.Future { matchGrammar( GrammarType.TIME, sentence) }
    futureMatch map {
      case Left(m) => Ok( m )
      case Right(m) => Ok( formatTime( m ) )
    }
  }

  def number( sentence: String ) = Action.async {
    val futureMatch: Future[Either[String,String]] = scala.concurrent.Future { matchGrammar( GrammarType.NUMBER, sentence) }
    futureMatch map {
      case Left(m) => Ok( m )
      case Right(m) => Ok( m )
    }
  }

  def ordinal( sentence: String ) = Action.async {
    val futureMatch: Future[Either[String,String]] = scala.concurrent.Future { matchGrammar( GrammarType.ORDINAL, sentence) }
    futureMatch map {
      case Left(m) => Ok( m )
      case Right(m) => Ok( m )
    }
  }

  def matchSentence( sentence:String ) = Action.async {
    val futureMatch: Future[String] = scala.concurrent.Future {
      grammarChain.foldLeft(sentence) { (sent,grammar) =>
        findAndReplaceMatches( grammar, sent )
      }
    }
    futureMatch map { s =>
      Ok(s)
    }
  }

  //Helper methods
//  def nameToGrammarType( name: String) : Option[GrammarType] = {
//    name.toLowerCase match {
//      case "date" => Some( GrammarType.DATE )
//      case "currency" => Some( GrammarType.CURRENCY )
//      case "number" => Some( GrammarType.NUMBER )
//      case "ordinal" => Some( GrammarType.ORDINAL )
//      case "time" => Some( GrammarType.TIME )
//      case _ => None
//    }
//
//  }


  def matchGrammar( grammarType: GrammarType, sentence: String ) : Either[String,String] = {
    val jsgfRecognizer = new JsgfGrammarMatcher
    //could block here waiting for a grammar recognizer
    val recognizer = gmService.get()
    try {
      val matchRes = jsgfRecognizer.recognize( recognizer, grammarType, sentence ) match {
        case "NOMATCH" => Left("NOMATCH")
        case m: String => Right(m)
      }
      matchRes
    } catch {
      case ex: Exception => {
        Logger.error( ex.getMessage )
        Left("NOMATCH")
      }
    } finally {
      gmService.put( recognizer )
    }
  }

  /**
   * format a date from the date grammar (yyyyMMdd format) into MMM dd yyyy format
   * @param inDate
   * @return
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

  //The order that grammars should be run
  val grammarChain = List( GrammarType.CURRENCY, GrammarType.DATE, GrammarType.TIME, GrammarType.ORDINAL, GrammarType.NUMBER )

  def grammarFormatter( grammarType:GrammarType, sentence:String ) = grammarType match {
    case GrammarType.CURRENCY => formatCurrency( sentence )
    case GrammarType.DATE => formatDate( sentence )
    case GrammarType.NUMBER => sentence
    case GrammarType.ORDINAL => sentence
    case GrammarType.TIME => formatTime( sentence )
  }


  def grammarMaxMin( grammarType:GrammarType ) = grammarType match {
    //returns (MAX,MIN) word counts for the different grammars
    case GrammarType.CURRENCY => ( 14,1 )
    case GrammarType.DATE => ( 8,2 )
    case GrammarType.NUMBER => ( 9,1 )
    case GrammarType.ORDINAL => ( 9,1 )
    case GrammarType.TIME => ( 4,2 )
  }

  def wordCombinations( sentence: String, slideSize: Int ) = {
    val words = sentence.split(" ")
    words.sliding(slideSize).foldLeft( Vector[String]() ) { (vs,v) =>
      vs :+ v.mkString(" ")
    }
  }


  def findFirstAndReplaceAll( phrases:Vector[String], sentence:String, grammar: GrammarType ): Option[String] = {
    def replaceAll( sentence:String, target:String, replacement:String ) = {
      var newSentence = sentence.replace( target,replacement )
      var oldSentence = sentence
      while ( newSentence != oldSentence ) {
        oldSentence = newSentence
        newSentence = newSentence.replace( target, replacement )

      }
      newSentence
    }

    for ( phrase <- phrases ) {
      matchGrammar( grammar, phrase ) match {
        case Right(s) =>
          return Some( replaceAll( sentence, phrase, grammarFormatter( grammar,s )))
        case _ =>
      }
    }
    None
  }

  def findAndReplaceMatches( grammarType:GrammarType, sentence: String ) : String = {
    var newSentence = sentence

    val (gramMax,gramMin) = grammarMaxMin( grammarType )

    for ( slide <- gramMax to gramMin by -1 ) {
      var phrases = wordCombinations( newSentence, slide )
      var replacementSentence = findFirstAndReplaceAll( phrases, newSentence, grammarType )

      while ( replacementSentence.isDefined ) {
        newSentence = replacementSentence.get
        phrases = wordCombinations( newSentence , slide )
        replacementSentence = findFirstAndReplaceAll( phrases, newSentence, grammarType )
      }
    }
    newSentence
  }
}
