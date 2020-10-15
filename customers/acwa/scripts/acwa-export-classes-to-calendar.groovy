/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import groovy.transform.ToString

import javax.activation.DataHandler
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMultipart
import javax.mail.Multipart
import javax.mail.util.ByteArrayDataSource

import ish.oncourse.server.scripting.api.CollegePreferenceService
import ish.oncourse.server.scripting.api.EmailService
import groovy.time.TimeCategory

class iCal {
    String m_version = "1.0"
    String m_prodid = "-//ish onCourse//Sessions Calendar//EN"
    Date m_dtStamp = new Date()
    List sessions = new ArrayList()

    iCal() { }

    void addSession(session) {
        this.sessions.add(session)
    }


    String toString() {
        def b = new StringBuffer()
        b.append("BEGIN:VCALENDAR\r\n")
        b.append("VERSION:"+ m_version + "\r\n")
        b.append("PRODID:" + m_prodid + "\r\n")

        b.append(this.sessionsToString())

        b.append("END:VCALENDAR\r\n")

        return b.toString()
    }

    String sessionsToString() {
        def b = new StringBuffer()

        for (session in sessions) {
            b.append("BEGIN:VEVENT\r\n")
            b.append("UID:" + session.id + "\r\n")
            b.append("SUMMARY:"+ session.courseClass.course.name + "\r\n")

            if (session.room?.name != null) {
                b.append("LOCATION:"+ session.room?.name + "\r\n")
            }
            b.append("DESCRIPTION:" + session.courseClass.notes + "\r\n" )
            b.append("DTSTART;VALUE=DATE:" + session.startDatetime.format("YYYYMMdd") + "T" + session.startDatetime.format("HHmmss") + "\r\n" )
            b.append("DTEND;VALUE=DATE:" + session.endDatetime.format("YYYYMMdd") + "T" + session.endDatetime.format("HHmmss") + "\r\n")
            b.append("END:VEVENT\r\n")
        }
        return b.toString()
    }

}
def run(args) {

    CourseClass courseClass = args.entity

    if (!courseClass || courseClass.isDistantLearningCourse || courseClass.sessionsCount < 1) {
        return
    }

    def calendar = new iCal()

    courseClass.sessions.each { s ->
        calendar.addSession(s)
    }

    String filePrefix = 'icalExport-' + courseClass.course.name
    File exportResult = File.createTempFile(filePrefix, ".ics")

    try {
        String toEmailAddress = preference.email.admin
        //String toEmailAddress = "jackson@ish.com.au"


        FileWriter fileWriter = new FileWriter(exportResult)
        fileWriter << calendar.toString()
        fileWriter.flush()

        sendEmail(exportResult, toEmailAddress, courseClass.uniqueCode)
        fileWriter.close()

    } finally {
        exportResult.deleteOnExit()
    }
}

void sendEmail(File exportResult, String toEmailAddress, String courseName) {
    def email = Email.create()

    email.from(Preferences.get("email.from"))
    email.to(toEmailAddress)
    email.subject("${courseName} sessions for Calendar import")

    def dataSource = new ByteArrayDataSource(new FileInputStream(exportResult), "application/octet-stream")
    def dataHandler = new DataHandler(dataSource)

    def fileBodyPart = new MimeBodyPart()
    fileBodyPart.setDataHandler(dataHandler)
    fileBodyPart.setFileName(courseName + ".ics")
    fileBodyPart.setHeader("Content-Transfer-Encoding", "base64")

    String message = "Attached iCalendar events exported for ${courseName}"

    def messageBodyPart = new MimeBodyPart()
    messageBodyPart.setContent(message,"text/html")
    email.addPart(messageBodyPart)
    email.addPart(fileBodyPart)

    email.send()
}
