import { generateArraysOfRecords, getEntityResponse, removeItemByEntity } from "../../mockUtils";

export function mockTutorRoles() {
  this.getPlainTutorRoles = () => this.tutorRoles;
  
  this.getTutorRole = id => {
    const row = this.tutorRoles.rows.find(row => Number(row.id) === Number(id));
    return {
      id: row.id,
      name: row.values[0],
      description: row.values[1],
      active: row.values[2],
      payRates: [
        {
          id: 220,
          type: "Per session",
          validFrom: "2012-11-07",
          rate: 85.00,
          oncostRate: 0.1000,
          notes: null
        }
      ]
    };
  };

  this.createTutorRole = item => {
    const data = JSON.parse(item);
    const tutorRoles = this.tutorRoles;
    const totalRows = tutorRoles.rows;

    data.id = totalRows.length + 1;

    tutorRoles.rows.push({
      id: data.id,
      values: [data.name, data.description, data.active]
    });

    this.tutorRoles = tutorRoles;
  };

  this.removeTutorRole = id => {
    this.tutorRoles = removeItemByEntity(this.tutorRoles, id);
  };

  const rows = generateArraysOfRecords(20, [
    { name: "id", type: "number" },
    { name: "name", type: "string" },
    { name: "description", type: "string" },
    { name: "active", type: "boolean" }
  ]).map(l => ({
    id: l.id,
    values: [l.name, l.description, true]
  }));

  return getEntityResponse({
    entity: "DefinedTutorRole",
    rows,
    plain: true
  });
}