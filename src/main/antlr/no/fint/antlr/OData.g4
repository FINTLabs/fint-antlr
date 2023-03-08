grammar OData;

@header{
    package no.fint.antlr;
}

comparison
   : (notOperator ' ')? property ' ' comparisonOperator ' ' value
   ;

lambda
   : (notOperator ' ')? collection '/' lambdaOperator '(' STRING ':' STRING '/' comparison ')'
   ;

filter
    : (lambda | comparison) ( ' ' logicalOperator ' ' (lambda | comparison))*
    | EOF
    ;

logicalOperator
    : 'and' | 'or'
    ;

notOperator
    : 'not'
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
   : '-' | '_' | ':' | '/' | '$' | '.' | '{' | '}' | ' ' | NUMERIC | ALPHA
   ;

fragment NUMERIC
   : ('0'..'9')
   ;

fragment ALPHA
   : ('A'..'Z' | 'a'..'z' | 'Æ' | 'æ' | 'Ø' | 'ø' | 'Å' | 'å' | 'Ä' | 'ä' | 'Ö' | 'ö')
   ;

