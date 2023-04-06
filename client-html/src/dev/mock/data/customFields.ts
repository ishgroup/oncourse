import { CustomFieldType } from "@api/model";
import { generateArraysOfRecords, getEntityResponse } from "../mockUtils";

export function mockCustomFields(): CustomFieldType[] {
  this.saveCustomFields = items => {
    this.customFields = items;
  };

  this.removeCustomField = id => {
    this.customFields = this.customFields.filter(it => Number(it.id) !== Number(id));
  };

  this.getCustomFields = () => {
    const rows = generateArraysOfRecords(20, [
      { name: "id", type: "number" },
      { name: "fieldKey", type: "string" },
      { name: "name", type: "string" },
      { name: "defaultValue", type: "string" },
      { name: "mandatory", type: "boolean" },
      { name: "dataType", type: "string" },
      { name: "sortOrder", type: "number" },
    ]).map(({ id, ...rest }) => ({
      id,
      values: Object.values(rest)
    }));

    return getEntityResponse({
      entity: "CustomFieldType",
      rows,
      plain: true
    });
  };

  return [
    {
      "id": "280",
      "name": "Agent Commision",
      "dataType": "Text",
      "pattern": null,
      "defaultValue": null,
      "fieldKey": "agentCommision",
      "mandatory": false,
      "sortOrder": 0,
      "entityType": "Contact",
      "created": "2015-11-12T04:15:46.000Z",
      "modified": "2020-08-06T05:00:27.000Z"
    },
    {
      "id": "398",
      "name": "Category (age)",
      "dataType": "List",
      "pattern": null,
      "defaultValue": "[{\"value\" :\"4-6 years\"}, {\"value\" :\" 7-10 years\"}, {\"value\" :\" 11-14 years\"}, {\"value\" :\" 15-17 years\"}]",
      "fieldKey": "catgory",
      "mandatory": false,
      "sortOrder": 1,
      "entityType": "Enrolment",
      "created": "2018-08-21T08:26:19.000Z",
      "modified": "2020-08-06T05:00:27.000Z"
    },
    {
      "id": "394",
      "name": "Medium 3D",
      "dataType": "List",
      "pattern": null,
      "defaultValue": "[{\"value\" :\"textile\"}, {\"value\" :\" ceramic\"}, {\"value\" :\" mixed media\"}, {\"value\" :\" other\"}]",
      "fieldKey": "medium3d",
      "mandatory": false,
      "sortOrder": 2,
      "entityType": "Enrolment",
      "created": "2018-08-21T08:05:54.000Z",
      "modified": "2020-08-06T05:00:27.000Z"
    },
    {
      "id": "221",
      "name": "Drivers Licence Number",
      "dataType": "Text",
      "pattern": null,
      "defaultValue": null,
      "fieldKey": "driversLicenceNumber",
      "mandatory": false,
      "sortOrder": 3,
      "entityType": "Contact",
      "created": "2014-07-22T11:21:12.000Z",
      "modified": "2020-09-15T05:19:13.000Z"
    },
    {
      "id": "282",
      "name": "Facebook Key",
      "dataType": "Text",
      "pattern": null,
      "defaultValue": null,
      "fieldKey": "facebookKey",
      "mandatory": false,
      "sortOrder": 4,
      "entityType": "Course",
      "created": "2017-07-03T05:46:34.000Z",
      "modified": "2020-08-06T05:00:27.000Z"
    },
    {
      "id": "396",
      "name": "Title of Artwork",
      "dataType": "Text",
      "pattern": null,
      "defaultValue": "",
      "fieldKey": "title1",
      "mandatory": false,
      "sortOrder": 5,
      "entityType": "Enrolment",
      "created": "2018-08-21T08:05:54.000Z",
      "modified": "2020-08-06T05:00:27.000Z"
    },
    {
      "id": "200",
      "name": "How did you hear about us? ",
      "dataType": "List",
      "pattern": null,
      "defaultValue": "[{\"value\" :\"Radio\"}, {\"value\" :\" TV\"}, {\"value\" :\" Internet\"}, {\"value\" :\" Word of mouth\"}, {\"value\" :\" Catalogue\"}, {\"value\" :\" *\"}]",
      "fieldKey": "howDidYouHearAboutUs",
      "mandatory": false,
      "sortOrder": 6,
      "entityType": "Contact",
      "created": "2013-02-20T11:20:09.000Z",
      "modified": "2020-09-15T05:19:12.000Z"
    },
    {
      "id": "281",
      "name": "Membership Number",
      "dataType": "Text",
      "pattern": null,
      "defaultValue": null,
      "fieldKey": "MembershipNumber",
      "mandatory": false,
      "sortOrder": 7,
      "entityType": "Contact",
      "created": "2017-03-16T07:18:55.000Z",
      "modified": "2020-08-06T05:00:27.000Z"
    },
    {
      "id": "388",
      "name": "Prior study",
      "dataType": "List",
      "pattern": null,
      "defaultValue": "[{\"value\" :\"< 10 hrs\"}, {\"value\" :\" 10 to 20hrs\"}, {\"value\" :\" 20 to 60hrs\"}, {\"value\" :\" 60 to 100hrs\"}, {\"value\" :\" 100+ hrs\"}]",
      "fieldKey": "priorStudy",
      "mandatory": false,
      "sortOrder": 8,
      "entityType": "Application",
      "created": "2018-03-06T10:36:40.000Z",
      "modified": "2020-08-06T05:00:27.000Z"
    },
    {
      "id": "387",
      "name": "School Attended",
      "dataType": "Text",
      "pattern": null,
      "defaultValue": null,
      "fieldKey": "School",
      "mandatory": false,
      "sortOrder": 9,
      "entityType": "Enrolment",
      "created": "2017-12-19T10:49:34.000Z",
      "modified": "2020-08-06T05:00:27.000Z"
    },
    {
      "id": "340",
      "name": "Smart & Skilled Student Consent",
      "dataType": "List",
      "pattern": null,
      "defaultValue": "[{\"value\" :\"Yes - 1\"}, {\"value\" :\" No - 2\"}]",
      "fieldKey": "ssConsent",
      "mandatory": false,
      "sortOrder": 10,
      "entityType": "Application",
      "created": "2017-11-03T10:07:53.000Z",
      "modified": "2020-08-06T05:00:27.000Z"
    }
  ];
}
