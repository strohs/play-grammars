package controllers

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



  def show( name: String, sentence: String ) = Action {
    //val source = scala.io.Source.fromFile("conf/grammars/currency.gram")
    //val lines = try source.mkString finally source.close()

    val jsgfRecognizer = new JsgfGrammarMatcher
    //could block here waiting for a grammar recognizer
    val recognizer = gmService.get()
    val theMatch = jsgfRecognizer.recognize( recognizer, GrammarType.valueOf( name.toUpperCase ), sentence)
    gmService.put( recognizer )
    Ok( "Grammar Matcher returned: " + theMatch )
  }


}
