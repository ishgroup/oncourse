/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Sorting } from "@api/model";
import { generateArraysOfRecords } from "../../mockUtils";

export function mockReportOverlays() {
  this.getReportOverlays = () => this.reportOverlays;

  this.getReportOverlay = id => {
    const row = this.reportOverlays.rows.find(row => row.id == id);
    return {
      id: row.id,
      name: row.values[0],
      preview:
        "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"
    };
  };

  this.getPlainReportOverlays = () => {
    const rows = generateArraysOfRecords(20, [
      { name: "id", type: "number" },
      { name: "name", type: "string" },
      { name: "enabled", type: "string" }
    ]).map(l => ({
      id: l.id,
      values: [l.name, l.enabled]
    }));

    const columns = [];

    const response = { rows, columns } as any;
    response.entity = "ReportOverlay";
    response.count = null;
    response.filterColumnWidth = null;
    response.filteredCount = null;
    response.layout = null;
    response.sort = [];
    return response;
  };

  this.createReportOverlay = item => {
    const data = JSON.parse(item);
    const reportOverlays = this.reportOverlays;
    const totalRows = reportOverlays.rows;

    data.id = totalRows.length + 1;

    reportOverlays.rows.push({
      id: data.id,
      values: [data.name, data.enabled]
    });

    this.reportOverlays = reportOverlays;
  };

  this.removeReportOverlay = id => {
    this.reportOverlays.rows = this.reportOverlays.rows.filter(a => a.id !== id);
  };

  const rows = generateArraysOfRecords(20, [
    { name: "id", type: "number" },
    { name: "name", type: "string" },
    { name: "enabled", type: "string" }
  ]).map(l => ({
    id: l.id,
    values: [l.name, l.enabled]
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
      title: "Enabled",
      attribute: "enabled",
      sortable: false,
      visible: true,
      width: 200
    }
  ];

  const response = { rows, columns } as any;

  response.entity = "ReportOverlay";
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
