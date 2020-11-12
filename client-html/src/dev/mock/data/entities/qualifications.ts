import { generateArraysOfRecords } from "../../mockUtils";

export function mockQualifications() {
  this.getQualifications = () => {
    return this.qualifications;
  };

  this.getPlainQualifications = () => {
    const rows = generateArraysOfRecords(20, [
      { name: "id", type: "number" },
      { name: "nationalCode", type: "string" },
      { name: "title", type: "string" },
      { name: "level", type: "string" }
    ]).map(l => ({
      id: l.id,
      values: [l.nationalCode, l.title, l.level]
    }));

    const columns = [];

    const response = { rows, columns } as any;

    response.entity = "Qualification";
    response.offset = 0;
    response.filterColumnWidth = null;
    response.layout = null;
    response.pageSize = rows.length;
    response.search = null;
    response.count = null;
    response.sort = [];

    return response;
  };

  this.getQualification = id => {
    const row = this.qualifications.rows.find(row => row.id == id);

    return {
      id: row.id,
      type: "Skill set",
      name: null,
      nationalCode: row.values[0],
      title: row.values[1],
      qualLevel: row.values[2],
      nominalHours: row.values[3],
      isOffered: row.values[4]
    };
  };

  this.createQualification = item => {
    const data = JSON.parse(item);
    const qualifications = this.qualifications;
    const totalRows = qualifications.rows;

    data.id = totalRows.length + 1;

    qualifications.rows.push({
      id: data.id,
      values: [data.nationalCode, data.title, data.qualLevel, data.nominalHours, data.isOffered]
    });

    this.qualifications = qualifications;
  };

  this.removeQualification = id => {
    this.qualifications = this.qualifications.rows.filter(q => q.id !== id);
  };

  const rows = generateArraysOfRecords(20, [
    { name: "id", type: "number" },
    { name: "title", type: "string" },
    { name: "qualLevel", type: "string" },
    { name: "nominalHours", type: "number" },
    { name: "nationalCode", type: "string" },
    { name: "isOffered", type: "boolean" }
  ]).map(l => ({
    id: l.id,
    values: [l.nationalCode, l.title, l.qualLevel, 10, false]
  }));

  const columns = [
    {
      title: "Code",
      attribute: "nationalCode",
      type: null,
      sortable: true,
      visible: true,
      width: 400,
      sortFields: []
    },
    {
      title: "Title",
      attribute: "title",
      type: null,
      sortable: true,
      visible: true,
      width: 200,
      sortFields: []
    },
    {
      title: "Level",
      attribute: "level",
      type: null,
      sortable: true,
      visible: true,
      width: 200,
      sortFields: []
    },
    {
      title: "Hours",
      attribute: "nominalHours",
      type: null,
      sortable: true,
      visible: true,
      width: 200,
      sortFields: []
    },
    {
      title: "Offered",
      attribute: "isOffered",
      type: "Boolean",
      sortable: true,
      visible: true,
      width: 200,
      sortFields: []
    }
  ];

  const response = { rows, columns } as any;

  response.entity = "Qualification";
  response.offset = 0;
  response.filterColumnWidth = 200;
  response.layout = "Three column";
  response.pageSize = 20;
  response.search = null;
  response.count = rows.length;
  response.filteredCount = rows.length;
  response.sort = [
    {
      attribute: "nationalCode",
      ascending: true,
      complexAttribute: []
    }
  ];

  return response;
}
