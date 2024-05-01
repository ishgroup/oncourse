/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { FETCH_SUCCESS } from "../../js/common/actions";
import { deleteTag, getAllTags } from "../../js/containers/tags/actions";
import { DefaultEpic } from "../common/Default.Epic";

describe("Delete tag epic tests", () => {
  it("EpicDeleteTag should returns correct values", async () => {
    const { EpicDeleteTag } = await import("../../js/containers/tags/epics/EpicDeleteTag");
    
    return DefaultEpic({
      action: deleteTag({ id: 1 } as any),
      epic: EpicDeleteTag,
      processData: () => {
        const tag = {} as any;

        return [
          {
            type: FETCH_SUCCESS,
            payload: { message: `${tag.name} was successfully deleted` }
          },
          getAllTags()
        ];
      }
    });
  });
});
