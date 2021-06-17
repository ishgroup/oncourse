import { LoginResponse } from '@api/model';
import { promiseResolve } from '../utils';
import { MockAdapterType } from '../types';

export default function LoginApiMock(this: MockAdapterType) {
  this.api.onPut('/v1/login/connect')
    .reply((config) => promiseResolve<LoginResponse>(config, {
      user: {
        id: 1111,
        firstName: 'John',
        lastName: 'Smith',
        companyName: null,
        email: 'test@test.com'
      },
      isNew: false
    }));
  this.api.onPut('/v1/login')
    .reply((config) => promiseResolve<LoginResponse>(config, {
      user: {
        id: 1111,
        firstName: 'John',
        lastName: 'Smith',
        companyName: null,
        email: 'test@test.com'
      },
      isNew: false
    }));
  this.api.onPost('/v1/login')
    .reply((config) => promiseResolve<LoginResponse>(config, {
      user: {
        id: 1111,
        firstName: 'John',
        lastName: 'Smith',
        companyName: null,
        email: 'test@test.com'
      },
      isNew: true
    }));
}
