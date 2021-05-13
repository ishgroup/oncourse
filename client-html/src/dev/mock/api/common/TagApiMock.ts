import { Tag } from "@api/model";
import { promiseResolve } from "../../MockAdapter";
import { getParamsId } from "../../mockUtils";

export function TagApiMock(mock) {
  /**
   * List items
   * */
  this.api.onGet("/v1/tag").reply(config => {
    const tags: Tag[] = this.db.getTags();

    return promiseResolve(config, tags);
  });

  this.api.onPost("/v1/tag").reply(config =>
    promiseResolve(config, {}));

  this.api.onPut(new RegExp(`v1/tag/\\d+`)).reply(config =>
    promiseResolve(config, {}));

  this.api.onDelete(new RegExp(`v1/tag/\\d+`)).reply(config => {
    const id = getParamsId(config);
    this.db.removeTag(id);
    return promiseResolve(config, {});
  });
}
