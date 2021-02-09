import { ContactRelationType } from "@api/model";
import { generateArraysOfRecords, getEntityResponse } from "../mockUtils";

export function mockContactRelationTypes(): ContactRelationType[] {
  this.saveContactRelationTypes = items => {
    this.contactRelationTypes = items;
  };

  this.removeContactRelationType = id => {
    this.contactRelationTypes = this.contactRelationTypes.filter(it => Number(it.id) !== Number(id));
  };

  this.getPlainContactRelationTypes = () => {
    const rows = generateArraysOfRecords(20, [
      { name: "id", type: "number" },
      { name: "toContactName", type: "string" }
    ]).map(l => ({
      id: l.id,
      values: [l.toContactName]
    }));

    return getEntityResponse({
      entity: "ContactRelationType",
      rows,
      plain: true
    });
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
