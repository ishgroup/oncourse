import axiosMockAdapter from 'axios-mock-adapter';
import {Col, Container, Row} from 'reactstrap';
import {defaultAxios} from "../../js/common/services/DefaultHttpClient";
import {authApiMock} from "./AuthApiMock";
import {menuApiMock} from "./MenuApiMock";

export function initMockAdapter() {
  const mock = new axiosMockAdapter(defaultAxios);
  authApiMock(mock);
  menuApiMock(mock);
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
