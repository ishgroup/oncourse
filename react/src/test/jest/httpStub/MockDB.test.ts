import * as L from "lodash";
import {MockDB} from "../../../js/httpStub/mocks/MockDB";
import {Enrolment} from "../../../js/model/checkout/Enrolment";

import {Contact} from "../../../js/model/web/Contact";
import {CourseClass} from "../../../js/model/web/CourseClass";


test('test MockDB', () => {

  console.log(MockDB.DB.contacts);
  console.log(MockDB.DB.classes);


  console.log(MockDB.DB.contacts.result[0]);
  console.log(MockDB.DB.classes.result[0]);

  const enrolment: Enrolment = MockDB.DB.createEnrolment(MockDB.DB.contacts.result[0], MockDB.DB.classes.result[0]);

  console.log(enrolment);

  console.log(MockDB.DB.getCourseClassByIndex(0));

  console.log(MockDB.DB.getContactByIndex(0));


  const contacts: Contact[] = [MockDB.DB.getContactByIndex(0), MockDB.DB.getContactByIndex(1)];
  const classes: CourseClass[] = [MockDB.DB.getCourseClassByIndex(0), MockDB.DB.getCourseClassByIndex(1)];


  const result = contacts.map((c: Contact) => {
    return classes.map((cc: CourseClass) => {
      return MockDB.DB.createEnrolment(c.id, cc.id)
    })
  });

  console.log(L.flatten(result));


  L.filter(MockDB.DB.contacts.entities.contacts, (c:Contact) => c.firstName === 'Leanne' )
});