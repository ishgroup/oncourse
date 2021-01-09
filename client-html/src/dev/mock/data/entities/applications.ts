import { generateArraysOfRecords } from "../../mockUtils";

export function mockApplications() {
  this.getApplications = () => this.applications;

  this.getApplication = id => {
    const row = this.applications.rows.find(row => row.id == id);
    return {
      id: row.id,
      source: row.values[0],
      studentName: row.values[1],
      courseName: row.values[2],
      createdOn: row.values[3],
      applicationDate: row.values[3],
      enrolBy: row.values[3],
      modifiedOn: row.values[3],
      status: "Withdrawn",
      feeOverride: row.values[5],
      contactId: 1,
      courseId: 1,
      createdBy: null,
      customFields: {},
      documents: [],
      notes: [],
      reason: "Your application has been approved as being eligible to pay the second qualification fee.",
      tags: [this.getTag(1)]
    };
  };

  this.createApplication = item => {
    const data = JSON.parse(item);
    const applications = this.applications;
    const totalRows = applications.rows;

    data.id = totalRows.length + 1;

    applications.rows.push({
      id: data.id,
      values: [data.source, data.studentName, data.courseName, data.createdOn, data.status, data.feeOverride]
    });

    this.applications = applications;
  };

  this.mockedCreateApplication = () => ({
    id: 21,
    source: "source 21",
    studentName: "studentName 21",
    courseName: "courseName 21",
    createdOn: "2021-01-09T11:42:23.549Z",
    applicationDate: "2021-01-09T11:42:23.549Z",
    enrolBy: "2021-01-09T11:42:23.549Z",
    modifiedOn: "2021-01-09T11:42:23.549Z",
    status: "Withdrawn",
    feeOverride: "feeOverride 21",
    contactId: 1,
    courseId: 1,
    createdBy: null,
    customFields: {},
    documents: [],
    notes: [],
    reason: "Your application has been approved as being eligible to pay the second qualification fee.",
    tags: [this.getTag(1)]
  });

  this.removeApplication = id => {
    this.applications.rows = this.applications.rows.filter(a => a.id !== id);
  };

  const rows = generateArraysOfRecords(20, [
    { name: "id", type: "number" },
    { name: "source", type: "string" },
    { name: "studentName", type: "string" },
    { name: "courseName", type: "string" },
    { name: "createdOn", type: "Datetime" },
    { name: "status", type: "string" },
    { name: "feeOverride", type: "number" }
  ]).map(l => ({
    id: l.id,
    values: [l.source, l.studentName, l.courseName, l.createdOn, "Withdrawn", l.feeOverride]
  }));

  const columns = [
    {
      title: "Source",
      attribute: "source",
      sortable: true,
      visible: true,
      width: 200,
      type: null,
      sortFields: []
    },
    {
      title: "Student",
      attribute: "student.contact.fullName",
      sortable: true,
      visible: true,
      width: 300,
      type: null,
      sortFields: ["student.contact.lastName", "student.contact.firstName", "student.contact.middleName"]
    },
    {
      title: "Course",
      attribute: "course.name",
      sortable: true,
      visible: true,
      width: 200,
      type: null,
      sortFields: []
    },
    {
      title: "Date of application",
      attribute: "createdOn",
      sortable: true,
      visible: true,
      width: 100,
      type: "Datetime",
      sortFields: []
    },
    {
      title: "Status",
      attribute: "displayStatus",
      sortable: true,
      visible: true,
      width: 200,
      type: null,
      sortFields: ["status"]
    },
    {
      title: "Fee Override",
      attribute: "feeOverride",
      sortable: true,
      visible: true,
      width: 200,
      type: "Money",
      sortFields: []
    }
  ];

  const response = { rows, columns } as any;

  response.entity = "Application";
  response.offset = 0;
  response.filterColumnWidth = 200;
  response.layout = "Three column";
  response.pageSize = 20;
  response.search = "( ((status == NEW) and ((enrolBy >= today) or (enrolBy == null))) or ((status == IN_PROGRESS) and ((enrolBy >= today) or (enrolBy == null))) or ((status == OFFERED) and ((enrolBy >= today) or (enrolBy == null))) or ((status == ACCEPTED)) or ((status == REJECTED)) or ((status == WITHDRAWN)) )";
  response.count = rows.length;
  response.filteredCount = rows.length;
  response.sort = [{ attribute: "source", ascending: true, complexAttribute: [] }];

  return response;
}
