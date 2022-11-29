package no.fint.antlr

import no.fint.antlr.exception.FilterException
import no.fint.antlr.odata.ODataFilterService
import no.fint.model.felles.Person
import no.fint.model.felles.kompleksedatatyper.Identifikator
import no.fint.model.felles.kompleksedatatyper.Periode
import no.fint.model.resource.Link
import no.fint.model.resource.personvern.samtykke.BehandlingResource
import no.fint.model.resource.personvern.samtykke.SamtykkeResource
import spock.lang.Specification

import java.time.ZonedDateTime
import java.util.stream.Collectors
import java.util.stream.Stream

class ODataFilterSpec extends Specification {

    ODataFilterService oDataFilterService = new ODataFilterService()

    def "Invalid syntax throws exception"() {
        given:
        def resources = Stream.of(newSamtykkeResource('system-id', '01010122222', '2020-11-25T10:30:30Z'))

        when:
        oDataFilterService.from(resources, 'systemId/identifikatorverdi = \'system-id\'')

        then:
        thrown(FilterException)
    }

    def "Invalid property throws exception"() {
        given:
        def resources = Stream.of(newSamtykkeResource('system-id', '01010122222', '2020-11-25T10:30:30Z'))

        when:
        oDataFilterService.from(resources, 'systemId/identifikato eq \'system-id\'').collect(Collectors.toList())

        then:
        thrown(FilterException)
    }

    def "Invalid value throws exception"() {
        given:
        def resources = Stream.of(newSamtykkeResource('system-id', '01010122222', '2020-11-25T10:30:30Z'))

        when:
        oDataFilterService.from(resources, 'systemId/gyldighetsperiode/start eq \'2020-12-01T\'').collect(Collectors.toList())

        then:
        thrown(FilterException)
    }

    def "Invalid collection throws exception"() {
        given:
        def resources = Stream.of(newSamtykkeResource('system-id', '01010122222', '2020-11-25T10:30:30Z'))

        when:
        oDataFilterService.from(resources, 'systemId/gyldighetsperiode/any(s:s/start eq \'2020-11-25T10:30:30Z\')').collect(Collectors.toList())

        then:
        thrown(FilterException)
    }

    def "String equals"() {
        given:
        def resources = Stream.of(newSamtykkeResource('system-id-1', '01010111111', '2020-11-25T10:30:30Z'),
                newSamtykkeResource('system-id-2', '01010122222', '2020-11-25T10:30:30Z'),
                newSamtykkeResource('system-id-3', '01010133333', '2020-11-25T10:30:30Z'),
                new SamtykkeResource())

        when:
        def test = oDataFilterService.from(resources, 'systemId/identifikatorverdi eq \'system-id-1\'')

        then:
        test.count() == 1
    }

    def "String not equals"() {
        given:
        def resources = Stream.of(newSamtykkeResource('system-id-1', '01010111111', '2020-11-25T10:30:30Z'),
                newSamtykkeResource('system-id-2', '01010122222', '2020-11-25T10:30:30Z'),
                newSamtykkeResource('system-id-3', '01010133333', '2020-11-25T10:30:30Z'),
                new SamtykkeResource())

        when:
        def test = oDataFilterService.from(resources, 'systemId/identifikatorverdi ne \'system-id-1\'')

        then:
        test.count() == 2
    }

    def "String greater than"() {
        given:
        def resources = Stream.of(newSamtykkeResource('system-id-1', '01010111111', '2020-11-25T10:30:30Z'),
                newSamtykkeResource('system-id-2', '01010122222', '2020-11-25T10:30:30Z'),
                newSamtykkeResource('system-id-3', '01010133333', '2020-11-25T10:30:30Z'),
                new SamtykkeResource())

        when:
        def test = oDataFilterService.from(resources, 'systemId/identifikatorverdi gt \'system-id-1\'')

        then:
        test.count() == 2
    }

    def "String less than"() {
        given:
        def resources = Stream.of(newSamtykkeResource('system-id-1', '01010111111', '2020-11-25T10:30:30Z'),
                newSamtykkeResource('system-id-2', '01010122222', '2020-11-25T10:30:30Z'),
                newSamtykkeResource('system-id-3', '01010133333', '2020-11-25T10:30:30Z'),
                new SamtykkeResource())

        when:
        def test = oDataFilterService.from(resources, 'systemId/identifikatorverdi lt \'system-id-1\'')

        then:
        test.count() == 0
    }

