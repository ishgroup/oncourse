import { initialize } from "redux-form";
import { DefaultEpic } from "../../common/Default.Epic";
import { SET_LIST_EDIT_RECORD } from "../../../js/common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../js/common/components/list-view/constants";
import { EpicGetOutcome } from "../../../js/containers/entities/outcomes/epics/EpicGetOutcome";
import { getOutcome } from "../../../js/containers/entities/outcomes/actions";

describe("Get outcome epic tests", () => {
  it("EpicGetOutcome should returns correct values", () => DefaultEpic({
    action: getOutcome("1"),
    epic: EpicGetOutcome,
    processData: mockedApi => {
      const outcome = mockedApi.db.getOutcome(1);
      return [
        {
          type: SET_LIST_EDIT_RECORD,
          payload: { editRecord: outcome, name: outcome.studentName }
        },
        initialize(LIST_EDIT_VIEW_FORM_NAME, outcome)
      ];
    }
  }));
});
