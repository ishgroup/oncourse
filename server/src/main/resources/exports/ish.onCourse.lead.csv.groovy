import org.apache.commons.lang3.StringUtils

records.each { Lead lead ->
    csv << [
            "created"        : lead.createdOn?.format("yyyy-MM-dd'T'HH:mm:ssXXX"),
            "Student count"  : lead.studentCount,
            "Notes"          : lead.notes,
            "Student notes"  : lead.studentNotes,
            "Estimated value": lead.estimatedValue,
            "Next action on" : lead.nextActionOn?.format("yyyy-MM-dd'T'HH:mm:ssXXX"),
            "Notification"   : lead.notify,
            "Status"         : lead.status,
            "Title"          : lead.customer.title,
            "First name"     : lead.customer.firstName,
            "Last name"      : lead.customer.lastName,
            "Email"          : lead.customer.email,
            "Street"         : lead.customer.street,
            "Suburb"         : lead.customer.suburb,
            "State"          : lead.customer.state,
            "Post code"      : lead.customer.postcode,
            "Mobile phone"   : lead.customer.mobilePhone,
            "Home phone"     : lead.customer.homePhone,
            "Work phone"     : lead.customer.workPhone,
            "Birth date"     : lead.customer.birthDate?.format("yyyy-MM-dd"),
            "Courses"        : StringUtils.join(lead.courses.collect {it.name}, ','),
            "Products"       : StringUtils.join(lead.products.collect {it.name}, ','),
            "Assigned to"    : lead.assignedTo?.email
    ]
}