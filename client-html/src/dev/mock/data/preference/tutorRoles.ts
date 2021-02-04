import { generateArraysOfRecords, getEntityResponse } from "../../mockUtils";

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