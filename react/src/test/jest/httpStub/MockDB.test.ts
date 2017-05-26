import * as L from "lodash";
import {MockDB} from "../../../js/httpStub/mocks/MockDB";
import {Enrolment} from "../../../js/model/checkout/Enrolment";

import {Contact} from "../../../js/model/web/Contact";
import {CourseClass} from "../../../js/model/web/CourseClass";
import {normalize} from "normalizr";

import {ClassesListSchema} from "../../../js/NormalizeSchema";

test('test MockDB', () => {

  const db:MockDB = new MockDB();
  console.log(db.contacts);
  console.log(db.classes);
  console.log(db.contacts.result[0]);
  console.log(db.classes.result[0]);

  const enrolment: Enrolment = db.createEnrolment(db.contacts.result[0], db.classes.result[0]);
  const contacts: Contact[] = [db.getContactByIndex(0), db.getContactByIndex(1)];
  const classes: CourseClass[] = [db.getCourseClassByIndex(0), db.getCourseClassByIndex(1)];

  console.log(normalize(classes, ClassesListSchema));


  const result = contacts.map((c: Contact) => {
    return classes.map((cc: CourseClass) => {
      return db.createEnrolment(c.id, cc.id)
    })
  });
  L.filter(db.contacts.entities.contacts, (c:Contact) => c.firstName === 'Leanne' )
});