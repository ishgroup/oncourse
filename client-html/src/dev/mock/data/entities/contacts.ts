import { generateArraysOfRecords } from "../../mockUtils";

export function mockContacts() {
  this.getContacts = () => this.contacts;

  this.getContact = id => {
    const row = this.contacts.rows.find(row => row.id == id);
    return {
      id: row.id,
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
    this.contacts.rows = this.contacts.rows.filter(a => a.id !== id);
  };

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

    const columns = [];

    const response = { rows, columns } as any;

    response.entity = "Contact";
    response.offset = 0;
    response.filterColumnWidth = null;
    response.layout = null;
    response.pageSize = rows.length;
    response.search = null;
    response.count = null;
    response.sort = [];

    return response;
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

    const columns = [];

    const response = { rows, columns } as any;

    response.entity = "PriorLearning";
    response.offset = 0;
    response.filterColumnWidth = null;
    response.layout = null;
    response.pageSize = rows.length;
    response.search = null;
    response.count = null;
    response.sort = [];

    return response;
  };

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

  const columns = [
    {
      title: "Name",
      attribute: "fullName",
      sortable: true,
      visible: true,
      width: 200,
      type: null,
      sortFields: []
    },
    {
      title: "Birthdate",
      attribute: "birthDate",
      sortable: true,
      visible: true,
      width: 100,
      type: "Datetime",
      sortFields: []
    },
    {
      title: "Street",
      attribute: "street",
      sortable: true,
      visible: true,
      width: 100,
      type: null,
      sortFields: []
    },
    {
      title: "Suburb",
      attribute: "suburb",
      sortable: true,
      visible: true,
      width: 100,
      type: null,
      sortFields: []
    },
    {
      title: "Student #",
      attribute: "studentId",
      sortable: true,
      visible: true,
      width: 100,
      type: null,
      sortFields: []
    }
  ];

  const response = { rows, columns } as any;

  response.entity = "Contact";
  response.offset = 0;
  response.filterColumnWidth = 200;
  response.layout = "Three column";
  response.pageSize = 20;
  response.search = null;
  response.count = rows.length;
  response.sort = [];

  return response;
}
