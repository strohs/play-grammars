package controllers

import java.text.NumberFormat
import java.time.format.DateTimeFormatter
import java.util.Locale
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
class Grammar @Inject() ( gmService: GrammarMatcherService ) extends Controller {


  def date( sentence: String ) = Action.async {
    val futureMatch: Future[Either[String,String]] = scala.concurrent.Future { matchGrammar( GrammarType.DATE, sentence) }
    futureMatch map {
      case Left(m) => Ok( m )
      case Right(m) => Ok( formatDate( m ) )
    }
  }


  def currency( sentence: String ) = Action.async {
    val currencyFormatter = NumberFormat.getCurrencyInstance( Locale.US )
    val futureMatch: Future[Either[String,String]] = scala.concurrent.Future { matchGrammar( GrammarType.CURRENCY, sentence) }
    futureMatch map {
      case Left(m) => Ok( m )
      case Right(m) => Ok( currencyFormatter.format( m.toDouble ) )
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

  def show() = Action {
    NotImplemented
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


}
