import { Audit } from "@api/model";
import { generateArraysOfRecords, getEntityResponse } from "../mockUtils";

export function mockAudits() {
  this.getAudit = (id: number): Audit => {
    const row = this.audit.rows.find(audit => Number(audit.id) === Number(id));
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

  this.getPlainAudits = params => {
    const columns = params.columns;
    let rows: any[] = [];
    if (columns === "entityId,created,action") {
      rows = [];
    }

    return getEntityResponse({
      entity: "Audit",
      rows,
      plain: true
    });
  };

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

  return getEntityResponse({
    entity: "Audit",
    rows,
    columns: [
      {
        title: "User Name",
        attribute: "systemUser.fullName",
        sortable: true,
        sortFields: ["systemUser.lastName", "systemUser.firstName"]
      },
      {
        title: "Datetime",
        attribute: "created",
        sortable: true,
        type: "Datetime",
      },
      {
        title: "Entity identifier",
        attribute: "entityIdentifier",
        sortable: true,
      },
      {
        title: "Action",
        attribute: "action",
        sortable: true,
      },
      {
        title: "Entity id",
        attribute: "entityId",
        sortable: true,
        width: 100,
      },
    ],
    res: {
      sort: [{
        attribute: 'created',
        ascending: false
      }]
    }
  });
}
