import { getDaysInMonth } from "date-fns";
import { Session } from "@api/model";
import { generateArraysOfRecords } from "../mockUtils";

export function mockTimetable() {
  this.getDates = (year, month): number[] => generateArraysOfRecords(
    getDaysInMonth(new Date(year, month)), [{ name: "day", type: "number" }]
  ).map(l => l.day);

  this.findTimetableSession = (): Session[] => generateArraysOfRecords(20, [
      { name: "classId", type: "number" },
      { name: "code", type: "number" },
      { name: "end", type: "Datetime" },
      { name: "id", type: "number" },
      { name: "name", type: "string" },
      { name: "room", type: "string" },
      { name: "site", type: "string" },
      { name: "start", type: "Datetime" },
      { name: "tutors", type: "Array" }
    ]).map(l => ({
      classId: null,
      code: null,
      end: l.end,
      id: l.id,
      name: null,
      room: null,
      site: null,
      start: l.start,
      tutors: []
    }));

  this.getTimetableSessions = (ids: string): Session[] => generateArraysOfRecords(20, [
      { name: "classId", type: "number" },
      { name: "code", type: "string" },
      { name: "end", type: "Datetime" },
      { name: "id", type: "number" },
      { name: "name", type: "string" },
      { name: "room", type: "string" },
      { name: "site", type: "string" },
      { name: "start", type: "Datetime" },
      { name: "tutors", type: "Array" }
    ]).map(l => ({
      classId: l.classId,
      code: l.code,
      end: l.end,
      id: l.id,
      name: l.name,
      room: l.room,
      site: l.site,
      start: l.start,
      tutors: ["Kim", "James Nash", "Gonzalez"]
    }));
  
  this.getTimetableSessionList = (): Session[] => [
    {
      "id": 9500,
      "temporaryId": null,
      "name": "Certificate III in Early Childhood Education and Care (no units)",
      "code": null,
      "room": "Room 1",
      "site": "Perth office",
      "tutors": [

      ],
      "tutorAttendances": [

      ],
      "classId": null,
      "courseId": 780,
      "roomId": 280,
      "siteId": 260,
      "siteTimezone": "Australia/West",
      "start": "2023-05-02T03:30:00.000Z",
      "end": "2023-05-02T09:30:00.000Z",
      "publicNotes": null,
      "privateNotes": null,
      "hasPaylines": false
    },
    {
      "id": 9499,
      "temporaryId": null,
      "name": "Certificate III in Early Childhood Education and Care (no units)",
      "code": null,
      "room": "Electronic & Hardcopy",
      "site": "Electronic & Hardcopy",
      "tutors": [

      ],
      "tutorAttendances": [

      ],
      "classId": null,
      "courseId": 780,
      "roomId": 468,
      "siteId": 446,
      "siteTimezone": "Australia/Brisbane",
      "start": "2023-05-02T05:30:00.000Z",
      "end": "2023-05-02T11:30:00.000Z",
      "publicNotes": null,
      "privateNotes": null,
      "hasPaylines": false
    },
    {
      "id": 3596,
      "temporaryId": null,
      "name": "Certificate III in Early Childhood Education and Care (no units)",
      "code": null,
      "room": "Room 1",
      "site": "Perth office",
      "tutors": [

      ],
      "tutorAttendances": [

      ],
      "classId": null,
      "courseId": 780,
      "roomId": 280,
      "siteId": 260,
      "siteTimezone": "Australia/West",
      "start": "2023-05-03T03:30:00.000Z",
      "end": "2023-05-03T09:30:00.000Z",
      "publicNotes": null,
      "privateNotes": null,
      "hasPaylines": false
    },
    {
      "id": 9498,
      "temporaryId": null,
      "name": "Certificate III in Early Childhood Education and Care (no units)",
      "code": null,
      "room": "Room 1",
      "site": "Perth office",
      "tutors": [

      ],
      "tutorAttendances": [

      ],
      "classId": null,
      "courseId": 780,
      "roomId": 280,
      "siteId": 260,
      "siteTimezone": "Australia/West",
      "start": "2023-05-03T03:30:00.000Z",
      "end": "2023-05-03T09:30:00.000Z",
      "publicNotes": null,
      "privateNotes": null,
      "hasPaylines": false
    },
    {
      "id": 4444,
      "temporaryId": null,
      "name": "Certificate III in Early Childhood Education and Care (no units)",
      "code": null,
      "room": "Room 1",
      "site": "Perth office",
      "tutors": [

      ],
      "tutorAttendances": [

      ],
      "classId": null,
      "courseId": 780,
      "roomId": 280,
      "siteId": 260,
      "siteTimezone": "Australia/West",
      "start": "2023-05-04T03:30:00.000Z",
      "end": "2023-05-04T09:30:00.000Z",
      "publicNotes": null,
      "privateNotes": null,
      "hasPaylines": false
    },
    {
      "id": 4993,
      "temporaryId": null,
      "name": "Certificate III in Early Childhood Education and Care (no units)",
      "code": null,
      "room": "Room 1",
      "site": "Perth office",
      "tutors": [

      ],
      "tutorAttendances": [

      ],
      "classId": null,
      "courseId": 780,
      "roomId": 280,
      "siteId": 260,
      "siteTimezone": "Australia/West",
      "start": "2023-05-05T03:30:00.000Z",
      "end": "2023-05-05T09:30:00.000Z",
      "publicNotes": null,
      "privateNotes": null,
      "hasPaylines": false
    },
    {
      "id": 4551,
      "temporaryId": null,
      "name": "Certificate III in Early Childhood Education and Care (no units)",
      "code": null,
      "room": "Room 1",
      "site": "Perth office",
      "tutors": [

      ],
      "tutorAttendances": [

      ],
      "classId": null,
      "courseId": 780,
      "roomId": 280,
      "siteId": 260,
      "siteTimezone": "Australia/West",
      "start": "2023-05-06T03:30:00.000Z",
      "end": "2023-05-06T09:30:00.000Z",
      "publicNotes": null,
      "privateNotes": null,
      "hasPaylines": false
    },
    {
      "id": 4750,
      "temporaryId": null,
      "name": "Certificate III in Early Childhood Education and Care (no units)",
      "code": null,
      "room": "Room 1",
      "site": "Perth office",
      "tutors": [

      ],
      "tutorAttendances": [

      ],
      "classId": null,
      "courseId": 780,
      "roomId": 280,
      "siteId": 260,
      "siteTimezone": "Australia/West",
      "start": "2023-05-07T03:30:00.000Z",
      "end": "2023-05-07T09:30:00.000Z",
      "publicNotes": null,
      "privateNotes": null,
      "hasPaylines": false
    },
    {
      "id": 4275,
      "temporaryId": null,
      "name": "Certificate III in Early Childhood Education and Care (no units)",
      "code": null,
      "room": "Room 1",
      "site": "Perth office",
      "tutors": [

      ],
      "tutorAttendances": [

      ],
      "classId": null,
      "courseId": 780,
      "roomId": 280,
      "siteId": 260,
      "siteTimezone": "Australia/West",
      "start": "2023-05-10T03:30:00.000Z",
      "end": "2023-05-10T09:30:00.000Z",
      "publicNotes": null,
      "privateNotes": null,
      "hasPaylines": false
    },
    {
      "id": 3838,
      "temporaryId": null,
      "name": "Certificate III in Early Childhood Education and Care (no units)",
      "code": null,
      "room": "Room 1",
      "site": "Perth office",
      "tutors": [

      ],
      "tutorAttendances": [

      ],
      "classId": null,
      "courseId": 780,
      "roomId": 280,
      "siteId": 260,
      "siteTimezone": "Australia/West",
      "start": "2023-05-11T03:30:00.000Z",
      "end": "2023-05-11T09:30:00.000Z",
      "publicNotes": null,
      "privateNotes": null,
      "hasPaylines": false
    },
    {
      "id": 3884,
      "temporaryId": null,
      "name": "Certificate III in Early Childhood Education and Care (no units)",
      "code": null,
      "room": "Room 1",
      "site": "Perth office",
      "tutors": [

      ],
      "tutorAttendances": [

      ],
      "classId": null,
      "courseId": 780,
      "roomId": 280,
      "siteId": 260,
      "siteTimezone": "Australia/West",
      "start": "2023-05-12T03:30:00.000Z",
      "end": "2023-05-12T09:30:00.000Z",
      "publicNotes": null,
      "privateNotes": null,
      "hasPaylines": false
    },
    {
      "id": 3543,
      "temporaryId": null,
      "name": "Certificate III in Early Childhood Education and Care (no units)",
      "code": null,
      "room": "Room 1",
      "site": "Perth office",
      "tutors": [

      ],
      "tutorAttendances": [

      ],
      "classId": null,
      "courseId": 780,
      "roomId": 280,
      "siteId": 260,
      "siteTimezone": "Australia/West",
      "start": "2023-05-13T03:30:00.000Z",
      "end": "2023-05-13T09:30:00.000Z",
      "publicNotes": null,
      "privateNotes": null,
      "hasPaylines": false
    },
    {
      "id": 4867,
      "temporaryId": null,
      "name": "Certificate III in Early Childhood Education and Care (no units)",
      "code": null,
      "room": "Room 1",
      "site": "Perth office",
      "tutors": [

      ],
      "tutorAttendances": [

      ],
      "classId": null,
      "courseId": 780,
      "roomId": 280,
      "siteId": 260,
      "siteTimezone": "Australia/West",
      "start": "2023-05-14T03:30:00.000Z",
      "end": "2023-05-14T09:30:00.000Z",
      "publicNotes": null,
      "privateNotes": null,
      "hasPaylines": false
    },
    {
      "id": 4702,
      "temporaryId": null,
      "name": "Certificate III in Early Childhood Education and Care (no units)",
      "code": null,
      "room": "Room 1",
      "site": "Perth office",
      "tutors": [

      ],
      "tutorAttendances": [

      ],
      "classId": null,
      "courseId": 780,
      "roomId": 280,
      "siteId": 260,
      "siteTimezone": "Australia/West",
      "start": "2023-05-17T03:30:00.000Z",
      "end": "2023-05-17T09:30:00.000Z",
      "publicNotes": null,
      "privateNotes": null,
      "hasPaylines": false
    },
    {
      "id": 3893,
      "temporaryId": null,
      "name": "Certificate III in Early Childhood Education and Care (no units)",
      "code": null,
      "room": "Room 1",
      "site": "Perth office",
      "tutors": [

      ],
      "tutorAttendances": [

      ],
      "classId": null,
      "courseId": 780,
      "roomId": 280,
      "siteId": 260,
      "siteTimezone": "Australia/West",
      "start": "2023-05-18T03:30:00.000Z",
      "end": "2023-05-18T09:30:00.000Z",
      "publicNotes": null,
      "privateNotes": null,
      "hasPaylines": false
    },
    {
      "id": 4387,
      "temporaryId": null,
      "name": "Certificate III in Early Childhood Education and Care (no units)",
      "code": null,
      "room": "Room 1",
      "site": "Perth office",
      "tutors": [

      ],
      "tutorAttendances": [

      ],
      "classId": null,
      "courseId": 780,
      "roomId": 280,
      "siteId": 260,
      "siteTimezone": "Australia/West",
      "start": "2023-05-19T03:30:00.000Z",
      "end": "2023-05-19T09:30:00.000Z",
      "publicNotes": null,
      "privateNotes": null,
      "hasPaylines": false
    },
    {
      "id": 4625,
      "temporaryId": null,
      "name": "Certificate III in Early Childhood Education and Care (no units)",
      "code": null,
      "room": "Room 1",
      "site": "Perth office",
      "tutors": [

      ],
      "tutorAttendances": [

      ],
      "classId": null,
      "courseId": 780,
      "roomId": 280,
      "siteId": 260,
      "siteTimezone": "Australia/West",
      "start": "2023-05-20T03:30:00.000Z",
      "end": "2023-05-20T09:30:00.000Z",
      "publicNotes": null,
      "privateNotes": null,
      "hasPaylines": false
    },
    {
      "id": 5309,
      "temporaryId": null,
      "name": "Certificate III in Early Childhood Education and Care (no units)",
      "code": null,
      "room": "Room 1",
      "site": "Perth office",
      "tutors": [

      ],
      "tutorAttendances": [

      ],
      "classId": null,
      "courseId": 780,
      "roomId": 280,
      "siteId": 260,
      "siteTimezone": "Australia/West",
      "start": "2023-05-21T03:30:00.000Z",
      "end": "2023-05-21T09:30:00.000Z",
      "publicNotes": null,
      "privateNotes": null,
      "hasPaylines": false
    },
    {
      "id": 5251,
      "temporaryId": null,
      "name": "Certificate III in Early Childhood Education and Care (no units)",
      "code": null,
      "room": "Room 1",
      "site": "Perth office",
      "tutors": [

      ],
      "tutorAttendances": [

      ],
      "classId": null,
      "courseId": 780,
      "roomId": 280,
      "siteId": 260,
      "siteTimezone": "Australia/West",
      "start": "2023-05-24T03:30:00.000Z",
      "end": "2023-05-24T09:30:00.000Z",
      "publicNotes": null,
      "privateNotes": null,
      "hasPaylines": false
    },
    {
      "id": 3974,
      "temporaryId": null,
      "name": "Certificate III in Early Childhood Education and Care (no units)",
      "code": null,
      "room": "Room 1",
      "site": "Perth office",
      "tutors": [

      ],
      "tutorAttendances": [

      ],
      "classId": null,
      "courseId": 780,
      "roomId": 280,
      "siteId": 260,
      "siteTimezone": "Australia/West",
      "start": "2023-05-25T03:30:00.000Z",
      "end": "2023-05-25T09:30:00.000Z",
      "publicNotes": null,
      "privateNotes": null,
      "hasPaylines": false
    },
  ];

  this.getTimetableSessionsTags = (sessionIds: string) => [
      {
        Rozelle: "2d4d63",
        "Access2Ed - Disability Support": "9656d6",
        "Supported Programmes": "e89c3f",
        Dance: "a7ceeb",
        Fax: "cec2f0"
      },
      {
        Rozelle: "2d4d63",
        "Access2Ed - Disability Support": "9656d6",
        "Supported Programmes": "e89c3f",
        Fax: "cec2f0"
      },
      {
        Marcus: "8e99ab",
        Contractor: "0aa648",
        "Adobe Training": "645396",
        "N-GST": "ebeff5",
        "Computer Room": "086962",
        "Buisness Day Prog": "282436",
        City: "642b9e",
        "Adobe-Courses": "075426",
        "Graphic Design Classes": "421527"
      },
      {
        "Training Qualifications": "33210c",
        "Communication & Career Development": "086962",
        Cherril: "d6246e",
        "Funded Programs": "4d4dd1"
      },
      {
        "Training Qualifications": "33210c",
        Fax: "cec2f0",
        Cherril: "d6246e",
        "Funded Programs": "4d4dd1"
      },
      {
        Judith: "8a4d03",
        Rozelle: "2d4d63",
        Contractor: "0aa648",
        Accounting: "8e99ab",
        "Father's Day Special": "064d70",
        "N-GST": "ebeff5",
        Sabrina: "48799c",
        Computing: "645396",
        Business: "645396"
      },
      {
        Jewellery: "064d70",
        Wage: "3272d9",
        Judith: "8a4d03",
        Rozelle: "2d4d63",
        "Arts Workshop Pass": "15233b",
        GST: "f5bc76",
        Sabrina: "48799c",
        "Jewellery Classes": "202057",
        "Studio Workshops": "84b1fa",
        Arts: "8e99ab"
      },
      {
        Wage: "3272d9",
        Sport: "376180",
        Rozelle: "2d4d63",
        GST: "f5bc76",
        Karen: "6767e6",
        Fitness: "fab4d1"
      },
      {
        Rozelle: "2d4d63",
        "Access2Ed - Disability Support": "9656d6",
        "Supported Programmes": "7a68b3",
        Fax: "cec2f0"
      },
      {
        "Visual Arts Classes": "b5a6e3",
        Rozelle: "2d4d63",
        "Access2Ed - Disability Support": "9656d6",
        "Supported Programmes": "e89c3f",
        Fax: "cec2f0"
      },
      {
        City: "642b9e",
        Fax: "cec2f0"
      },
      {
        Wage: "3272d9",
        Judith: "8a4d03",
        Language: "84b7db",
        "Father's Day Special": "064d70",
        "N-GST": "ebeff5",
        Languages: "b80d0d",
        Sabrina: "48799c",
        City: "642b9e",
        "Recent Gmail": "0c2938",
        "Learn Spanish": "08827a"
      },
      {
        "Handcrafts Classes": "371c52",
        Wage: "3272d9",
        "Sewing Classes": "c2c2fc",
        "Textiles & Fashion Classes": "dde3ed",
        "Father's Day Special": "064d70",
        "Textiles & Fashion": "8f0e0e",
        GST: "f5bc76",
        Karen: "6767e6",
        City: "642b9e",
        Notes: "4f4178",
        Arts: "8e99ab"
      },
      {
        "Handcrafts Classes": "371c52",
        Wage: "3272d9",
        "Sewing Classes": "c2c2fc",
        Rozelle: "2d4d63",
        "Textiles & Fashion Classes": "dde3ed",
        "Father's Day Special": "064d70",
        "Textiles & Fashion": "8f0e0e",
        GST: "f5bc76",
        Karen: "6767e6",
        Fax: "cec2f0",
        Arts: "8e99ab"
      },
      {
        Wage: "3272d9",
        "Textiles & Fashion Classes": "dde3ed",
        "Textiles & Fashion": "8f0e0e",
        Karen: "6767e6",
        City: "642b9e",
        Arts: "8e99ab"
      },
      {
        Claire: "9656d6",
        Wage: "3272d9",
        "Visual Arts Classes": "b5a6e3",
        Rozelle: "2d4d63",
        "Visual Art": "3737b3",
        "Father's Day Special": "064d70",
        GST: "f5bc76",
        Fax: "cec2f0",
        "Drawing Classes": "451717",
        Arts: "8e99ab"
      },
      {
        Tennis: "21c2b7",
        Sport: "376180",
        GST: "f5bc76",
        Karen: "6767e6",
        "Tennis Camperdown": "3737b3",
        Fax: "cec2f0"
      },
      {
        City: "642b9e",
        Fax: "cec2f0"
      },
      {
        "Photography Classes": "43ded3",
        "New Courses": "6767e6",
        City: "642b9e",
        Fax: "cec2f0"
      },
      {
        Claire: "9656d6",
        Contractor: "0aa648",
        "Essential Ingredient course": "c8d1e0",
        "Food & Wine": "fab6b6",
        "Cooking Classes": "a7a7fa",
        Fax: "cec2f0",
        "Cooking - International Cuisine": "afbacc"
      },
      {
        Wage: "3272d9",
        Judith: "8a4d03",
        Language: "84b7db",
        "New Courses": "6767e6",
        City: "642b9e",
        Sabrina: "48799c",
        "Learn Indonesian": "3272d9"
      },
      {
        Judith: "8a4d03",
        Contractor: "0aa648",
        Language: "84b7db",
        "N-GST": "ebeff5",
        Languages: "b80d0d",
        "Sign Language Classes": "6767e6",
        Sabrina: "48799c",
        City: "642b9e"
      },
      {
        Wage: "3272d9",
        Rozelle: "2d4d63",
        Judith: "8a4d03",
        Music: "09524d",
        "New Courses": "6767e6",
        Sabrina: "48799c",
        "Music & Singing Classes": "accbfc"
      },
      {
        Photography: "114599",
        Marcus: "8e99ab",
        Contractor: "0aa648",
        "Photography Classes": "43ded3",
        "Father's Day Special": "064d70",
        "N-GST": "ebeff5",
        City: "642b9e",
        Arts: "8e99ab"
      },
      {
        Contractor: "0aa648",
        "Father's Day Special": "064d70",
        Lifestyle: "086962",
        GST: "f5bc76",
        Karen: "6767e6",
        City: "642b9e",
        Fax: "cec2f0",
        "Makeup & Personal Presentation": "ed5393",
        Notes: "4f4178"
      },
      {
        Wage: "3272d9",
        Rozelle: "2d4d63",
        Judith: "8a4d03",
        Music: "09524d",
        Sabrina: "48799c",
        Fax: "cec2f0",
        "Music & Singing Classes": "accbfc"
      },
      {
        Judith: "8a4d03",
        "Father's Day Special": "064d70",
        "N-GST": "ebeff5",
        GST: "f5bc76",
        Languages: "b80d0d",
        Karen: "6767e6",
        "Tennis Marrickville": "075426",
        "Learn Italian": "0aa648",
        "Four Seasons": "cf7911",
        Language: "84b7db",
        Coaches: "46e385",
        Sabrina: "48799c",
        Fax: "cec2f0"
      },
      {
        Tennis: "21c2b7",
        Sport: "376180",
        GST: "f5bc76",
        Karen: "6767e6",
        "Tennis Camperdown": "3737b3",
        Fax: "cec2f0"
      },
      {
        City: "642b9e",
        Fax: "cec2f0"
      },
      {
        "Photography Classes": "43ded3",
        "New Courses": "6767e6",
        City: "642b9e",
        Fax: "cec2f0"
      }
    ];
}
