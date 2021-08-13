/**
 * Service for mock backend functionality, used in dev mode
 * Library: axios-mock-adapter - https://github.com/ctimmerm/axios-mock-adapter
 * */
import MockAdapter from 'axios-mock-adapter';
import { defaultAxios } from '../js/constants/DefaultHttpClient';
import { promiseReject } from './utils';
import { MockAdapterType } from './types';
import LoginApiMock from './api/LoginApiMock';
import SessionApiMock from './api/SessionApiMock';
import CourseClassApiMock from './api/CourseClassApiMock';

export class MockApi implements MockAdapterType {
  public api = new MockAdapter(defaultAxios);

  constructor() {
    LoginApiMock.apply(this);
    SessionApiMock.apply(this);
    CourseClassApiMock.apply(this);

    // Handle all other requests
    this.api.onAny().reply<any>((config, data = {}, headers = {}) => {
      console.log('%c UNHANDLED REQUEST', 'color: red');
      console.log(config);
      return promiseReject(config, data, headers);
    });
  }
}

export const initMockDB = () => new MockApi();
