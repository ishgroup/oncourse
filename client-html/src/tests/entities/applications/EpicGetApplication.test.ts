import { initialize } from "redux-form";
import { DefaultEpic } from "../../common/Default.Epic";
import {
  GET_APPLICATION_ITEM_FULFILLED,
  getApplication
} from "../../../js/containers/entities/applications/actions";
import { SET_LIST_EDIT_RECORD } from "../../../js/common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../js/common/components/list-view/constants";
import { EpicGetApplication } from "../../../js/containers/entities/applications/epics/EpicGetApplication";
import { getNoteItems } from "../../../js/common/components/form/notes/actions";

describe("Get application epic tests", () => {
  it("EpicGetApplication should returns correct values", () => DefaultEpic({
    action: getApplication("1"),
    epic: EpicGetApplication,
    processData: mockedApi => {
      const application = mockedApi.db.getApplication(1);
      return [
        {
          type: GET_APPLICATION_ITEM_FULFILLED,
          payload: { application }
        },
        {
          type: SET_LIST_EDIT_RECORD,
          payload: { editRecord: application, name: application.courseName }
        },
        getNoteItems("Application", "1" as any, LIST_EDIT_VIEW_FORM_NAME),
        initialize(LIST_EDIT_VIEW_FORM_NAME, application)
      ];
    }
  }));
});
