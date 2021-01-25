import { generateArraysOfRecords, getEntityResponse } from "../../mockUtils";

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

  this.getPlainEnrolments = params => {
    const columnList = params.columns.split(",");
    const ids = params.search.replace(/(id in|\(|\))/g, '').trim().split(",");

    let rows = [];

    if (columnList.length) {
      if (columnList.includes("status")) {
        ids.forEach(id => {
          rows.push({
            id,
            values: ["Active"]
          });
        });
      } else if (columnList.includes("invoiceLine.invoice.invoiceNumber")) {
        rows = generateArraysOfRecords(20, [
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
      }
    } else {
      rows = generateArraysOfRecords(20, [
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
    }

    return getEntityResponse(
      "Enrolment",
      rows,
      [],
      {
        filterColumnWidth: null,
        layout: null
      }
    );
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

  return getEntityResponse(
    "Enrolment",
    rows,
    [
      {
        title: "Source",
        attribute: "source",
        sortable: true,
        width: 100
      },
      {
        title: "Name",
        attribute: "student.contact.fullName",
        sortable: true
      },
      {
        title: "Class",
        attribute: "courseClass.uniqueCode",
        sortable: true,
        width: 138
      },
      {
        title: "Course name",
        attribute: "courseClass.course.name",
        sortable: true
      },
      {
        title: "Status",
        attribute: "status",
        sortable: true,
        width: 100
      },
      {
        title: "Enrolled",
        attribute: "createdOn",
        sortable: true,
        type: "Datetime"
      }
    ],
    {
      sort: [
        {
          ascending: true,
          attribute: "source",
          complexAttribute: []
        }
      ]
    }
  );
}
