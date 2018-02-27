import {promiseReject, promiseResolve} from "../MockAdapter";
import {API} from "../../../js/constants/Config";

export function versionApiMock() {
  this.api.onGet(API.VERSION).reply(config => promiseResolve(
    config,
    this.db.versions,
  ));

  this.api.onPatch(API.VERSION_UPDATE).reply(config => {
    return promiseResolve(
      '',
      null,
    );
  });
}
