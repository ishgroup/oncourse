import { generateArraysOfRecords } from "../../mockUtils";
export function mockTraineeships() {
    this.getTraineeships = () => {
        return this.traineeships;
    };
    const rows = generateArraysOfRecords(20, [
        { name: "id", type: "number" },
        { name: "courseName", type: "string" },
        { name: "courseCode", type: "string" },
        { name: "start", type: "Datetime" },
        { name: "end", type: "Datetime" },
        { name: "fee", type: "number" },
        { name: "tutor", type: "string" },
        { name: "studentId", type: "number" }
    ]).map(l => ({
        id: l.id,
        values: [l.courseName, l.courseCode, l.start, l.end, l.fee, l.tutor, l.studentId]
    }));
    const columns = [
        {
            title: "Course",
            attribute: "courseName",
            sortable: true,
            visible: true,
            width: 200,
            type: null,
            sortFields: []
        },
        {
            title: "Code",
            attribute: "courseCode",
            sortable: true,
            visible: true,
            width: 100,
            type: null,
            sortFields: []
        },
        {
            title: "Starts",
            attribute: "start",
            sortable: true,
            visible: true,
            width: 100,
            type: null,
            sortFields: []
        },
        {
            title: "Ends",
            attribute: "end",
            sortable: true,
            visible: true,
            width: 100,
            type: null,
            sortFields: []
        },
        {
            title: "Fee",
            attribute: "fee",
            sortable: true,
            visible: true,
            width: 100,
            type: null,
            sortFields: []
        },
        {
            title: "Tutor",
            attribute: "tutor",
            sortable: true,
            visible: true,
            width: 100,
            type: null,
            sortFields: []
        },
        {
            title: "Student",
            attribute: "studentId",
            sortable: true,
            visible: true,
            width: 100,
            type: null,
            sortFields: []
        }
    ];
    const response = { rows, columns };
    response.entity = "Traineeship";
    response.offset = 0;
    response.filterColumnWidth = 200;
    response.layout = "Three column";
    response.pageSize = 20;
    response.search = null;
    response.count = rows.length;
    response.sort = [];
    return response;
}
//# sourceMappingURL=traineeships.js.map