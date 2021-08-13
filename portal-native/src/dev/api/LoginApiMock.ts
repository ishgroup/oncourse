import { LoginResponse, PasswordComplexity } from '@api/model';
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

  this.api.onGet('/v1/sso')
    .reply((config) => promiseResolve<{ [key: string]: string; }>(config, {
      Google: '568692144060-nku44p171f3sar4v06g7ve0vdmf2ppen.apps.googleusercontent.com',
      Facebook: '837945397102277',
      Microsoft: '5aa7f417-c05e-49ae-8c10-026999bd12d2'
    }));

  this.api.onPut(new RegExp('v1\\/login\\/pass\\/\\w+'))
    .reply((config) => {
      const password = config.url.replace('/v1/login/pass/', '');

      if (password.length > 6 && password.length < 8) {
        return promiseResolve<PasswordComplexity>(config, {
          score: 2,
          feedback: 'Normal password'
        });
      }

      if (password.length > 7) {
        return promiseResolve<PasswordComplexity>(config, {
          score: 5,
          feedback: 'Good password'
        });
      }

      return promiseResolve<PasswordComplexity>(config, {
        score: 1,
        feedback: 'Weak password'
      });
    });
}
