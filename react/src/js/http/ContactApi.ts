import {HttpService} from "../services/HttpService";
import { Contact } from "../model/Contact";
import { ModelError } from "../model/ModelError";

export class ContactApi {
  constructor(private http: HttpService) {
  }

  getContact(studentUniqueIdentifier: string): Promise<Contact> {
    return this.http.GET(`/contact/${studentUniqueIdentifier}`)
  }
}
