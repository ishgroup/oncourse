import {HttpService} from "../services/HttpService";
import { Contact } from "../model/Contact";
import { CreateContactParams } from "../model/CreateContactParams";
import { ModelError } from "../model/ModelError";
import { ValidationError } from "../model/ValidationError";

export class ContactApi {
  constructor(private http: HttpService) {
  }

  createOrGetContact(createContactParams: CreateContactParams): Promise<Contact> {
    return this.http.PUT(`/contact`, createContactParams)
  }
  getContact(studentUniqueIdentifier: string): Promise<Contact> {
    return this.http.GET(`/contact/{studentUniqueIdentifier}`)
  }
}
