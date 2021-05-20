import { generateArraysOfRecords, getEntityResponse, removeItemByEntity } from "../../mockUtils";

export function mockCourses() {
  this.getCourses = () => this.courses;

  this.getCourse = id => {
    const row = this.courses.rows.find(row => row.id == id);

    return {
      id: row.id,
      name: row.values[0],
      code: row.values[1],
      tags: [],
      enrolmentType: "Open enrolment",
      allowWaitingLists: true,
      dataCollectionRuleId: 102,
      dataCollectionRuleName: "Accredited course",
      status: "Enabled and visible online",
      brochureDescription: "Accounting bro description",
      classes: [
        {
          id: 500,
          code: "ACT1",
          selfPaced: false
        },
        {
          id: 501,
          code: "ACT2",
          selfPaced: false
        },
        {
          id: 522,
          code: "ACT3",
          selfPaced: false
        },
        {
          id: 540,
          code: "4",
          selfPaced: false
        },
        {
          id: 541,
          code: "ACT5",
          selfPaced: false
        },
        {
          id: 601,
          code: "ACT7",
          selfPaced: false
        },
        {
          id: 640,
          code: "act8",
          selfPaced: false
        },
        {
          id: 1061,
          code: "act9",
          selfPaced: false
        },
        {
          id: 1461,
          code: "act10",
          selfPaced: false
        },
        {
          id: 1546,
          code: "act12",
          selfPaced: false
        },
        {
          id: 1547,
          code: "act11",
          selfPaced: false
        }
      ],
      webDescription: "Lorem ipsum",
      documents: null,
      relatedProducts: [
        {
          id: 381,
          name: "Certificate IV Training and Assessment TRAS",
          type: "Course"
        },
        {
          id: 212,
          name: "Email Filtering and Security SPAM",
          type: "Course"
        },
        {
          id: 210,
          name: "Tutor Record Management for AQTF TUTR",
          type: "Course"
        }
      ],
      notes: [
        {
          id: 211,
          created: "2015-03-10T05:24:13.000Z",
          modified: "2015-03-10T05:24:13.000Z",
          message: "this is a notes of accounting",
          createdBy: "system",
          modifiedBy: null
        },
        {
          id: 239,
          created: "2015-03-10T07:10:47.000Z",
          modified: "2015-03-10T07:10:47.000Z",
          message: "A note by natalie",
          createdBy: "onCourse Administrator",
          modifiedBy: null
        },
        {
          id: 240,
          created: "2015-03-10T07:11:25.000Z",
          modified: "2015-03-10T07:11:25.000Z",
          message: "Another new note",
          createdBy: "onCourse Administrator",
          modifiedBy: null
        },
        {
          id: 241,
          created: "2015-03-10T07:11:55.000Z",
          modified: "2015-03-10T07:12:24.000Z",
          message: "Some more notes. Will the field sizes collapase?\nIf I edit the note, will the label change?",
          createdBy: "onCourse Administrator",
          modifiedBy: "onCourse Administrator"
        }
      ],
      qualificationId: null,
      qualNationalCode: null,
      qualTitle: null,
      qualLevel: null,
      isSufficientForQualification: true,
      isVET: true,
      fieldOfEducation: "1299",
      reportableHours: null,
      modules: [],
      customFields: {},
      rules: []
    };
  };

  this.addCourse = item => {
    const data = JSON.parse(item);
    const courses = this.courses;
    const totalRows = courses.rows;

    data.id = totalRows.length + 1;

    courses.rows.push({
      id: data.id,
      values: [data.name, data.code, null, null, 0]
    });

    this.courses = courses;
  };

  this.createNewCourse = (id = 21) => ({
    id,
    name: `name ${id}`,
    code: `code ${id}`,
    tags: [],
    enrolmentType: "Open enrolment",
    allowWaitingLists: true,
    dataCollectionRuleId: 102,
    dataCollectionRuleName: "Accredited course",
    status: "Enabled and visible online",
    brochureDescription: "Accounting bro description",
    classes: [
      {
        id: 500,
        code: "ACT1",
        selfPaced: false
      },
      {
        id: 501,
        code: "ACT2",
        selfPaced: false
      },
      {
        id: 522,
        code: "ACT3",
        selfPaced: false
      },
      {
        id: 540,
        code: "4",
        selfPaced: false
      },
      {
        id: 541,
        code: "ACT5",
        selfPaced: false
      },
      {
        id: 601,
        code: "ACT7",
        selfPaced: false
      },
      {
        id: 640,
        code: "act8",
        selfPaced: false
      },
      {
        id: 1061,
        code: "act9",
        selfPaced: false
      },
      {
        id: 1461,
        code: "act10",
        selfPaced: false
      },
      {
        id: 1546,
        code: "act12",
        selfPaced: false
      },
      {
        id: 1547,
        code: "act11",
        selfPaced: false
      }
    ],
    webDescription: "Lorem ipsum",
    documents: null,
    relatedProducts: [
      {
        id: 381,
        name: "Certificate IV Training and Assessment TRAS",
        type: "Course"
      },
      {
        id: 212,
        name: "Email Filtering and Security SPAM",
        type: "Course"
      },
      {
        id: 210,
        name: "Tutor Record Management for AQTF TUTR",
        type: "Course"
      }
    ],
    notes: [
      {
        id: 211,
        created: "2015-03-10T05:24:13.000Z",
        modified: "2015-03-10T05:24:13.000Z",
        message: "this is a notes of accounting",
        createdBy: "system",
        modifiedBy: null
      },
      {
        id: 239,
        created: "2015-03-10T07:10:47.000Z",
        modified: "2015-03-10T07:10:47.000Z",
        message: "A note by natalie",
        createdBy: "onCourse Administrator",
        modifiedBy: null
      },
      {
        id: 240,
        created: "2015-03-10T07:11:25.000Z",
        modified: "2015-03-10T07:11:25.000Z",
        message: "Another new note",
        createdBy: "onCourse Administrator",
        modifiedBy: null
      },
      {
        id: 241,
        created: "2015-03-10T07:11:55.000Z",
        modified: "2015-03-10T07:12:24.000Z",
        message: "Some more notes. Will the field sizes collapase?\nIf I edit the note, will the label change?",
        createdBy: "onCourse Administrator",
        modifiedBy: "onCourse Administrator"
      }
    ],
    qualificationId: null,
    qualNationalCode: null,
    qualTitle: null,
    qualLevel: null,
    isSufficientForQualification: true,
    isVET: true,
    fieldOfEducation: "1299",
    reportableHours: null,
    modules: [],
    customFields: {},
    rules: []
  });

  this.removeCourse = id => {
    this.courses = removeItemByEntity(this.courses, id);
  };

  this.duplicateCourse = ids => {
    const courses = this.courses;

    ids.forEach(id => {
      const item = { ...courses.rows.find(c => c.id == id) };
      const totalRows = courses.rows;

      item.id = totalRows.length + 1;

      courses.rows.push({
        id: item.id,
        values: [item.name, item.code, null, null, 0]
      });
    });

    this.courses = courses;
  };

  const rows = generateArraysOfRecords(20, [
    { name: "id", type: "number" },
    { name: "name", type: "string" },
    { name: "code", type: "string" },
    { name: "fieldOfEducation", type: "string" },
    { name: "qualNationalCode", type: "string" }
  ]).map(l => ({
    id: l.id,
    values: [l.name, l.code, null, null, 0]
  }));

  return getEntityResponse({
    entity: "Course",
    rows,
    columns: [
      {
        title: "Name",
        attribute: "name",
        sortable: true,
        width: 400
      },
      {
        title: "Code",
        attribute: "code",
        sortable: true
      },
      {
        title: "Field",
        attribute: "fieldOfEducation",
        sortable: true
      },
      {
        title: "Qualification",
        attribute: "qualification.nationalCode",
        sortable: true
      },
      {
        title: "Current classes",
        attribute: "current_classes_count",
        sortable: true
      }
    ],
    res: {
      sort: [
        {
          attribute: "name",
          ascending: true,
          complexAttribute: []
        }
      ]
    }
  });
}
