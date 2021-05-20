import { DefaultEpic } from "../common/Default.Epic";
import {
  GET_ENTITY_TAGS_REQUEST_FULFILLED,
  getEntityTags
} from "../../js/containers/tags/actions";
import { EpicGetEntityTags } from "../../js/containers/tags/epics/EpicGetEntityTags";

const entityName: string = "Course";

describe("Get entity tags epic tests", () => {
  it("EpicGetEntityTags should returns correct values", () => DefaultEpic({
    action: getEntityTags(entityName),
    epic: EpicGetEntityTags,
    processData: mockedApi => {
      const tags = mockedApi.db.getTags();

      return [
        {
          type: GET_ENTITY_TAGS_REQUEST_FULFILLED,
          payload: { tags, entityName }
        }
      ];
    }
  }));
});
