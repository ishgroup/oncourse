import org.apache.commons.lang.time.DateUtils

def now = new Date()
def reportStart = DateUtils.addMonths(now, -12).clearTime()

records.each { Qualification q ->
    def enrolments = q.certificates*.outcomes*.enrolment
    def classes = enrolments*.courseClass

    csv << [
            "Code"                        		      : q.nationalCode,
            "Qualification Title"                     : q.title,
            "Number of Current Enrolments"            : q.courses*.courseClasses.flatten().findAll{ it.endDateTime > now }*.enrolments.size(),
            "Number Quals issued in last 12 months"   : q.certificates.findAll{ it.createdOn > reportStart && !it.revokedOn }.size(),
            "Principal Funding Source"                : enrolments*.fundingSource*.displayName?.flatten()?.countBy{ it }?.max{ it.value }?.key ?: "No data in last 12 months",
            "Delivery Venues"					      : classes*.room*.site.flatten().collect{ it.suburb + "-" + it.state }.unique().join("; ") ?: "No data in last 12 months",
            "Principal Delivery Mode"                 : classes*.deliveryMode?.countBy{ it }?.max{ it.value }?.key?.displayName?.get(0) ?: "No data in last 12 months",
            "Partnership / Subcontract Arrangements"  : "",
            "Principal Client Cohort"                 : "",
            "Comments"                                : ""
    ]
}
