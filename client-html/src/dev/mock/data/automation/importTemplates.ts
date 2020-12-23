import { Sorting } from "@api/model";
import { generateArraysOfRecords } from "../../mockUtils";

export function mockImportTemplates() {
  this.getImportTemplates = () => this.importTemplates;

  this.getImportTemplate = id => {
    const row = this.importTemplates.rows.find(row => row.id == id);
    return {
      id: row.id,
      name: row.values[0],
      keyCode: row.values[1],
      enabled: row.values[2],
      entity: null,
      "body": "import ish.oncourse.server.imports.CsvParser"
        + "\n\ndef reader = new CsvParser(new InputStreamReader(new ByteArrayInputStream(contactFile)))"
        + "\n\nreader.eachLine { line ->"
        + "\n"
        + "\n    if (line.email) {"
        + "\n        ObjectSelect.query(Contact)"
        + "\n                .where(Contact.EMAIL.eq(line.email))"
        + "\n                .select(context)"
        + "\n                .each { contact ->"
        + "\n"
        + "\n            contact.allowEmail = line.allowEmail?.toBoolean() ?: false"
        + "\n            contact.allowPost = line.allowPost?.toBoolean() ?: false"
        + "\n            contact.allowSms = line.allowSms?.toBoolean() ?: false"
        + "\n"
        + "\n            if (line.notes) {"
        + "\n                ContactNoteRelation contactNoteRelation = context.newObject(ContactNoteRelation)"
        + "\n                Note note = context.newObject(Note)"
        + "\n                contactNoteRelation.note = note"
        + "\n                contactNoteRelation.notableEntity = contact"
        + "\n                note.note = line.notes"
        + "\n            }"
        + "\n"
        + "\n            context.commitChanges()"
        + "\n        }"
        + "\n    }"
        + "\n"
        + "}\n",
      variables: [
        {
          "name": "contactFile",
          "label": "File with contacts",
          "type": "File",
          "value": null,
          "system": null,
          "valueDefault": null
        }
      ],
      options: [],
      createdOn: "2019-04-12T07:59:34.000Z",
      modifiedOn: "2019-04-12T07:59:34.000Z",
      description: "Import contacts to update subscriptions from csv file"
    };
  };

  this.getPlainImportTemplates = () => {
    const rows = generateArraysOfRecords(20, [
      { name: "id", type: "number" },
      { name: "name", type: "string" },
      { name: "keyCode", type: "string" },
      { name: "enabled", type: "string" }
    ]).map(l => ({
      id: l.id,
      values: [l.name, l.keyCode, l.enabled]
    }));

    const columns = [];

    const response = { rows, columns } as any;
    response.entity = "Import";
    response.count = null;
    response.filterColumnWidth = null;
    response.filteredCount = null;
    response.layout = null;
    response.sort = [];
    return response;
  };

  this.createImportTemplate = item => {
    const data = JSON.parse(item);
    const importTemplates = this.importTemplates;
    const totalRows = importTemplates.rows;

    data.id = totalRows.length + 1;

    importTemplates.rows.push({
      id: data.id,
      values: [data.name, data.keyCode, data.enabled]
    });

    this.importTemplates = importTemplates;
  };

  this.removeImportTemplate = id => {
    this.importTemplates.rows = this.importTemplates.rows.filter(a => a.id !== id);
  };

  const rows = generateArraysOfRecords(20, [
    { name: "id", type: "number" },
    { name: "name", type: "string" },
    { name: "keyCode", type: "string" },
    { name: "enabled", type: "string" }
  ]).map(l => ({
    id: l.id,
    values: [l.name, l.keyCode, l.enabled]
  }));

  const columns = [
    {
      title: "Name",
      attribute: "name",
      sortable: true,
      visible: true,
      width: 200
    },
    {
      title: "Key code",
      attribute: "keyCode",
      sortable: true,
      visible: true,
      width: 200
    },
    {
      title: "Enabled",
      attribute: "enabled",
      sortable: false,
      visible: true,
      width: 200
    }
  ];

  const response = { rows, columns } as any;

  response.entity = "Import";
  response.offset = 0;
  response.pageSize = 10;
  response.search = "";
  response.count = rows.length;
  response.layout = null;
  response.filteredCount = null;
  response.filterColumnWidth = null;
  response.sort = response.columns.map(col => ({
    attribute: col.attribute,
    ascending: true
  })) as Sorting[];

  return response;
}
