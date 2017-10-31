import {promiseResolve} from "../MockAdapter";
import {API} from "../../../js/constants/Config";

export function authApiMock(mock) {
  this.api.onPost(API.LOGIN).reply(config => {
    console.log(config);

    return promiseResolve(config, {
      user: this.db.users[0],
    });
  });

  this.api.onPost(API.LOGOUT).reply(config => promiseResolve(config));
}
