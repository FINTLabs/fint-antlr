package no.fint.antlr

import no.fint.antlr.odata.ODataFilterService
import no.fint.model.felles.Person
import no.fint.model.felles.kompleksedatatyper.Identifikator
import no.fint.model.felles.kompleksedatatyper.Periode
import no.fint.model.resource.Link
import no.fint.model.resource.personvern.samtykke.SamtykkeResource
import spock.lang.Specification

import java.time.ZonedDateTime
import java.util.stream.Stream

class ODataFilterNotSpec extends Specification {

    ODataFilterService oDataFilterService = new ODataFilterService()

    def "String not equals"() {
        given:
        def resources = Stream.of(newSamtykkeResource('system-id-1', '01010111111', '2020-11-25T10:30:30Z'),
                newSamtykkeResource('system-id-2', '01010122222', '2020-11-25T10:30:30Z'),
                newSamtykkeResource('system-id-3', '01010133333', '2020-11-25T10:30:30Z'),
                new SamtykkeResource())

        when:
        def test = oDataFilterService.from(resources, 'not systemId/identifikatorverdi eq \'system-id-1\'')

        then:
        test.filter {
            s ->
                s.getSystemId().getIdentifikatorverdi() == 'system-id-2' ||
                        s.getSystemId().getIdentifikatorverdi() == 'system-id-3'
        }.count() == 2

    }

    def "String startswith and not contains"() {
        given:
        def resources = Stream.of(newSamtykkeResource('system-id-1', '01010111111', '2020-11-25T10:30:30Z'),
                newSamtykkeResource('system-id-2', '01010122222', '2020-11-25T10:30:30Z'),
                newSamtykkeResource('system-id-3', '01010133333', '2020-11-25T10:30:30Z'),
                new SamtykkeResource())

        when:
        def test = oDataFilterService.from(resources, 'systemId/identifikatorverdi startswith \'system-id-\' and not systemId/identifikatorverdi contains \'3\'')

        then:
        test.filter {
            s ->
                        s.getSystemId().getIdentifikatorverdi() == 'system-id-1' ||
                        s.getSystemId().getIdentifikatorverdi() == 'system-id-2'
        }.count() == 2

    }

    def "List all not equals"() {
        given:
        def resource = newSamtykkeResource('system-id-1', '01010111111', '2020-11-25T10:30:30Z')
        resource.addPerson(Link.with(Person.class, 'fodselsnummer', '02020211111'))

        def resources = Stream.of(newSamtykkeResource('system-id-2', '01010122222', '2020-11-25T10:30:30Z'),
                newSamtykkeResource('system-id-3', '01010133333', '2020-11-25T10:30:30Z'),
                resource, new SamtykkeResource())

        when:
        def test = oDataFilterService.from(resources, 'not links/person/all(p:p/href eq \'${felles.person}/fodselsnummer/01010111111\')')

        then:
        test.count() == 4
    }

    def "List all not equals and not contains"() {
        given:
        def resource = newSamtykkeResource('system-id-1', '01010111111', '2020-11-25T10:30:30Z')
        resource.addPerson(Link.with(Person.class, 'fodselsnummer', '02020211111'))

        def resources = Stream.of(newSamtykkeResource('system-id-2', '01010122222', '2020-11-25T10:30:30Z'),
                newSamtykkeResource('system-id-3', '01010133333', '2020-11-25T10:30:30Z'),
                resource, new SamtykkeResource())

        when:
        def test = oDataFilterService.from(resources, 'not links/person/all(p:p/href eq \'${felles.person}/fodselsnummer/01010111111\') and not links/person/all(p:p/href contains \'zzz\')')

        then:
        test.count() == 4
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
