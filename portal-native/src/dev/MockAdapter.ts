/**
 * Service for mock backend functionality, used in dev mode
 * Library: axios-mock-adapter - https://github.com/ctimmerm/axios-mock-adapter
 * */

import MockAdapter from 'axios-mock-adapter';
import { defaultAxios } from '../js/constants/DefaultHttpClient';
import { promiseReject } from './utils';
import { MockAdapterType } from './types';
import LoginApiMock from './api/LoginApiMock';
import { IS_JEST } from '../js/constants/Environment';

export class MockApi implements MockAdapterType {
  public api = new MockAdapter(defaultAxios, { delayResponse: IS_JEST ? 0 : 1000 });

  constructor() {
    LoginApiMock.apply(this);

    // Handle all other requests
    this.api.onAny().reply((config) => {
      console.log('%c UNHANDLED REQUEST', 'color: red');
      console.log(config);
      console.log(this.api);
      return promiseReject(config);
    });
  }
}

export const initMockDB = () => new MockApi();
