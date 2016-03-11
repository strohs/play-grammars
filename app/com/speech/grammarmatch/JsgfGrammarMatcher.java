package com.speech.grammarmatch;

import com.sun.speech.engine.recognition.BaseRecognizer;
import edu.cmu.sphinx.tools.tags.ActionTagsParser;

import javax.speech.recognition.GrammarException;
import javax.speech.recognition.RuleGrammar;
import javax.speech.recognition.RuleParse;

/**
 * matches a String against JSGF grammar file using a BaseRecogninzer
 * User: Cliff
 * Date: 2/25/2016
 * Time: 1:52 PM
 */
public class JsgfGrammarMatcher implements IGrammarMatcher {


    //replace special characters that will throw off the grammar matcher and/or ecmascript parser
    private static String replaceChars( String in ) {
        return in.replaceAll( "['\"]", "" );
    }


    @Override
    public String recognize( BaseRecognizer recognizer, GrammarType grammarType, String sentence ) throws GrammarException {
        String out = sentence;
        RuleGrammar grammar = recognizer.getRuleGrammar( grammarType.grammarName() );

        try {
            grammar.setEnabled( true );
            recognizer.commitChanges();

            //replace special chars
            sentence = replaceChars( sentence );

            RuleParse p = grammar.parse( sentence, null );
            if ( p != null ) {
                //a match was found
                ActionTagsParser parser = new ActionTagsParser();
                parser.parseTags( p );
                out = ( String ) parser.get( "$value" );
            } else {
                out = "NOMATCH";
            }

        } finally {
            grammar.setEnabled( false );
            recognizer.commitChanges();
        }
        return out;
    }
}
