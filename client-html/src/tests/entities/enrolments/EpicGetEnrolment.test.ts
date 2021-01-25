import { initialize } from "redux-form";
import { DefaultEpic } from "../../common/Default.Epic";
import { SET_LIST_EDIT_RECORD } from "../../../js/common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../js/common/components/list-view/constants";
import { EpicGetEnrolment } from "../../../js/containers/entities/enrolments/epics/EpicGetEnrolment";
import { GET_ENROLMENT_ITEM_FULFILLED, getEnrolment } from "../../../js/containers/entities/enrolments/actions";
import { getNoteItems } from "../../../js/common/components/form/notes/actions";

describe("Get enrolment epic tests", () => {
  it("EpicGetEnrolment should returns correct values", () => DefaultEpic({
    action: getEnrolment("1"),
    epic: EpicGetEnrolment,
    processData: mockedApi => {
      const id = 1;
      const enrolment = mockedApi.db.getEnrolment(id);
      if (enrolment.relatedFundingSourceId === null) {
        enrolment.relatedFundingSourceId = -1;
      }
      return [
        {
          type: GET_ENROLMENT_ITEM_FULFILLED,
          payload: { enrolment }
        },
        {
          type: SET_LIST_EDIT_RECORD,
          payload: { editRecord: enrolment, name: enrolment.studentName }
        },
        getNoteItems("Enrolment", "1" as any, LIST_EDIT_VIEW_FORM_NAME),
        initialize(LIST_EDIT_VIEW_FORM_NAME, enrolment),
      ];
    }
  }));
});
