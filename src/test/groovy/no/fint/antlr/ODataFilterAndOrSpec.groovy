package no.fint.antlr

import no.fint.model.felles.Person
import no.fint.model.felles.kompleksedatatyper.Identifikator
import no.fint.model.felles.kompleksedatatyper.Periode
import no.fint.model.resource.Link
import no.fint.model.resource.personvern.samtykke.SamtykkeResource
import spock.lang.Specification

import java.time.ZonedDateTime
import java.util.stream.Stream

class ODataFilterAndOrSpec extends Specification{

    def "String contains and"() {
        given:
        def resources = Stream.of(
                newSamtykkeResource('system-id-01', '01010111111', '2020-11-25T10:30:30Z'),
                newSamtykkeResource('system-id-507', '01010122222', '2020-11-25T10:30:30Z'),
                newSamtykkeResource('system-id-344', '01010133333', '2020-11-25T10:30:30Z'),
                newSamtykkeResource('system-id-402', '01010133333', '2020-11-25T10:30:30Z'),
                newSamtykkeResource('system-id-502', '01010133333', '2020-11-25T10:30:30Z'),
                new SamtykkeResource())

        when:
        def test = oDataFilterService.from(resources, 'systemId/identifikatorverdi contains \'50\' and systemId/identifikatorverdi contains \'id\'')

        then:
        test.count() == 2
    }

    def "String contains and mistake"() {
        given:
        def resources = Stream.of(
                newSamtykkeResource('system-id-01', '01010111111', '2020-11-25T10:30:30Z'),
                newSamtykkeResource('system-id-507', '01010122222', '2020-11-25T10:30:30Z'),
                newSamtykkeResource('system-id-344', '01010133333', '2020-11-25T10:30:30Z'),
                newSamtykkeResource('system-id-402', '01010133333', '2020-11-25T10:30:30Z'),
                newSamtykkeResource('system-id-502', '01010133333', '2020-11-25T10:30:30Z'),
                new SamtykkeResource())

        when:
        def test = oDataFilterService.from(resources, 'systemId/identifikatorverdi contains \'50\' and systemId/identifikatorverdi contains \'3\'')

        then:
        test.count() == 0
    }

    def "String contains or"() {
        given:
        def resources = Stream.of(
                newSamtykkeResource('system-id-01', '01010111111', '2020-11-25T10:30:30Z'),
                newSamtykkeResource('system-id-507', '01010122222', '2020-11-25T10:30:30Z'),
                newSamtykkeResource('system-id-344', '01010133333', '2020-11-25T10:30:30Z'),
                newSamtykkeResource('system-id-402', '01010133333', '2020-11-25T10:30:30Z'),
                newSamtykkeResource('system-id-502', '01010133333', '2020-11-25T10:30:30Z'),
                new SamtykkeResource())

        when:
        def test = oDataFilterService.from(resources, 'systemId/identifikatorverdi contains \'50\' or systemId/identifikatorverdi contains \'3\'')

        then:
        test.count() == 3
    }

    def "String contains or mistake"() {
        given:
        def resources = Stream.of(
                newSamtykkeResource('system-id-01', '01010111111', '2020-11-25T10:30:30Z'),
                newSamtykkeResource('system-id-507', '01010122222', '2020-11-25T10:30:30Z'),
                newSamtykkeResource('system-id-344', '01010133333', '2020-11-25T10:30:30Z'),
                newSamtykkeResource('system-id-402', '01010133333', '2020-11-25T10:30:30Z'),
                newSamtykkeResource('system-id-502', '01010133333', '2020-11-25T10:30:30Z'),
                new SamtykkeResource())

        when:
        def test = oDataFilterService.from(resources, 'systemId/identifikatorverdi contains \'trond\' or systemId/identifikatorverdi contains \'henrik\'')

        then:
        test.count() == 0
    }

    def "String contains two and"() {
        given:
        def resources = Stream.of(
                newSamtykkeResource('system-id-01', '01010111111', '2020-11-25T10:30:30Z'),
                newSamtykkeResource('system-id-507', '01010122222', '2020-11-25T10:30:30Z'),
                newSamtykkeResource('system-id-344', '01010133333', '2020-11-25T10:30:30Z'),
                newSamtykkeResource('system-id-402', '01010133333', '2020-11-25T10:30:30Z'),
                newSamtykkeResource('system-id-502', '01010133333', '2020-11-25T10:30:30Z'),
                new SamtykkeResource())

        when:
        def test = oDataFilterService.from(resources, 'systemId/identifikatorverdi contains \'50\' and systemId/identifikatorverdi contains \'id\' and systemId/identifikatorverdi contains \'system\'')

        then:
        test.count() == 2
    }

    def newSamtykkeResource(String systemId, String fodselsnummer, String date) {
        def dateTime = ZonedDateTime.parse(date)

        return new SamtykkeResource(
                systemId: new Identifikator(identifikatorverdi: systemId, gyldighetsperiode: new Periode(start: Date.from(dateTime.toInstant()), slutt: null)),
                opprettet: Date.from(dateTime.toInstant()),
                links: [('person'): [Link.with(Person.class, 'fodselsnummer', fodselsnummer)]]
        )
    }

}
