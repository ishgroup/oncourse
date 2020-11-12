import { generateArraysOfRecords } from "../../mockUtils";

export function mockSurvey() {
  this.getSurveys = () => {
    return this.surveys;
  };

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

  this.removeSurvey = id => {
    this.surveys.rows = this.surveys.rows.filter(a => a.id !== id);
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

  const columns = [
    {
      title: "Created on",
      attribute: "createdOn",
      sortable: true,
      visible: true,
      width: 200,
      type: "Datetime",
      sortFields: []
    },
    {
      title: "Student name",
      attribute: "enrolment.student.contact.fullName",
      sortable: true,
      visible: true,
      width: 200,
      type: null,
      sortFields: ["enrolment.student.contact.lastName", "enrolment.student.contact.firstName"]
    },
    {
      title: "Course",
      attribute: "enrolment.courseClass.course.name",
      sortable: true,
      visible: true,
      width: 200,
      type: null,
      sortFields: []
    },
    {
      title: "Class",
      attribute: "enrolment.courseClass.uniqueCode",
      sortable: true,
      visible: true,
      width: 200,
      type: null,
      sortFields: ["enrolment.courseClass.course.code", "enrolment.courseClass.code"]
    },
    {
      title: "Net promoter score",
      attribute: "netPromoterScore",
      sortable: true,
      visible: true,
      width: 200,
      type: null,
      sortFields: []
    },
    {
      title: "Field configuration",
      attribute: "fieldConfiguration.name",
      sortable: true,
      visible: true,
      width: 200,
      type: null,
      sortFields: []
    }
  ];

  const response = { rows, columns } as any;

  response.entity = "Survey";
  response.offset = 0;
  response.filterColumnWidth = 200;
  response.layout = "Three column";
  response.pageSize = 20;
  response.search = null;
  response.count = rows.length;
  response.filteredCount = rows.length;
  response.sort = [{ attribute: "createdOn", ascending: true, complexAttribute: [] }];

  return response;
}
