/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Sorting } from "@api/model";
import { generateArraysOfRecords } from "../../mockUtils";

export function mockExportTemplates() {
  this.getExportTemplates = () => this.exportTemplates;

  this.getExportTemplate = id => {
    console.log(this.exportTemplates);
    const row = this.exportTemplates.rows.find(row => row.id == id);
    return {
      id: row.id,
      name: row.values[0],
      keyCode: row.values[1],
      enabled: row.values[2],
      entity: "Account",
      body:
        "records.each { Invoice i ->"
        + "\n"
        + "\tcsv << [\n"
        + '\t\t\t"modifiedOn"      : i.modifiedOn?.format("yyyy-MM-dd\'T\'HH:mm:ssXXX"),"\n'
        + '\t\t\t"createdOn"       : i.createdOn?.format("yyyy-MM-dd\'T\'HH:mm:ssXXX"),\n'
        + '\t\t\t"contactLastName" : i.contact.lastName,"\n'
        + '\t\t\t"contactFirstName": i.contact.firstName,\n'
        + '\t\t\t"courseclassID": i.invoiceLines.enrolment.courseClass.course.name : null,\n'
        + '\t\t\t"total"           : i.total?.toPlainString(),\n'
        + '\t\t\t"totalIncTax"     : i.totalIncTax?.toPlainString(),\n'
        + '\t\t\t"totalTax"        : i.totalTax?.toPlainString(),\n'
        + '\t\t\t"amountOwing"     : i.amountOwing?.toPlainString(),\n'
        + '\t\t\t"billToAddress"   : i.billToAddress,\n'
        + '\t\t\t"dateDue"         : i.dateDue?.format("yyyy-MM-dd"),\n'
        + '\t\t\t"description"     : i.description,\n'
        + '\t\t\t"invoiceDate"     : i.invoiceDate?.format("yyyy-MM-dd")\n'
        + "\t]\n"
        + "}",
      variables: [],
      options: [],
      outputType: "xml",
      createdOn: "2019-04-12T07:59:34.000Z",
      modifiedOn: "2019-04-12T07:59:34.000Z",
      description: "Export template"
    };
  };

  this.getPlainExportTemplates = () => {
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
    response.entity = "ExportTemplate";
    response.columns = [];
    response.count = null;
    response.filterColumnWidth = null;
    response.filteredCount = null;
    response.layout = null;
    response.sort = [];
    return response;
  };

  this.createExportTemplate = item => {
    const data = JSON.parse(item);
    const exportTemplates = this.exportTemplates;
    const totalRows = exportTemplates.rows;

    data.id = totalRows.length + 1;

    exportTemplates.rows.push({
      id: data.id,
      values: [data.name, data.keyCode, data.enabled]
    });

    this.exportTemplates = exportTemplates;
  };

  this.removeExportTemplate = id => {
    this.exportTemplates.rows = this.exportTemplates.rows.filter(a => a.id !== id);
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

  response.entity = "ExportTemplate";
  response.offset = 0;
  response.pageSize = 10;
  response.search = "";
  response.count = rows.length;
  response.sort = response.columns.map(col => ({
    attribute: col.attribute,
    ascending: true
  })) as Sorting[];

  return response;
}
