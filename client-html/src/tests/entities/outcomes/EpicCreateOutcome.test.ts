import { initialize } from "redux-form";
import { DefaultEpic } from "../../common/Default.Epic";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import {
  GET_RECORDS_REQUEST,
  setListSelection
} from "../../../js/common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../js/common/components/list-view/constants";
import { EpicCreateOutcome } from "../../../js/containers/entities/outcomes/epics/EpicCreateOutcome";
import { createOutcome } from "../../../js/containers/entities/outcomes/actions";

describe("Create outcome epic tests", () => {
  it("EpicCreateOutcome should returns correct values", () => DefaultEpic({
    action: mockedApi => createOutcome(mockedApi.db.getOutcome(1)),
    epic: EpicCreateOutcome,
    processData: () => [
      {
        type: FETCH_SUCCESS,
        payload: { message: "Outcome created" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "Outcome" }
      },
      setListSelection([]),
      initialize(LIST_EDIT_VIEW_FORM_NAME, null)
    ]
  }));
});
