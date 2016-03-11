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
            long startTime = System.currentTimeMillis();

            grammar.setEnabled( true );
            recognizer.commitChanges();
            System.err.println( "GRAMMARMATCH " + grammar.getName() + " committed changes time: " + (System.currentTimeMillis() - startTime) );
            //match all input sentences against the grammar, one at a time

            long parseTime = System.currentTimeMillis();

            //replace special chars
            sentence = replaceChars( sentence );

            System.err.println( "GRAMMARMATCH " + grammar.getName() + " matching sentence: " + sentence );
            RuleParse p = grammar.parse( sentence, null );
            if ( p != null ) {
                //a match was found
                ActionTagsParser parser = new ActionTagsParser();
                parser.parseTags( p );
                System.err.println( "GRAMMARMATCH MATCH, parse   time:         " + (System.currentTimeMillis() - parseTime) );
                out = ( String ) parser.get( "$value" );
            } else {
                System.err.println( "GRAMMARMATCH NOMatch, parse time:         " + (System.currentTimeMillis() - parseTime) );
                out = "NOMATCH";
            }

        } finally {
            grammar.setEnabled( false );
            try {
                recognizer.commitChanges();
            } catch ( GrammarException e ) {
                System.err.println( "could not disable grammar" );
            }
        }
        return out;
    }
}
