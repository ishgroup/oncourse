import { generateArraysOfRecords } from "../../mockUtils";

export function mockTraineeshipCourses() {
  this.getTraineeshipCourses = () => {
    return this.traineeshipCourses;
  };

  const rows = generateArraysOfRecords(20, [
    { name: "id", type: "number" },
    { name: "name", type: "string" },
    { name: "code", type: "string" },
    { name: "fieldOfEducation", type: "string" },
    { name: "qualification", type: "string" },
    { name: "enrolments", type: "number" }
  ]).map(l => ({
    id: l.id,
    values: [l.name, l.code, l.fieldOfEducation, l.qualification, 0]
  }));

  const columns = [
    {
      title: "Course",
      attribute: "name",
      sortable: true,
      visible: true,
      width: 200,
      type: null,
      sortFields: []
    },
    {
      title: "Code",
      attribute: "code",
      sortable: true,
      visible: true,
      width: 100,
      type: null,
      sortFields: []
    },
    {
      title: "Starts",
      attribute: "start",
      sortable: true,
      visible: true,
      width: 100,
      type: null,
      sortFields: []
    },
    {
      title: "Field",
      attribute: "fieldOfEducation",
      sortable: true,
      visible: true,
      width: 100,
      type: null,
      sortFields: []
    },
    {
      title: "Qualification",
      attribute: "qualification",
      sortable: true,
      visible: true,
      width: 100,
      type: null,
      sortFields: []
    },
    {
      title: "Current enrolments",
      attribute: "enrolments",
      sortable: true,
      visible: true,
      width: 100,
      type: null,
      sortFields: []
    }
  ];

  const response = { rows, columns } as any;

  response.entity = "TraineeshipCourse";
  response.offset = 0;
  response.filterColumnWidth = 200;
  response.layout = "Three column";
  response.pageSize = 20;
  response.search = null;
  response.count = rows.length;
  response.sort = [];

  return response;
}
