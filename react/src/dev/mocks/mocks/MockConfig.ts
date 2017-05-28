import {AxiosResponse} from "axios";
import faker from "faker";
import uuid from "uuid";
import {MockDB, CreateMockDB} from "./MockDB";

export class MockConfig {
  private id:string = uuid();
  public static CONFIG = new MockConfig();

  public db: MockDB = CreateMockDB();
  public commonError: boolean = false;
  public plainTextError: boolean = false;
  public validationError: boolean = false;


  createResponse(response: any): Promise<any> {
    if (this.plainTextError) {
      return CreatePromiseReject(faker.hacker.phrase());
    } else if (this.commonError) {
      return CreatePromiseReject({
        code: 1000,
        message: faker.hacker.phrase()
      });
    } else if (this.validationError) {
      return CreatePromiseReject({
        formErrors: [faker.hacker.phrase(), faker.hacker.phrase(), faker.hacker.phrase()],
        fieldsErrors: [{name: "email", error: faker.hacker.phrase()}, {name: "street", error: faker.hacker.phrase()}]
      });
    }
    else {
      return Promise.resolve(response);
    }
  }
}

export const CreatePromiseReject = (data: any, code: number = 400): Promise<any> => {
  return Promise.reject({
    data: data,
    status: code
  } as AxiosResponse);
};
