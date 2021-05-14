/**
 * Service for mock backend functionality, used in dev mode
 * Library: axios-mock-adapter - https://github.com/ctimmerm/axios-mock-adapter
 * */

import axiosMockAdapter from "axios-mock-adapter";
import {defaultAxios} from "../../js/api/services/DefaultHttpClient";
import {BillingApiMock} from "./api/BillingApiMock";

export const initMockDB = () => new MockAdapter();

export class MockAdapter {
  public api = new axiosMockAdapter(defaultAxios, { delayResponse: 1000 });

  constructor() {
    BillingApiMock.apply(this);


    // Handle all other requests
    this.api.onAny().reply(config => {
      console.log("%c UNHANDLED REQUEST", "color: red");
      console.log(config);
      console.log(this.api);
      return promiseReject(config);
    });
  }
}

// Resolve function with logger
export function promiseResolve<D = any>(config, data: D = {} as any, headers = {}) {
  console.log("%c ----------------", "color: black");
  console.log(`%c Api request to: ${config.url}`, "color: #bada55");
  console.log(`%c Api request method: ${config.method}`, "color: #bada55");
  console.log(`%c request params:`, "color: #bada55");
  if (config.method === "get") {
    console.log(config.params);
  } else {
    console.log(config.data && [parseJson(config.data)]);
  }
  console.log(`%c response params:`, "color: #bada55");
  console.log([data]);
  console.log("%c ----------------", "color: black");

  return [200, data, headers];
};

// Reject function with logger
export const promiseReject = (config, data = {}, headers = {}) => {
  console.log(`%c Api request ${config.method} to: /${config.url}`, "color: red");
  console.log(`%c request params:`, "color: #bada55");
  if (config.method === "get") {
    console.log(config.params);
  } else {
    console.log(config.data && [parseJson(config.data)]);
  }
  console.log(`%c request params:`, "color: #bada55");
  return [400, data, headers];
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
