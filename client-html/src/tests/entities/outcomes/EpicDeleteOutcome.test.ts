import { initialize } from "redux-form";
import { DefaultEpic } from "../../common/Default.Epic";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import {
  GET_RECORDS_REQUEST,
  setListSelection
} from "../../../js/common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../js/common/components/list-view/constants";
import { EpicDeleteOutcome } from "../../../js/containers/entities/outcomes/epics/EpicDeleteOutcome";
import { deleteOutcome } from "../../../js/containers/entities/outcomes/actions";

describe("Delete outcome epic tests", () => {
  it("EpicDeleteOutcome should returns correct values", () => DefaultEpic({
    action: deleteOutcome("1"),
    epic: EpicDeleteOutcome,
    processData: () => [
      {
        type: FETCH_SUCCESS,
        payload: { message: "Outcome deleted" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "Outcome", listUpdate: true }
      },
      setListSelection([]),
      initialize(LIST_EDIT_VIEW_FORM_NAME, null)
    ]
  }));
});
