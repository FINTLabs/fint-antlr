grammar OData;

@header{
    package no.fint.antlr;
}

comparison
   : property ' ' comparisonOperator ' ' value
   ;

lambda
   : collection '/' lambdaOperator '(' STRING ':' STRING '/' comparison ')'
   ;

filter
   : ( lambda | comparison ) EOF
   ;

collection
   : property
   ;

property
   : STRING ( '/' STRING )*
   ;

value
   : CHAR
   ;

comparisonOperator
   : 'eq' | 'ne' | 'gt' | 'lt' | 'ge' | 'le' | 'startswith' | 'contains' | 'endswith'
   ;

lambdaOperator
   : 'any' | 'all'
   ;

STRING
   : ALPHA+
   ;

CHAR
   : '\'' CHARS+ '\''
   ;

CHARS
   : '-' | '_' | ':' | '/' | '$' | '.' | '{' | '}' | NUMERIC | ALPHA
   ;

fragment NUMERIC
   : ('0'..'9')
   ;

fragment ALPHA
   : ( 'A'..'Z' | 'a'..'z' )
   ;

