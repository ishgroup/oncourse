import {promiseReject, promiseResolve} from "../MockAdapter";
import {API} from "../../../js/constants/Config";
import {LoginRequest} from "../../../js/model";

export function authApiMock(mock) {
  this.api.onPost(API.LOGIN).reply(config => {
    const data: LoginRequest = JSON.parse(config.data);

    if (data.email === 'error@error.com') {
      return promiseReject(config, {
        message: 'Incorrect user',
      });
    }

    return promiseResolve(config, this.db.users[0]);
  });

  this.api.onPost(API.LOGOUT).reply(config => promiseResolve(config));
}
