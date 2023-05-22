import { ClassCost, CourseClass } from "@api/model";
import { generateArraysOfRecords, getEntityResponse } from "../../mockUtils";
import { CourseClass as CourseClassQueryModel } from "../../../../../build/generated-sources/aql-model/queryLanguageModel";
import * as Models from "../../../../../build/generated-sources/aql-model/queryLanguageModel";

export function mockCourseClasses() {
  this.getCourseClass = (id: any): CourseClass => {
    const row = this.courseClasses.rows.find(courseClass => Number(courseClass.id) === Number(id));
    const value = row.values;
    return {
      id: row.id,
      code: value[1],
      courseId: row.id,
      courseCode: value[1],
      courseName: value[0],
      endDateTime: value[3],
      startDateTime: value[2],
      attendanceType: "No information",
      deliveryMode: "Classroom",
      fundingSource: "Domestic full fee paying student",
      budgetedPlaces: 0,
      censusDate: "",
      createdOn: value[10],
      modifiedOn: value[10],
      deposit: 95.45,
      detBookingId: "",
      expectedHours: 0,
      feeExcludeGST: 95.45,
      finalDetExport: null,
      initialDetExport: null,
      isActive: true,
      isCancelled: false,
      isDistantLearningCourse: false,
      isShownOnWeb: true,
      maxStudentAge: 25,
      maximumDays: null,
      maximumPlaces: 50,
      message: "Lorem ipsum",
      midwayDetExport: null,
      minStudentAge: 10,
      minimumPlaces: 1,
      suppressAvetmissExport: false,
      vetCourseSiteID: 10,
      vetFundingSourceStateID: "F3",
      vetPurchasingContractID: "",
      vetPurchasingContractScheduleID: "",
      webDescription: "Lorem ipsum",
      relatedFundingSourceId: 4,
      qualificationHours: 0,
      nominalHours: 246.0,
      classroomHours: 246.0,
      studentContactHours: 240,
      reportableHours: 240,
      roomId: value[7],
      virtualSiteId: null,
      taxId: 1,
      summaryFee: null,
      summaryDiscounts: null,
      enrolmentsToProfitLeftCount: 0,
      allEnrolmentsCount: 1,
      allOutcomesCount: 1,
      inProgressOutcomesCount: 1,
      passOutcomesCount: 0,
      failedOutcomesCount: 0,
      withdrawnOutcomesCount: 0,
      otherOutcomesCount: 0,
      successAndQueuedEnrolmentsCount: 1,
      canceledEnrolmentsCount: 0,
      failedEnrolmentsCount: 0,
      tags: [this.getTag(1)],
      documents: [],
      isTraineeship: false,
      customFields: {},
      feeHelpClass: false,
    };
  };

  this.getCourseClasses = () => this.courseClasses;

  this.getCourseClassesTotalRows = (): String => String(this.getCourseClasses().rows.length);

  this.getPlainCourseClassList = params => {
    let rows: any[];
    const columns = params.columns;
    const keysForGeneratingArrayOfRecords = [{ name: "id", type: "number" }];

    if (columns) {
      columns.split(",").forEach(column => {
        let newItem;

        if (column.includes(".")) {
          // const numberOfDots = column.match(/\./g).length;
          const updColumn = column[0].toUpperCase() + column.substring(1);
          const entityName = updColumn.slice(0, updColumn.indexOf('.'));
          const fieldName = column.slice(column.lastIndexOf('.') + 1);
          const models = Models[entityName];
          const field = models ? models[fieldName] : 'String';

          newItem = { name: column, type: field };
        } else {
          const type = CourseClassQueryModel[column];
          newItem = { name: column, type: type || 'String' };
        }

        keysForGeneratingArrayOfRecords.push(newItem);
      });
    }

    if (columns.includes("course.name,course.code,code,feeIncGst,startDateTime,endDateTime")) {
      rows = generateArraysOfRecords(20, [
        { name: "id", type: "number" },
        { name: "courseName", type: "string" },
        { name: "courseCode", type: "string" },
        { name: "code", type: "number" },
        { name: "feeIncGst", type: "number" },
        { name: "startDateTime", type: "Datetime" },
        { name: "endDateTime", type: "Datetime" },
        { name: "placesLeft", type: "number" },
        { name: "roomName", type: "string" },
        { name: "roomSiteName", type: "string" },
        { name: "roomSiteLocalTimezone", type: "string" },
        { name: "contactFullName", type: "string" },
        { name: "sessionsStart", type: "Datetime" },
        { name: "sessionsend", type: "Datetime" },
        { name: "paymentPlanLinesId", type: "string" },
        { name: "isVET", type: "boolean" },
        { name: "isDistantLearningCourse", type: "boolean" },
        { name: "message", type: "string" },
        { name: "relatedFundingSourceId", type: "number" },
        { name: "sessionsId", type: "number" },
        { name: "fundingProviderId", type: "number" },
        { name: "vetPurchasingContractID", type: "number" },
        { name: "createdOn", type: "Datetime" },
      ]).map(l => ({
        id: l.id,
        values: [
          l.courseName,
          l.courseCode,
          l.code,
          "132.00",
          l.startDateTime,
          l.endDateTime,
          "9",
          l.roomName,
          l.roomSiteName,
          "Australia/Perth",
          "[[Eliatan Hill]]",
          `[${l.sessionsStart}]`,
          `[${l.sessionsEnd}]`,
          "[]",
          "false",
          "false",
          l.message,
          null,
          l.sessionsId,
          null,
          null,
          l.createdOn
        ]
      }));
    } else {
      rows = generateArraysOfRecords(20, keysForGeneratingArrayOfRecords).map(l => {
        const copiedObject = { ...l };
        delete copiedObject.id;

        const result = [];
        for (const key in copiedObject) {
          result.push(l[key]);
        }

        return {
          id: l.id,
          values: result
        };
      });
    }

    return getEntityResponse({
      entity: "CourseClass",
      rows,
      plain: true
    });
  };

  this.getCourseClassBudget = (classId: number): ClassCost[] => [
    {
      "id": 6455,
      "courseClassid": classId,
      "taxId": 1,
      "accountId": null,
      "invoiceId": null,
      "description": "attendance cost (automatically generated) 4994",
      "invoiceToStudent": false,
      "payableOnEnrolment": true,
      "isSunk": false,
      "maximumCost": null,
      "minimumCost": null,
      "onCostRate": 0.1500,
      "perUnitAmountExTax": 200.00,
      "perUnitAmountIncTax": null,
      "actualAmount": 4830.00,
      "unitCount": null,
      "contactId": 62,
      "contactName": "Eliatan Hill",
      "flowType": "Wages",
      "repetitionType": "Per timetabled hour",
      "isOverriden": true,
      "courseClassDiscount": null,
      "paymentPlan": [

      ],
      "courseClassTutorId": classId,
      "tutorRole": "General Tutor"
    },
    {
      "id": 7017,
      "courseClassid": classId,
      "taxId": 1,
      "accountId": null,
      "invoiceId": null,
      "description": "Fractions",
      "invoiceToStudent": false,
      "payableOnEnrolment": true,
      "isSunk": false,
      "maximumCost": null,
      "minimumCost": null,
      "onCostRate": null,
      "perUnitAmountExTax": 28.18,
      "perUnitAmountIncTax": null,
      "actualAmount": 0.00,
      "unitCount": 1.0000,
      "contactId": null,
      "contactName": null,
      "flowType": "Discount",
      "repetitionType": "Discount",
      "isOverriden": false,
      "courseClassDiscount": {
        "discount": {
          "id": 480,
          "name": "Fractions",
          "discountType": "Fee override",
          "rounding": "Nearest dollar",
          "discountValue": 144.55,
          "discountPercent": null,
          "discountMin": 0.00,
          "discountMax": 0.00,
          "cosAccount": null,
          "predictedStudentsPercentage": 0.10,
          "availableFor": 'Online and office',
          "code": null,
          "validFrom": null,
          "validFromOffset": null,
          "validTo": "2015-01-01",
          "validToOffset": null,
          "hideOnWeb": null,
          "description": null,
          "studentEnrolledWithinDays": null,
          "studentAgeUnder": null,
          "studentAge": null,
          "studentPostcode": null,
          "discountConcessionTypes": [

          ],
          "discountMemberships": [

          ],
          "discountCourseClasses": [

          ],
          "addByDefault": null,
          "minEnrolments": null,
          "minValue": null,
          "corporatePassDiscounts": [

          ],
          "createdOn": null,
          "modifiedOn": null,
          "limitPreviousEnrolment": null,
          "relationDiscount": false
        },
        "forecast": null,
        "discountOverride": null
      },
      "paymentPlan": [

      ],
      "courseClassTutorId": null,
      "tutorRole": null
    },
    {
      "id": 7257,
      "courseClassid": classId,
      "taxId": 1,
      "accountId": 1,
      "invoiceId": null,
      "description": "Student enrolment fee",
      "invoiceToStudent": true,
      "payableOnEnrolment": true,
      "isSunk": false,
      "maximumCost": null,
      "minimumCost": null,
      "onCostRate": null,
      "perUnitAmountExTax": 172.73,
      "perUnitAmountIncTax": 190.00,
      "actualAmount": 863.65,
      "unitCount": null,
      "contactId": null,
      "contactName": null,
      "flowType": "Income",
      "repetitionType": "Per enrolment",
      "isOverriden": false,
      "courseClassDiscount": null,
      "paymentPlan": [
        {
          "dayOffset": 28,
          "amount": 20.00
        },
        {
          "dayOffset": 35,
          "amount": 20.00
        },
        {
          "dayOffset": 14,
          "amount": 20.00
        },
        {
          "dayOffset": null,
          "amount": 90.00
        },
        {
          "dayOffset": 21,
          "amount": 20.00
        },
        {
          "dayOffset": 7,
          "amount": 20.00
        }
      ],
      "courseClassTutorId": null,
      "tutorRole": null
    },
    {
      "id": 7862,
      "courseClassid": classId,
      "taxId": 1,
      "accountId": null,
      "invoiceId": null,
      "description": "CorpPass Discount",
      "invoiceToStudent": false,
      "payableOnEnrolment": true,
      "isSunk": false,
      "maximumCost": null,
      "minimumCost": null,
      "onCostRate": null,
      "perUnitAmountExTax": 17.27,
      "perUnitAmountIncTax": null,
      "actualAmount": 0.00,
      "unitCount": 1.0000,
      "contactId": null,
      "contactName": null,
      "flowType": "Discount",
      "repetitionType": "Discount",
      "isOverriden": false,
      "courseClassDiscount": {
        "discount": {
          "id": 527,
          "name": "CorpPass Discount",
          "discountType": "Percent",
          "rounding": "No Rounding",
          "discountValue": null,
          "discountPercent": 0.100,
          "discountMin": null,
          "discountMax": null,
          "cosAccount": null,
          "predictedStudentsPercentage": 0.10,
          "availableFor": 'Online and office',
          "code": null,
          "validFrom": null,
          "validFromOffset": null,
          "validTo": null,
          "validToOffset": null,
          "hideOnWeb": null,
          "description": null,
          "studentEnrolledWithinDays": null,
          "studentAgeUnder": null,
          "studentAge": null,
          "studentPostcode": null,
          "discountConcessionTypes": [

          ],
          "discountMemberships": [

          ],
          "discountCourseClasses": [

          ],
          "addByDefault": null,
          "minEnrolments": null,
          "minValue": null,
          "corporatePassDiscounts": [

          ],
          "createdOn": null,
          "modifiedOn": null,
          "limitPreviousEnrolment": null,
          "relationDiscount": false
        },
        "forecast": null,
        "discountOverride": null
      },
      "paymentPlan": [

      ],
      "courseClassTutorId": null,
      "tutorRole": null
    },
    {
      "id": 8209,
      "courseClassid": classId,
      "taxId": 1,
      "accountId": null,
      "invoiceId": null,
      "description": "Small organisation discount",
      "invoiceToStudent": false,
      "payableOnEnrolment": true,
      "isSunk": false,
      "maximumCost": null,
      "minimumCost": null,
      "onCostRate": null,
      "perUnitAmountExTax": 15.00,
      "perUnitAmountIncTax": null,
      "actualAmount": 0.00,
      "unitCount": 1.0000,
      "contactId": null,
      "contactName": null,
      "flowType": "Discount",
      "repetitionType": "Discount",
      "isOverriden": false,
      "courseClassDiscount": {
        "discount": {
          "id": 528,
          "name": "Small organisation discount",
          "discountType": "Dollar",
          "rounding": "No Rounding",
          "discountValue": 15.00,
          "discountPercent": 0.100,
          "discountMin": null,
          "discountMax": null,
          "cosAccount": null,
          "predictedStudentsPercentage": 0.10,
          "availableFor": 'Online and office',
          "code": "tenoff",
          "validFrom": null,
          "validFromOffset": null,
          "validTo": null,
          "validToOffset": null,
          "hideOnWeb": null,
          "description": null,
          "studentEnrolledWithinDays": null,
          "studentAgeUnder": null,
          "studentAge": null,
          "studentPostcode": null,
          "discountConcessionTypes": [

          ],
          "discountMemberships": [

          ],
          "discountCourseClasses": [

          ],
          "addByDefault": null,
          "minEnrolments": null,
          "minValue": null,
          "corporatePassDiscounts": [

          ],
          "createdOn": null,
          "modifiedOn": null,
          "limitPreviousEnrolment": null,
          "relationDiscount": false
        },
        "forecast": null,
        "discountOverride": null
      },
      "paymentPlan": [

      ],
      "courseClassTutorId": null,
      "tutorRole": null
    },
    {
      "id": 28007,
      "courseClassid": classId,
      "taxId": 1,
      "accountId": null,
      "invoiceId": null,
      "description": "1 YR DISCOUNT",
      "invoiceToStudent": false,
      "payableOnEnrolment": true,
      "isSunk": false,
      "maximumCost": null,
      "minimumCost": null,
      "onCostRate": null,
      "perUnitAmountExTax": 17.27,
      "perUnitAmountIncTax": null,
      "actualAmount": 0.00,
      "unitCount": 1.0000,
      "contactId": null,
      "contactName": null,
      "flowType": "Discount",
      "repetitionType": "Discount",
      "isOverriden": false,
      "courseClassDiscount": {
        "discount": {
          "id": 541,
          "name": "1 YR DISCOUNT",
          "discountType": "Percent",
          "rounding": "No Rounding",
          "discountValue": null,
          "discountPercent": 0.100,
          "discountMin": null,
          "discountMax": null,
          "cosAccount": null,
          "predictedStudentsPercentage": 0.10,
          "availableFor": 'Online and office',
          "code": null,
          "validFrom": "2018-01-01",
          "validFromOffset": null,
          "validTo": "2018-05-21",
          "validToOffset": null,
          "hideOnWeb": null,
          "description": null,
          "studentEnrolledWithinDays": null,
          "studentAgeUnder": null,
          "studentAge": null,
          "studentPostcode": null,
          "discountConcessionTypes": [

          ],
          "discountMemberships": [

          ],
          "discountCourseClasses": [

          ],
          "addByDefault": null,
          "minEnrolments": null,
          "minValue": null,
          "corporatePassDiscounts": [

          ],
          "createdOn": null,
          "modifiedOn": null,
          "limitPreviousEnrolment": null,
          "relationDiscount": false
        },
        "forecast": null,
        "discountOverride": null
      },
      "paymentPlan": [

      ],
      "courseClassTutorId": null,
      "tutorRole": null
    },
    {
      "id": 28574,
      "courseClassid": classId,
      "taxId": 1,
      "accountId": null,
      "invoiceId": null,
      "description": "Linksea Test",
      "invoiceToStudent": false,
      "payableOnEnrolment": true,
      "isSunk": false,
      "maximumCost": null,
      "minimumCost": null,
      "onCostRate": null,
      "perUnitAmountExTax": 172.73,
      "perUnitAmountIncTax": null,
      "actualAmount": 0.00,
      "unitCount": 1.0000,
      "contactId": null,
      "contactName": null,
      "flowType": "Discount",
      "repetitionType": "Discount",
      "isOverriden": false,
      "courseClassDiscount": {
        "discount": {
          "id": 542,
          "name": "Linksea Test",
          "discountType": "Percent",
          "rounding": "No Rounding",
          "discountValue": null,
          "discountPercent": 1.000,
          "discountMin": null,
          "discountMax": null,
          "cosAccount": null,
          "predictedStudentsPercentage": 0.10,
          "availableFor": 'Online and office',
          "code": null,
          "validFrom": null,
          "validFromOffset": null,
          "validTo": null,
          "validToOffset": null,
          "hideOnWeb": null,
          "description": null,
          "studentEnrolledWithinDays": null,
          "studentAgeUnder": null,
          "studentAge": null,
          "studentPostcode": null,
          "discountConcessionTypes": [

          ],
          "discountMemberships": [

          ],
          "discountCourseClasses": [

          ],
          "addByDefault": null,
          "minEnrolments": null,
          "minValue": null,
          "corporatePassDiscounts": [

          ],
          "createdOn": null,
          "modifiedOn": null,
          "limitPreviousEnrolment": null,
          "relationDiscount": false
        },
        "forecast": null,
        "discountOverride": null
      },
      "paymentPlan": [

      ],
      "courseClassTutorId": null,
      "tutorRole": null
    }
  ];

  this.getCourseClassSelectedSessions = () => [4671, 25336, 4140, 4362];

  this.getCourseClassTimetable = () => [
    {
      "id": 4671,
      "temporaryId": null,
      "name": "Certificate III in Permaculture",
      "code": null,
      "room": "Room 1",
      "site": "Perth office",
      "tutors": this.getCourseClassTutors().map(t => t.tutorName),
      "tutorAttendances": [

      ],
      "classId": null,
      "courseId": 782,
      "roomId": 280,
      "siteId": 260,
      "siteTimezone": "Australia/West",
      "start": "2023-06-20T23:00:00.000Z",
      "end": "2023-06-21T05:00:00.000Z",
      "publicNotes": "",
      "privateNotes": "",
      "hasPaylines": false
    },
    {
      "id": 25336,
      "temporaryId": null,
      "name": "Certificate III in Permaculture",
      "code": null,
      "room": "Room 1",
      "site": "Perth office",
      "tutors": this.getCourseClassTutors().map(t => t.tutorName),
      "tutorAttendances": [

      ],
      "classId": null,
      "courseId": 782,
      "roomId": 280,
      "siteId": 260,
      "siteTimezone": "Australia/West",
      "start": "2023-06-20T23:00:00.000Z",
      "end": "2023-06-21T05:00:00.000Z",
      "publicNotes": "",
      "privateNotes": "",
      "hasPaylines": false
    },
    {
      "id": 4140,
      "temporaryId": null,
      "name": "Certificate III in Permaculture",
      "code": null,
      "room": "Room 1",
      "site": "Perth office",
      "tutors": this.getCourseClassTutors().map(t => t.tutorName),
      "tutorAttendances": [

      ],
      "classId": null,
      "courseId": 782,
      "roomId": 280,
      "siteId": 260,
      "siteTimezone": "Australia/West",
      "start": "2023-06-21T23:00:00.000Z",
      "end": "2023-06-22T05:00:00.000Z",
      "publicNotes": "",
      "privateNotes": "",
      "hasPaylines": false
    },
    {
      "id": 4362,
      "temporaryId": null,
      "name": "Certificate III in Permaculture",
      "code": null,
      "room": "Room 1",
      "site": "Perth office",
      "tutors": this.getCourseClassTutors().map(t => t.tutorName),
      "tutorAttendances": [

      ],
      "classId": null,
      "courseId": 782,
      "roomId": 280,
      "siteId": 260,
      "siteTimezone": "Australia/West",
      "start": "2023-06-22T23:00:00.000Z",
      "end": "2023-06-23T05:00:00.000Z",
      "publicNotes": "",
      "privateNotes": "",
      "hasPaylines": false
    },
    {
      "id": 4139,
      "temporaryId": null,
      "name": "Certificate III in Permaculture",
      "code": null,
      "room": "Room 1",
      "site": "Perth office",
      "tutors": this.getCourseClassTutors().map(t => t.tutorName),
      "tutorAttendances": [

      ],
      "classId": null,
      "courseId": 782,
      "roomId": 280,
      "siteId": 260,
      "siteTimezone": "Australia/West",
      "start": "2023-06-23T23:00:00.000Z",
      "end": "2023-06-24T05:00:00.000Z",
      "publicNotes": "",
      "privateNotes": "",
      "hasPaylines": false
    },
    {
      "id": 3531,
      "temporaryId": null,
      "name": "Certificate III in Permaculture",
      "code": null,
      "room": "Room 1",
      "site": "Perth office",
      "tutors": this.getCourseClassTutors().map(t => t.tutorName),
      "tutorAttendances": [

      ],
      "classId": null,
      "courseId": 782,
      "roomId": 280,
      "siteId": 260,
      "siteTimezone": "Australia/West",
      "start": "2023-06-24T23:00:00.000Z",
      "end": "2023-06-25T05:00:00.000Z",
      "publicNotes": "",
      "privateNotes": "",
      "hasPaylines": false
    },
    {
      "id": 5161,
      "temporaryId": null,
      "name": "Certificate III in Permaculture",
      "code": null,
      "room": "Room 1",
      "site": "Perth office",
      "tutors": this.getCourseClassTutors().map(t => t.tutorName),
      "tutorAttendances": [

      ],
      "classId": null,
      "courseId": 782,
      "roomId": 280,
      "siteId": 260,
      "siteTimezone": "Australia/West",
      "start": "2023-06-27T23:00:00.000Z",
      "end": "2023-06-28T05:00:00.000Z",
      "publicNotes": "",
      "privateNotes": "",
      "hasPaylines": false
    },
    {
      "id": 3473,
      "temporaryId": null,
      "name": "Certificate III in Permaculture",
      "code": null,
      "room": "Room 1",
      "site": "Perth office",
      "tutors": this.getCourseClassTutors().map(t => t.tutorName),
      "tutorAttendances": [

      ],
      "classId": null,
      "courseId": 782,
      "roomId": 280,
      "siteId": 260,
      "siteTimezone": "Australia/West",
      "start": "2023-06-28T23:00:00.000Z",
      "end": "2023-06-29T05:00:00.000Z",
      "publicNotes": "",
      "privateNotes": "",
      "hasPaylines": false
    },
    {
      "id": 4926,
      "temporaryId": null,
      "name": "Certificate III in Permaculture",
      "code": null,
      "room": "Room 1",
      "site": "Perth office",
      "tutors": this.getCourseClassTutors().map(t => t.tutorName),
      "tutorAttendances": [

      ],
      "classId": null,
      "courseId": 782,
      "roomId": 280,
      "siteId": 260,
      "siteTimezone": "Australia/West",
      "start": "2023-06-29T23:00:00.000Z",
      "end": "2023-06-30T05:00:00.000Z",
      "publicNotes": "",
      "privateNotes": "",
      "hasPaylines": false
    },
    {
      "id": 4925,
      "temporaryId": null,
      "name": "Certificate III in Permaculture",
      "code": null,
      "room": "Room 1",
      "site": "Perth office",
      "tutors": this.getCourseClassTutors().map(t => t.tutorName),
      "tutorAttendances": [

      ],
      "classId": null,
      "courseId": 782,
      "roomId": 280,
      "siteId": 260,
      "siteTimezone": "Australia/West",
      "start": "2023-06-30T23:00:00.000Z",
      "end": "2023-07-01T05:00:00.000Z",
      "publicNotes": "",
      "privateNotes": "",
      "hasPaylines": false
    },
    {
      "id": 3402,
      "temporaryId": null,
      "name": "Certificate III in Permaculture",
      "code": null,
      "room": "Room 1",
      "site": "Perth office",
      "tutors": this.getCourseClassTutors().map(t => t.tutorName),
      "tutorAttendances": [

      ],
      "classId": null,
      "courseId": 782,
      "roomId": 280,
      "siteId": 260,
      "siteTimezone": "Australia/West",
      "start": "2023-07-01T23:00:00.000Z",
      "end": "2023-07-02T05:00:00.000Z",
      "publicNotes": "",
      "privateNotes": "",
      "hasPaylines": false
    },
    {
      "id": 4249,
      "temporaryId": null,
      "name": "Certificate III in Permaculture",
      "code": null,
      "room": "Room 1",
      "site": "Perth office",
      "tutors": this.getCourseClassTutors().map(t => t.tutorName),
      "tutorAttendances": [

      ],
      "classId": null,
      "courseId": 782,
      "roomId": 280,
      "siteId": 260,
      "siteTimezone": "Australia/West",
      "start": "2023-07-04T23:00:00.000Z",
      "end": "2023-07-05T05:00:00.000Z",
      "publicNotes": "",
      "privateNotes": "",
      "hasPaylines": false
    },
    {
      "id": 4195,
      "temporaryId": null,
      "name": "Certificate III in Permaculture",
      "code": null,
      "room": "Room 1",
      "site": "Perth office",
      "tutors": this.getCourseClassTutors().map(t => t.tutorName),
      "tutorAttendances": [

      ],
      "classId": null,
      "courseId": 782,
      "roomId": 280,
      "siteId": 260,
      "siteTimezone": "Australia/West",
      "start": "2023-07-05T23:00:00.000Z",
      "end": "2023-07-06T05:00:00.000Z",
      "publicNotes": "",
      "privateNotes": "",
      "hasPaylines": false
    },
    {
      "id": 3710,
      "temporaryId": null,
      "name": "Certificate III in Permaculture",
      "code": null,
      "room": "Room 1",
      "site": "Perth office",
      "tutors": this.getCourseClassTutors().map(t => t.tutorName),
      "tutorAttendances": [

      ],
      "classId": null,
      "courseId": 782,
      "roomId": 280,
      "siteId": 260,
      "siteTimezone": "Australia/West",
      "start": "2023-07-06T23:00:00.000Z",
      "end": "2023-07-07T05:00:00.000Z",
      "publicNotes": "",
      "privateNotes": "",
      "hasPaylines": false
    },
    {
      "id": 3602,
      "temporaryId": null,
      "name": "Certificate III in Permaculture",
      "code": null,
      "room": "Room 1",
      "site": "Perth office",
      "tutors": this.getCourseClassTutors().map(t => t.tutorName),
      "tutorAttendances": [

      ],
      "classId": null,
      "courseId": 782,
      "roomId": 280,
      "siteId": 260,
      "siteTimezone": "Australia/West",
      "start": "2023-07-07T23:00:00.000Z",
      "end": "2023-07-08T05:00:00.000Z",
      "publicNotes": "",
      "privateNotes": "",
      "hasPaylines": false
    },
    {
      "id": 3326,
      "temporaryId": null,
      "name": "Certificate III in Permaculture",
      "code": null,
      "room": "Room 1",
      "site": "Perth office",
      "tutors": this.getCourseClassTutors().map(t => t.tutorName),
      "tutorAttendances": [

      ],
      "classId": null,
      "courseId": 782,
      "roomId": 280,
      "siteId": 260,
      "siteTimezone": "Australia/West",
      "start": "2023-07-08T23:00:00.000Z",
      "end": "2023-07-09T05:00:00.000Z",
      "publicNotes": "",
      "privateNotes": "",
      "hasPaylines": false
    },
    {
      "id": 4556,
      "temporaryId": null,
      "name": "Certificate III in Permaculture",
      "code": null,
      "room": "Room 1",
      "site": "Perth office",
      "tutors": this.getCourseClassTutors().map(t => t.tutorName),
      "tutorAttendances": [

      ],
      "classId": null,
      "courseId": 782,
      "roomId": 280,
      "siteId": 260,
      "siteTimezone": "Australia/West",
      "start": "2023-07-11T23:00:00.000Z",
      "end": "2023-07-12T05:00:00.000Z",
      "publicNotes": "",
      "privateNotes": "",
      "hasPaylines": false
    },
    {
      "id": 4796,
      "temporaryId": null,
      "name": "Certificate III in Permaculture",
      "code": null,
      "room": "Room 1",
      "site": "Perth office",
      "tutors": this.getCourseClassTutors().map(t => t.tutorName),
      "tutorAttendances": [

      ],
      "classId": null,
      "courseId": 782,
      "roomId": 280,
      "siteId": 260,
      "siteTimezone": "Australia/West",
      "start": "2023-07-12T23:00:00.000Z",
      "end": "2023-07-13T05:00:00.000Z",
      "publicNotes": "",
      "privateNotes": "",
      "hasPaylines": false
    },
    {
      "id": 4560,
      "temporaryId": null,
      "name": "Certificate III in Permaculture",
      "code": null,
      "room": "Room 1",
      "site": "Perth office",
      "tutors": this.getCourseClassTutors().map(t => t.tutorName),
      "tutorAttendances": [

      ],
      "classId": null,
      "courseId": 782,
      "roomId": 280,
      "siteId": 260,
      "siteTimezone": "Australia/West",
      "start": "2023-07-13T23:00:00.000Z",
      "end": "2023-07-14T05:00:00.000Z",
      "publicNotes": "",
      "privateNotes": "",
      "hasPaylines": false
    },
    {
      "id": 5227,
      "temporaryId": null,
      "name": "Certificate III in Permaculture",
      "code": null,
      "room": "Room 1",
      "site": "Perth office",
      "tutors": this.getCourseClassTutors().map(t => t.tutorName),
      "tutorAttendances": [

      ],
      "classId": null,
      "courseId": 782,
      "roomId": 280,
      "siteId": 260,
      "siteTimezone": "Australia/West",
      "start": "2023-07-14T23:00:00.000Z",
      "end": "2023-07-15T05:00:00.000Z",
      "publicNotes": "",
      "privateNotes": "",
      "hasPaylines": false
    },
  ];

  this.getCourseClassTimetableSessions = () => [
    {
      "sessionId": 4671,
      "temporaryId": null,
      "type": "Room",
      "referenceId": 280,
      "label": "Room 1",
      "message": "Room Room 1 is already booked for 30868QLDD-5 at Wed. 21 Jun. 7:00(Australia/West) \nCHC30113-5 at Wed. 21 Jun. 7:00(Australia/West) \n30868QLDD-8 at Wed. 21 Jun. 7:00(Australia/West) \n"
    },
    {
      "sessionId": 4671,
      "temporaryId": null,
      "type": "Session",
      "referenceId": null,
      "label": null,
      "message": "Class already has session for that time"
    },
    {
      "sessionId": 25336,
      "temporaryId": null,
      "type": "Room",
      "referenceId": 280,
      "label": "Room 1",
      "message": "Room Room 1 is already booked for 30868QLDD-5 at Wed. 21 Jun. 7:00(Australia/West) \nCHC30113-5 at Wed. 21 Jun. 7:00(Australia/West) \n30868QLDD-8 at Wed. 21 Jun. 7:00(Australia/West) \n"
    },
    {
      "sessionId": 25336,
      "temporaryId": null,
      "type": "Session",
      "referenceId": null,
      "label": null,
      "message": "Class already has session for that time"
    },
    {
      "sessionId": 4140,
      "temporaryId": null,
      "type": "Room",
      "referenceId": 280,
      "label": "Room 1",
      "message": "Room Room 1 is already booked for 30868QLDD-5 at Thu. 22 Jun. 7:00(Australia/West) \nCHC30113-5 at Thu. 22 Jun. 7:00(Australia/West) \n30868QLDD-8 at Thu. 22 Jun. 7:00(Australia/West) \n"
    },
    {
      "sessionId": 4362,
      "temporaryId": null,
      "type": "Room",
      "referenceId": 280,
      "label": "Room 1",
      "message": "Room Room 1 is already booked for CHC30113-5 at Fri. 23 Jun. 7:00(Australia/West) \n30868QLDD-5 at Fri. 23 Jun. 7:00(Australia/West) \nCHC30113-12 at Fri. 23 Jun. 7:00(Australia/West) \n"
    },
    {
      "sessionId": 4139,
      "temporaryId": null,
      "type": "Room",
      "referenceId": 280,
      "label": "Room 1",
      "message": "Room Room 1 is already booked for 30868QLDD-5 at Sat. 24 Jun. 7:00(Australia/West) \nCHC30113-5 at Sat. 24 Jun. 7:00(Australia/West) \nCHC30113-12 at Sat. 24 Jun. 7:00(Australia/West) \n"
    },
    {
      "sessionId": 3531,
      "temporaryId": null,
      "type": "Room",
      "referenceId": 280,
      "label": "Room 1",
      "message": "Room Room 1 is already booked for CHC30113-5 at Sun. 25 Jun. 7:00(Australia/West) \n30868QLDD-5 at Sun. 25 Jun. 7:00(Australia/West) \nCHC30113-12 at Sun. 25 Jun. 7:00(Australia/West) \n"
    },
    {
      "sessionId": 5161,
      "temporaryId": null,
      "type": "Room",
      "referenceId": 280,
      "label": "Room 1",
      "message": "Room Room 1 is already booked for CHC30113-5 at Wed. 28 Jun. 7:00(Australia/West) \n30868QLDD-5 at Wed. 28 Jun. 7:00(Australia/West) \n30868QLDD-8 at Wed. 28 Jun. 7:00(Australia/West) \n"
    },
    {
      "sessionId": 3473,
      "temporaryId": null,
      "type": "Room",
      "referenceId": 280,
      "label": "Room 1",
      "message": "Room Room 1 is already booked for CHC30113-5 at Thu. 29 Jun. 7:00(Australia/West) \n30868QLDD-5 at Thu. 29 Jun. 7:00(Australia/West) \n30868QLDD-8 at Thu. 29 Jun. 7:00(Australia/West) \n"
    },
    {
      "sessionId": 4926,
      "temporaryId": null,
      "type": "Room",
      "referenceId": 280,
      "label": "Room 1",
      "message": "Room Room 1 is already booked for CHC30113-5 at Fri. 30 Jun. 7:00(Australia/West) \n30868QLDD-5 at Fri. 30 Jun. 7:00(Australia/West) \nCHC30113-12 at Fri. 30 Jun. 7:00(Australia/West) \n"
    },
    {
      "sessionId": 4925,
      "temporaryId": null,
      "type": "Room",
      "referenceId": 280,
      "label": "Room 1",
      "message": "Room Room 1 is already booked for CHC30113-5 at Sat. 1 Jul. 7:00(Australia/West) \n30868QLDD-5 at Sat. 1 Jul. 7:00(Australia/West) \nCHC30113-12 at Sat. 1 Jul. 7:00(Australia/West) \n"
    },
    {
      "sessionId": 3402,
      "temporaryId": null,
      "type": "Room",
      "referenceId": 280,
      "label": "Room 1",
      "message": "Room Room 1 is already booked for 30868QLDD-5 at Sun. 2 Jul. 7:00(Australia/West) \nCHC30113-5 at Sun. 2 Jul. 7:00(Australia/West) \nCHC30113-12 at Sun. 2 Jul. 7:00(Australia/West) \n"
    },
    {
      "sessionId": 4249,
      "temporaryId": null,
      "type": "Room",
      "referenceId": 280,
      "label": "Room 1",
      "message": "Room Room 1 is already booked for 30868QLDD-5 at Wed. 5 Jul. 7:00(Australia/West) \nCHC30113-5 at Wed. 5 Jul. 7:00(Australia/West) \n30868QLDD-8 at Wed. 5 Jul. 7:00(Australia/West) \n"
    },
    {
      "sessionId": 4195,
      "temporaryId": null,
      "type": "Room",
      "referenceId": 280,
      "label": "Room 1",
      "message": "Room Room 1 is already booked for CHC30113-5 at Thu. 6 Jul. 7:00(Australia/West) \n30868QLDD-5 at Thu. 6 Jul. 7:00(Australia/West) \n30868QLDD-8 at Thu. 6 Jul. 7:00(Australia/West) \n"
    },
    {
      "sessionId": 3710,
      "temporaryId": null,
      "type": "Room",
      "referenceId": 280,
      "label": "Room 1",
      "message": "Room Room 1 is already booked for CHC30113-5 at Fri. 7 Jul. 7:00(Australia/West) \n30868QLDD-5 at Fri. 7 Jul. 7:00(Australia/West) \nCHC30113-12 at Fri. 7 Jul. 7:00(Australia/West) \n"
    },
    {
      "sessionId": 3602,
      "temporaryId": null,
      "type": "Room",
      "referenceId": 280,
      "label": "Room 1",
      "message": "Room Room 1 is already booked for 30868QLDD-5 at Sat. 8 Jul. 7:00(Australia/West) \nCHC30113-5 at Sat. 8 Jul. 7:00(Australia/West) \nCHC30113-12 at Sat. 8 Jul. 7:00(Australia/West) \n"
    },
    {
      "sessionId": 3326,
      "temporaryId": null,
      "type": "Room",
      "referenceId": 280,
      "label": "Room 1",
      "message": "Room Room 1 is already booked for 30868QLDD-5 at Sun. 9 Jul. 7:00(Australia/West) \nCHC30113-5 at Sun. 9 Jul. 7:00(Australia/West) \nCHC30113-12 at Sun. 9 Jul. 7:00(Australia/West) \n"
    },
    {
      "sessionId": 4556,
      "temporaryId": null,
      "type": "Room",
      "referenceId": 280,
      "label": "Room 1",
      "message": "Room Room 1 is already booked for 30868QLDD-5 at Wed. 12 Jul. 7:00(Australia/West) \n30868QLDD-8 at Wed. 12 Jul. 7:00(Australia/West) \n30868QLDD-9 at Wed. 12 Jul. 7:00(Australia/West) \n"
    },
    {
      "sessionId": 4796,
      "temporaryId": null,
      "type": "Room",
      "referenceId": 280,
      "label": "Room 1",
      "message": "Room Room 1 is already booked for 30868QLDD-5 at Thu. 13 Jul. 7:00(Australia/West) \n30868QLDD-8 at Thu. 13 Jul. 7:00(Australia/West) \n30868QLDD-9 at Thu. 13 Jul. 7:00(Australia/West) \n"
    },
    {
      "sessionId": 4560,
      "temporaryId": null,
      "type": "Room",
      "referenceId": 280,
      "label": "Room 1",
      "message": "Room Room 1 is already booked for 30868QLDD-5 at Fri. 14 Jul. 7:00(Australia/West) \nCHC30113-12 at Fri. 14 Jul. 7:00(Australia/West) \n30868QLDD-8 at Fri. 14 Jul. 7:00(Australia/West) \n"
    },
    {
      "sessionId": 5227,
      "temporaryId": null,
      "type": "Room",
      "referenceId": 280,
      "label": "Room 1",
      "message": "Room Room 1 is already booked for 30868QLDD-5 at Sat. 15 Jul. 7:00(Australia/West) \nCHC30113-12 at Sat. 15 Jul. 7:00(Australia/West) \n30868QLDD-8 at Sat. 15 Jul. 7:00(Australia/West) \n"
    },
    {
      "sessionId": 3776,
      "temporaryId": null,
      "type": "Room",
      "referenceId": 280,
      "label": "Room 1",
      "message": "Room Room 1 is already booked for 30868QLDD-5 at Sun. 16 Jul. 7:00(Australia/West) \nCHC30113-12 at Sun. 16 Jul. 7:00(Australia/West) \n30868QLDD-8 at Sun. 16 Jul. 7:00(Australia/West) \n"
    },
    {
      "sessionId": 3891,
      "temporaryId": null,
      "type": "Room",
      "referenceId": 280,
      "label": "Room 1",
      "message": "Room Room 1 is already booked for 30868QLDD-5 at Wed. 19 Jul. 7:00(Australia/West) \n30868QLDD-8 at Wed. 19 Jul. 7:00(Australia/West) \n30868QLDD-9 at Wed. 19 Jul. 7:00(Australia/West) \n"
    },
    {
      "sessionId": 3779,
      "temporaryId": null,
      "type": "Room",
      "referenceId": 280,
      "label": "Room 1",
      "message": "Room Room 1 is already booked for 30868QLDD-5 at Thu. 20 Jul. 7:00(Australia/West) \n30868QLDD-8 at Thu. 20 Jul. 7:00(Australia/West) \n30868QLDD-9 at Thu. 20 Jul. 7:00(Australia/West) \n"
    },
    {
      "sessionId": 4734,
      "temporaryId": null,
      "type": "Room",
      "referenceId": 280,
      "label": "Room 1",
      "message": "Room Room 1 is already booked for 30868QLDD-5 at Fri. 21 Jul. 7:00(Australia/West) \nCHC30113-12 at Fri. 21 Jul. 7:00(Australia/West) \n30868QLDD-8 at Fri. 21 Jul. 7:00(Australia/West) \n"
    },
    {
      "sessionId": 3957,
      "temporaryId": null,
      "type": "Room",
      "referenceId": 280,
      "label": "Room 1",
      "message": "Room Room 1 is already booked for 30868QLDD-5 at Sat. 22 Jul. 7:00(Australia/West) \nCHC30113-12 at Sat. 22 Jul. 7:00(Australia/West) \n30868QLDD-8 at Sat. 22 Jul. 7:00(Australia/West) \n"
    },
    {
      "sessionId": 3643,
      "temporaryId": null,
      "type": "Room",
      "referenceId": 280,
      "label": "Room 1",
      "message": "Room Room 1 is already booked for 30868QLDD-5 at Sun. 23 Jul. 7:00(Australia/West) \nCHC30113-12 at Sun. 23 Jul. 7:00(Australia/West) \n30868QLDD-8 at Sun. 23 Jul. 7:00(Australia/West) \n"
    },
    {
      "sessionId": 3587,
      "temporaryId": null,
      "type": "Room",
      "referenceId": 280,
      "label": "Room 1",
      "message": "Room Room 1 is already booked for 30868QLDD-8 at Wed. 26 Jul. 7:00(Australia/West) \n30868QLDD-9 at Wed. 26 Jul. 7:00(Australia/West) \n"
    },
    {
      "sessionId": 4025,
      "temporaryId": null,
      "type": "Room",
      "referenceId": 280,
      "label": "Room 1",
      "message": "Room Room 1 is already booked for 30868QLDD-8 at Thu. 27 Jul. 7:00(Australia/West) \n30868QLDD-9 at Thu. 27 Jul. 7:00(Australia/West) \n"
    },
    {
      "sessionId": 4095,
      "temporaryId": null,
      "type": "Room",
      "referenceId": 280,
      "label": "Room 1",
      "message": "Room Room 1 is already booked for CHC30113-12 at Fri. 28 Jul. 7:00(Australia/West) \n30868QLDD-8 at Fri. 28 Jul. 7:00(Australia/West) \n30868QLDD-9 at Fri. 28 Jul. 7:00(Australia/West) \n"
    },
    {
      "sessionId": 5226,
      "temporaryId": null,
      "type": "Room",
      "referenceId": 280,
      "label": "Room 1",
      "message": "Room Room 1 is already booked for CHC30113-12 at Sat. 29 Jul. 7:00(Australia/West) \n30868QLDD-8 at Sat. 29 Jul. 7:00(Australia/West) \n30868QLDD-9 at Sat. 29 Jul. 7:00(Australia/West) \n"
    },
    {
      "sessionId": 4835,
      "temporaryId": null,
      "type": "Room",
      "referenceId": 280,
      "label": "Room 1",
      "message": "Room Room 1 is already booked for CHC30113-12 at Sun. 30 Jul. 7:00(Australia/West) \n30868QLDD-8 at Sun. 30 Jul. 7:00(Australia/West) \n30868QLDD-9 at Sun. 30 Jul. 7:00(Australia/West) \n"
    },
    {
      "sessionId": 3825,
      "temporaryId": null,
      "type": "Room",
      "referenceId": 280,
      "label": "Room 1",
      "message": "Room Room 1 is already booked for 30868QLDD-8 at Wed. 2 Aug. 7:00(Australia/West) \n30868QLDD-9 at Wed. 2 Aug. 7:00(Australia/West) \n"
    },
    {
      "sessionId": 4442,
      "temporaryId": null,
      "type": "Room",
      "referenceId": 280,
      "label": "Room 1",
      "message": "Room Room 1 is already booked for 30868QLDD-8 at Thu. 3 Aug. 7:00(Australia/West) \n30868QLDD-9 at Thu. 3 Aug. 7:00(Australia/West) \n"
    },
    {
      "sessionId": 4358,
      "temporaryId": null,
      "type": "Room",
      "referenceId": 280,
      "label": "Room 1",
      "message": "Room Room 1 is already booked for CHC30113-12 at Fri. 4 Aug. 7:00(Australia/West) \n30868QLDD-8 at Fri. 4 Aug. 7:00(Australia/West) \n30868QLDD-9 at Fri. 4 Aug. 7:00(Australia/West) \n"
    },
    {
      "sessionId": 5119,
      "temporaryId": null,
      "type": "Room",
      "referenceId": 280,
      "label": "Room 1",
      "message": "Room Room 1 is already booked for CHC30113-12 at Sat. 5 Aug. 7:00(Australia/West) \n30868QLDD-8 at Sat. 5 Aug. 7:00(Australia/West) \n30868QLDD-9 at Sat. 5 Aug. 7:00(Australia/West) \n"
    },
    {
      "sessionId": 4496,
      "temporaryId": null,
      "type": "Room",
      "referenceId": 280,
      "label": "Room 1",
      "message": "Room Room 1 is already booked for CHC30113-12 at Sun. 6 Aug. 7:00(Australia/West) \n30868QLDD-8 at Sun. 6 Aug. 7:00(Australia/West) \n30868QLDD-9 at Sun. 6 Aug. 7:00(Australia/West) \n"
    },
    {
      "sessionId": 4989,
      "temporaryId": null,
      "type": "Room",
      "referenceId": 280,
      "label": "Room 1",
      "message": "Room Room 1 is already booked for 30868QLDD-8 at Wed. 9 Aug. 7:00(Australia/West) \n30868QLDD-9 at Wed. 9 Aug. 7:00(Australia/West) \n"
    },
    {
      "sessionId": 3472,
      "temporaryId": null,
      "type": "Room",
      "referenceId": 280,
      "label": "Room 1",
      "message": "Room Room 1 is already booked for 30868QLDD-8 at Thu. 10 Aug. 7:00(Australia/West) \n30868QLDD-9 at Thu. 10 Aug. 7:00(Australia/West) \n"
    },
    {
      "sessionId": 5037,
      "temporaryId": null,
      "type": "Room",
      "referenceId": 280,
      "label": "Room 1",
      "message": "Room Room 1 is already booked for 30868QLDD-8 at Fri. 11 Aug. 7:00(Australia/West) \n30868QLDD-9 at Fri. 11 Aug. 7:00(Australia/West) \n"
    },
    {
      "sessionId": 4673,
      "temporaryId": null,
      "type": "Room",
      "referenceId": 280,
      "label": "Room 1",
      "message": "Room Room 1 is already booked for 30868QLDD-8 at Sat. 12 Aug. 7:00(Australia/West) \n30868QLDD-9 at Sat. 12 Aug. 7:00(Australia/West) \n"
    },
    {
      "sessionId": 5285,
      "temporaryId": null,
      "type": "Room",
      "referenceId": 280,
      "label": "Room 1",
      "message": "Room Room 1 is already booked for 30868QLDD-8 at Sun. 13 Aug. 7:00(Australia/West) \n30868QLDD-9 at Sun. 13 Aug. 7:00(Australia/West) \n"
    }
  ];

  this.getCourseClassTutors = classId => [
    {
      "id": classId,
      classId,
      "contactId": classId,
      "roleId": 1,
      "tutorName": "Eliatan Hill",
      "roleName": "General Tutor",
      "confirmedOn": "",
      "isInPublicity": true
    }
  ];

  this.getCourseClassAssessment = classId => [
    {
      "id": 1,
      "assessmentId": classId,
      "courseClassId": classId,
      "assessmentCode": `code ${classId}`,
      "assessmentName": `name ${classId}`,
      "gradingTypeId": 1,
      "contactIds": [
        classId
      ],
      "moduleIds": [],
      "releaseDate": null,
      "dueDate": "2021-06-10T07:04:19.000Z",
      "submissions": []
    }
  ];

  this.getCourseClassAttendanceStudents = () => [
    {
      "id": 19450,
      "sessionId": 3643,
      "contactId": 2979,
      "contactName": "Artyom Tutor",
      "attendanceType": "Unmarked",
      note: "",
      "attendedFrom": null,
      "attendedUntil": null
    },
    {
      "id": 19452,
      "sessionId": 5226,
      "contactId": 2979,
      "contactName": "Artyom Tutor",
      "attendanceType": "Unmarked",
      note: "",
      "attendedFrom": null,
      "attendedUntil": null
    },
    {
      "id": 19454,
      "sessionId": 5285,
      "contactId": 2979,
      "contactName": "Artyom Tutor",
      "attendanceType": "Unmarked",
      note: "",
      "attendedFrom": null,
      "attendedUntil": null
    },
    {
      "id": 19456,
      "sessionId": 4560,
      "contactId": 2979,
      "contactName": "Artyom Tutor",
      "attendanceType": "Unmarked",
      note: "",
      "attendedFrom": null,
      "attendedUntil": null
    },
    {
      "id": 19458,
      "sessionId": 3472,
      "contactId": 2979,
      "contactName": "Artyom Tutor",
      "attendanceType": "Unmarked",
      note: "",
      "attendedFrom": null,
      "attendedUntil": null
    },
    {
      "id": 19460,
      "sessionId": 4358,
      "contactId": 2979,
      "contactName": "Artyom Tutor",
      "attendanceType": "Unmarked",
      note: "",
      "attendedFrom": null,
      "attendedUntil": null
    },
    {
      "id": 19462,
      "sessionId": 4442,
      "contactId": 2979,
      "contactName": "Artyom Tutor",
      "attendanceType": "Unmarked",
      note: "",
      "attendedFrom": null,
      "attendedUntil": null
    },
    {
      "id": 19464,
      "sessionId": 4140,
      "contactId": 2979,
      "contactName": "Artyom Tutor",
      "attendanceType": "Unmarked",
      note: "",
      "attendedFrom": null,
      "attendedUntil": null
    },
    {
      "id": 19466,
      "sessionId": 3891,
      "contactId": 2979,
      "contactName": "Artyom Tutor",
      "attendanceType": "Unmarked",
      note: "",
      "attendedFrom": null,
      "attendedUntil": null
    },
    {
      "id": 19468,
      "sessionId": 4926,
      "contactId": 2979,
      "contactName": "Artyom Tutor",
      "attendanceType": "Unmarked",
      note: "",
      "attendedFrom": null,
      "attendedUntil": null
    },
    {
      "id": 19470,
      "sessionId": 4025,
      "contactId": 2979,
      "contactName": "Artyom Tutor",
      "attendanceType": "Unmarked",
      note: "",
      "attendedFrom": null,
      "attendedUntil": null
    },
    {
      "id": 19472,
      "sessionId": 3776,
      "contactId": 2979,
      "contactName": "Artyom Tutor",
      "attendanceType": "Unmarked",
      note: "",
      "attendedFrom": null,
      "attendedUntil": null
    },
    {
      "id": 19474,
      "sessionId": 4673,
      "contactId": 2979,
      "contactName": "Artyom Tutor",
      "attendanceType": "Unmarked",
      note: "",
      "attendedFrom": null,
      "attendedUntil": null
    },
    {
      "id": 19476,
      "sessionId": 3531,
      "contactId": 2979,
      "contactName": "Artyom Tutor",
      "attendanceType": "Unmarked",
      note: "",
      "attendedFrom": null,
      "attendedUntil": null
    },
    {
      "id": 19478,
      "sessionId": 4796,
      "contactId": 2979,
      "contactName": "Artyom Tutor",
      "attendanceType": "Unmarked",
      note: "",
      "attendedFrom": null,
      "attendedUntil": null
    },
    {
      "id": 19480,
      "sessionId": 3602,
      "contactId": 2979,
      "contactName": "Artyom Tutor",
      "attendanceType": "Unmarked",
      note: "",
      "attendedFrom": null,
      "attendedUntil": null
    },
    {
      "id": 19482,
      "sessionId": 4556,
      "contactId": 2979,
      "contactName": "Artyom Tutor",
      "attendanceType": "Unmarked",
      note: "",
      "attendedFrom": null,
      "attendedUntil": null
    },
    {
      "id": 19484,
      "sessionId": 3402,
      "contactId": 2979,
      "contactName": "Artyom Tutor",
      "attendanceType": "Unmarked",
      note: "",
      "attendedFrom": null,
      "attendedUntil": null
    },
    {
      "id": 19486,
      "sessionId": 5037,
      "contactId": 2979,
      "contactName": "Artyom Tutor",
      "attendanceType": "Unmarked",
      note: "",
      "attendedFrom": null,
      "attendedUntil": null
    },
    {
      "id": 19488,
      "sessionId": 4249,
      "contactId": 2979,
      "contactName": "Artyom Tutor",
      "attendanceType": "Unmarked",
      note: "",
      "attendedFrom": null,
      "attendedUntil": null
    },
    {
      "id": 19490,
      "sessionId": 4362,
      "contactId": 2979,
      "contactName": "Artyom Tutor",
      "attendanceType": "Unmarked",
      note: "",
      "attendedFrom": null,
      "attendedUntil": null
    },
    {
      "id": 19492,
      "sessionId": 4139,
      "contactId": 2979,
      "contactName": "Artyom Tutor",
      "attendanceType": "Unmarked",
      note: "",
      "attendedFrom": null,
      "attendedUntil": null
    },
    {
      "id": 19494,
      "sessionId": 3825,
      "contactId": 2979,
      "contactName": "Artyom Tutor",
      "attendanceType": "Unmarked",
      note: "",
      "attendedFrom": null,
      "attendedUntil": null
    },
    {
      "id": 19496,
      "sessionId": 4989,
      "contactId": 2979,
      "contactName": "Artyom Tutor",
      "attendanceType": "Unmarked",
      note: "",
      "attendedFrom": null,
      "attendedUntil": null
    },
    {
      "id": 19498,
      "sessionId": 3957,
      "contactId": 2979,
      "contactName": "Artyom Tutor",
      "attendanceType": "Unmarked",
      note: "",
      "attendedFrom": null,
      "attendedUntil": null
    },
    {
      "id": 19500,
      "sessionId": 4925,
      "contactId": 2979,
      "contactName": "Artyom Tutor",
      "attendanceType": "Unmarked",
      note: "",
      "attendedFrom": null,
      "attendedUntil": null
    },
    {
      "id": 19502,
      "sessionId": 3710,
      "contactId": 2979,
      "contactName": "Artyom Tutor",
      "attendanceType": "Unmarked",
      note: "",
      "attendedFrom": null,
      "attendedUntil": null
    },
    {
      "id": 19504,
      "sessionId": 5161,
      "contactId": 2979,
      "contactName": "Artyom Tutor",
      "attendanceType": "Unmarked",
      note: "",
      "attendedFrom": null,
      "attendedUntil": null
    },
    {
      "id": 19506,
      "sessionId": 4671,
      "contactId": 2979,
      "contactName": "Artyom Tutor",
      "attendanceType": "Attended",
      note: "",
      "attendedFrom": null,
      "attendedUntil": null
    },
    {
      "id": 19508,
      "sessionId": 3473,
      "contactId": 2979,
      "contactName": "Artyom Tutor",
      "attendanceType": "Unmarked",
      note: "",
      "attendedFrom": null,
      "attendedUntil": null
    },
    {
      "id": 19510,
      "sessionId": 3587,
      "contactId": 2979,
      "contactName": "Artyom Tutor",
      "attendanceType": "Unmarked",
      note: "",
      "attendedFrom": null,
      "attendedUntil": null
    },
    {
      "id": 19512,
      "sessionId": 4835,
      "contactId": 2979,
      "contactName": "Artyom Tutor",
      "attendanceType": "Unmarked",
      note: "",
      "attendedFrom": null,
      "attendedUntil": null
    },
    {
      "id": 19514,
      "sessionId": 4496,
      "contactId": 2979,
      "contactName": "Artyom Tutor",
      "attendanceType": "Unmarked",
      note: "",
      "attendedFrom": null,
      "attendedUntil": null
    },
    {
      "id": 19516,
      "sessionId": 3326,
      "contactId": 2979,
      "contactName": "Artyom Tutor",
      "attendanceType": "Unmarked",
      note: "",
      "attendedFrom": null,
      "attendedUntil": null
    },
    {
      "id": 19518,
      "sessionId": 5119,
      "contactId": 2979,
      "contactName": "Artyom Tutor",
      "attendanceType": "Unmarked",
      note: "",
      "attendedFrom": null,
      "attendedUntil": null
    },
    {
      "id": 19520,
      "sessionId": 4195,
      "contactId": 2979,
      "contactName": "Artyom Tutor",
      "attendanceType": "Unmarked",
      note: "",
      "attendedFrom": null,
      "attendedUntil": null
    },
    {
      "id": 19522,
      "sessionId": 5227,
      "contactId": 2979,
      "contactName": "Artyom Tutor",
      "attendanceType": "Unmarked",
      note: "",
      "attendedFrom": null,
      "attendedUntil": null
    },
    {
      "id": 19524,
      "sessionId": 4095,
      "contactId": 2979,
      "contactName": "Artyom Tutor",
      "attendanceType": "Unmarked",
      note: "",
      "attendedFrom": null,
      "attendedUntil": null
    },
    {
      "id": 19526,
      "sessionId": 3779,
      "contactId": 2979,
      "contactName": "Artyom Tutor",
      "attendanceType": "Unmarked",
      note: "",
      "attendedFrom": null,
      "attendedUntil": null
    },
    {
      "id": 19528,
      "sessionId": 4734,
      "contactId": 2979,
      "contactName": "Artyom Tutor",
      "attendanceType": "Unmarked",
      note: "",
      "attendedFrom": null,
      "attendedUntil": null
    },
    {
      "id": 61426,
      "sessionId": 25336,
      "contactId": 2979,
      "contactName": "Artyom Tutor",
      "attendanceType": "Unmarked",
      note: "",
      "attendedFrom": null,
      "attendedUntil": null
    }
  ];

  this.getCourseClassTrainingPlan = () => [];

  this.cancelCourseClassPayload = () => ({
    classIds: ["2"],
    refundManualInvoices: true,
    sendEmail: true,
  });

  const rows: CourseClass[] = generateArraysOfRecords(20, [
    { name: "id", type: "number" },
    { name: "courseName", type: "string" },
    { name: "code", type: "string" },
    { name: "startDateTime", type: "Datetime" },
    { name: "endDateTime", type: "Datetime" },
    { name: "sessionsCount", type: "number" },
    { name: "feeIncGst", type: "string" },
    { name: "tutorsAbridged", type: "string" },
    { name: "roomId", type: "number" },
    { name: "validEnrolmentCount", type: "number" },
    { name: "placesLeft", type: "number" },
    { name: "createdOn", type: "Datetime" }
  ]).map(l => ({
    id: l.id,
    values: [
      l.courseName,
      l.code,
      l.startDateTime,
      l.endDateTime,
      20,
      300,
      "not set",
      280,
      10,
      20,
      l.createdOn,
    ]
  }));

  return getEntityResponse({
    entity: "CourseClass",
    rows,
    columns: [
      {
        title: "Course",
        attribute: "course.name",
        type: null,
        sortable: true,
        visible: true,
        width: 200,
        sortFields: []
      },
      {
        title: "Code",
        attribute: "uniqueCode",
        type: null,
        sortable: true,
        visible: true,
        width: 200,
        sortFields: []
      },
      {
        title: "Starts",
        attribute: "startDateTime",
        type: "Datetime",
        sortable: true,
        visible: true,
        width: 200,
        sortFields: []
      },
      {
        title: "Ends",
        attribute: "endDateTime",
        type: "Datetime",
        sortable: true,
        visible: true,
        width: 200,
        sortFields: []
      },
      {
        title: "Sessions",
        attribute: "sessionsCount",
        type: null,
        sortable: true,
        visible: true,
        width: 200,
        sortFields: []
      },
      {
        title: "Fee",
        attribute: "feeIncGst",
        type: null,
        sortable: false,
        visible: true,
        width: 200,
        sortFields: []
      },
      {
        title: "Tutor",
        attribute: "tutorsAbridged",
        type: null,
        sortable: false,
        visible: true,
        width: 200,
        sortFields: []
      },
      {
        title: "Site name",
        attribute: "room.site",
        type: null,
        sortable: true,
        visible: true,
        width: 200,
        sortFields: []
      },
      {
        title: "Enrolments",
        attribute: "validEnrolmentCount",
        type: null,
        sortable: false,
        visible: true,
        width: 200,
        sortFields: []
      },
      {
        title: "Vacancies",
        attribute: "placesLeft",
        type: null,
        sortable: false,
        visible: true,
        width: 200,
        sortFields: []
      },
      {
        title: "Created",
        attribute: "createdOn",
        type: "Datetime",
        sortable: true,
        visible: true,
        width: 200,
        sortFields: []
      },
    ],
    res: {
      sort: [
        {
          attribute: "startDateTime",
          ascending: false,
          complexAttribute: []
        }
      ]
    }
  });
}
