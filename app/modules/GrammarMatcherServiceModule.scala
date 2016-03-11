package modules

import com.google.inject.AbstractModule
import com.speech.grammarmatch.GrammarMatcherService

/**
 * Created with IntelliJ IDEA.
 * User: Cliff
 * Date: 2/25/2016
 * Time: 3:20 PM
 */
class GrammarMatcherServiceModule extends AbstractModule {

  override def configure( ): Unit = {
    bind( classOf[GrammarMatcherService] ).asEagerSingleton()
    bind( classOf[GrammarMatcherServiceStart]).asEagerSingleton()
    bind( classOf[GrammarMatcherServiceStop]).asEagerSingleton()
  }
}
