import ish.oncourse.server.cayenne.Contact
import ish.oncourse.server.cayenne.PortalWebsite
import org.apache.cayenne.query.ObjectSelect
import org.apache.http.HttpStatus

def portalSubdomain = ObjectSelect.query(PortalWebsite).selectFirst(context)

if (portalSubdomain) {
    def todayEnrolments = query {
        entity "Enrolment"
        query "createdOn yesterday"
    }

    def contacts = todayEnrolments.student.contact.unique().sort { it.id }
    contacts = checkRemoteRequiredIds(contacts)

    contacts.each { contact ->
        message {
            template "ish.email.studentPortalInvitation"
            record contact.student
            "subDomain" portalSubdomain.subDomain
        }
    }
}


List<Contact> checkRemoteRequiredIds(List<Contact> contactsToCheck) {
    HttpURLConnection postConnection = null
    if (contactsToCheck.empty)
        return contactsToCheck

    contactsToCheck.sort { it.id }

    try {
        def host = servicesHost as String
        if(!host.endsWith("/"))
            host+="/"

        def postmanGet = new URL("${host}services/ish.contact?college=${collegeKey}&key=${servicesKey}")
        postConnection = (HttpURLConnection) postmanGet.openConnection()

        postConnection.setDoOutput(true)
        postConnection.setRequestMethod("POST")
        postConnection.setRequestProperty("Content-Type", "text/plain")
        postConnection.getOutputStream().write(contactsToCheck.id.join(",").getBytes())

        def content = postConnection.inputStream.readLines().first()
        if (postConnection.getResponseCode() != HttpStatus.SC_OK) {
            throw new Exception("Error connecting remote services : " + content)
        }

        def receivedIds = content.split(",").collect {Long.parseLong(it)}.toSet()
        return contactsToCheck.findAll {receivedIds.contains(it.id)}
    } finally {
        if (postConnection && postConnection.inputStream)
            postConnection.inputStream.close()
    }
}
