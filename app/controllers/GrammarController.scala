package controllers

import java.util.regex.{Pattern, Matcher}
import javax.inject.Inject

import com.speech.grammarmatch.{GrammarUtils, JsgfGrammarMatcher, GrammarMatcherService, GrammarType}
import models.{Sentence, GrammarMatchInfo}
import play.api.Logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._

import play.api.data._
import play.api.data.Forms._

import scala.collection.mutable.{ArrayBuffer, ListBuffer}

import play.api.libs.concurrent.Execution.Implicits.defaultContext

import scala.concurrent.Future
import scala.util.Either


/**
 * Controller for matching a string of words against a 'chain' of speech grammars OR for matching a string of words
 * against a specific speech recognition grammar
 * User: Cliff
 * Date: 2/24/2016
 * Time: 1:51 PM
 */
class GrammarController @Inject() ( gmService: GrammarMatcherService, val messagesApi: MessagesApi ) extends Controller with I18nSupport {

  //GrammarMatch Form
  val gmForm = Form (
    mapping(
      "sentence" -> nonEmptyText
    )(Sentence.apply)(Sentence.unapply)
  )

  def index = Action {
    Ok( views.html.grammarMatch(gmForm) )
  }


  def grammarMatchPost = Action { implicit request =>
    gmForm.bindFromRequest.fold(
      formWithErrors => {
        //binding failure
        BadRequest( views.html.grammarMatch(formWithErrors))
      },
      sentence => {
        //binding success, value of forms bound into case class 'sentence'
        Redirect( routes.GrammarController.matchSentence( sentence.sentence ) )
      }
    )
  }

  def date( sentence: String ) = Action.async {
    val futureMatch: Future[Either[String,String]] = scala.concurrent.Future { matchGrammar( GrammarType.DATE, sentence) }
    futureMatch map {
      case Left(m) => Ok( m )
      case Right(m) => Ok( GrammarUtils.formatDate( m ) )
    }
  }


  def currency( sentence: String ) = Action.async {
    val futureMatch: Future[Either[String,String]] = scala.concurrent.Future { matchGrammar( GrammarType.CURRENCY, sentence) }
    futureMatch map {
      case Left(m) => Ok( m )
      case Right(m) => Ok( GrammarUtils.formatCurrency( m ) )
    }
  }

  def time( sentence: String ) = Action.async {
    val futureMatch: Future[Either[String,String]] = scala.concurrent.Future { matchGrammar( GrammarType.TIME, sentence) }
    futureMatch map {
      case Left(m) => Ok( m )
      case Right(m) => Ok( GrammarUtils.formatTime( m ) )
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
//    val futureMatch: Future[String] = scala.concurrent.Future {
//      GrammarUtils.defaultChain.foldLeft(sentence) { (sent,grammar) =>
//        findAndReplaceMatches( grammar, sent )
//      }
//    }
//    futureMatch map { s =>
//      Ok(s)
//    }
      val grammarMatches: Future[List[GrammarMatchInfo]] = scala.concurrent.Future {
        traceMatches( sentence )
      }
      grammarMatches map { gmi =>
        Ok( views.html.matchResult( gmi ) )
      }

  }

  //using this to test Play's marshalling to JSON
  def trace( trace:Boolean, sentence:String ) = Action.async {
    val grammarMatches: Future[List[GrammarMatchInfo]] = scala.concurrent.Future {
      traceMatches( sentence )
    }
    grammarMatches map { gmr =>
      Ok( Json.toJson(gmr) )
    }
  }


  /**
   * matches sentence against the specified grammar and returns an [[Either]] containing the result of the match
   * @param grammarType the grammar to match against
   * @param sentence space seperated string of words to match against the grammar
   * @return [[Right]] if the grammar matched the sentence, in which case Right will contain the result of the match
   *        [[Left]] if the grammar did not match, in which case case Left will contain the string "NOMATCH"
   */
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
   * builds a list of inputs and outputs to the speech recognition grammars
   * @param sentence - space seperated list of words to be matched against
   * @return list of [[GrammarMatchInfo]] objects
   */
  def traceMatches( sentence:String ) : List[GrammarMatchInfo] = {
    var in,out = sentence
    var gms = List[GrammarMatchInfo]()

    for ( gram <- GrammarUtils.defaultChain ) {
      val st = System.nanoTime()
      in = out
      out = findAndReplaceMatches( gram, in )
      val elapsedMillis:Long = (System.nanoTime() - st) / 1000000
      if (in != out )
        gms = gms :+ GrammarMatchInfo( gram.grammarName(), in, out, elapsedMillis )
    }
    gms
  }




  def findFirstAndReplaceAll( phrases:Vector[String], sentence:String, grammar: GrammarType ): Option[String] = {
    for ( phrase <- phrases ) {
      matchGrammar( grammar, phrase ) match {
        case Right(s) =>
          return Some( sentence.replaceAll( phrase, Matcher.quoteReplacement( GrammarUtils.grammarFormatter( grammar, s ))))
        case _ =>
      }
    }
    None
  }

  def findAndReplaceMatches( grammarType:GrammarType, sentence: String ) : String = {
    var newSentence = sentence

    val (gramMax,gramMin) = GrammarUtils.grammarMaxMin( grammarType )

    for ( slide <- gramMax to gramMin by -1 ) {
      var phrases = GrammarUtils.wordCombinations( newSentence, slide )
      var replacementSentence = findFirstAndReplaceAll( phrases, newSentence, grammarType )

      while ( replacementSentence.isDefined ) {
        newSentence = replacementSentence.get
        phrases = GrammarUtils.wordCombinations( newSentence , slide )
        replacementSentence = findFirstAndReplaceAll( phrases, newSentence, grammarType )
      }
    }
    newSentence
  }


  //define implicits for converting to/from JSON
  implicit val grammarMatchInfoWrites: Writes[GrammarMatchInfo] = (
    (JsPath \ "grammarType").write[String] and
      (JsPath \ "in").write[String] and
      (JsPath \ "out").write[String] and
      (JsPath \ "grammarMs").write[Long]
    )(unlift(GrammarMatchInfo.unapply))
}
