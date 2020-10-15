/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.ContentType
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClients

def run(args) {
    def enrolment = args.entity

    if (enrolment.status == EnrolmentStatus.SUCCESS && enrolment.courseClass.course.code == "JSA" &&
            enrolment?.invoiceLine?.invoice?.contact?.email != null) {

        def client = HttpClients.createDefault()

        def sendFlowRequest = new HttpPost("https://api.surveymonkey.net/v2/batch/send_flow?api_key=wnpvgghmn2tnba4zr6etu8d5")

        sendFlowRequest.addHeader("Authorization",
                "bearer paTBz61kMlD0mPWrsaHR3921EuYbHlBvqU0GDZQ.5nahBvB1GbdZpbh2WnOzXY4SaovFBY6leML0jYh4WjB4lWjo9OU3FXjpCHITHSCUj4s=")

        sendFlowRequest.addHeader("Content-Type", "application/json")

        def body = new StringEntity(
                "{\n" +
                        "    \"collector\": {\n" +
                        "        \"name\": \"onCourse email invite\",\n" +
                        "        \"recipients\": [\n" +
                        "            {\n" +
                        "                \"email\": \"${enrolment.invoiceLine.invoice.contact.email}\",\n" +
                        "                \"first_name\": \"${enrolment.invoiceLine.invoice.contact.firstName}\",\n" +
                        "                \"last_name\": \"${enrolment.invoiceLine.invoice.contact.lastName}\",\n" +
                        "                \"custom_id\": \"${enrolment.student.studentNumber}\"\n" +
                        "            }\n" +
                        "        ],\n" +
                        "        \"send\": true,\n" +
                        "        \"type\": \"email\"\n" +
                        "    },\n" +
                        "    \"email_message\": {\n" +
                        "        \"body_text\": \"Please complete a short survey to complete the enrolment by Monday, 22 June. \\n[SurveyLink] \\nThe information will be used to deliver and develop the program and ensure the safety of all children. Please complete one survey for each child attending. \\n[RemoveLink]\",\n" +
                        "        \"reply_email\": \"${Preferences.get("email.from")}\",\n" +
                        "        \"subject\": \"Junior Science Academy additional information required\"\n" +
                        "    },\n" +
                        "    \"survey_id\": \"62087474\"\n" +
                        "}", ContentType.APPLICATION_JSON)

        sendFlowRequest.setEntity(body)

        client.execute(sendFlowRequest)
    }
}
