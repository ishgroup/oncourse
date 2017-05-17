import {HttpService} from "../common/services/HttpService";
import { CommonError } from "../model/common/CommonError";
import { ValidationError } from "../model/common/ValidationError";
import { ContactFields } from "../model/field/ContactFields";
import { ContactFieldsRequest } from "../model/field/ContactFieldsRequest";
import { SubmitFieldsRequest } from "../model/field/SubmitFieldsRequest";
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
  getContactFields(contactFieldsRequest: ContactFieldsRequest): Promise<ContactFields> {
    return this.http.POST(`/contactFields`, contactFieldsRequest)
  }
  submitContactDetails(contactFields: SubmitFieldsRequest): Promise<any> {
    return this.http.PUT(`/submitContactDetails`, contactFields)
  }
}
