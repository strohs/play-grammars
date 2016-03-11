package modules

import javax.inject.{Inject, Singleton}

import com.speech.grammarmatch.GrammarMatcherService
import play.Logger
import play.api.inject.ApplicationLifecycle

import scala.concurrent.Future

/**
 * Created with IntelliJ IDEA.
 * User: Cliff
 * Date: 2/25/2016
 * Time: 2:57 PM
 */
@Singleton
class GrammarMatcherServiceStop @Inject() ( lifecycle: ApplicationLifecycle, gmService: GrammarMatcherService ) {

  lifecycle.addStopHook( () => {
    Logger.info("Stopping Grammar Matcher Service")
    Future.successful( gmService.shutdown() )
  })
}
