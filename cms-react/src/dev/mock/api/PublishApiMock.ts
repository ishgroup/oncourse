import {promiseReject, promiseResolve} from "../MockAdapter";
import {API} from "../../../js/constants/Config";

export function publishApiMock() {
  this.api.onGet(API.GET_VERSIONS).reply(config => promiseResolve(
    config,
    this.db.versions,
  ));

  this.api.onPost(API.PUBLISH).reply(config => promiseResolve(
    config,
    null,
  ));

  this.api.onPost(API.SET_VERSION).reply(config => promiseResolve(
    config,
    null,
  ));
}
