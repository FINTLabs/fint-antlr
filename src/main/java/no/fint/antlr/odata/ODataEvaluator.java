package no.fint.antlr.odata;

import no.fint.antlr.ODataBaseVisitor;
import no.fint.antlr.ODataParser;
import no.fint.antlr.exception.InvalidArgumentException;
import org.apache.commons.beanutils.NestedNullException;
import org.apache.commons.beanutils.PropertyUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class ODataEvaluator extends ODataBaseVisitor<Boolean> {
    private final Object object;

    private String collection;
    private String lambdaOperator;

    private String property;
    private String comparisonOperator;
    private String value;

    public ODataEvaluator(Object object) {
        this.object = object;
    }

    @Override
    public Boolean visitFilter(ODataParser.FilterContext ctx) {
        return ctx.lambda() == null ? visit(ctx.comparison()) : visit(ctx.lambda());
    }

    @Override
    public Boolean visitLambda(ODataParser.LambdaContext ctx) {
        visit(ctx.collection());
        visit(ctx.lambdaOperator());

        return visit(ctx.comparison());
    }

    @Override
    public Boolean visitComparison(ODataParser.ComparisonContext ctx) {
        visit(ctx.property());
        visit(ctx.comparisonOperator());
        visit(ctx.value());

        try {
            return this.collection == null ? objectComparison() : collectionComparison();
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
            throw new InvalidArgumentException(ex);
        } catch (NestedNullException ex) {
            return false;
        }
    }

    @Override
    public Boolean visitCollection(ODataParser.CollectionContext ctx) {
        this.collection = ctx.getText().replaceAll("/", ".");

        return true;
    }

    @Override
    public Boolean visitLambdaOperator(ODataParser.LambdaOperatorContext ctx) {
        this.lambdaOperator = ctx.getText();

        return true;
    }

    @Override
    public Boolean visitProperty(ODataParser.PropertyContext ctx) {
        this.property = ctx.getText().replaceAll("/", ".");

        return true;
    }

    @Override
    public Boolean visitComparisonOperator(ODataParser.ComparisonOperatorContext ctx) {
        this.comparisonOperator = ctx.getText();

        return true;
    }

    @Override
    public Boolean visitValue(ODataParser.ValueContext ctx) {
        this.value = ctx.getText().replaceAll("'", "");

        return true;
    }

    private Boolean objectComparison() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException, NestedNullException {
        Object property = PropertyUtils.getProperty(object, this.property);

        if (property == null) {
            return false;
        }

        return compare(property, value);
    }

    private Boolean collectionComparison() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException, NestedNullException {
        Object collection = PropertyUtils.getProperty(object, this.collection);

        if (collection == null) {
            return false;
        }

        if (collection instanceof List) {
            List<Object> objects = new ArrayList<>((List<?>) collection);

            List<Boolean> classification = new ArrayList<>(objects.size());

            for (Object object : objects) {
                Object property = PropertyUtils.getProperty(object, this.property);

                classification.add(compare(property, value));
            }

            if (lambdaOperator.equals("all")) {
                return classification.stream().allMatch(Predicate.isEqual(Boolean.TRUE));
            } else if (lambdaOperator.equals("any")) {
                return classification.stream().anyMatch(Predicate.isEqual(Boolean.TRUE));
            } else {
                return false;
            }

        } else {
            throw new InvalidArgumentException(String.format("%s is not a collection", this.collection));
        }
    }

    private Boolean compare(Object property, String value) {
        try {
            switch (comparisonOperator) {
                case "eq":
                    return ODataOperator.eq(property, value);

                case "ne":
                    return ODataOperator.ne(property, value);

                case "gt":
                    return ODataOperator.gt(property, value);

                case "lt":
                    return ODataOperator.lt(property, value);

                case "ge":
                    return ODataOperator.ge(property, value);

                case "le":
                    return ODataOperator.le(property, value);

                default:
                    return false;
            }

        } catch (Exception ex) {
            throw new InvalidArgumentException(ex);
        }
    }
}
