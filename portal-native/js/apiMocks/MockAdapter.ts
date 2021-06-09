/**
 * Service for mock backend functionality, used in dev mode
 * Library: axios-mock-adapter - https://github.com/ctimmerm/axios-mock-adapter
 * */

import axiosMockAdapter from "axios-mock-adapter";
import {defaultAxios} from "../constants/DefaultHttpClient";
import {LoginApiMock} from "./api/LoginApiMock";
import {promiseReject} from "./utils";

export const initMockDB = () => new MockAdapter();

export class MockAdapter {
  public api = new axiosMockAdapter(defaultAxios, { delayResponse: 1000 });

  constructor() {
    LoginApiMock.apply(this);

    // Handle all other requests
    this.api.onAny().reply(config => {
      console.log("%c UNHANDLED REQUEST", "color: red");
      console.log(config);
      console.log(this.api);
      return promiseReject(config);
    });
  }
}

