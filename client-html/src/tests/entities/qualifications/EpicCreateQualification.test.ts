import { initialize } from "redux-form";
import { DefaultEpic } from "../../common/Default.Epic";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import {
  clearListNestedEditRecord,
  GET_RECORDS_REQUEST,
  setListSelection
} from "../../../js/common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../js/common/components/list-view/constants";
import { EpicCreateQualification } from "../../../js/containers/entities/qualifications/epics/EpicCreateQualification";
import {
  CREATE_QUALIFICATION_ITEM_FULFILLED,
  createQualification
} from "../../../js/containers/entities/qualifications/actions";

describe("Create qualification epic tests", () => {
  it("EpicCreateQualification should returns correct values", () => DefaultEpic({
    action: mockedApi => createQualification(mockedApi.db.getQualification(1)),
    epic: EpicCreateQualification,
    processData: () => [
      {
        type: CREATE_QUALIFICATION_ITEM_FULFILLED
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "New Qualification created" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "Qualification" }
      },
      setListSelection([]),
      clearListNestedEditRecord(0),
      initialize(LIST_EDIT_VIEW_FORM_NAME, null)
    ]
  }));
});
