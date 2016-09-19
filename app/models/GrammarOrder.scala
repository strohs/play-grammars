package models

import com.speech.grammarmatch.GrammarType

/**
 * holds the order that the speech recognition grammars should be run in
 * User: Cliff
 * Date: 5/18/2016
 * Time: 1:25 PM
 */
object GrammarOrder {

  //The order that grammars should be run. The rule to follow when listing a chain is that the speech grammar that could
  // match the greatest "slice" of words (at one time) should be run first, then the next greatest, etc.
  val default = List( GrammarType.CURRENCY, GrammarType.DATE, GrammarType.TIME, GrammarType.ORDINAL, GrammarType.NUMBER )

}
