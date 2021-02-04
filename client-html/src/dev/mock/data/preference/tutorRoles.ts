import { generateArraysOfRecords, getEntityResponse } from "../../mockUtils";

export function mockTutorRoles() {
  this.getPlainTutorRoles = () => this.tutorRoles;

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