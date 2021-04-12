import axiosMockAdapter from 'axios-mock-adapter';
import {defaultAxios} from "../../js/common/services/DefaultHttpClient";
import {CreateMockDB, MockDB} from "./MockDB";

import {authApiMock} from "./api/AuthApiMock";
import {menuApiMock} from "./api/MenuApiMock";
import {pageApiMock} from "./api/PageApiMock";
import {blockApiMock} from "./api/BlockApiMock";
import {themeApiMock} from "./api/ThemeApiMock";
import {versionApiMock} from "./api/VersionApiMock";
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
    versionApiMock.apply(this);
    settingsApiMock.apply(this);

    // handle all remaining requests
    this.api.onAny().reply(config => {
      console.warn('UNHANDLED REQUEST');
      console.log(config);
      return promiseReject(
        config,
        null,
      );
    });
  }

}

export const promiseResolve = (config, data = {}) => {
  console.log('%c ----------------', 'color: black');
  console.log(`%c Api request to: /${config.url}`, 'color: #bada55');
  console.log(`%c request params:`, 'color: #bada55'); console.log(config.data && [parseJson(config.data)]);
  console.log(`%c response params:`, 'color: #bada55'); console.log([data]);
  console.log('%c ----------------', 'color: black');


  return [200, data];
};

export const promiseReject = (config, data = {}, status = 400) => {
  console.log(`%c Api request to: /${config.url}`, 'color: red');
  console.log(`%c request params: ${config.data}`, 'color: red');
  return [status, data];
};

const parseJson = data => {
  let json;

  try {
    json = JSON.parse(data);
  } catch (e) {
    json = data;
  }

  return json;
};
