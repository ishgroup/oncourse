import { initialize } from "redux-form";
import { DefaultEpic } from "../../common/Default.Epic";
import {
  DELETE_APPLICATION_ITEM_FULFILLED,
  removeApplication
} from "../../../js/containers/entities/applications/actions";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import { GET_RECORDS_REQUEST, setListSelection } from "../../../js/common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../js/common/components/list-view/constants";
import { EpicDeleteApplication } from "../../../js/containers/entities/applications/epics/EpicDeleteApplication";

describe("Delete application epic tests", () => {
  it("EpicDeleteApplication should returns correct values", () => DefaultEpic({
    action: removeApplication("1"),
    epic: EpicDeleteApplication,
    processData: () => [
      {
        type: DELETE_APPLICATION_ITEM_FULFILLED
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Application record deleted" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "Application", listUpdate: true }
      },
      setListSelection([]),
      initialize(LIST_EDIT_VIEW_FORM_NAME, null)
    ]
  }));
});
