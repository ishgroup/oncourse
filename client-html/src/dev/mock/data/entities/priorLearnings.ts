import { generateArraysOfRecords, getEntityResponse, removeItemByEntity } from "../../mockUtils";

export function mockPriorLearnings() {
  this.getPriorLearnings = () => this.priorLearnings;

  this.getPriorLearning = id => {
    const row = this.priorLearnings.rows.find(priorLearning => Number(priorLearning.id) === Number(id));
    return {
      "id": row.id,
      "createdOn": "2016-01-27T07:50:57.000Z",
      "modifiedOn": "2016-01-27T08:00:54.000Z",
      "title": row.values[2],
      "externalReference": row.values[1],
      "qualificationId": 1,
      "qualificationNationalCode": "nationalCode 1",
      "qualificationLevel": "",
      "qualificationName": "title 1",
      "outcomes": [
        {
          "id": 1,
          "contactId": 5,
          "enrolmentId": null,
          "studentName": "Madonna",
          "moduleId": 10,
          "moduleCode": "BSBADM301A",
          "moduleName": "Produce texts from shorthand notes",
          "trainingPlanStartDate": null,
          "startDate": "2018-04-02",
          "startDateOverridden": false,
          "trainingPlanEndDate": null,
          "endDate": "2018-06-11",
          "endDateOverridden": false,
          "reportableHours": 0,
          "deliveryMode": null,
          "fundingSource": null,
          "status": "Not set",
          "hoursAttended": null,
          "vetPurchasingContractID": null,
          "vetPurchasingContractScheduleID": null,
          "vetFundingSourceStateID": null,
          "specificProgramIdentifier": null,
          "isPriorLearning": true,
          "hasCertificate": false,
          "printed": false,
          "createdOn": "2016-01-27T08:00:54.000Z",
          "modifiedOn": "2016-01-27T08:01:16.000Z"
        }
      ],
      "documents": [

      ],
      "notes": "Lorem ipsum",
      "contactId": row.id,
      "contactName": row.values[0],
      "outcomeIdTrainingOrg": row.values[3],
    };
  };

  this.createPriorLearning = item => {
    const data = JSON.parse(item);
    const priorLearnings = this.priorLearnings;
    const totalRows = priorLearnings.rows;

    data.id = totalRows.length + 1;

    priorLearnings.rows.push({
      id: data.id,
      values: [
        `studentName ${data.id}`,
        null,
        data.title,
        null,
        `nationalCode ${data.id}`
      ]
    });

    this.priorLearnings = priorLearnings;
  };

  this.removePriorLearning = id => {
    this.priorLearnings = removeItemByEntity(this.priorLearnings, id);
  };

  this.getPlainPriorLearnings = () => {
    const rows = generateArraysOfRecords(20, [
      { name: "id", type: "number" },
      { name: "title", type: "string" },
      { name: "externalRef", type: "string" },
      { name: "qualNationalCode", type: "string" },
      { name: "qualLevel", type: "string" },
      { name: "qualName", type: "string" }
    ]).map(l => ({
      id: l.id,
      values: [l.title, l.externalRef, l.qualNationalCode, l.qualLevel, l.qualName]
    }));

    return getEntityResponse({
      entity: "PriorLearning",
      rows,
      plain: true
    });
  };

  const rows = generateArraysOfRecords(20, [
    { name: "id", type: "number" },
    { name: "studentName", type: "string" },
    { name: "externalRef", type: "string" },
    { name: "title", type: "string" },
    { name: "outcomeIdTrainingOrg", type: "number" },
    { name: "nationalCode", type: "string" }
  ]).map(l => ({
    id: l.id,
    values: [l.studentName, l.externalRef, l.title, l.outcomeIdTrainingOrg, l.nationalCode]
  }));

  return getEntityResponse({
    entity: "PriorLearning",
    rows,
    columns: [
      {
        "title": "Name",
        "attribute": "student.contact.fullName",
        "sortable": true,
        "sortFields": ["student.contact.lastName", "student.contact.firstName", "student.contact.middleName"]
      },
      {
        "title": "External reference",
        "attribute": "externalRef",
        "sortable": true
      },
      {
        "title": "Title",
        "attribute": "title",
        "sortable": true
      },
      {
        "title": "Training Org",
        "attribute": "outcomeIdTrainingOrg",
        "sortable": true
      },
      {
        "title": "Qualification",
        "attribute": "qualification.nationalCode",
        "sortable": true,
        "sortFields": ["qualification+.nationalCode"]
      }
    ],
    res: {
      sort: [{ attribute: "title", ascending: true, complexAttribute: [] }]
    }
  });
}
