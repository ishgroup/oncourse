import { generateArraysOfRecords } from "../../mockUtils";

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
      studentDateOfBirth: null,
      studentSuburb: null
    };
  };

  this.getPlainCertificates = params => {
    let rows;
    const searchedColumns = params.columns.split(",");
    let columns = [];
    if (searchedColumns.includes("revokedOn")) {
      rows = generateArraysOfRecords(1, [
        { name: "id", type: "number" },
        { name: "revokedOn", type: "Datetime" }
      ]).map(l => ({
        id: l.id,
        values: [null]
      }));
      columns = searchedColumns;
    } else {
      rows = generateArraysOfRecords(20, [
        { name: "id", type: "number" },
        { name: "revokedOn", type: "Datetime" }
      ]).map(l => ({
        id: l.id,
        values: [l.revokedOn]
      }));
    }

    const response = { rows, columns } as any;

    response.entity = "Certificate";
    response.offset = 0;
    response.filterColumnWidth = null;
    response.layout = null;
    response.pageSize = rows.length;
    response.search = null;
    response.count = null;
    response.sort = [];

    return response;
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
    this.certificates.rows = this.certificates.rows.filter(a => a.id !== id);
  };

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

  const columns = [
    {
      title: "Student name",
      attribute: "student.contact.fullName",
      sortable: true,
      visible: true,
      width: 200,
      type: null,
      sortFields: ["student.contact.lastName", "student.contact.firstName", "student.contact.middleName"]
    },
    {
      title: "Full qualification",
      attribute: "isQualification",
      sortable: true,
      visible: true,
      width: 200,
      type: "Boolean",
      sortFields: []
    },
    {
      title: "Qualification code",
      attribute: "qualification.nationalCode",
      sortable: false,
      visible: true,
      width: 200,
      type: null,
      sortFields: []
    },
    {
      title: "Qualification level",
      attribute: "qualification.level",
      sortable: false,
      visible: true,
      width: 200,
      type: null,
      sortFields: []
    },
    {
      title: "Qualification title",
      attribute: "qualification.title",
      sortable: false,
      visible: true,
      width: 200,
      type: null,
      sortFields: []
    },
    {
      title: "Certificate number",
      attribute: "certificateNumber",
      sortable: true,
      visible: true,
      width: 200,
      type: null,
      sortFields: []
    },
    {
      title: "Awarded On",
      attribute: "awardedOn",
      sortable: true,
      visible: true,
      width: 200,
      type: "Datetime",
      sortFields: []
    },
    {
      title: "Issued On",
      attribute: "issuedOn",
      sortable: true,
      visible: true,
      width: 200,
      type: "Datetime",
      sortFields: []
    },
    {
      title: "Revoked On",
      attribute: "revokedOn",
      sortable: true,
      visible: true,
      width: 200,
      type: "Datetime",
      sortFields: []
    }
  ];

  const response = { rows, columns } as any;

  response.entity = "Certificate";
  response.offset = 0;
  response.filterColumnWidth = 200;
  response.layout = "Three column";
  response.pageSize = 20;
  response.search = "";
  response.count = rows.length;
  response.filteredCount = rows.length;
  response.sort = [{ attribute: "awardedOn", ascending: true, complexAttribute: [] }];

  return response;
}
