package models

/**
 * holds the inputs and outputs of a grammar match
 * @param grammarType
 * @param in
 * @param out
 */
case class GrammarMatchInfo( grammarType:String, in:String, out:String, grammarMs:Long )
