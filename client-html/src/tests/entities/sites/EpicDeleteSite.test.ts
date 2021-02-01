import { initialize } from "redux-form";
import { DefaultEpic } from "../../common/Default.Epic";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import {
  GET_RECORDS_REQUEST,
  setListSelection
} from "../../../js/common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../js/common/components/list-view/constants";
import { EpicDeleteSite } from "../../../js/containers/entities/sites/epics/EpicDeleteSite";
import { DELETE_SITE_ITEM_FULFILLED, removeSite } from "../../../js/containers/entities/sites/actions";

describe("Delete site epic tests", () => {
  it("EpicDeleteSite should returns correct values", () => DefaultEpic({
    action: removeSite("1"),
    epic: EpicDeleteSite,
    processData: () => [
      {
        type: DELETE_SITE_ITEM_FULFILLED
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Site deleted" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "Site", listUpdate: true }
      },
      setListSelection([]),
      initialize(LIST_EDIT_VIEW_FORM_NAME, null)
    ]
  }));
});
