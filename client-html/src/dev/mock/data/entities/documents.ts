import { generateArraysOfRecords, getEntityResponse } from "../../mockUtils";

export function mockDocuments() {
  this.getDocuments = () => this.documents;

  const rows = generateArraysOfRecords(20, [
    { name: "id", type: "number" },
    { name: "link", type: "string" },
    { name: "name", type: "string" },
    { name: "added", type: "Datetime" },
    { name: "byteSize", type: "string" },
    { name: "webVisibility", type: "string" },
    { name: "fileName", type: "string" },
    { name: "mimeType", type: "string" },
    { name: "active", type: "boolean" }
  ]).map(l => ({
    id: l.id,
    values: [
      "https://ish-oncourse-sttrianians.s3.amazonaws.com/4e6c5b71-ff96-4d16-a95d-d1d64f290b6e",
      l.name, "2011-11-18T11:06:41.000Z", "134.40 kb", "Website", `${l.fileName}.jpg`, "image/jpeg", true]
  }));

  return getEntityResponse(
    "Document",
    rows,
    [
      { title: "Link", attribute: "link" },
      { title: "Document name", attribute: "name", sortable: true },
      {
        title: "Date added", attribute: "added", type: "Date", sortable: true
      },
      { title: "Size", attribute: "currentVersion.byteSize" },
      {
        title: "Security level", attribute: "webVisibility", sortable: true, width: 100
      },
      { title: "File name", attribute: "currentVersion.fileName", width: 100 },
      { title: "Type", attribute: "currentVersion.mimeType", width: 100 },
      {
        title: "Active", attribute: "currentVersion.active", type: "Boolean", visible: false, system: true, width: 100
      },
    ],
    {
      sort: [{ attribute: "name", ascending: true, complexAttribute: [] }]
    }
  );
}