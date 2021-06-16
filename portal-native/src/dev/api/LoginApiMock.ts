import { promiseResolve } from '../utils';
import { MockAdapterType } from '../types';

export default function LoginApiMock(this: MockAdapterType) {
  this.api.onPut('/v1/login')
    .reply((config) => promiseResolve(config, true));
  this.api.onPost('/v1/login')
    .reply((config) => promiseResolve(config, true));
}
