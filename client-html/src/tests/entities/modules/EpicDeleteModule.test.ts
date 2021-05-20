import { initialize } from "redux-form";
import { DefaultEpic } from "../../common/Default.Epic";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import {
  GET_RECORDS_REQUEST,
  setListSelection
} from "../../../js/common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../js/common/components/list-view/constants";
import { EpicDeleteModule } from "../../../js/containers/entities/modules/epics/EpicDeleteModule";
import { DELETE_MODULE_ITEM_FULFILLED, removeModule } from "../../../js/containers/entities/modules/actions";

describe("Delete module epic tests", () => {
  it("EpicDeleteModule should returns correct values", () => DefaultEpic({
    action: removeModule("1"),
    epic: EpicDeleteModule,
    processData: () => [
      {
        type: DELETE_MODULE_ITEM_FULFILLED
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Module record deleted" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "Module", listUpdate: true }
      },
      setListSelection([]),
      initialize(LIST_EDIT_VIEW_FORM_NAME, null)
    ]
  }));
});
