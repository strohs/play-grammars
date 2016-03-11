package com.speech.grammarmatch;

import com.sun.speech.engine.recognition.BaseRecognizer;

import javax.speech.recognition.GrammarException;

/**
 * Created with IntelliJ IDEA.
 * User: Cliff
 * Date: 2/25/2016
 * Time: 1:18 PM
 */
public interface IGrammarMatcher {

    String recognize( BaseRecognizer recognizer, GrammarType grammarName, String sentence ) throws GrammarException;
}
