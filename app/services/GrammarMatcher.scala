package services

import javax.inject.{Singleton, Inject}

import com.speech.grammarmatch.GrammarMatcherService
import play.api.Logger
import play.api.inject.ApplicationLifecycle
import play.api.{Configuration, Logger, Environment}
import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer
import scala.concurrent.Future

/**
 * starts and stops the grammar matcher service. Used for Dependency Injection
 * User: Cliff
 * Date: 3/9/2016
 * Time: 4:25 PM
 */
@Singleton
class GrammarMatcher @Inject( )( environment: Environment, gmService: GrammarMatcherService, conf: Configuration, appLifecycle: ApplicationLifecycle ) {


  Logger.info( "Starting Grammar Matcher Service" )
  val gramFiles: java.util.List[java.io.File] = createGrammarFiles( )

  //start the grammar matcher service with 20 matchers
  gmService.init( gramFiles, 20 )


  def createGrammarFiles( ): ListBuffer[java.io.File] = {
    //get directory containing the grammar files
    val grammarDir = conf.underlying.getString("grammar.dir")

    val grammarFiles = new java.io.File( grammarDir ).listFiles.filter(_.getName.endsWith(".gram"))

    ListBuffer() ++ grammarFiles
  }


  appLifecycle.addStopHook( () => {
    Logger.info("Stopping Grammar Matcher Service")
    Future.successful( gmService.shutdown() )
  })
}