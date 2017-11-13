import axiosMockAdapter from 'axios-mock-adapter';
import {Col, Container, Row} from 'reactstrap';
import {defaultAxios} from "../../js/common/services/DefaultHttpClient";
import {CreateMockDB, MockDB} from "./MockDB";

import {authApiMock} from "./api/AuthApiMock";
import {menuApiMock} from "./api/MenuApiMock";
import {pageApiMock} from "./api/PageApiMock";
import {blockApiMock} from "./api/BlockApiMock";
import {themeApiMock} from "./api/ThemeApiMock";
import {publishApiMock} from "./api/PublishApiMock";
import {settingsApiMock} from "./api/SettingsApiMock";

export class MockAdapter {
  public api = new axiosMockAdapter(defaultAxios);
  public db: MockDB = CreateMockDB();

  constructor() {
    authApiMock.apply(this);
    menuApiMock.apply(this);
    pageApiMock.apply(this);
    blockApiMock.apply(this);
    themeApiMock.apply(this);
    publishApiMock.apply(this);
    settingsApiMock.apply(this);
  }

}

export const promiseResolve = (config, data = {}) => {
  console.log('%c ----------------', 'color: black');
  console.log(`%c Api request to: /${config.url}`, 'color: #bada55');
  console.log(`%c request params: ${config.data}`, 'color: #bada55');
  console.log(`%c response params:`, 'color: #bada55'); console.log(data);
  console.log('%c ----------------', 'color: black');


  return [200, data];
}

export const promiseReject = (config, data = {}) => {
  console.log(`%c Api request to: /${config.url}`, 'color: red');
  console.log(`%c request params: ${config.data}`, 'color: red');
  return [400, data];
}
