import { generateArraysOfRecords, getEntityResponse } from "../../mockUtils";

export function mockSurvey() {
  this.getSurveys = () => this.surveys;

  this.getSurvey = id => {
    const row = this.surveys.rows.find(row => row.id == id);
    return {
      id: row.id,
      studentName: row.values[1],
      netPromoterScore: row.values[4],
      classId: 1,
      className: `${row.values[3]} ${row.values[2]}`,
      comment: "Override my previous comment",
      courseScore: 0,
      customFields: {},
      roomId: 1,
      roomName: "Elder Hall",
      siteId: 1,
      siteName: "Adelaide Campus",
      studentContactId: 1,
      testimonial: null,
      tutorScore: 0,
      tutors: { 1866: "Temple, Hannah" },
      venueScore: 0,
      visibility: "Waiting review"
    };
  };

  const rows = generateArraysOfRecords(20, [
    { name: "id", type: "number" },
    { name: "createdOn", type: "Datetime" },
    { name: "studentName", type: "string" },
    { name: "courseName", type: "string" },
    { name: "className", type: "string" },
    { name: "netPromoterScore", type: "number" },
    { name: "fieldConfigurationName", type: "string" }
  ]).map(l => ({
    id: l.id,
    values: [
      l.createdOn,
      l.studentName,
      l.courseName,
      l.className,
      l.netPromoterScore,
      "Default Field form (Student feedback)"
    ]
  }));

  return getEntityResponse({
    entity: "Survey",
    rows,
    columns: [
      {
        title: "Created on",
        attribute: "createdOn",
        sortable: true,
        type: "Datetime"
      },
      {
        title: "Student name",
        attribute: "enrolment.student.contact.fullName",
        sortable: true,
        sortFields: ["enrolment.student.contact.lastName", "enrolment.student.contact.firstName"]
      },
      {
        title: "Course",
        attribute: "enrolment.courseClass.course.name",
        sortable: true
      },
      {
        title: "Class",
        attribute: "enrolment.courseClass.uniqueCode",
        sortable: true,
        sortFields: ["enrolment.courseClass.course.code", "enrolment.courseClass.code"]
      },
      {
        title: "Net promoter score",
        attribute: "netPromoterScore",
        sortable: true
      },
      {
        title: "Field configuration",
        attribute: "fieldConfiguration.name",
        sortable: true
      }
    ],
    res: {
      sort: [{ attribute: "createdOn", ascending: true, complexAttribute: [] }]
    }
  });
}
