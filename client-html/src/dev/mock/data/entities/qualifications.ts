import { generateArraysOfRecords, getEntityResponse, removeItemByEntity } from "../../mockUtils";

export function mockQualifications() {
  this.getQualifications = () => this.qualifications;

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
    this.qualifications = removeItemByEntity(this.qualifications, id);
  };

  this.getPlainQualifications = params => {
    const rows = generateArraysOfRecords(20, [
      { name: "id", type: "number" },
      { name: "nationalCode", type: "string" },
      { name: "title", type: "string" },
      { name: "level", type: "string" }
    ]).map(l => ({
      id: l.id,
      values: [l.nationalCode, l.title, l.level]
    }));

    return getEntityResponse({
      entity: "Qualification",
      rows,
      plain: true
    });
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

  return getEntityResponse({
    entity: "Qualification",
    rows,
    columns: [
      {
        title: "Code",
        attribute: "nationalCode",
        sortable: true,
        width: 400
      },
      {
        title: "Title",
        attribute: "title",
        sortable: true
      },
      {
        title: "Level",
        attribute: "level",
        sortable: true
      },
      {
        title: "Hours",
        attribute: "nominalHours",
        sortable: true
      },
      {
        title: "Offered",
        attribute: "isOffered",
        type: "Boolean",
        sortable: true
      }
    ],
    res: {
      sort: [
        {
          attribute: "nationalCode",
          ascending: true,
          complexAttribute: []
        }
      ]
    }
  });
}
