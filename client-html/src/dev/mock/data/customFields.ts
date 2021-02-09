import { CustomFieldType } from "@api/model";
import { generateArraysOfRecords } from "../mockUtils";

export function mockCustomFields(): CustomFieldType[] {
  this.saveCustomFields = items => {
    this.customFields = items;
  };

  this.removeCustomField = id => {
    this.customFields = this.customFields.filter(it => Number(it.id) !== Number(id));
  };

  this.getCustomFields = () => {
    const rows = generateArraysOfRecords(20, [
      { name: "id", type: "number" },
      { name: "fieldKey", type: "string" },
      { name: "name", type: "string" },
      { name: "defaultValue", type: "string" },
      { name: "mandatory", type: "boolean" },
      { name: "dataType", type: "string" },
      { name: "sortOrder", type: "number" },
    ]).map(({ id, ...rest }) => ({
      id,
      values: Object.values(rest)
    }));

    const columns = [];
    const response = { rows, columns } as any;

    response.entity = "CustomFieldType";
    response.offset = 0;
    response.filterColumnWidth = null;
    response.layout = null;
    response.pageSize = 0;
    response.search = null;
    response.count = rows.length;
    response.sort = [];

    return response;
  };

  return [
    {
      id: "886543",
      name: "Seniors card",
      defaultValue: "Default value",
      dataType: "Text",
      fieldKey: "886543",
      mandatory: true,
      entityType: "Application"
    },
    {
      id: "5684452",
      name: "Student ",
      defaultValue: null,
      dataType: "Text",
      fieldKey: "5684452",
      mandatory: true,
      entityType: "WaitingList"
    },
    {
      id: "32435",
      name: "Pensioner",
      defaultValue: "Default value",
      dataType: "Text",
      fieldKey: "32435",
      mandatory: true,
      entityType: "Enrolment"
    }
  ];
}
