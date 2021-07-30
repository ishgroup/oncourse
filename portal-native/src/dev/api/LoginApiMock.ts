import { LoginResponse } from '@api/model';
import { promiseResolve } from '../utils';
import { MockAdapterType } from '../types';

export default function LoginApiMock(this: MockAdapterType) {
  this.api.onPost('/v1/login')
    .reply((config) => promiseResolve<LoginResponse>(config, {
      user: {
        id: 1111,
        email: 'test@test.com'
      },
      token: 'sdfdg0waerwjajisdfjisajdfoa',
      vefiryEmail: false
    }));
  this.api.onGet('/v1/login')
    .reply((config) => promiseResolve<LoginResponse>(config, {
      vefiryEmail: true
    }));
}
