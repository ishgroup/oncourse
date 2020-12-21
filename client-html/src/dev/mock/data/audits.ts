import { Audit } from "@api/model";
import { generateArraysOfRecords } from "../mockUtils";

export function mockAudits() {
  this.getAudit = (id: number): Audit => {
    const row = this.audit.rows.find(row => row.id == id);
    return {
      id: row.id,
      systemUser: row.values[0],
      created: row.values[1],
      entityIdentifier: row.values[2],
      action: row.values[3],
      entityId: row.values[4],
      message: "test message",
    };
  };

  this.getAudits = () => this.audit;

  const rows: Audit[] = generateArraysOfRecords(20, [
    { name: "id", type: "number" },
    { name: "systemUser", type: "string" },
    { name: "created", type: "Datetime" },
    { name: "entityIdentifier", type: "string" },
    { name: "action", type: "string" },
    { name: "entityId", type: "string" },
  ]).map(l => ({
    id: l.id,
    values: [l.systemUser, l.created, l.entityIdentifier, l.action, l.entityId]
  }));

  const columns = [
    {
      title: "User Name",
      attribute: "systemUser.fullName",
      sortable: true,
      visible: true,
      width: 200,
      type: null,
      sortFields: ["systemUser.lastName", "systemUser.firstName"]
    },
    {
      title: "Datetime",
      attribute: "created",
      sortable: true,
      visible: true,
      width: 200,
      type: "Datetime",
      sortFields: []
    },
    {
      title: "Entity identifier",
      attribute: "entityIdentifier",
      sortable: true,
      visible: true,
      width: 200,
      type: null,
      sortFields: []
    },
    {
      title: "Action",
      attribute: "action",
      sortable: true,
      visible: true,
      width: 200,
      type: null,
      sortFields: []
    },
    {
      title: "Entity id",
      attribute: "entityId",
      sortable: true,
      visible: true,
      width: 100,
      type: null,
      sortFields: []
    },
  ];

  const response = { rows, columns } as any;

  response.entity = "Audit";
  response.offset = 0;
  response.filterColumnWidth = 200;
  response.layout = "Three column";
  response.pageSize = 10;
  response.search = "";
  response.count = rows.length;
  response.sort = [{
    attribute: 'created',
    ascending: false
  }];

  return response;
}
