
#run to develop mode
1. copy application.properties from billing-api/src/dist/application.properties to billing-api/application.properties
2. adjus client (billing) to use api url:
billing/src/js/api/constants.ts
`export const CONTEXT: string = "http://127.0.0.1:11900/billing/"
`
3. enable stdout, change billing-api/src/main/resources/log4j2.xml to look like:

`<?xml version="1.0" encoding="UTF-8"?>
<Configuration status="warn" monitorInterval="30">
    <Properties>
        <Property name="name">billing-api</Property>
    </Properties>
    <Appenders>
        <Console name="STDOUT" target="SYSTEM_OUT">
            <PatternLayout>
                <Pattern>[%d{dd/MMM/yyyy:HH:mm:ss}] %t %x %-5p %c{1}: %m%n</Pattern>
            </PatternLayout>
        </Console>
    </Appenders>
    <Loggers>
        <Root level="warn">
            <AppenderRef ref="STDOUT"/>
        </Root>
    </Loggers>
</Configuration>`

5. build api
`./gardlew -x test clean billing-api:build`
6. run server
`./gardlew billing-api:start
`
7. run client: got to /billing/ and run 
`yarn start`