    def "String greater than or equal"() {
        given:
        def resources = Stream.of(newSamtykkeResource('system-id-1', '01010111111', '2020-11-25T10:30:30Z'),
                newSamtykkeResource('system-id-2', '01010122222', '2020-11-25T10:30:30Z'),
                newSamtykkeResource('system-id-3', '01010133333', '2020-11-25T10:30:30Z'),
                new SamtykkeResource())

        when:
        def test = oDataFilterService.from(resources, 'systemId/identifikatorverdi ge \'system-id-1\'')

        then:
        test.count() == 3
    }

    def "String less than or equal"() {
        given:
        def resources = Stream.of(newSamtykkeResource('system-id-1', '01010111111', '2020-11-25T10:30:30Z'),
                newSamtykkeResource('system-id-2', '01010122222', '2020-11-25T10:30:30Z'),
                newSamtykkeResource('system-id-3', '01010133333', '2020-11-25T10:30:30Z'),
                new SamtykkeResource())

        when:
        def test = oDataFilterService.from(resources, 'systemId/identifikatorverdi le \'system-id-1\'')

        then:
        test.count() == 1
    }

    def "Boolean equals"() {
        given:
        def resources = Stream.of(newBehandlingResource(true), newBehandlingResource(true), newBehandlingResource(false), new BehandlingResource())

        when:
        def test = oDataFilterService.from(resources, 'aktiv eq \'true\'')

        then:
        test.count() == 2
    }

    def "Boolean not equals"() {
        given:
        def resources = Stream.of(newBehandlingResource(true), newBehandlingResource(true), newBehandlingResource(false), new BehandlingResource())

        when:
        def test = oDataFilterService.from(resources, 'aktiv ne \'true\'')

        then:
        test.count() == 1
    }

    def "Date equals"() {
        given:
        def resources = Stream.of(newSamtykkeResource('system-id-1', '01010111111', '2020-11-25T10:30:30Z'),
                newSamtykkeResource('system-id-2', '01010122222', '2020-11-25T10:30:30Z'),
                newSamtykkeResource('system-id-3', '01010133333', '2020-11-25T11:30:30Z'),
                new SamtykkeResource())

        when:
        def test = oDataFilterService.from(resources, 'opprettet eq \'2020-11-25T10:30:30Z\'')

        then:
        test.count() == 2
    }

    def "Date not equals"() {
        given:
        def resources = Stream.of(newSamtykkeResource('system-id-1', '01010111111', '2020-11-25T10:30:30Z'),
                newSamtykkeResource('system-id-2', '01010122222', '2020-11-25T10:30:30Z'),
                newSamtykkeResource('system-id-3', '01010133333', '2020-11-25T11:30:30Z'),
                new SamtykkeResource())

        when:
        def test = oDataFilterService.from(resources, 'opprettet ne \'2020-11-25T10:30:30Z\'')

        then:
        test.count() == 1
    }

    def "Date greater than"() {
        given:
        def resources = Stream.of(newSamtykkeResource('system-id-1', '01010111111', '2020-11-25T10:30:30Z'),
                newSamtykkeResource('system-id-2', '01010122222', '2020-11-25T10:30:30Z'),
                newSamtykkeResource('system-id-3', '01010133333', '2020-11-25T11:30:30Z'),
                new SamtykkeResource())

        when:
        def test = oDataFilterService.from(resources, 'opprettet gt \'2020-11-25T10:30:30Z\'')

        then:
        test.count() == 1
    }

    def "Date less than"() {
        given:
        def resources = Stream.of(newSamtykkeResource('system-id-1', '01010111111', '2020-11-25T10:30:30Z'),
                newSamtykkeResource('system-id-2', '01010122222', '2020-11-25T10:30:30Z'),
                newSamtykkeResource('system-id-3', '01010133333', '2020-11-25T11:30:30Z'),
                new SamtykkeResource())

        when:
        def test = oDataFilterService.from(resources, 'opprettet lt \'2020-11-25T11:30:30Z\'')

        then:
        test.count() == 2
    }

    def "Date greater than or equal"() {
        given:
        def resources = Stream.of(newSamtykkeResource('system-id-1', '01010111111', '2020-11-25T10:30:30Z'),
                newSamtykkeResource('system-id-2', '01010122222', '2020-11-25T10:30:30Z'),
                newSamtykkeResource('system-id-3', '01010133333', '2020-11-25T11:30:30Z'),
                new SamtykkeResource())

        when:
        def test = oDataFilterService.from(resources, 'opprettet ge \'2020-11-25T10:30:30Z\'')

        then:
        test.count() == 3
    }

