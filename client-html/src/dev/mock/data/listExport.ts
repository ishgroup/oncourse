import { Report, ExportTemplate } from "@api/model";
import { generateArraysOfRecords } from "../mockUtils";

export function mockListExport() {
  this.getPdfTemplate = (entityName: string) => generateArraysOfRecords(1, [
      { name: "activeEnrolments", type: "boolean" },
      { name: "backgroundId", type: "number" },
      { name: "description", type: "string" },
      { name: "id", type: "number" },
      { name: "name", type: "string" },
      { name: "keyCode", type: "string" }
    ]).map(
      (l): Report => ({
        backgroundId: l.backgroundId,
        description: `List of all ${entityName.toLowerCase()}`,
        id: l.id,
        name: `${entityName} Report`,
        entity: entityName,
        status: "Enabled",
        keyCode: l.keyCode,
        body: null,
        sortOn: "",
        preview: "",
        variables: []
      })
    );
  
  this.getCsvTemplate = (entityName: string) => generateArraysOfRecords(1, [
      { name: "id", type: "number" },
      { name: "name", type: "string" }
    ]).map(l => ({
      id: l.id,
      name: `${entityName} Logging CSV export`
    }));

  this.getXmlTemplate = (entityName: string) => generateArraysOfRecords(1, [
      { name: "id", type: "number" },
      { name: "name", type: "string" }
    ]).map(l => ({
      id: l.id,
      name: `${entityName} XML export`
    }));

  this.listEmailTemplate = (entityName: string) => generateArraysOfRecords(1, [
      { name: "id", type: "number" },
      { name: "name", type: "string" },
      { name: "entity", type: "string" },
      { name: "subject", type: "string" },
      { name: "plainBody", type: "string" },
      { name: "htmlBody", type: "string" }
    ]).map(l => ({
      id: l.id,
      name: `${entityName} Email Export`,
      entity: l.entity,
      subject: l.subject,
      plainBody: l.plainBody,
      htmlBody: l.htmlBody
    }));

  this.listExportTemplate = (entityName: string) => generateArraysOfRecords(1, [
      { name: "body", type: "string" },
      { name: "createdOn", type: "Datetime" },
      { name: "description", type: "string" },
      { name: "id", type: "number" },
      { name: "name", type: "string" },
      { name: "enabled", type: "boolean" },
      { name: "entity", type: "string" },
      { name: "keyCode", type: "string" },
      { name: "modifiedOn", type: "Datetime" },
      { name: "options", type: "array" },
      { name: "outputType", type: "string" },
      { name: "variables", type: "array" }
    ]).map(
      (l): ExportTemplate => ({
        body: "Test",
        createdOn: l.createdOn,
        description: `List of all ${entityName.toLowerCase()}`,
        status: "Enabled",
        entity: entityName,
        id: l.id,
        keyCode: l.keyCode,
        modifiedOn: l.modifiedOn,
        name: `${entityName} XML export`,
        options: [],
        outputType: "xml",
        variables: []
      })
    );
}
