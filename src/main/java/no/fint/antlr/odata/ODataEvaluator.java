package no.fint.antlr.odata;

import lombok.extern.slf4j.Slf4j;
import no.fint.antlr.ODataBaseVisitor;
import no.fint.antlr.ODataParser;
import no.fint.antlr.exception.InvalidArgumentException;
import org.apache.commons.beanutils.NestedNullException;
import org.apache.commons.beanutils.PropertyUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
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
        if (ctx == null) return false;
//        if(ctx.lambda() == null && ctx.comparison() == null) return false;
        if(ctx.lambda().size() == 0 && ctx.comparison().size() == 0) return false;

        Boolean result = (ctx.lambda() != null && ctx.lambda().size() > 0) ? visit(ctx.lambda(0)) : visit(ctx.comparison(0));
        if (ctx.logicalOperator() != null) {
            for (int i = 0; i < ctx.logicalOperator().size(); i++) {
                String operator = ctx.logicalOperator(i).getText();
                Boolean nextResult = !ctx.lambda().isEmpty() ? visit(ctx.lambda(i + 1)) : visit(ctx.comparison(i + 1));
                if (operator.equals("and")) {
                    result = result && nextResult;
                } else if (operator.equals("or")) {
                    result = result || nextResult;
                }
            }
        }
        return result;
    }
//    @Override
//    public Boolean visitFilter(ODataParser.FilterContext ctx) {
//
//        boolean comparisonResult = ctx.comparison().stream().allMatch(this::visit);
//        boolean lambdaResult = ctx.lambda().stream().allMatch(this::visit);
//
//        return comparisonResult && lambdaResult;

//        boolean comparisonResult = ctx.comparison().stream().allMatch(this::visit);
//        boolean lambdaResult = ctx.lambda().stream().map(this::visit).reduce(true, (a, b) -> a && b);
//
//        return comparisonResult && lambdaResult;
//        log.info(ctx.getText());
//        Boolean reduce = null;
//
//
//        if (ctx.getText().contains(" and ")) {
//
//            log.info(ctx.comparison().stream().collect(Collectors.toList()).toString());
//
//            reduce = ctx.lambda().stream()
//                    .map(this::visit)
//                    .reduce(ctx.comparison().stream()
//                            .map(this::visit)
//                            .peek(b -> log.info(b.toString()))
//                            .reduce(true, (a, b) -> a && b), (a, b) -> a && b);
//        } else {
//            reduce = ctx.lambda().stream()
//                    .map(this::visit)
//                    .reduce(ctx.comparison().stream()
//                            .map(this::visit)
//                            .peek(b -> log.info(b.toString()))
////                        .reduce(true, (a, b) -> a && b), (a, b) -> a && b);
//                            .reduce(false, (a, b) -> a || b), (a, b) -> a || b);
//
//
//        }
//        log.info("Result {}", reduce);
//        return reduce;
//    }

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

                case "contains":
                    return ODataOperator.contains(property, value);

                case "startswith":
                    return ODataOperator.startswith(property, value);

                case "endswith":
                    return ODataOperator.endswith(property, value);

                default:
                    return false;
            }

        } catch (Exception ex) {
            throw new InvalidArgumentException(ex);
        }
    }
}
