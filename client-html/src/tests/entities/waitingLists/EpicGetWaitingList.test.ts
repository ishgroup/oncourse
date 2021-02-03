import { initialize } from "redux-form";
import { DefaultEpic } from "../../common/Default.Epic";
import { SET_LIST_EDIT_RECORD } from "../../../js/common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../js/common/components/list-view/constants";
import { EpicGetWaitingList } from "../../../js/containers/entities/waitingLists/epics/EpicGetWaitingList";
import { GET_WAITING_LIST_ITEM_FULFILLED, getWaitingList } from "../../../js/containers/entities/waitingLists/actions";

describe("Get waitingList epic tests", () => {
  it("EpicGetWaitingList should returns correct values", () => DefaultEpic({
    action: getWaitingList("1"),
    epic: EpicGetWaitingList,
    processData: mockedApi => {
      const waitingList = mockedApi.db.getWaitingList(1);
      return [
        {
          type: GET_WAITING_LIST_ITEM_FULFILLED
        },
        {
          type: SET_LIST_EDIT_RECORD,
          payload: { editRecord: waitingList, name: waitingList.courseName }
        },
        initialize(LIST_EDIT_VIEW_FORM_NAME, waitingList)
      ];
    }
  }));
});
