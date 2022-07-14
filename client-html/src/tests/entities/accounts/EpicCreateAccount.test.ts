import { initialize } from "redux-form";
import { DefaultEpic } from "../../common/Default.Epic";
import { EpicCreateEntityRecord } from "../../../js/containers/entities/common/epics/EpicCreateEntityRecord";
import { createEntityRecord } from "../../../js/containers/entities/common/actions";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import { getRecords, setListSelection } from "../../../js/common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../js/common/components/list-view/constants";

describe("Create account epic tests", () => {
  it("EpicCreateAccount should returns correct values", () => DefaultEpic({
    action: mockedApi => createEntityRecord("Account", mockedApi.db.createNewAccount()),
    epic: EpicCreateEntityRecord,
    processData: () => [
      {
        type: FETCH_SUCCESS,
        payload: { message: "Account record created" }
      },
      getRecords("Account", true),
      setListSelection([]),
      initialize(LIST_EDIT_VIEW_FORM_NAME, null)
    ]
  }));
});
