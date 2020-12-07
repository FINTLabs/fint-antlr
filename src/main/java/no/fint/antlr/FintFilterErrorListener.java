package no.fint.antlr;

import no.fint.antlr.exception.InvalidSyntaxException;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

public class FintFilterErrorListener extends BaseErrorListener {

    public static final FintFilterErrorListener INSTANCE = new FintFilterErrorListener();

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
        throw new InvalidSyntaxException("line " + line + ":" + charPositionInLine + " " + msg);
    }
}
