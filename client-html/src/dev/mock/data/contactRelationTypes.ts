import { ContactRelationType } from "@api/model";
import {generateArraysOfRecords} from "../mockUtils";

export function mockContactRelationTypes(): ContactRelationType[] {
  this.saveContactRelationTypes = items => {
    this.contactRelationTypes = items;
  };

  this.removeContactRelationType = id => {
    this.contactRelationTypes = this.contactRelationTypes.filter(it => it.id !== id);
  };

  this.getPlainContactRelationTypes = () => {
    const rows = generateArraysOfRecords(20, [
      { name: "id", type: "number" },
      { name: "toContactName", type: "string" }
    ]).map(l => ({
      id: l.id,
      values: [l.toContactName]
    }));

    const columns = [];

    const response = { rows, columns } as any;

    response.entity = "ContactRelationType";
    response.offset = 0;
    response.filterColumnWidth = null;
    response.layout = null;
    response.pageSize = rows.length;
    response.search = null;
    response.count = null;
    response.sort = [];

    return response;
  };

  return [
    {
      id: "886543",
      relationName: "Employer",
      reverseRelationName: "Staff",
      portalAccess: true,
      systemType: false
    },
    {
      id: "5684452",
      relationName: "Friend",
      reverseRelationName: "Employer",
      portalAccess: false,
      systemType: false
    },
    {
      id: "32435",
      relationName: "Spouse",
      reverseRelationName: "Friend",
      portalAccess: false,
      systemType: true
    }
  ];
}
