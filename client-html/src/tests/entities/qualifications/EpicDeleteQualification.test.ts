import { initialize } from "redux-form";
import { DefaultEpic } from "../../common/Default.Epic";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import {
  GET_RECORDS_REQUEST,
  setListSelection
} from "../../../js/common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../js/common/components/list-view/constants";
import { EpicDeleteQualification } from "../../../js/containers/entities/qualifications/epics/EpicDeleteQualification";
import {
  DELETE_QUALIFICATION_ITEM_FULFILLED,
  removeQualification
} from "../../../js/containers/entities/qualifications/actions";

describe("Delete qualification epic tests", () => {
  it("EpicDeleteQualification should returns correct values", () => DefaultEpic({
    action: removeQualification("1"),
    epic: EpicDeleteQualification,
    processData: () => [
      {
        type: DELETE_QUALIFICATION_ITEM_FULFILLED
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Qualification deleted" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "Qualification", listUpdate: true }
      },
      setListSelection([]),
      initialize(LIST_EDIT_VIEW_FORM_NAME, null)
    ]
  }));
});
