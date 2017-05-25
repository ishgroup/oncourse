import * as L from "lodash";

import {CourseClass} from "../../../js/model/web/CourseClass";
import {Enrolment} from "../../../js/model/checkout/Enrolment";
import {Contact} from "../../../js/model/web/Contact";
import {mockContact, mockCourseClass} from "./MockFunctions";
import {normalize} from "normalizr";
import {ClassesListSchema, ContactsListSchema} from "../../NormalizeSchema";
import faker from "faker";
import uuid from "uuid";


export class MockDB {

  private id: string = uuid();

  public static DB: MockDB = new MockDB();

  contacts: { entities: any, result: any } = null;
  classes: { entities: any, result: any } = null;

  constructor() {
    this.contacts = normalize([mockContact(), mockContact(), mockContact()], ContactsListSchema);
    this.classes = normalize([
      mockCourseClass(),
      mockCourseClass(),
      mockCourseClass(),
      mockCourseClass(),
      mockCourseClass(),
      mockCourseClass(),
      mockCourseClass()
    ], ClassesListSchema);
  }

  createEnrolment(contactId: string, classId: string, errors: boolean = false, warnings: boolean = false): Enrolment {
    return {
      contactId: this.contacts.entities.contacts[contactId].id,
      classId: this.classes.entities.classes[classId].id,
      errors: errors ? [faker.hacker.phrase(), faker.hacker.phrase()] : [],
      warnings: warnings ? [faker.hacker.phrase(), faker.hacker.phrase()] : [],
      price: this.classes.entities.classes[classId].price,
    }
  }

  addContact(contact: Contact): string {
    const nc = normalize([contact], ContactsListSchema);
    this.contacts.result = [...this.contacts.result, ...nc.result];
    this.contacts.entities.contacts = {...this.contacts.entities.contacts, ...nc.entities.contacts};
    return contact.id;
  };

  getContactByIndex(index: number): Contact {
    return this.contacts.entities.contacts[this.contacts.result[index]];
  }

  getContactByDetails(firstName: String, lastName: String, email: String): Contact {
    return L.find(this.contacts.entities.contacts,
      (c: Contact) => c.firstName === firstName && c.lastName == lastName && c.email == email);
  }


  getCourseClassByIndex(index: number): CourseClass {
    return this.classes.entities.classes[this.classes.result[index]];
  };

  getContactById(id: string): Contact {
    return this.contacts.entities.contacts[id];
  }

  getCourseClassById(id: string): CourseClass {
    return this.classes.entities.classes[id];
  };
}

