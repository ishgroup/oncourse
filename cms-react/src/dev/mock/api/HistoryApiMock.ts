import {promiseReject, promiseResolve} from "../MockAdapter";
import {API} from "../../../js/constants/Config";

export function historyApiMock() {
  this.api.onGet(API.GET_VERSIONS).reply(config => promiseResolve(
    config,
    this.db.versions,
  ));
}
