## Table of contents
* [General info](#general-info)
* [Technologies](#technologies)
* [Setup](#setup)

## General info
This java library is used to filter streams of objects based on grammars and parse trees built with ANTLR. At present the library has support for most of OData 4.01 Logical Operators (5.1.1.1) and Lambda Operators (5.1.1.13) as specified in http://docs.oasis-open.org/odata/odata/v4.01/odata-v4.01-part2-url-conventions.html.


## Technologies
Project is created with:
* Spring Boot version: 2.4.0
* ANTLR 4 version: 4.8

## Setup

http://dl.bintray.com/fint/maven

```
implementation 'no.fint:fint-antlr:1.0.0-alpha-14'
```

```
@EnableFintFilter
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

```
@RestController
@RequestMapping("/persons")
public class PersonController {

    @Autowired
    private FintFilterService oDataFilterService;    
    
    @GetMapping
    private Stream<Person> getPersons(@RequestParam(required = false) String $filter) {
        Stream<Person> persons = Stream.of(new Person("Given name", "Family name"));
        
        return oDataFilterService.from(persons, $filter);
    }
}
```

```
GET ~/persons?$filter=familyName eq 'Family name'
```
