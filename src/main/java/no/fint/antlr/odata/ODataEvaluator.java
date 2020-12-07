package no.fint.antlr.odata;

import no.fint.antlr.ODataBaseVisitor;
import no.fint.antlr.ODataParser;
import no.fint.antlr.exception.InvalidArgumentException;
import org.apache.commons.beanutils.NestedNullException;
import org.apache.commons.beanutils.PropertyUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

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
    public Boolean visitComparisonOperator(ODataParser.ComparisonOperatorContext ctx) {
        this.comparisonOperator = ctx.getText();

        return true;
    }

    @Override
    public Boolean visitLambdaOperator(ODataParser.LambdaOperatorContext ctx) {
        this.lambdaOperator = ctx.getText();

        return true;
    }

    @Override
    public Boolean visitCollection(ODataParser.CollectionContext ctx) {
        this.collection = ctx.getText().replaceAll("/", ".");

        return true;
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
            if (this.collection == null) {
                Object property = PropertyUtils.getProperty(object, this.property);

                if (property == null) {
                    return false;
                }
                return compare(property, value);
            } else {
                Object collectionProperty = PropertyUtils.getProperty(object, this.collection);

                if (collectionProperty == null) {
                    return false;
                }

                if (collectionProperty instanceof List) {
                    List<Object> collectionObjects = new ArrayList<>((List<?>) collectionProperty);

                    for (Object collectionObject : collectionObjects) {
                        Object collectionObjectProperty = PropertyUtils.getProperty(collectionObject, this.property);

                        if (compare(collectionObjectProperty, value)) {
                            return true;
                        }
                    }
                    return false;
                } else {
                    throw new InvalidArgumentException(String.format("%s is not a collection", this.collection));
                }
            }
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
            throw new InvalidArgumentException(ex);
        } catch (NestedNullException ex) {
            return false;
        }
    }

    @Override
    public Boolean visitFilter(ODataParser.FilterContext ctx) {
        return ctx.lambda() == null ? visit(ctx.comparison()) : visit(ctx.lambda());
    }

    @Override
    public Boolean visitProperty(ODataParser.PropertyContext ctx) {
        this.property = ctx.getText().replaceAll("/", ".");

        return true;
    }

    @Override
    public Boolean visitValue(ODataParser.ValueContext ctx) {
        this.value = ctx.getText().replaceAll("'", "");

        return true;
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
