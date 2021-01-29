import { generateArraysOfRecords, getEntityResponse } from "../../mockUtils";

export function mockPriorLearnings() {
  this.getPriorLearnings = () => this.priorLearnings;

  this.getPlainPriorLearnings = () => {
    const rows = generateArraysOfRecords(20, [
      { name: "id", type: "number" },
      { name: "title", type: "string" },
      { name: "externalRef", type: "string" },
      { name: "qualNationalCode", type: "string" },
      { name: "qualLevel", type: "string" },
      { name: "qualName", type: "string" }
    ]).map(l => ({
      id: l.id,
      values: [l.title, l.externalRef, l.qualNationalCode, l.qualLevel, l.qualName]
    }));

    return getEntityResponse({
      entity: "PriorLearning",
      rows,
      plain: true
    });
  };

  const rows = generateArraysOfRecords(20, [
    { name: "id", type: "number" },
    { name: "studentName", type: "string" },
    { name: "externalRef", type: "string" },
    { name: "title", type: "string" },
    { name: "outcomeIdTrainingOrg", type: "number" },
    { name: "nationalCode", type: "string" }
  ]).map(l => ({
    id: l.id,
    values: [l.studentName, null, l.title, null, l.nationalCode]
  }));

  return getEntityResponse({
    entity: "PriorLearning",
    rows,
    columns: [
      {
        "title": "Name",
        "attribute": "student.contact.fullName",
        "sortable": true,
        "sortFields": ["student.contact.lastName", "student.contact.firstName", "student.contact.middleName"]
      },
      {
        "title": "External reference",
        "attribute": "externalRef",
        "sortable": true
      },
      {
        "title": "Title",
        "attribute": "title",
        "sortable": true
      },
      {
        "title": "Training Org",
        "attribute": "outcomeIdTrainingOrg",
        "sortable": true
      },
      {
        "title": "Qualification",
        "attribute": "qualification.nationalCode",
        "sortable": true,
        "sortFields": ["qualification+.nationalCode"]
      }
    ],
    res: {
      sort: [{ attribute: "title", ascending: true, complexAttribute: [] }]
    }
  });
}
