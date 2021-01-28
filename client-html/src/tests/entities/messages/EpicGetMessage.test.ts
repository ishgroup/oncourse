import { initialize } from "redux-form";
import { DefaultEpic } from "../../common/Default.Epic";
import { SET_LIST_EDIT_RECORD } from "../../../js/common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../js/common/components/list-view/constants";
import { EpicGetMessage } from "../../../js/containers/entities/messages/epics/EpicGetMessage";
import { GET_MESSAGE_ITEM_FULFILLED, getMessage } from "../../../js/containers/entities/messages/actions";

describe("Get message epic tests", () => {
  it("EpicGetMessage should returns correct values", () => DefaultEpic({
    action: getMessage("1"),
    epic: EpicGetMessage,
    processData: mockedApi => {
      const message = mockedApi.db.getMessage(1);
      return [
        {
          type: GET_MESSAGE_ITEM_FULFILLED,
          payload: { message }
        },
        {
          type: SET_LIST_EDIT_RECORD,
          payload: { editRecord: message, name: `${message.sentToContactFullname} (${message.subject})` }
        },
        initialize(LIST_EDIT_VIEW_FORM_NAME, message)
      ];
    }
  }));
});
