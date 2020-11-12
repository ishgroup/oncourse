import { generateArraysOfRecords } from "../../mockUtils";
export function mockWaitingLists() {
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
            customFields: {},
            id: row.id,
            sites: []
        };
    };
    this.getWaitingLists = () => {
        return this.waitingLists;
    };
    const rows = generateArraysOfRecords(20, [
        { name: "id", type: "number" },
        { name: "studentName", type: "string" },
        { name: "courseName", type: "string" },
        { name: "courseId", type: "number" },
        { name: "createdOn", type: "Datetime" },
        { name: "studentNotes", type: "string" }
    ]).map(l => ({
        id: l.id,
        values: [l.createdOn, l.studentName, l.courseName, l.courseId, l.studentNotes]
    }));
    const columns = [
        {
            title: "Created",
            attribute: "createdOn",
            sortable: true,
            visible: true,
            width: 200,
            type: "Datetime",
            sortFields: []
        },
        {
            title: "Student",
            attribute: "student.contact.full_name",
            sortable: true,
            visible: true,
            width: 300,
            type: null,
            sortFields: ["student.contact.lastName", "student.contact.firstName", "student.contact.middleName"]
        },
        {
            title: "Course name",
            attribute: "course.name",
            sortable: true,
            visible: true,
            width: 200,
            type: null,
            sortFields: []
        },
        {
            title: "Course code",
            attribute: "course.code",
            sortable: true,
            visible: true,
            width: 100,
            type: null,
            sortFields: []
        },
        {
            title: "Student requirements",
            attribute: "studentNotes",
            sortable: true,
            visible: true,
            width: 200,
            type: null,
            sortFields: []
        }
    ];
    const response = { rows, columns };
    response.entity = "WaitingList";
    response.offset = 0;
    response.filterColumnWidth = 200;
    response.layout = "Three column";
    response.pageSize = 10;
    response.search = "";
    response.count = rows.length;
    response.sort = [
        {
            attribute: "createdOn",
            ascending: false
        }
    ];
    return response;
}
//# sourceMappingURL=waitingLists.js.map