import { generateArraysOfRecords } from "../../mockUtils";

export function mockEnrolments() {
  this.getEnrolments = () => this.enrolments;

  this.getEnrolment = id => {
    const row = this.enrolments.rows.find(row => row.id == id);
    return {
      id: row.id,
      associatedCourseIdentifier: null,
      attendanceType: "QLD - Non-concessional participant, WA - Health Care Card (N)",
      clientIdentifier: null,
      courseClassName: row.values[3],
      courseClassId: 1,
      creditOfferedValue: null,
      creditUsedValue: null,
      creditFOEId: null,
      creditProvider: null,
      creditProviderType: null,
      creditType: null,
      creditLevel: "Diploma",
      cricosConfirmation: null,
      customFields: {},
      eligibilityExemptionIndicator: false,
      feeExemption: "Not set",
      feeCharged: 550,
      feeHelpAmount: 0,
      fundingSource: "Domestic full fee paying student",
      fundingContractId: null,
      fundingContractName: null,
      fundingSourceState: null,
      fullTime: false,
      loanFee: 0,
      loanTotal: 0,
      source: row.values[0],
      status: row.values[4],
      studentName: row.values[1],
      studentContactId: 1,
      studyReason: "Not stated",
      suppressAvetmissExport: false,
      trainingPlanDeveloped: null,
      vetPurchasingContractIdentifier: "2",
      vetInSchools: null,
      vetTrainingContractID: null,
      vetFeeIndicator: false,
      documents: [],
      notes: [],
      tags: [this.getTag(1)]
    };
  };

  this.createEnrolment = item => {
    const data = JSON.parse(item);
    const enrolments = this.enrolments;
    const totalRows = enrolments.rows;

    data.id = totalRows.length + 1;

    enrolments.rows.push({
      id: data.id,
      values: [data.source, data.studentName, data.classCode, data.courseClassName, data.status, data.createdOn]
    });

    this.enrolments = enrolments;
  };

  this.removeEnrolment = id => {
    this.enrolments = this.enrolments.rows.filter(a => a.id !== id);
  };

  this.getPlainEnrolments = () => {
    const rows = generateArraysOfRecords(20, [
      { name: "id", type: "number" },
      { name: "invoiceNumber", type: "number" },
      { name: "createdOn", type: "Datetime" },
      { name: "uniqueCode", type: "string" },
      { name: "courseName", type: "string" },
      { name: "status", type: "string" }
    ]).map(l => ({
      id: l.id,
      values: [l.invoiceNumber, "2021-01-16T06:31:09.463Z", l.uniqueCode, l.courseName, l.status]
    }));

    const columns = [];

    const response = { rows, columns } as any;

    response.entity = "Enrolment";
    response.offset = 0;
    response.filterColumnWidth = null;
    response.layout = null;
    response.pageSize = 20;
    response.search = null;
    response.count = rows.length;
    response.filteredCount = rows.length;
    response.sort = [];

    return response;
  };

  const rows = generateArraysOfRecords(20, [
    { name: "id", type: "number" },
    { name: "source", type: "string" },
    { name: "studentName", type: "string" },
    { name: "classCode", type: "string" },
    { name: "courseClassName", type: "string" },
    { name: "status", type: "string" },
    { name: "createdOn", type: "Datetime" }
  ]).map(l => ({
    id: l.id,
    values: [l.source, l.studentName, l.classCode, l.courseClassName, l.status, l.createdOn]
  }));

  const columns = [
    {
      title: "Source",
      attribute: "source",
      sortable: true,
      visible: true,
      width: 100,
      type: null,
      sortFields: []
    },
    {
      title: "Name",
      attribute: "student.contact.fullName",
      sortable: true,
      visible: true,
      width: 200,
      type: null,
      sortFields: []
    },
    {
      title: "Class",
      attribute: "courseClass.uniqueCode",
      sortable: true,
      visible: true,
      width: 138,
      type: null,
      sortFields: []
    },
    {
      title: "Course name",
      attribute: "courseClass.course.name",
      sortable: true,
      visible: true,
      width: 200,
      type: null,
      sortFields: []
    },
    {
      title: "Status",
      attribute: "status",
      sortable: true,
      visible: true,
      width: 100,
      type: null,
      sortFields: []
    },
    {
      title: "Enrolled",
      attribute: "createdOn",
      sortable: true,
      visible: true,
      width: 200,
      type: "Datetime",
      sortFields: []
    }
  ];

  const response = { rows, columns } as any;

  response.entity = "Enrolment";
  response.offset = 0;
  response.filterColumnWidth = 200;
  response.layout = "Three column";
  response.pageSize = 20;
  response.search = null;
  response.count = rows.length;
  response.filteredCount = rows.length;
  response.sort = [
    {
      ascending: true,
      attribute: "source",
      complexAttribute: []
    }
  ];

  return response;
}
