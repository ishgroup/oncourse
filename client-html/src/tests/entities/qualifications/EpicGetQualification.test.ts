import { initialize } from "redux-form";
import { DefaultEpic } from "../../common/Default.Epic";
import { SET_LIST_EDIT_RECORD } from "../../../js/common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../js/common/components/list-view/constants";
import { EpicGetQualification } from "../../../js/containers/entities/qualifications/epics/EpicGetQualification";
import {
  GET_QUALIFICATION_ITEM_FULFILLED,
  getQualification
} from "../../../js/containers/entities/qualifications/actions";

describe("Get qualification epic tests", () => {
  it("EpicGetQualification should returns correct values", () => DefaultEpic({
    action: getQualification("1"),
    epic: EpicGetQualification,
    processData: mockedApi => {
      const qualification = mockedApi.db.getQualification(1);
      return [
        {
          type: GET_QUALIFICATION_ITEM_FULFILLED,
          payload: { qualification }
        },
        {
          type: SET_LIST_EDIT_RECORD,
          payload: { editRecord: qualification, name: qualification.title }
        },
        initialize(LIST_EDIT_VIEW_FORM_NAME, qualification)
      ];
    }
  }));
});
