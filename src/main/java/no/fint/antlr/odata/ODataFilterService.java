package no.fint.antlr.odata;

import no.fint.antlr.FintFilterErrorListener;
import no.fint.antlr.FintFilterService;
import no.fint.antlr.ODataLexer;
import no.fint.antlr.ODataParser;
import no.fint.antlr.exception.FilterException;
import no.fint.antlr.exception.InvalidArgumentException;
import no.fint.antlr.exception.InvalidSyntaxException;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ConsoleErrorListener;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.stream.Stream;

public class ODataFilterService implements FintFilterService {

    public <T> Stream<T> from(Stream<T> resources, String filter) {
        ParseTree parseTree = getParseTree(filter);
        return resources.filter(resource -> evaluate(resource, parseTree));
    }

    public boolean validate(String filter) {
        try {
            getParseTree(filter);
            return true;
        } catch (FilterException e) {
            return false;
        }
    }

    private ParseTree getParseTree(String filter) {
        ODataLexer lexer = new ODataLexer(CharStreams.fromString(filter));
        CommonTokenStream tokens = new CommonTokenStream(lexer);

        ODataParser parser = new ODataParser(tokens);
        parser.removeErrorListener(ConsoleErrorListener.INSTANCE);
        parser.addErrorListener(FintFilterErrorListener.INSTANCE);

        ParseTree parseTree;

        try {
            parseTree = parser.filter();
        } catch (InvalidSyntaxException ex) {
            throw new FilterException(ex);
        }
        return parseTree;
    }

    private <T> boolean evaluate(T resource, ParseTree parseTree) {
        try {
            return new ODataEvaluator(resource).visit(parseTree);
        } catch (InvalidArgumentException ex) {
            throw new FilterException(ex);
        }
    }
}