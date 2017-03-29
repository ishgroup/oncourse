import {HttpService} from "../services/HttpService";
import {Contact} from "../model/Contact";
import {ModelError} from "../model/ModelError";

export class ContactApi {
  constructor(private http: HttpService) {
  }

  getContact(studentUniqueIdentifier: string): Promise<Contact> {
    return Promise.resolve({
      "id": "5138961",
      "uniqueIdentifier": "5qHShQVb3a6nnYn2",
      "firstName": "John",
      "lastName": "Shepard",
      "email": "tuchanka@tut.by"
    });

    // return this.http.GET(`/contact/${studentUniqueIdentifier}`)
  }
}
