/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { generateArraysOfRecords } from "../../mockUtils";

export function mockAssessments() {
  this.getAssessments = () => this.assessments;

  this.getAssessment = id => {
    const row = this.assessments.rows.find(row => row.id == id);
    return {
      id: row.id,
      code: row.values[0],
      name: row.values[1],
      description: "test description",
      active: true,
      documents: [],
      notes: [],
      tags: [this.getTag(1)]
    };
  };

  this.createAssessment = item => {
    const data = JSON.parse(item);
    const { assessments } = this;
    const totalRows = assessments.rows;

    data.id = totalRows.length + 1;

    assessments.rows.push({
      id: data.id,
      values: [data.code, data.name]
    });

    this.assessments = assessments;
  };

  this.createNewAssessment = () => ({
    id: 21,
    code: "code 21",
    name: "name 21",
    description: "test description",
    active: true,
    documents: [],
    notes: [],
    tags: [this.getTag(1)]
  });

  this.removeAssessment = id => {
    this.assessments = this.assessments.rows.filter(a => a.id !== id);
  };

  const rows = generateArraysOfRecords(20, [
    { name: "id", type: "number" },
    { name: "code", type: "string" },
    { name: "name", type: "string" }
  ]).map(l => ({
    id: l.id,
    values: [l.code, l.name]
  }));

  const columns = [
    {
      title: "Code",
      attribute: "code",
      sortable: true,
      visible: true,
      width: 200,
      type: null,
      sortFields: []
    },
    {
      title: "Name",
      attribute: "name",
      sortable: true,
      visible: true,
      width: 200,
      type: null,
      sortFields: []
    }
  ];

  const response = { rows, columns } as any;

  response.entity = "Assessment";
  response.offset = 0;
  response.filterColumnWidth = 200;
  response.layout = "Three column";
  response.pageSize = 20;
  response.search = null;
  response.count = rows.length;
  response.filteredCount = rows.length;
  response.sort = [];

  return response;
}
