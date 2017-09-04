grammar Parameter;

parameter:
    ID '=' VALUE
    ;

ID:
      'minResolution'
    | 'maxDeviceWidth'
    | 'maxDeviceHeight'
    | 'routingSpacing'
    | 'portSpacing'
    | 'channelSpacing'
    | 'valveSpacing'
    | 'componentSpacing'
    ;

VALUE:
    [A-Za-z0-9.]+
    ;

WS:
    [ \t\r\n]+ -> skip
    ;

COMMENT:
    '%' ~ [\r\n]* -> skip
    ;
