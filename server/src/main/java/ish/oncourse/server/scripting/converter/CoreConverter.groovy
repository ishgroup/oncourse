package ish.oncourse.server.scripting.converter

abstract class CoreConverter {

    protected String content

    CoreConverter() {
    }

    abstract String convert()
}