    def "Date less than or equal"() {
        given:
        def resources = Stream.of(newSamtykkeResource('system-id-1', '01010111111', '2020-11-25T10:30:30Z'),
                newSamtykkeResource('system-id-2', '01010122222', '2020-11-25T10:30:30Z'),
                newSamtykkeResource('system-id-3', '01010133333', '2020-11-25T11:30:30Z'),
                new SamtykkeResource())

        when:
        def test = oDataFilterService.from(resources, 'opprettet le \'2020-11-25T10:30:30Z\'')

        then:
        test.count() == 2
    }

    def "List equals"() {
        given:
        def resources = Stream.of(newSamtykkeResource('system-id-1', '01010111111', '2020-11-25T10:30:30Z'),
                newSamtykkeResource('system-id-2', '01010122222', '2020-11-25T10:30:30Z'),
                newSamtykkeResource('system-id-3', '01010133333', '2020-11-25T10:30:30Z'),
                new SamtykkeResource())

        when:
        def test = oDataFilterService.from(resources, 'links/person/any(p:p/href eq \'${felles.person}/fodselsnummer/01010111111\')')

        then:
        test.count() == 1
    }

    def "List not equals"() {
        given:
        def resources = Stream.of(newSamtykkeResource('system-id-1', '01010111111', '2020-11-25T10:30:30Z'),
                newSamtykkeResource('system-id-2', '01010122222', '2020-11-25T10:30:30Z'),
                newSamtykkeResource('system-id-3', '01010133333', '2020-11-25T10:30:30Z'),
                new SamtykkeResource())

        when:
        def test = oDataFilterService.from(resources, 'links/person/any(p:p/href ne \'${felles.person}/fodselsnummer/01010111111\')')

        then:
        test.count() == 2
    }

    def "List any equals"() {
        given:
        def resource = newSamtykkeResource('system-id-1', '01010111111', '2020-11-25T10:30:30Z')
        resource.addPerson(Link.with(Person.class, 'fodselsnummer', '02020211111'))

        def resources = Stream.of(newSamtykkeResource('system-id-2', '01010122222', '2020-11-25T10:30:30Z'),
                newSamtykkeResource('system-id-3', '01010133333', '2020-11-25T10:30:30Z'),
                resource, new SamtykkeResource())

        when:
        def test = oDataFilterService.from(resources, 'links/person/any(p:p/href eq \'${felles.person}/fodselsnummer/01010111111\')')

        then:
        test.count() == 1
    }

    def "List all equals"() {
        given:
        def resource = newSamtykkeResource('system-id-1', '01010111111', '2020-11-25T10:30:30Z')
        resource.addPerson(Link.with(Person.class, 'fodselsnummer', '02020211111'))

        def resources = Stream.of(newSamtykkeResource('system-id-2', '01010122222', '2020-11-25T10:30:30Z'),
                newSamtykkeResource('system-id-3', '01010133333', '2020-11-25T10:30:30Z'),
                resource, new SamtykkeResource())

        when:
        def test = oDataFilterService.from(resources, 'links/person/all(p:p/href eq \'${felles.person}/fodselsnummer/01010111111\')')

        then:
        test.count() == 0
    }

    def "Validate invalid syntax"() {
        when:
        def result = oDataFilterService.validate('systemId/identifikatorverdi = \'system-id\'')

        then:
        !result
    }

    def "Validate valid syntax"() {
        when:
        def result = oDataFilterService.validate('systemId/identifikatorverdi eq \'system-id-1\'')

        then:
        result
    }

    def newSamtykkeResource(String systemId, String fodselsnummer, String date) {
        def dateTime = ZonedDateTime.parse(date)

        return new SamtykkeResource(
                systemId: new Identifikator(identifikatorverdi: systemId, gyldighetsperiode: new Periode(start: Date.from(dateTime.toInstant()), slutt: null)),
                opprettet: Date.from(dateTime.toInstant()),
                links: [('person'): [Link.with(Person.class, 'fodselsnummer', fodselsnummer)]]
        )
    }

    def newBehandlingResource(boolean aktiv) {
        return new BehandlingResource(
                aktiv: aktiv
        )
    }
}
