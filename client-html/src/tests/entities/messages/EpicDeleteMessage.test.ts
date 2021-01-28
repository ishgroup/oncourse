import { initialize } from "redux-form";
import { DefaultEpic } from "../../common/Default.Epic";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import {
  GET_RECORDS_REQUEST,
  setListSelection
} from "../../../js/common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../js/common/components/list-view/constants";
import { EpicDeleteMessage } from "../../../js/containers/entities/messages/epics/EpicDeleteMessage";
import { DELETE_MESSAGE_ITEM_FULFILLED, removeMessage } from "../../../js/containers/entities/messages/actions";

describe("Delete message epic tests", () => {
  it("EpicDeleteMessage should returns correct values", () => DefaultEpic({
    action: removeMessage("1"),
    epic: EpicDeleteMessage,
    processData: () => [
      {
        type: DELETE_MESSAGE_ITEM_FULFILLED
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Message record deleted" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "Message", listUpdate: true }
      },
      setListSelection([]),
      initialize(LIST_EDIT_VIEW_FORM_NAME, null)
    ]
  }));
});
