/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { Tag } from "@api/model";
import { generateArraysOfRecords, getEntityResponse } from "../mockUtils";

export function mockTags(): Tag[] {
  this.getTag = id => this.tags.find(tag => Number(tag.id) === Number(id));

  this.getTags = () => this.tags;

  this.removeTag = id => {
    this.tags = this.tags.filter(tag => Number(tag.id) !== Number(id));
  };

  this.getPlainTags = () => getEntityResponse({
      entity: "Tag",
      rows: [
        {
          id: 1,
          values: ["Tag1"]
        },
        {
          id: 2,
          values: ["Tag2"]
        }
      ],
      plain: true
    });

  const ofRecord = [
    { name: "id", type: "number" },
    { name: "name", type: "string" },
    { name: "status", type: "string" },
    { name: "system", type: "boolean" },
    { name: "urlPath", type: "string" },
    { name: "content", type: "string" },
    { name: "color", type: "string" },
    { name: "weight", type: "number" },
    { name: "taggedRecordsCount", type: "number" },
    { name: "childrenCount", type: "number" },
    { name: "created", type: "Datetime" },
    { name: "modified", type: "Datetime" },
    { name: "requirements", type: "array" },
    { name: "childTags", type: "array" }
  ];

  const childTags = generateArraysOfRecords(1, ofRecord).map(l => ({
    id: l.id,
    name: l.name,
    status: "Show on website",
    type: "Tag",
    system: false,
    urlPath: null,
    content: null,
    color: "21c2b7",
    weight: null,
    taggedRecordsCount: l.taggedRecordsCount,
    childrenCount: 0,
    created: l.created,
    modified: l.modified,
    requirements: [],
    childTags: []
  }));

  const requirements = [{
    id: 1,
    limitToOneTag: false,
    mandatory: false,
    system: false,
    type: "WaitingList"
  }];
  
  return generateArraysOfRecords(2, ofRecord).map(l => ({
    id: l.id,
    name: l.name,
    status: "Show on website",
    type: "Tag",
    system: false,
    urlPath: null,
    content: null,
    color: "f5bc76",
    weight: null,
    taggedRecordsCount: l.taggedRecordsCount,
    childrenCount: childTags.length,
    created: l.created,
    modified: l.modified,
    requirements,
    childTags
  }));
}
