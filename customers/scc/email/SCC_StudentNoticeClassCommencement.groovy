/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

${render("Header")}

Dear ${enrolment.student.contact.firstName},

Just a reminder that your class for ${enrolment.courseClass.course.name} starts at ${enrolment.courseClass.startDateTime.format("h:mm a EEEE d MMMM yyyy zzzz", enrolment.courseClass.getTimeZone())}

<% if (["3 - William Angliss Institute"].contains(enrolment.courseClass?.room?.site?.name)) { %>

The venue for this class is: ${enrolment.courseClass.displayableLocation}.
Please arrive for your class 15 minutes before the start time.
This is a security building, so if you arrive earlier or after the class has started, you may find the front doors locked. If this is the case, please be assured a staff member will check every few minutes to admit new arrivals.
If you need to leave during the class time, please tell your tutor, who will make arrangements for your return.

<% } else { %>

The venue for this class is ${enrolment.courseClass.displayableLocation}.
Please check your room number upon arrival.
<strong>Please note that we are now past the census date for this class and withdrawal with refund is no longer possible</strong>.  See<a href="https://www.sydneycommunitycollege.edu.au/policies/withdrawal"<Withdrawing from Your Enrolment</a>
<% } %>

If you need further information, please call Customer Service on 8752-7555 or simply reply to this email.

Kind regards
the Customer Service Team
Sydney Community College

${render("Footer")}
