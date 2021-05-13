import { Tag } from "@api/model";
import { generateArraysOfRecords } from "../mockUtils";

export function mockTags(): Tag[] {
  this.getTag = id => {
    const tags = this.tags.find(tag => Number(tag.id) === Number(id));
    return tags;
  };

  this.getTags = () => {
    return this.tags;
  };

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

  const tags = generateArraysOfRecords(1, ofRecord).map(l => ({
    id: l.id,
    name: l.name,
    status: "Show on website",
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

  return tags;
}
