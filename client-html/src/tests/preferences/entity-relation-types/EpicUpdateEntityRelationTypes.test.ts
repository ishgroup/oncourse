import { DefaultEpic } from "../../common/Default.Epic";
import {
  UPDATE_ENTITY_RELATION_TYPES_FULFILLED,
  updateEntityRelationTypes
} from "../../../js/containers/preferences/actions";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import { EpicUpdateEntityRelationTypes } from "../../../js/containers/preferences/containers/entity-relation-types/epics/EpicUpdateEntityRelationTypes";

describe("Update entity relation types epic tests", () => {
  it("EpicUpdateEntityRelationTypes should returns correct values", () => DefaultEpic({
    action: mockedApi => updateEntityRelationTypes(mockedApi.db.getEntityRelationTypes()),
    epic: EpicUpdateEntityRelationTypes,
    processData: mockedApi => {
      const entityRelationTypes = mockedApi.db.getEntityRelationTypes();

      return [
        {
          type: UPDATE_ENTITY_RELATION_TYPES_FULFILLED,
          payload: { entityRelationTypes }
        },
        {
          type: FETCH_SUCCESS,
          payload: { message: "Sellable items were successfully updated" }
        }
      ];
    }
  }));
});
