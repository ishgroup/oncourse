import {ContactApi} from "../http/ContactApi";
import {Contact} from "../model/web/Contact";
import {CreateContactParams} from "../model/web/CreateContactParams";
import {AxiosResponse} from "axios";
import {ContactFieldsRequest} from "../model/field/ContactFieldsRequest";
import {ContactFields} from "../model/field/ContactFields";

import MockContactFields from "./MockContactFields";
import {SubmitFieldsRequest} from "../model/field/SubmitFieldsRequest";

export class ContactApiStub extends ContactApi {
  getContact(studentUniqueIdentifier: string): Promise<Contact> {
    return Promise.resolve({
      "id": "5138961",
      "uniqueIdentifier": "5qHShQVb3a6nnYn2",
      "firstName": "John",
      "lastName": "Shepard",
      "email": "tuchanka@tut.by"
    } as Contact);
  }

  createOrGetContact(createContactParams: CreateContactParams): Promise<Contact> {
    if (new Date().getTime() % 3 === 0) {
      return Promise.reject({
        data: {
          formErrors: [
            "There are two student found.",
            "Another global error."
          ],
          fieldsErrors: [{
            name: "email",
            errors: [
              "Email format violation.",
              "Maybe force be with you."
            ]
          }]
        },
        status: 400
      } as AxiosResponse);
    }

    return Promise.resolve("5138961");
  }

  getContactFields(contactFieldsRequest: ContactFieldsRequest): Promise<ContactFields> {
    return Promise.resolve(MockContactFields);
  }


  submitContactDetails(submit: SubmitFieldsRequest): Promise<any> {
    return createPromiseReject({
      formErrors: [],
      fieldsErrors: [{
          name: "isMale",
          error: "You should choose between male or female"
        }
      ]
    })
  }
}


const createPromiseReject = (data: any): Promise<any> => {
  return Promise.reject({
    data: data,
    status: 400
  } as AxiosResponse);
};