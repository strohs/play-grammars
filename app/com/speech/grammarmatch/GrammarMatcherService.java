package com.speech.grammarmatch;

import com.sun.speech.engine.recognition.BaseRecognizer;
import javax.inject.Singleton;
import javax.speech.EngineException;
import javax.speech.recognition.GrammarException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Grammar matcher service that will be dependency injected. Since grammar matching can be CPU intensive, a number of
 * these recognizers can be started by the service
 * User: cliff
 * Date: Aug 17, 2011
 * Time: 5:52:28 PM
 */
@Singleton
public class GrammarMatcherService {

    //max number of grammar recognizers that should be created by this service
    private int MAX_RECOGNIZERS = 20;

    //blocking queue of grammar recognizers
    private LinkedBlockingQueue<BaseRecognizer> recognizers = null;


    public void init( List<File> grammarFiles, int maxRecognizers ) {

        this.MAX_RECOGNIZERS = maxRecognizers;

        recognizers = new LinkedBlockingQueue<BaseRecognizer>( MAX_RECOGNIZERS );
        for ( int i = 0; i < MAX_RECOGNIZERS; i++ ) {
            try {
                recognizers.offer( createRecognizer( grammarFiles ) );
            } catch ( EngineException e ) {
                e.printStackTrace();
            } catch ( IOException e ) {
                e.printStackTrace();
            } catch ( GrammarException e ) {
                e.printStackTrace();
            }
        }
    }

    /**
     * creates a single grammar recognizer
     *
     * @return a Grammar Recognizer capable of matching against our grammar files
     * @throws EngineException
     * @throws IOException
     * @throws GrammarException
     */
    private BaseRecognizer createRecognizer( List<File> grammarFiles ) throws EngineException, IOException, GrammarException {
        //create recognizer
        BaseRecognizer recognizer = new BaseRecognizer();
        recognizer.allocate();
        //load JSGF grammars
        for ( File filePath : grammarFiles ) {
            recognizer.loadJSGF( new FileReader( filePath ) );
        }
        recognizer.commitChanges();
        return recognizer;
    }


    public BaseRecognizer get() throws InterruptedException {
        return recognizers.take();
    }

    public void put( BaseRecognizer r) {
        recognizers.offer( r );
    }


    public void shutdown() {
        for ( int i = 0; i < MAX_RECOGNIZERS; i++ ) {
            BaseRecognizer r = recognizers.poll();
            r.deleteRuleGrammar( r.getRuleGrammar( GrammarType.CURRENCY.grammarName() ) );
            r.deleteRuleGrammar( r.getRuleGrammar( GrammarType.DATE.grammarName() ) );
            r.deleteRuleGrammar( r.getRuleGrammar( GrammarType.TIME.grammarName() ) );
            r.deleteRuleGrammar( r.getRuleGrammar( GrammarType.NUMBER.grammarName() ) );
            r.deleteRuleGrammar( r.getRuleGrammar( GrammarType.ORDINAL.grammarName() ) );

            try {
                r.commitChanges();
                r.deallocate();

            } catch ( Exception e ) {
                e.printStackTrace();
            }

        }
    }


}
