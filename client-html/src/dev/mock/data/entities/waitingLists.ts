import { generateArraysOfRecords, getEntityResponse } from "../../mockUtils";

export function mockWaitingLists() {
  this.getWaitingLists = () => this.waitingLists;

  this.getWaitingList = id => {
    const row = this.waitingLists.rows.find(row => row.id == id);
    return {
      tags: this.getTags(),
      studentCount: 10,
      contactId: 1,
      createdOn: row.values[0],
      studentName: row.values[1],
      courseName: row.values[2],
      courseId: row.values[3],
      studentNotes: row.values[4],
      privateNotes: `private notes ${id}`,
      customFields: {
        waitLocation: "Sydney CBD"
      },
      id: row.id,
      sites: [],
      modifiedOn: row.values[0]
    };
  };

  this.createWaitingList = item => {
    const data = JSON.parse(item);
    const waitingLists = this.waitingLists;
    const totalRows = waitingLists.rows;

    const studentName = this.getContact(data.contactId);

    data.id = totalRows.length + 1;

    waitingLists.rows.push({
      id: data.id,
      values: [
        new Date().toISOString(),
        `${studentName.firstName} ${studentName.lastName}`,
        this.getCourse(data.courseId),
        data.courseId,
        data.studentNotes
      ]
    });

    this.waitingLists = waitingLists;
  };

  const rows = generateArraysOfRecords(20, [
    { name: "id", type: "number" },
    { name: "createdOn", type: "Datetime" },
    { name: "studentName", type: "string" },
    { name: "courseName", type: "string" },
    { name: "courseId", type: "number" },
    { name: "studentNotes", type: "string" }
  ]).map(l => ({
    id: l.id,
    values: [l.createdOn, l.studentName, l.courseName, l.courseId, l.studentNotes]
  }));

  return getEntityResponse({
    entity: "WaitingList",
    rows,
    columns: [
      {
        title: "Created",
        attribute: "createdOn",
        sortable: true,
        type: "Datetime"
      },
      {
        title: "Student",
        attribute: "student.contact.fullName",
        sortable: true,
        width: 300,
        sortFields: ["student.contact.lastName", "student.contact.firstName", "student.contact.middleName"]
      },
      {
        title: "Course name",
        attribute: "course.name",
        sortable: true
      },
      {
        title: "Course code",
        attribute: "course.code",
        sortable: true,
        width: 100
      },
      {
        title: "Student requirements",
        attribute: "studentNotes",
        sortable: true
      }
    ],
    res: {
      sort: [{ attribute: "createdOn", ascending: false, complexAttribute: [] }]
    }
  });
}
