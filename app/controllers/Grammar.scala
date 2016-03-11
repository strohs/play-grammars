package controllers

import java.text.NumberFormat
import java.util.Locale
import javax.inject.Inject

import com.speech.grammarmatch.{JsgfGrammarMatcher, GrammarMatcherService, GrammarType}
import play.api.Logger
import play.api.mvc.{Action, Controller}

import scala.collection.mutable.{ArrayBuffer, ListBuffer}


/**
 * Created with IntelliJ IDEA.
 * User: Cliff
 * Date: 2/24/2016
 * Time: 1:51 PM
 */
class Grammar @Inject() ( gmService: GrammarMatcherService ) extends Controller {

  def date( sentence: String ) = Action {
    matchGrammar( GrammarType.DATE, sentence ) match {
      case Left(m) => Ok( sentence + " did not match" )
      case Right(m) => Ok("Date: " + formatGrammarDate( m ) )
    }

  }


  def currency( sentence: String ) = Action {
    val currencyFormatter = NumberFormat.getCurrencyInstance( Locale.US )
    matchGrammar( GrammarType.CURRENCY, sentence ) match {
      case Left(m) => Ok( sentence + " did not match" )
      case Right(m) => Ok("Currency: " + currencyFormatter.format( m.toDouble ) )
    }
  }

  def time( sentence: String ) = Action {
    matchGrammar( GrammarType.TIME, sentence ) match {
      case Left(m) => Ok( sentence + " did not match" )
      case Right(m) => Ok("Time: " + m )
    }
  }

  def number( sentence: String ) = Action {
    matchGrammar( GrammarType.NUMBER, sentence ) match {
      case Left(m) => Ok( sentence + " did not match" )
      case Right(m) => Ok("Number: " + m )
    }
  }

  def ordinal( sentence: String ) = Action {
    matchGrammar( GrammarType.ORDINAL, sentence ) match {
      case Left(m) => Ok( sentence + " did not match" )
      case Right(m) => Ok("Ordinal: " + m )
    }
  }

  def show() = Action {
    NotImplemented
  }

  //Helper methods
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
  def formatGrammarDate( inDate: String ) : String = {
    val dateFormat = new java.text.SimpleDateFormat("yyyyMMdd")
    val outFormat = new java.text.SimpleDateFormat("MMM dd yyyy")
    outFormat.format( dateFormat.parse( inDate ))
  }


}
