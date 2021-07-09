import { CourseClass } from "@api/model";
import { generateArraysOfRecords, getEntityResponse } from "../../mockUtils";

export function mockCourseClasses() {
  this.getCourseClass = id => {
    const row = this.courseClasses.rows.find(courseClass => courseClass.id == id);
    const value = row.values;
    return {
      id: row.id,
      courseName: value[0],
      code: value[1],
      startDateTime: value[2],
      endDateTime: value[3],
      sessionsCount: value[4],
      feeIncGst: value[5],
      tutorsAbridged: value[6],
      roomSite: value[7],
      validEnrolmentCount: value[8],
      placesLeft: value[9],
      createdOn: value[10],
      attendanceType: "No information",
      censusDate: "",
      deliveryMode: "Classroom",
      classFundingSource: "Domestic full fee",
      webDescription: "Lorem ipsum",
      accountId: 10,
      reportableHours: 20,
      suppressAvetmissExport: false,
      vetCourseSiteId: "10",
      vetFundingContract: "Skills Tasmania",
      vetFundingSourceNationalId: "",
      vetFundingSourceStateId: "",
      vetPurchasingContractId: "",
      vetPurchasingContractScheduleId: "",
      qualificationHours: 0,
      nominalHours: 15,
      classroomHours: 15,
      studentContactHours: 0,
      courseCode: value[1],
      notes: [],
      tags: [],
      tutors: [
        {
          name: "Hill, Eliatan",
          confirmedOn: "",
          role: "Course Manager",
          inPublicity: true
        }
      ],
      documents: []
    };
  };

  this.getCourseClasses = () => this.courseClasses;

  this.getPlainCourseClassList = params => {
    let rows: any[];
    const columns = params.columns;

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
    } else if (columns.includes("createdOn,startDateTime,maximumPlaces,uniqueCode")) {
      rows = generateArraysOfRecords(20, [
        { name: "id", type: "number" },
        { name: "createdOn", type: "Datetime" },
        { name: "startDateTime", type: "Datetime" },
        { name: "maximumPlaces", type: "number" },
        { name: "uniqueCode", type: "string" },
      ]).map(l => ({
        id: l.id,
        values: [
          l.createdOn,
          l.startDateTime,
          l.maximumPlaces,
          l.uniqueCode,
        ]
      }));
    } else {
      rows = generateArraysOfRecords(20, [
        { name: "id", type: "number" },
        { name: "courseName", type: "string" },
        { name: "code", type: "string" },
        { name: "price", type: "number" },
        { name: "createdOn", type: "Datetime" },
      ]).map(l => ({
        id: l.id,
        values: [l.courseName, l.code, l.code, l.price, l.createdOn]
      }));
    }

    return getEntityResponse({
      entity: "CourseClass",
      rows,
      plain: true
    });
  };
  
  this.getCourseClassBudget = () => [
      {
        "id": 6455,
        "courseClassid": 2104,
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
        "courseClassTutorId": 1426,
        "tutorRole": "General Tutor"
      },
      {
        "id": 7017,
        "courseClassid": 2104,
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
            "availableOnWeb": null,
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
        "courseClassid": 2104,
        "taxId": 1,
        "accountId": 15,
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
        "courseClassid": 2104,
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
            "availableOnWeb": null,
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
        "courseClassid": 2104,
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
            "availableOnWeb": null,
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
        "courseClassid": 2104,
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
            "availableOnWeb": null,
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
        "courseClassid": 2104,
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
            "availableOnWeb": null,
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

  const rows: CourseClass[] = generateArraysOfRecords(20, [
    { name: "id", type: "number" },
    { name: "courseName", type: "string" },
    { name: "code", type: "string" },
    { name: "startDateTime", type: "Datetime" },
    { name: "endDateTime", type: "Datetime" },
    { name: "sessionsCount", type: "number" },
    { name: "feeIncGst", type: "string" },
    { name: "tutorsAbridged", type: "string" },
    { name: "roomSite", type: "string" },
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
      10,
      300,
      "not set",
      "ish.oncourse.server.entity.mixins.SiteMixin@e41ab61",
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
