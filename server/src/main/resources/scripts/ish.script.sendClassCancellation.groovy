import groovy.time.TimeCategory

def last5Mins
use(TimeCategory) {
    last5Mins = new Date() - 5.minutes
}

records = Enrolment.STATUS.in(EnrolmentStatus.CANCELLED, EnrolmentStatus.REFUNDED).andExp(Enrolment.MODIFIED_ON.gte(last5Mins)).filterObjects(record.getEnrolments())

message {
    template cancellationTemplate
    record records
}