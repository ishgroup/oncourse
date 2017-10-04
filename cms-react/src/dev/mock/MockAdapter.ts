import axiosMockAdapter from 'axios-mock-adapter';
import {Col, Container, Row} from 'reactstrap';
import {defaultAxios} from "../../js/common/services/DefaultHttpClient";
import {CreateMockDB, MockDB} from "./MockDB";

import {authApiMock} from "./api/AuthApiMock";
import {menuApiMock} from "./api/MenuApiMock";
import {pageApiMock} from "./api/PageApiMock";
import {blockApiMock} from "./api/BlockApiMock";
import {themeApiMock} from "./api/ThemeApiMock";

export class MockAdapter {
  public api = new axiosMockAdapter(defaultAxios);
  public db: MockDB = CreateMockDB();

  constructor() {
    authApiMock.apply(this);
    menuApiMock.apply(this);
    pageApiMock.apply(this);
    blockApiMock.apply(this);
    themeApiMock.apply(this);
  }

}

export const promiseResolve = (config, data = {}) => {
  console.log(`Api request to: /${config.url}`);
  console.log(`request params: ${config.data}`);
  console.log(`response params:`); console.log(data);

  return [200, data];
}

export const promiseReject = (config, data = {}) => {
  console.log(`Api request to: /${config.url}`);
  console.log(`request params: ${config.data}`);
  return [400, data];
}
