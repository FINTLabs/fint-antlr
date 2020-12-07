package no.fint.antlr.odata;

import no.fint.antlr.ODataLexer;
import no.fint.antlr.ODataParser;
import no.fint.antlr.exception.FilterException;
import no.fint.antlr.exception.InvalidArgumentException;
import no.fint.antlr.exception.InvalidSyntaxException;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ConsoleErrorListener;
import org.antlr.v4.runtime.tree.ParseTree;
import org.springframework.stereotype.Service;

import java.util.stream.Stream;

@Service
public class ODataFilter {

    public <T> Stream<T> from(Stream<T> resources, String filter) {
        ODataLexer lexer = new ODataLexer(CharStreams.fromString(filter));
        CommonTokenStream tokens = new CommonTokenStream(lexer);

        ODataParser parser = new ODataParser(tokens);
        parser.removeErrorListener(ConsoleErrorListener.INSTANCE);
        parser.addErrorListener(ODataErrorListener.INSTANCE);

        ParseTree parseTree;

        try {
            parseTree = parser.filter();
        } catch (InvalidSyntaxException ex) {
            throw new FilterException(ex);
        }

        return resources.filter(resource -> evaluate(resource, parseTree));
    }

    private static <T> boolean evaluate(T resource, ParseTree parseTree) {
        try {
            return new ODataEvaluator(resource).visit(parseTree);
        } catch (InvalidArgumentException ex) {
            throw new FilterException(ex);
        }
    }
}