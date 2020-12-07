package no.fint.antlr.odata;

import no.fint.antlr.exception.InvalidSyntaxException;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

public class ODataErrorListener extends BaseErrorListener {

    public static final ODataErrorListener INSTANCE = new ODataErrorListener();

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
        throw new InvalidSyntaxException("line " + line + ":" + charPositionInLine + " " + msg);
    }
}
