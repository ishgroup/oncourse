/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Sorting } from "@api/model";
import { generateArraysOfRecords } from "../../mockUtils";

export function mockReports() {
  this.getReports = () => this.reports;

  this.getReport = id => {
    const row = this.reports.rows.find(row => row.id == id);
    return {
      id: row.id,
      name: row.values[0],
      keyCode: row.values[1],
      enabled: row.values[2],
      entity: "Enrolment",
      description:
        "To provide students with the information retained in onCourse regarding their enrolment and current status of their outcomes.This report prints in Portrait format.",
      preview: "",
      body: null,
      subreport: true,
      backgroundId: 1,
      sortOn: "",
      variables: [],
      options: [],
      createdOn: "2019-04-12T07:59:34.000Z",
      modifiedOn: "2019-04-12T07:59:34.000Z"
    };
  };

  this.getPlainReports = () => {
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
    response.entity = "Report";
    response.count = null;
    response.filterColumnWidth = null;
    response.filteredCount = null;
    response.layout = null;
    response.sort = [];
    return response;
  };

  this.createReport = item => {
    const data = JSON.parse(item);
    const reports = this.reports;
    const totalRows = reports.rows;

    data.id = totalRows.length + 1;

    reports.rows.push({
      id: data.id,
      values: [data.name, data.keyCode, data.enabled]
    });

    this.reports = reports;
  };

  this.removeReport = id => {
    this.reports.rows = this.reports.rows.filter(a => a.id !== id);
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

  response.entity = "Report";
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
