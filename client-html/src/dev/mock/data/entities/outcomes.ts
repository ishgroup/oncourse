import { generateArraysOfRecords, getEntityResponse, removeItemByEntity } from "../../mockUtils";

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

    return getEntityResponse({
      entity: "Outcome",
      rows,
      plain: true
    });
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

  this.removeOutcome = id => {
    this.outcomes = removeItemByEntity(this.outcomes, id);
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

  return getEntityResponse({
    entity: "Outcome",
    rows,
    columns: [
      {
        title: "Student Name",
        attribute: "studentName",
        sortable: true,
        width: 100
      },
      {
        title: "Course",
        attribute: "enrolment.courseClass.course.name",
        sortable: true,
        width: 100
      },
      {
        title: "Code",
        attribute: "enrolment.courseClass.course.code",
        sortable: true,
        width: 100
      },
      {
        title: "National Code",
        attribute: "moduleCode",
        sortable: true,
        width: 120
      },
      {
        title: "UOC name",
        attribute: "moduleName",
        sortable: true,
        width: 120
      },
      {
        title: "Status",
        attribute: "status",
        sortable: true,
        width: 100
      },
      {
        title: "Start Date",
        attribute: "startDate",
        sortable: true,
        width: 100,
        type: "Datetime"
      },
      {
        title: "End Date",
        attribute: "endDate",
        sortable: true,
        width: 100,
        type: "Datetime"
      }
    ]
  });
}
