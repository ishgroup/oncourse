import org.apache.commons.lang3.time.DateUtils
def run(args) {

  EMAIL_ADDRESS = "jacqui.davey@westernpower.com.au"
  RELATION_TYPE_ID = 280
    
  def fromDate = Calendar.instance
  fromDate.set(hourOfDay: 0, minute: 0, second: 0)
  while (fromDate.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
    fromDate.add(Calendar.DAY_OF_WEEK, +1)
  }

  fromDate = fromDate.getTime()
  def toDate = fromDate + 7

  // get sessions happening in the next week
  foundSessions = ObjectSelect.query(Session.class)
          .where(Session.START_DATETIME.between(fromDate, toDate))
          .select(args.context)

  // find contacts for those enrolments
  def contacts = foundSessions*.courseClass*.enrolments*.student*.contact.flatten().unique()

  def reportingGroups = [:]

  // group the students into a map with the key being the contact responsible for a group of students
  contacts.each { c -> 
    relatedContacts = c?.getFromContacts().findAll{ it.relationType.id == RELATION_TYPE_ID }*.getFromContact().flatten()

    if (relatedContacts.size() == 1) {
      Contact group = relatedContacts[0]
      if (!reportingGroups[group]) {
        reportingGroups[group] = []
      }
      reportingGroups[group] << c
    }
  }

    smtp {
      subject "weekly reminders sent"
      to EMAIL_ADDRESS
      content "Sent reminder emails for upcoming week's schedule to " + reportingGroups.size() + " people."
    }

  // send an email to each group with a list of the sessions for the students they are responsible for
  def allAttendance = foundSessions*.attendance.flatten().unique()

  reportingGroups.each { group, contactList ->

    attendance = allAttendance.findAll { contactList.contains(it.student.contact) }

    if (attendance.size() > 0) {

      smtp {
        subject "PTS timetable for week commencing " + fromDate.format("EEE dd MMM yyyy")
        to (group?.email) ?  group.email : EMAIL_ADDRESS
        content "PTS will be delivering training to your team in the coming week. Please find attached training enrolments for your group for week commencing " + fromDate.format("EEE dd MMM yyyy") \
          + "\n" + "\n\n" + "Email sent to " + group.fullName + " at " + group.email
        attachment fromDate.format("yyyy-MM-dd") + ".csv", "text/csv", export {
          template "wp.sessions.csv"
          records attendance
        }
      }
    }
  }
}