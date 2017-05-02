import {HttpService} from "../common/services/HttpService";
import { CommonError } from "../model/common/CommonError";
import { ValidationError } from "../model/common/ValidationError";
import { Contact } from "../model/web/Contact";
import { CreateContactParams } from "../model/web/CreateContactParams";

export class ContactApi {
  constructor(private http: HttpService) {
  }

  createOrGetContact(createContactParams: CreateContactParams): Promise<string> {
    return this.http.PUT(`/contact`, createContactParams)
  }
  getContact(studentUniqueIdentifier: string): Promise<Contact> {
    return this.http.GET(`/contact/${studentUniqueIdentifier}`)
  }
}
