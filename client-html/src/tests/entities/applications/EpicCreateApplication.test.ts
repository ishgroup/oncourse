import { initialize } from "redux-form";
import { DefaultEpic } from "../../common/Default.Epic";
import {
  CREATE_APPLICATION_ITEM_FULFILLED,
  createApplication
} from "../../../js/containers/entities/applications/actions";
import { EpicCreateApplication } from "../../../js/containers/entities/applications/epics/EpicCreateApplication";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import { GET_RECORDS_REQUEST, setListSelection } from "../../../js/common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../js/common/components/list-view/constants";

describe("Create application epic tests", () => {
  it("EpicCreateApplication should returns correct values", () => DefaultEpic({
    action: mockedApi => createApplication(mockedApi.db.mockedCreateApplication()),
    epic: EpicCreateApplication,
    processData: () => [
      {
        type: CREATE_APPLICATION_ITEM_FULFILLED
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Application Record created" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "Application" }
      },
      setListSelection([]),
      initialize(LIST_EDIT_VIEW_FORM_NAME, null)
    ]
  }));
});
