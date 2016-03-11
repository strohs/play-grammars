package com.speech.grammarmatch;

import java.io.File;
import java.util.ArrayList;

/**
 * Copyright Yap Inc. All Rights Reserved
 * User: cliff
 * Date: Aug 30, 2011
 * Time: 2:57:12 PM
 */
public class GrammarMatcherTester {


    public static void main(String[] args) {

        //arg0 must contain grammar name
        System.err.println("grammar:" + args[0]);
        //arg1 contains the sentence
        System.err.println("sentence:" + args[1]);

        String grammar = args[0];
        String sentence = args[1];
        //contains the match from a grammar, i.e. the semantic interpretation
        String matchStr = sentence;
        GrammarMatcherService rs = new GrammarMatcherService();
        ArrayList<File> files = new ArrayList<>();
        files.add( new File("PATH/TO/GRAMMAR/FILE") );

//        try {
//
//            rs.init( files, 5 );
//            System.err.println("Recognition Service started");
//
//            if ( grammar.equalsIgnoreCase( GrammarType.CURRENCY.name() ) ) {
//                matchStr = rs.recognize( GrammarType.CURRENCY, sentence );
//            } else if (grammar.equalsIgnoreCase( GrammarType.DATE.name() ) ) {
//                matchStr = rs.recognize( GrammarType.DATE, sentence );
//            } else if (grammar.equalsIgnoreCase( GrammarType.ORDINAL.name() ) ) {
//                matchStr = rs.recognize( GrammarType.ORDINAL, sentence );
//            } else if (grammar.equalsIgnoreCase( GrammarType.NUMBER.name() ) ) {
//                matchStr = rs.recognize( GrammarType.NUMBER, sentence );
//            } else if (grammar.equalsIgnoreCase( GrammarType.TIME.name() ) ) {
//                matchStr = rs.recognize( GrammarType.TIME, sentence );
//            } else {
//                System.err.println("unknown grammar specified: " + grammar );
//            }
//            //print the match to stdout
//            System.out.println(matchStr);
//        } catch (Exception e) {
//            System.err.println( e.getMessage() );
//        } finally {
//            System.out.println("Shutting down grammar recognizers");
//            //shutdown recognizer
//            rs.shutdown();
//        }



    }

    /*InputStreamReader getResource( String path ) throws IOException {
        InputStream in = getClass().getResourceAsStream( path );
        InputStreamReader isr = new InputStreamReader( in );
        return isr;
    }*/
}
