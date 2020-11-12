import { promiseResolve } from "../../MockAdapter";
import { Filter } from "@api/model";

export function filterApiMock(mock) {
  /**
   * List items
   **/
  this.api.onGet("/v1/filter").reply(config => {
    const { entity } = config.params;
    const filters: Filter[] = this.db.getFilters(entity);

    return promiseResolve(config, filters);
  });
}
