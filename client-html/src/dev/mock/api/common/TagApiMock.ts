import { promiseResolve } from "../../MockAdapter";
import { Tag } from "@api/model";

export function TagApiMock(mock) {
  /**
   * List items
   **/
  this.api.onGet("/v1/tag").reply(config => {
    const tags: Tag[] = this.db.getTags();

    return promiseResolve(config, tags);
  });
}
