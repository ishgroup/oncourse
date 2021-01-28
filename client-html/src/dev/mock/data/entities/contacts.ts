import { generateArraysOfRecords, getEntityResponse, removeItemByEntity } from "../../mockUtils";

export function mockContacts() {
  this.getContacts = () => this.contacts;

  this.getContact = id => {
    const row = this.contacts.rows.find(row => row.id == id);
    return {
      "id": row.id,
      "student": {
        "id": 1,
        "countryOfBirth": null,
        "disabilityType": "Not stated",
        "labourForceStatus": "Not stated",
        "englishProficiency": "Not stated",
        "highestSchoolLevel": "Not stated",
        "indigenousStatus": "Not stated",
        "isOverseasClient": false,
        "isStillAtSchool": false,
        "language": null,
        "priorEducationCode": "Not stated",
        "specialNeeds": null,
        "yearSchoolCompleted": null,
        "studentNumber": 1,
        "countryOfResidency": null,
        "visaNumber": null,
        "visaType": null,
        "visaExpiryDate": null,
        "passportNumber": null,
        "medicalInsurance": null,
        "uniqueLearnerIdentifier": null,
        "usi": null,
        "usiStatus": "Not supplied",
        "chessn": null,
        "feeHelpEligible": false,
        "citizenship": "No information",
        "townOfBirth": null,
        "specialNeedsAssistance": null,
        "clientIndustryEmployment": "Not Stated",
        "clientOccupationIdentifier": "Not Stated",
        "waitingLists": [

        ],
        "concessions": [

        ]
      },
      "tutor": {
        "id": row.id,
        "dateFinished": null,
        "dateStarted": null,
        "familyNameLegal": null,
        "givenNameLegal": null,
        "payrollRef": "ABC123",
        "resume": "Rex is ishâ€™s frontline support person, and he loves the challenge of solving clientsâ€™ problems of all sizes: from holding the hands of those with minimal technical experience to solving the most complex technical problems.Rex completed his Bachelor of Computer Engineering at the University of New South Wales, and has Systems Administration experience with Macs, Windows and Linux machines aplenty. His versatility and wide skill base, along with his good understanding of software design and programming make Rex an invaluable part of our team.",
        "wwChildrenCheckedOn": null,
        "wwChildrenExpiry": null,
        "wwChildrenRef": null,
        "wwChildrenStatus": "Not checked",
        "currentClassesCount": 0,
        "futureClasseCount": 1,
        "selfPacedclassesCount": 0,
        "unscheduledClasseCount": 0,
        "passedClasseCount": 71,
        "cancelledClassesCount": 2,
        "defaultPayType": null
      },
      "abn": null,
      "birthDate": null,
      "country": null,
      "fax": null,
      "isCompany": false,
      "gender": null,
      "message": null,
      "homePhone": null,
      "mobilePhone": null,
      "workPhone": null,
      "postcode": null,
      "state": null,
      "street": null,
      "suburb": null,
      "tfn": null,
      "deliveryStatusEmail": 0,
      "deliveryStatusSms": 0,
      "deliveryStatusPost": 0,
      "allowPost": true,
      "allowSms": true,
      "allowEmail": true,
      "uniqueCode": "m7jRCfA9QvurnJ6G",
      "honorific": null,
      "title": null,
      "email": "test@test.com",
      "firstName": "FirstName",
      "lastName": "LastName",
      "middleName": null,
      "invoiceTerms": null,
      "taxId": null,
      "customFields": {

      },
      "documents": [

      ],
      tags: [this.getTag(1)],
      "memberships": [

      ],
      "profilePicture": null,
      "relations": [

      ],
      "financialData": [
        {
          "relatedEntityId": 4476,
          "type": "Invoice",
          "description": "Invoice (office)",
          "date": "2018-04-17",
          "createdOn": "2018-04-17T12:18:05.000Z",
          "referenceNumber": "1451",
          "status": "Success",
          "owing": 0.00,
          "amount": 0.00,
          "balance": 0.00
        }
      ],
      "createdOn": null,
      "modifiedOn": null,
      "messages": [
        {
          "messageId": 2336,
          "createdOn": "2017-02-14T07:09:28.000Z",
          "sentOn": "2017-02-14T07:09:47.000Z",
          "subject": "Template message",
          "creatorKey": null,
          "status": "Sent",
          "type": "Email"
        },
        {
          "messageId": 3411,
          "createdOn": "2018-04-17T12:18:05.000Z",
          "sentOn": "2018-04-17T12:18:45.000Z",
          "subject": "Happy birthday",
          "creatorKey": null,
          "status": "Sent",
          "type": "Email"
        },
        {
          "messageId": 6742,
          "createdOn": "2018-08-21T05:36:51.000Z",
          "sentOn": "2018-08-21T05:37:50.000Z",
          "subject": "Voucher is about to Expire",
          "creatorKey": null,
          "status": "Sent",
          "type": "Email"
        }
      ],
      "rules": [

      ],
      "removeCChistory": null
    };
  };

  this.createContact = item => {
    const data = JSON.parse(item);
    const contacts = this.contacts;
    const totalRows = contacts.rows;

    data.id = totalRows.length + 1;

    contacts.rows.push({
      id: data.id,
      values: [data.name, data.birthDate, data.street, data.suburb]
    });

    this.contacts = contacts;
  };

  this.createNewContact = (id = 21) => ({
    id,
    firstName: "FirstName",
    lastName: "LastName",
    middleName: null,
    email: "test@test.com",
    title: null,
    deliveryStatusEmail: 0,
    deliveryStatusSms: 0,
    deliveryStatusPost: 0,
    allowPost: true,
    allowSms: true,
    allowEmail: true,
    uniqueCode: "LhRKB0bcvVBAGW14",
    abn: null,
    notes: [
      {
        id: 1,
        created: "2019-11-15T12:27:19.000Z",
        modified: "2019-11-15T12:27:19.000Z",
        message: "Merged student 6872 by onCourse Administrator on Fri 15 Nov 2019 5:57pm",
        createdBy: "onCourse Administrator",
        modifiedBy: null,
        entityName: "Contact",
        entityId: 2
      }
    ],
    memberships: [],
    concessions: [],
    profilePicture: null,
    relations: [],
    financialData: [
      {
        relatedEntityId: 1,
        type: "Invoice",
        description: "Invoice (web)",
        date: "2016-10-31",
        referenceNumber: "1104",
        status: "Success",
        owing: 335,
        amount: 335,
        balance: 335
      },
      {
        relatedEntityId: 1,
        type: "Invoice",
        description: "Invoice (office)",
        date: "2018-04-17",
        referenceNumber: "1450",
        status: "Success",
        owing: 0,
        amount: 0,
        balance: 335
      }
    ],
    messages: [
      {
        messageId: 1,
        createdOn: "2017-02-14T07:09:28.000Z",
        sentOn: "2017-02-14T07:09:47.000Z",
        subject: "Template message",
        creatorKey: null,
        status: null,
        type: null
      },
      {
        messageId: 2,
        createdOn: "2018-03-20T07:10:00.000Z",
        sentOn: "2018-03-20T07:10:45.000Z",
        subject: "CCE Class Cancelled Student",
        creatorKey: null,
        status: null,
        type: null
      }
    ],
    taxId: null,
    customFields: {},
    documents: [],
    tags: [this.getTag(1)]
  });

  this.removeContact = id => {
    this.contacts = removeItemByEntity(this.contacts, id);
  };

  this.getVerifyUSI = () => ({
    "errorMessage": "Invalid USI code format.",
    "verifyStatus": "Invalid format"
  });

  this.getContactsPlainList = () => {
    const rows = generateArraysOfRecords(20, [
      { name: "id", type: "number" },
      { name: "firstName", type: "string" },
      { name: "lastName", type: "string" },
      { name: "email", type: "string" },
      { name: "birthDate", type: "string" }
    ]).map(l => ({
      id: l.id,
      values: [l.firstName, l.lastName, null, null, null, null, null, null, null, null]
    }));

    return getEntityResponse({
      entity: "Contact",
      rows,
      plain: true
    });
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

  this.getMergeContacts = () => ({
    "mergeLines": [
      {
        "key": "Contact.birthDate",
        "label": "Birth date",
        "a": null,
        "b": "2001-01-01"
      },
      {
        "key": "Contact.email",
        "label": "Email",
        "a": null,
        "b": "thomas@ish.com.au"
      },
      {
        "key": "Contact.fax",
        "label": "Fax",
        "a": null,
        "b": "0233333333"
      },
      {
        "key": "Contact.firstName",
        "label": "First name",
        "a": "Test",
        "b": "test"
      },
      {
        "key": "Contact.gender",
        "label": "Gender",
        "a": null,
        "b": "Female"
      },
      {
        "key": "Contact.homePhone",
        "label": "Home phone",
        "a": null,
        "b": "0222222222"
      },
      {
        "key": "Contact.lastName",
        "label": "Last name",
        "a": "Student",
        "b": "test"
      },
      {
        "key": "Contact.message",
        "label": "Message",
        "a": "Not suitable to progress to stage 3",
        "b": null
      },
      {
        "key": "Contact.mobilePhone",
        "label": "Mobile phone",
        "a": "0403 112 223",
        "b": "0416230328"
      },
      {
        "key": "Contact.postcode",
        "label": "Postcode",
        "a": null,
        "b": "2222"
      },
      {
        "key": "Contact.street",
        "label": "Street",
        "a": null,
        "b": "a"
      },
      {
        "key": "Contact.suburb",
        "label": "Suburb",
        "a": null,
        "b": "s"
      },
      {
        "key": "Contact.workPhone",
        "label": "Work phone",
        "a": null,
        "b": ""
      },
      {
        "key": "Student.studentNumber",
        "label": "Student number",
        "a": "1",
        "b": "5"
      },
      {
        "key": "Student.citizenship",
        "label": "Citizenship",
        "a": "No information",
        "b": "No information"
      },
      {
        "key": "Student.clientIndustryEmployment",
        "label": "Client industry employment",
        "a": "Not Stated",
        "b": "Not Stated"
      },
      {
        "key": "Student.clientOccupationIdentifier",
        "label": "Client occupation identifier",
        "a": "Not Stated",
        "b": "Not Stated"
      },
      {
        "key": "Student.disabilityType",
        "label": "Disability type",
        "a": "not stated",
        "b": "not stated"
      },
      {
        "key": "Student.englishProficiency",
        "label": "Proficiency in spoken English",
        "a": "not stated",
        "b": "not stated"
      },
      {
        "key": "Student.feeHelpEligible",
        "label": "Fee help eligible",
        "a": "No",
        "b": "No"
      },
      {
        "key": "Student.highestSchoolLevel",
        "label": "Highest school level",
        "a": "not stated",
        "b": "not stated"
      },
      {
        "key": "Student.indigenousStatus",
        "label": "Indigenous status",
        "a": "not stated",
        "b": "not stated"
      },
      {
        "key": "Student.isOverseasClient",
        "label": "Overseas",
        "a": "No",
        "b": "No"
      },
      {
        "key": "Student.isStillAtSchool",
        "label": "Still at school",
        "a": "No",
        "b": "No"
      },
      {
        "key": "Student.labourForceStatus",
        "label": "Employment category",
        "a": "not stated",
        "b": "not stated"
      },
      {
        "key": "Student.priorEducationCode",
        "label": "Prior educational achievement",
        "a": "not stated",
        "b": "not stated"
      },
      {
        "key": "Student.specialNeedsAssistance",
        "label": "Disability support requested",
        "a": "No",
        "b": "No"
      },
      {
        "key": "tags",
        "label": "Tags",
        "a": "",
        "b": ""
      }
    ],
    "infoLines": [
      {
        "label": null,
        "a": "Student",
        "b": "Student"
      },
      {
        "label": "Created",
        "a": "Thu 16 Oct 2008",
        "b": "Thu 13 Nov 2008"
      },
      {
        "label": "Modified",
        "a": "1004 days ago",
        "b": "879 days ago"
      },
      {
        "label": "Last enrolled",
        "a": "Never",
        "b": "Fri 19 Aug 2011"
      },
      {
        "label": "Invoices",
        "a": "1",
        "b": "5"
      },
      {
        "label": "Enrolments",
        "a": "None",
        "b": "2"
      },
      {
        "label": "USI",
        "a": "",
        "b": ""
      }
    ]
  });

  this.submitMergeContacts = () => ({
    "contactA": "42",
    "contactB": "102",
    "data": {
      "Contact.birthDate": "B",
      "Contact.email": "B",
      "Contact.fax": "B",
      "Contact.firstName": "B",
      "Contact.gender": "B",
      "Contact.homePhone": "B",
      "Contact.lastName": "B",
      "Contact.message": "B",
      "Contact.mobilePhone": "B",
      "Contact.postcode": "B",
      "Contact.street": "B",
      "Contact.suburb": "B",
      "Contact.workPhone": "B",
      "Student.studentNumber": "B",
      "Student.citizenship": "B",
      "Student.clientIndustryEmployment": "B",
      "Student.clientOccupationIdentifier": "B",
      "Student.disabilityType": "B",
      "Student.englishProficiency": "B",
      "Student.feeHelpEligible": "B",
      "Student.highestSchoolLevel": "B",
      "Student.indigenousStatus": "B",
      "Student.isOverseasClient": "B",
      "Student.isStillAtSchool": "B",
      "Student.labourForceStatus": "B",
      "Student.priorEducationCode": "B",
      "Student.specialNeedsAssistance": "B",
      "tags": "B"
    }
  });

  this.getContactEmailTemplates = () => [
    {
      "id": 353,
      "type": "Sms",
      "keyCode": "ish.sms.simple",
      "name": "Simple SMS",
      "entity": "Contact",
      "subject": null,
      "plainBody": "${message}",
      "body": null,
      "enabled": true,
      "variables": [
        {
          "name": "message",
          "label": "Message",
          "type": "Text",
          "value": null,
          "system": null,
          "valueDefault": null
        }
      ],
      "options": [

      ],
      "createdOn": "2020-05-16T04:35:44.000Z",
      "modifiedOn": "2021-01-21T05:14:20.000Z",
      "description": "Send unformatted SMS"
    },
    {
      "id": 355,
      "type": "Email",
      "keyCode": "ish.email.simple",
      "name": "Simple Email",
      "entity": "Contact",
      "subject": "${subjectTxt}",
      "plainBody": "${body}",
      "body": "${render(header_keycode)}\n\n<tr>\n    <td align=\"center\" valign=\"top\" width=\"100%\" class=\"bg-color content-padding\">\n        <center>\n            <table cellpadding=\"0\" cellspacing=\"0\" width=\"600\" class=\"w320\">\n                <tr>\n                    <td class=\"mini-block-container\">\n                        <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" class=\"table-border-separate\">\n                            <tr>\n                                <td class=\"mini-block pull-left\" valign=\"top\">\n                                    ${body}\n                                </td>\n                            </tr>\n                        </table>\n                    </td>\n                </tr>\n            </table>\n        </center>\n    </td>\n</tr>\n${render(footer_keycode)}\n",
      "enabled": true,
      "variables": [
        {
          "name": "subjectTxt",
          "label": "Subject",
          "type": "Text",
          "value": null,
          "system": null,
          "valueDefault": null
        },
        {
          "name": "body",
          "label": "Body",
          "type": "Text",
          "value": null,
          "system": null,
          "valueDefault": null
        }
      ],
      "options": [
        {
          "name": "footer_keycode",
          "label": null,
          "type": "Text",
          "value": "ish.email.footer",
          "system": null,
          "valueDefault": null
        },
        {
          "name": "header_keycode",
          "label": null,
          "type": "Text",
          "value": "ish.email.header",
          "system": null,
          "valueDefault": null
        }
      ],
      "createdOn": "2020-05-16T04:35:44.000Z",
      "modifiedOn": "2021-01-21T05:14:20.000Z",
      "description": "Simple email template with minimal styling"
    },
    {
      "id": 360,
      "type": "Email",
      "keyCode": "custom.email.simple",
      "name": "Simple Email New",
      "entity": "Contact",
      "subject": "${subjectTxt}",
      "plainBody": "${body}",
      "body": "${render(header_keycode,bindings)}\n<!--\n  ~ Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au\n  ~ No copying or use of this code is allowed without permission in writing from ish.\n  -->\n\n<tr>\n    <td align=\"center\" valign=\"top\" width=\"100%\" class=\"bg-color content-padding\">\n        <center>\n            <table cellpadding=\"0\" cellspacing=\"0\" width=\"600\" class=\"w320\">\n                <tr>\n                    <td class=\"mini-block-container\">\n                        <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" class=\"table-border-separate\">\n                            <tr>\n                                <td class=\"mini-block pull-left\" valign=\"top\">\n                                    ${body}\n                                    \n                                    <br><br>\n                                    <ol>\n                                      <li>test123</li>\n                                      <li>test125</li>\n                                    </ol>\n                                </td>\n                            </tr>\n                        </table>\n                    </td>\n                </tr>\n            </table>\n        </center>\n    </td>\n</tr>\n${render(footer_keycode,bindings)}",
      "enabled": true,
      "variables": [
        {
          "name": "subjectTxt",
          "label": "Subject",
          "type": "Text",
          "value": null,
          "system": null,
          "valueDefault": null
        },
        {
          "name": "body",
          "label": "Body",
          "type": "Text",
          "value": null,
          "system": null,
          "valueDefault": null
        }
      ],
      "options": [
        {
          "name": "footer_keycode",
          "label": null,
          "type": "Text",
          "value": "ish.email.footer",
          "system": null,
          "valueDefault": null
        },
        {
          "name": "header_keycode",
          "label": null,
          "type": "Text",
          "value": "ish.email.header",
          "system": null,
          "valueDefault": null
        }
      ],
      "createdOn": "2020-05-25T07:04:17.000Z",
      "modifiedOn": "2020-05-25T07:05:54.000Z",
      "description": "Simple email template with minimal styling"
    }
  ];

  const rows = generateArraysOfRecords(20, [
    { name: "id", type: "number" },
    { name: "firstName", type: "string" },
    { name: "lastName", type: "string" },
    { name: "birthDate", type: "Datetime" },
    { name: "street", type: "string" },
    { name: "suburb", type: "string" },
    { name: "studentId", type: "number" }
  ]).map(l => ({
    id: l.id,
    values: [`${l.lastName} ${l.firstName}`, l.birthDate, l.street, l.suburb, l.studentId]
  }));

  return getEntityResponse({
    entity: "Contact",
    rows,
    columns: [
      {
        title: "Name",
        attribute: "fullName",
        sortable: true
      },
      {
        title: "Birthdate",
        attribute: "birthDate",
        sortable: true,
        width: 100,
        type: "Datetime"
      },
      {
        title: "Street",
        attribute: "street",
        sortable: true,
        width: 100
      },
      {
        title: "Suburb",
        attribute: "suburb",
        sortable: true,
        width: 100
      },
      {
        title: "Student #",
        attribute: "studentId",
        sortable: true,
        width: 100
      }
    ]
  });
}
