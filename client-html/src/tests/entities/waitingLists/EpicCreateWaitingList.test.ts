import { initialize } from "redux-form";
import { DefaultEpic } from "../../common/Default.Epic";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import { getRecords, setListSelection } from "../../../js/common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../js/common/components/list-view/constants";
import { EpicCreateEntityRecord } from "../../../js/containers/entities/common/epics/EpicCreateEntityRecord";
import { createEntityRecord } from "../../../js/containers/entities/common/actions";

describe("Create waitingList epic tests", () => {
  it("EpicCreateWaitingList should returns correct values", () => DefaultEpic({
    action: mockedApi => createEntityRecord(mockedApi.db.getWaitingList(1), "WaitingList"),
    epic: EpicCreateEntityRecord,
    processData: () => [
      {
        type: FETCH_SUCCESS,
        payload: { message: "WaitingList record created" }
      },
      getRecords("WaitingList", true),
      setListSelection([]),
      initialize(LIST_EDIT_VIEW_FORM_NAME, null)
    ]
  }));
});
