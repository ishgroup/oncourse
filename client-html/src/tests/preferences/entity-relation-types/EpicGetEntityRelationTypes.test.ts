import { DefaultEpic } from "../../common/Default.Epic";
import {
  GET_ENTITY_RELATION_TYPES_FULFILLED,
  getEntityRelationTypes
} from "../../../js/containers/preferences/actions";
import {
  EpicGetEntityRelationTypes
} from "../../../js/containers/preferences/containers/entity-relation-types/epics/EpicGetEntityRelationTypes";

describe("Get entity relation types epic tests", () => {
  it("EpicGetEntityRelationTypes should returns correct values", () => DefaultEpic({
    action: getEntityRelationTypes(),
    epic: EpicGetEntityRelationTypes,
    processData: mockedApi => {
      const entityRelationTypes = mockedApi.db.getEntityRelationTypes();

      return [
        {
          type: GET_ENTITY_RELATION_TYPES_FULFILLED,
          payload: { entityRelationTypes }
        }
      ];
    }
  }));
});
