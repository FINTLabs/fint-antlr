package no.fint.antlr

import no.fint.antlr.odata.ODataFilterService
import spock.lang.Specification

import java.time.LocalDate
import java.time.LocalDateTime
import java.util.stream.Stream

class ODataFilterLocalDateSpec extends Specification {

    ODataFilterService oDataFilterService = new ODataFilterService()

    def "LocalDateTime equals"() {
        given:
        def resources = Stream.of(
                new LocalDateTestResource(opprettet: LocalDateTime.parse('2020-11-25T10:30:30')),
                new LocalDateTestResource(opprettet: LocalDateTime.parse('2020-11-25T11:30:30')))

        when:
        def test = oDataFilterService.from(resources, 'opprettet eq \'2020-11-25T10:30:30\'')

        then:
        test.count() == 1
    }

    def "LocalDateTime not equals"() {
        given:
        def resources = Stream.of(
                new LocalDateTestResource(opprettet: LocalDateTime.parse('2020-11-25T10:30:30')),
                new LocalDateTestResource(opprettet: LocalDateTime.parse('2020-11-25T11:30:30')))

        when:
        def test = oDataFilterService.from(resources, 'opprettet ne \'2020-11-25T10:30:30\'')

        then:
        test.count() == 1
    }

    def "LocalDateTime greater than"() {
        given:
        def resources = Stream.of(
                new LocalDateTestResource(opprettet: LocalDateTime.parse('2020-11-25T10:30:30')),
                new LocalDateTestResource(opprettet: LocalDateTime.parse('2020-11-25T11:30:30')))

        when:
        def test = oDataFilterService.from(resources, 'opprettet gt \'2020-11-25T10:30:30\'')

        then:
        test.count() == 1
    }

    def "LocalDateTime less than"() {
        given:
        def resources = Stream.of(
                new LocalDateTestResource(opprettet: LocalDateTime.parse('2020-11-25T10:30:30')),
                new LocalDateTestResource(opprettet: LocalDateTime.parse('2020-11-25T11:30:30')))

        when:
        def test = oDataFilterService.from(resources, 'opprettet lt \'2020-11-25T11:30:30\'')

        then:
        test.count() == 1
    }

    def "LocalDateTime greater than or equal"() {
        given:
        def resources = Stream.of(
                new LocalDateTestResource(opprettet: LocalDateTime.parse('2020-11-25T10:30:30')),
                new LocalDateTestResource(opprettet: LocalDateTime.parse('2020-11-25T11:30:30')))

        when:
        def test = oDataFilterService.from(resources, 'opprettet ge \'2020-11-25T10:30:30\'')

        then:
        test.count() == 2
    }

    def "LocalDateTime less than or equal"() {
        given:
        def resources = Stream.of(
                new LocalDateTestResource(opprettet: LocalDateTime.parse('2020-11-25T10:30:30')),
                new LocalDateTestResource(opprettet: LocalDateTime.parse('2020-11-25T11:30:30')))

        when:
        def test = oDataFilterService.from(resources, 'opprettet le \'2020-11-25T11:30:30\'')

        then:
        test.count() == 2
    }

    def "LocalDate equals"() {
        given:
        def resources = Stream.of(
                new LocalDateTestResource(startdato: LocalDate.parse('2020-11-25')),
                new LocalDateTestResource(startdato: LocalDate.parse('2020-11-26')))

        when:
        def test = oDataFilterService.from(resources, 'startdato eq \'2020-11-25\'')

        then:
        test.count() == 1
    }

    def "LocalDate not equals"() {
        given:
        def resources = Stream.of(
                new LocalDateTestResource(startdato: LocalDate.parse('2020-11-25')),
                new LocalDateTestResource(startdato: LocalDate.parse('2020-11-26')))

        when:
        def test = oDataFilterService.from(resources, 'startdato ne \'2020-11-25\'')

        then:
        test.count() == 1
    }

    def "LocalDate greater than"() {
        given:
        def resources = Stream.of(
                new LocalDateTestResource(startdato: LocalDate.parse('2020-11-25')),
                new LocalDateTestResource(startdato: LocalDate.parse('2020-11-26')))

        when:
        def test = oDataFilterService.from(resources, 'startdato gt \'2020-11-25\'')

        then:
        test.count() == 1
    }

    def "LocalDate less than or equal"() {
        given:
        def resources = Stream.of(
                new LocalDateTestResource(startdato: LocalDate.parse('2020-11-25')),
                new LocalDateTestResource(startdato: LocalDate.parse('2020-11-26')))

        when:
        def test = oDataFilterService.from(resources, 'startdato le \'2020-11-25\'')

        then:
        test.count() == 1
    }
}

class LocalDateTestResource {
    LocalDateTime opprettet
    LocalDate startdato
}
