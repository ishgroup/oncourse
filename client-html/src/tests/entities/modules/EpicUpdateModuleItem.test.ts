import { DefaultEpic } from "../../common/Default.Epic";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import { GET_RECORDS_REQUEST } from "../../../js/common/components/list-view/actions";
import { EpicUpdateModuleItem } from "../../../js/containers/entities/modules/epics/EpicUpdateModuleItem";
import {
  GET_MODULE_ITEM,
  UPDATE_MODULE_ITEM_FULFILLED,
  updateModule
} from "../../../js/containers/entities/modules/actions";

describe("Update module epic tests", () => {
  it("EpicUpdateModuleItem should returns correct values", () => DefaultEpic({
    action: mockedApi => updateModule("1", mockedApi.db.createAndUpdateModule(1)),
    epic: EpicUpdateModuleItem,
    processData: () => [
      {
        type: UPDATE_MODULE_ITEM_FULFILLED
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Module Record updated" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "Module", listUpdate: true, savedID: "1" }
      },
      {
        type: GET_MODULE_ITEM,
        payload: "1"
      }
    ]
  }));
});
