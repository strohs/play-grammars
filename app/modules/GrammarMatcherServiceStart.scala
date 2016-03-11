package modules

import javax.inject.{Inject, Singleton}

import com.speech.grammarmatch.GrammarMatcherService
import play.api.{Environment, Logger}

import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer

/**
 * Created with IntelliJ IDEA.
 * User: Cliff
 * Date: 2/25/2016
 * Time: 2:42 PM
 */

@Singleton
class GrammarMatcherServiceStart @Inject( )( environment: Environment, gmService: GrammarMatcherService ) {

  Logger.info( "Starting Grammar Matcher Service" )
  val gramFiles: java.util.List[java.io.File] = createGrammarFiles( )
  gmService.init( gramFiles, 20 )


  def createGrammarFiles( ): ListBuffer[java.io.File] = {
    ListBuffer( new java.io.File( "public/grammars/currency.gram" ),
      new java.io.File( "public/grammars/date.gram" ),
      new java.io.File( "public/grammars/time.gram" ),
      new java.io.File( "public/grammars/number.gram" ),
      new java.io.File( "public/grammars/ordinal.gram" ) )
  }
}
