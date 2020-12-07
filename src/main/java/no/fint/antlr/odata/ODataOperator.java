package no.fint.antlr.odata;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class ODataOperator {

    public static Boolean eq(Object property, String value) {
        if (property instanceof String) {
            return String.valueOf(property).equalsIgnoreCase(value);

        } else if (property instanceof Boolean) {
            return property.equals(Boolean.parseBoolean(value));

        } else if (property instanceof Date) {
            return ((Date) property).toInstant().truncatedTo(ChronoUnit.SECONDS).equals(ZonedDateTime.parse(value).toInstant().truncatedTo(ChronoUnit.SECONDS));

        } else {
            return false;
        }
    }

    public static Boolean ne(Object property, String value) {
        if (property instanceof String) {
            return !String.valueOf(property).equalsIgnoreCase(value);

        } else if (property instanceof Boolean) {
            return !property.equals(Boolean.parseBoolean(value));

        } else if (property instanceof Date) {
            return !((Date) property).toInstant().truncatedTo(ChronoUnit.SECONDS).equals(ZonedDateTime.parse(value).toInstant().truncatedTo(ChronoUnit.SECONDS));

        } else {
            return false;
        }
    }

    public static Boolean gt(Object property, String value) {
        if (property instanceof String) {
            return String.valueOf(property).compareToIgnoreCase(value) > 0;

        } else if (property instanceof Date) {
            return ((Date) property).toInstant().truncatedTo(ChronoUnit.SECONDS).compareTo(ZonedDateTime.parse(value).toInstant().truncatedTo(ChronoUnit.SECONDS)) > 0;

        } else {
            return false;
        }
    }

    public static Boolean lt(Object property, String value) {
        if (property instanceof String) {
            return String.valueOf(property).compareToIgnoreCase(value) < 0;

        } else if (property instanceof Date) {
            return ((Date) property).toInstant().truncatedTo(ChronoUnit.SECONDS).compareTo(ZonedDateTime.parse(value).toInstant().truncatedTo(ChronoUnit.SECONDS)) < 0;

        } else {
            return false;
        }
    }

    public static Boolean ge(Object property, String value) {
        if (property instanceof String) {
            return property.toString().compareToIgnoreCase(value) >= 0;

        } else if (property instanceof Date) {
            return ((Date) property).toInstant().truncatedTo(ChronoUnit.SECONDS).compareTo(ZonedDateTime.parse(value).toInstant().truncatedTo(ChronoUnit.SECONDS)) >= 0;

        } else {
            return false;
        }
    }

    public static Boolean le(Object property, String value) {
        if (property instanceof String) {
            return property.toString().compareToIgnoreCase(value) <= 0;

        } else if (property instanceof Date) {
            return ((Date) property).toInstant().truncatedTo(ChronoUnit.SECONDS).compareTo(ZonedDateTime.parse(value).toInstant().truncatedTo(ChronoUnit.SECONDS)) <= 0;

        } else {
            return false;
        }
    }
}
