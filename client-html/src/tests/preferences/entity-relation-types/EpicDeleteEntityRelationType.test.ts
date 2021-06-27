import { DefaultEpic } from "../../common/Default.Epic";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import {
  DELETE_ENTITY_RELATION_TYPE_FULFILLED,
  deleteEntityRelationType
} from "../../../js/containers/preferences/actions";
import {
  EpicDeleteEntityRelationType
} from "../../../js/containers/preferences/containers/entity-relation-types/epics/EpicDeleteEntityRelationType";

const id = "2";

describe("Delete entity relation type epic tests", () => {
  it("EpicDeleteEntityRelationType should returns correct values", () => DefaultEpic({
    action: deleteEntityRelationType(id),
    epic: EpicDeleteEntityRelationType,
    processData: mockedApi => {
      const entityRelationTypes = mockedApi.db.entityRelationTypes.filter(type => Number(type.id) !== Number(id));

      return [
        {
          type: DELETE_ENTITY_RELATION_TYPE_FULFILLED,
          payload: { entityRelationTypes }
        },
        {
          type: FETCH_SUCCESS,
          payload: { message: "Sellable item was successfully deleted" }
        }
      ];
    }
  }));
});
