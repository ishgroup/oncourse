import { generateArraysOfRecords } from "../../mockUtils";

export function mockOutcomes() {
  this.getOutcomes = () => this.outcomes;

  this.getPlainOutcomes = () => {
    const rows = generateArraysOfRecords(20, [
      { name: "id", type: "number" },
      { name: "nationalCode", type: "string" },
      { name: "course", type: "string" },
      { name: "status", type: "string" },
      { name: "startDate", type: "Datetime" },
      { name: "endDate", type: "Datetime" },
      { name: "deliveryMode", type: "string" }
    ]).map(l => ({
      id: l.id,
      values: [l.nationalCode, l.course, "RCC granted (53)", "2018-04-17T12:18:05.000Z", "2018-04-17T12:18:05.000Z", "Online"]
    }));

    const columns = [];

    const response = { rows, columns } as any;

    response.entity = "Outcome";
    response.offset = 0;
    response.filterColumnWidth = null;
    response.layout = null;
    response.pageSize = rows.length;
    response.search = null;
    response.count = null;
    response.sort = [];

    return response;
  };

  this.getOutcome = id => {
    const row = this.outcomes.rows.find(row => row.id == id);
    return {
      id: row.id,
      studentName: row.values[0],
      moduleCode: row.values[3],
      moduleName: row.values[4],
      startDate: row.values[6],
      endDate: row.values[7],
      reportableHours: 1,
      deliveryMode: "Classroom",
      fundingSource: "Domestic full fee paying student",
      status: "RCC granted (53)",
      hoursAttended: 1,
      purchasingContractIdentifier: "test purchasing contract identifier",
      fundingSourceState: "test funding source state",
      specificProgramIdentifier: "test specific program identifier"
    };
  };

  const rows = generateArraysOfRecords(20, [
    { name: "id", type: "number" },
    { name: "studentName", type: "string" },
    { name: "name", type: "string" },
    { name: "code", type: "string" },
    { name: "moduleCode", type: "string" },
    { name: "moduleName", type: "string" },
    { name: "status", type: "string" },
    { name: "startDate", type: "Datetime" },
    { name: "endDate", type: "Datetime" }
  ]).map(l => ({
    id: l.id,
    values: [l.studentName, l.name, l.code, l.moduleCode, l.moduleName, l.status, l.startDate, l.endDate]
  }));

  const columns = [
    {
      title: "Student Name",
      attribute: "studentName",
      sortable: true,
      visible: true,
      width: 100,
      type: null,
      sortFields: []
    },
    {
      title: "Course",
      attribute: "enrolment.courseClass.course.name",
      sortable: true,
      visible: true,
      width: 100,
      type: null,
      sortFields: []
    },
    {
      title: "Code",
      attribute: "enrolment.courseClass.course.code",
      sortable: true,
      visible: true,
      width: 100,
      type: null,
      sortFields: []
    },
    {
      title: "National Code",
      attribute: "moduleCode",
      sortable: true,
      visible: true,
      width: 120,
      type: null,
      sortFields: []
    },
    {
      title: "UOC name",
      attribute: "moduleName",
      sortable: true,
      visible: true,
      width: 120,
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
      title: "Start Date",
      attribute: "startDate",
      sortable: true,
      visible: true,
      width: 100,
      type: "Datetime",
      sortFields: []
    },
    {
      title: "End Date",
      attribute: "endDate",
      sortable: true,
      visible: true,
      width: 100,
      type: "Datetime",
      sortFields: []
    }
  ];

  const response = { rows, columns } as any;

  response.entity = "Outcome";
  response.offset = 0;
  response.filterColumnWidth = 200;
  response.layout = "Three column";
  response.pageSize = 20;
  response.search = null;
  response.count = rows.length;
  response.sort = [];

  return response;
}
