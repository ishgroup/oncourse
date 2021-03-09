import { CourseClass } from "@api/model";
import { generateArraysOfRecords } from "../../mockUtils";

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

  this.getCourseClasses = () => {
    return this.courseClasses;
  };

  this.getPlainCourseClassList = () => {
    const rows = generateArraysOfRecords(20, [
      { name: "id", type: "number" },
      { name: "courseName", type: "string" },
      { name: "code", type: "string" },
      { name: "price", type: "number" }
    ]).map(l => ({
      id: l.id,
      values: [l.courseName, l.code, l.code, l.price]
    }));

    const columns = [];

    const response = { rows, columns } as any;

    response.entity = "CourseClass";
    response.offset = 0;
    response.filterColumnWidth = null;
    response.layout = null;
    response.pageSize = rows.length;
    response.search = null;
    response.count = null;
    response.sort = [];

    return response;
  };

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
    { name: "placesLeft", type: "number" }
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
      20
    ]
  }));

  const columns = [
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
      attribute: "code",
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
    }
  ];

  const response = { rows, columns } as any;

  response.entity = "CourseClass";
  response.offset = 0;
  response.filterColumnWidth = 200;
  response.layout = "Three column";
  response.pageSize = 10;
  response.search = null;
  response.count = rows.length;
  response.sort = [
    {
      attribute: "startDateTime",
      ascending: false,
      complexAttribute: []
    }
  ];

  return response;
}
