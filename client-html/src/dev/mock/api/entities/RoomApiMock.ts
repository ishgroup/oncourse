import { promiseResolve } from "../../MockAdapter";
import { getParamsId } from "../../mockUtils";

export function RoomApiMock(mock) {
  this.api.onGet(new RegExp(`v1/list/entity/room/\\d+`)).reply(config => {
    const id = getParamsId(config);
    return promiseResolve(config, this.db.getRoom(id));
  });

  this.api.onPut(new RegExp(`v1/list/entity/room/\\d+`)).reply(config => promiseResolve(config, JSON.parse(config.data)));

  this.api.onPost("v1/list/entity/room").reply(config => {
    this.db.createRoom(config.data);
    return promiseResolve(config, {});
  });
}
