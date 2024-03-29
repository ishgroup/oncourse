import { generateArraysOfRecords, getEntityResponse, removeItemByEntity } from "../../mockUtils";

export function mockCertificates() {
  this.getCertificates = () => this.certificates;

  this.getCertificate = id => {
    const row = this.certificates.rows.find(row => row.id == id);
    return {
      id: row.id,
      studentName: row.values[0],
      isQualification: row.values[1],
      nationalCode: row.values[2],
      level: row.values[3],
      title: row.values[4],
      number: row.values[5],
      awardedOn: row.values[6],
      printedOn: row.values[6],
      issuedOn: row.values[7],
      createdOn: row.values[7],
      modifiedOn: row.values[7],
      revokedOn: row.values[8],
      code: "cNwB7eb4yFsy",
      expiryDate: "2022-01-10T06:22:29.271Z",
      outcomes: [
        {
          code: "TAAENV402B",
          id: 1,
          issueDate: null,
          name: "Foster and promote an inclusive learning culture"
        },
        {
          code: "TAAENV401B",
          id: 2,
          issueDate: null,
          name: "Work effectively in vocational education and training"
        }
      ],
      privateNotes: "",
      publicNotes: "",
      qualificationId: 1,
      studentContactId: 1,
      studentDateOfBirth: null,
      studentSuburb: null
    };
  };

  this.getPlainCertificates = params => {
    let rows;
    const searchedColumns = params.columns.split(",");
    if (searchedColumns.includes("revokedOn")) {
      rows = generateArraysOfRecords(1, [
        { name: "id", type: "number" },
        { name: "revokedOn", type: "Datetime" }
      ]).map(l => ({
        id: l.id,
        values: [null]
      }));
    } else {
      rows = generateArraysOfRecords(20, [
        { name: "id", type: "number" },
        { name: "isQualification", type: "boolean" },
        { name: "nationalCode", type: "string" },
        { name: "title", type: "string" },
        { name: "certificateNumber", type: "number" },
        { name: "createdOn", type: "Datetime" },
        { name: "printedOn", type: "Datetime" },
        { name: "level", type: "string" }
      ]).map(l => ({
        id: l.id,
        values: [
          true,
          l.nationalCode,
          l.title,
          l.certificateNumber,
          "2022-01-08T06:22:29.271Z",
          "2022-01-08T06:22:29.271Z",
          l.level
        ]
      }));
    }

    return getEntityResponse({
      entity: "Certificate",
      rows,
      plain: true
    });
  };

  this.createCertificate = item => {
    const data = JSON.parse(item);
    const certificates = this.certificates;
    const totalRows = certificates.rows;

    data.id = totalRows.length + 1;

    certificates.rows.push({
      id: data.id,
      values: [
        data.studentName,
        data.isQualification,
        data.nationalCode,
        data.level,
        data.title,
        data.number,
        data.awardedOn,
        data.issuedOn,
        data.revokedOn
      ]
    });

    this.certificates = certificates;
  };

  this.createNewCertificate = (id = 21) => ({
    id,
    studentName: `studentName ${id}`,
    isQualification: true,
    nationalCode: `nationalCode ${id}`,
    level: `level ${id}`,
    title: `title ${id}`,
    "number": 10,
    awardedOn: "2013-04-16",
    printedOn: "2013-04-16",
    issuedOn: "2013-04-16",
    createdOn: "2013-04-16T05:29:36.000Z",
    modifiedOn: "2013-04-16T05:29:36.000Z",
    revokedOn: null,
    code: "cNwB7eb4yFsy",
    expiryDate: null,
    outcomes: [
      {
        code: "TAAENV402B",
        id: 1,
        issueDate: null,
        name: "Foster and promote an inclusive learning culture"
      },
      {
        code: "TAAENV401B",
        id: 2,
        issueDate: null,
        name: "Work effectively in vocational education and training"
      }
    ],
    privateNotes: null,
    publicNotes: null,
    qualificationId: 1,
    studentContactId: 1,
    studentDateOfBirth: "1986-05-16",
    studentSuburb: "Newtown"
  });

  this.removeCertificate = id => {
    this.certificates = removeItemByEntity(this.certificates, id);
  };

  this.validateCertificateUSIRequest = () => ({
    "search": "id == \"260\"",
    "filter": "",
    "tagGroups": [

    ],
    "sorting": [
      {
        "attribute": "awardedOn",
        "ascending": true,
        "complexAttribute": [

        ]
      }
    ]
  });

  this.validateCertificateStatus = () => "One of selected users has no USI.";

  const rows = generateArraysOfRecords(20, [
    { name: "id", type: "number" },
    { name: "studentName", type: "string" },
    { name: "isQualification", type: "boolean" },
    { name: "nationalCode", type: "string" },
    { name: "level", type: "string" },
    { name: "title", type: "string" },
    { name: "number", type: "number" },
    { name: "awardedOn", type: "Datetime" },
    { name: "issuedOn", type: "Datetime" },
    { name: "revokedOn", type: "Datetime" }
  ]).map(l => ({
    id: l.id,
    values: [
      l.studentName,
      l.isQualification,
      l.nationalCode,
      l.level,
      l.title,
      l.number,
      l.awardedOn,
      l.issuedOn,
      null
    ]
  }));

  return getEntityResponse({
    entity: "Certificate",
    rows,
    columns: [
      {
        title: "Student name",
        attribute: "student.contact.fullName",
        sortable: true,
        sortFields: ["student.contact.lastName", "student.contact.firstName", "student.contact.middleName"]
      },
      {
        title: "Full qualification",
        attribute: "isQualification",
        sortable: true,
        type: "Boolean"
      },
      {
        title: "Qualification code",
        attribute: "qualification.nationalCode"
      },
      {
        title: "Qualification level",
        attribute: "qualification.level"
      },
      {
        title: "Qualification title",
        attribute: "qualification.title"
      },
      {
        title: "Certificate number",
        attribute: "certificateNumber",
        sortable: true
      },
      {
        title: "Awarded On",
        attribute: "awardedOn",
        sortable: true,
        type: "Datetime"
      },
      {
        title: "Issued On",
        attribute: "issuedOn",
        sortable: true,
        type: "Datetime"
      },
      {
        title: "Revoked On",
        attribute: "revokedOn",
        sortable: true,
        type: "Datetime"
      }
    ],
    res: {
      sort: [{ attribute: "awardedOn", ascending: true, complexAttribute: [] }]
    }
  });
}
