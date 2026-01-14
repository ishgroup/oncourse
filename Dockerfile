FROM eclipse-temurin:11-jdk-alpine

WORKDIR /app

COPY artifacts/unpack/ .

ENTRYPOINT ["/bin/sh", "-c", "\
  exec java \
    -server -Djava.awt.headless=true \
    -Duser.timezone=Australia/Sydney \
    -Dlog4j2.configurationFile=/app/logSetup.xml \
    -Dfile.encoding=UTF-8 \
    -Dsun.jnu.encoding=UTF-8 \
    -classpath lib/onCourseServer.jar:plugins/* \
    ish.oncourse.server.AngelServer HEADLESS=true"]
