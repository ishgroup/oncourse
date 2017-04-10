import {ContactApi} from "../http/ContactApi";
import {Contact} from "../model/Contact";

export class ContactApiStub extends ContactApi {
  getContact(studentUniqueIdentifier: string): Promise<Contact> {
    return Promise.resolve({
      "id": "5138961",
      "uniqueIdentifier": "5qHShQVb3a6nnYn2",
      "firstName": "John",
      "lastName": "Shepard",
      "email": "tuchanka@tut.by"
    });
  }
}
