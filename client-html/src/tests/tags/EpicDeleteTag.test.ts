/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { DefaultEpic } from "../common/Default.Epic";
import {
  getAllTags,
  deleteTag
} from "../../js/containers/tags/actions";
import { FETCH_SUCCESS } from "../../js/common/actions";
import { EpicDeleteTag } from "../../js/containers/tags/epics/EpicDeleteTag";

describe("Delete tag epic tests", () => {
  it("EpicDeleteTag should returns correct values", () => DefaultEpic({
    action: deleteTag({ id: 1 } as any),
    epic: EpicDeleteTag,
    processData: () => {
      const tag = {} as any;

      return [
        {
          type: FETCH_SUCCESS,
          payload: {message: `${tag.type} was successfully deleted`}
        },
        getAllTags()
      ];
    }
  }));
});
