import axiosMockAdapter from 'axios-mock-adapter';
import {Container, Row, Col} from 'reactstrap';
import {defaultAxios} from "../../src/js/common/services/DefaultHttpClient";
import {authApiMock} from "./AuthApiMock";

export function initMockAdapter() {
  const mock = new axiosMockAdapter(defaultAxios);
  authApiMock(mock);
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
