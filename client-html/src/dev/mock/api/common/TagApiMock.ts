/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { Tag } from "@api/model";
import { promiseResolve } from "../../MockAdapter";
import { getParamsId } from "../../mockUtils";

export function TagApiMock() {
  /**
   * List items
   * */
  this.api.onGet("/v1/tag/list").reply(config => {
    const tags: Tag[] = this.db.getTags();

    return promiseResolve(config, tags);
  });

  this.api.onGet("/v1/tag/checklist").reply(config => {
    const tags: Tag[] = this.db.getTags();

    return promiseResolve(config, tags);
  });

  this.api.onPost("/v1/tag").reply(config =>
    promiseResolve(config, {}));

  this.api.onPut(new RegExp(`v1/tag/\\d+`)).reply(config =>
    promiseResolve(config, {}));

  this.api.onGet(new RegExp(`v1/tag/\\d+`)).reply(config =>
    promiseResolve(config, this.db.getTag(getParamsId(config))));

  this.api.onDelete(new RegExp(`v1/tag/\\d+`)).reply(config => {
    const id = getParamsId(config);
    const tag = this.db.getTag(id);
    this.db.removeTag(id);
    return promiseResolve(config, tag);
  });
}
