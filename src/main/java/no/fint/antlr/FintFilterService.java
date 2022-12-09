package no.fint.antlr;

import java.util.stream.Stream;

public interface FintFilterService {

    <T> Stream<T> from(Stream<T> resources, String filter);

    boolean validate(String filter);
}