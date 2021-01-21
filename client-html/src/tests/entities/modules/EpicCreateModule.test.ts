import { initialize } from "redux-form";
import { DefaultEpic } from "../../common/Default.Epic";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import {
  clearListNestedEditRecord,
  GET_RECORDS_REQUEST,
  setListSelection
} from "../../../js/common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../js/common/components/list-view/constants";
import { EpicCreateModule } from "../../../js/containers/entities/modules/epics/EpicCreateModule";
import { CREATE_MODULE_ITEM_FULFILLED, createModule } from "../../../js/containers/entities/modules/actions";

describe("Create module epic tests", () => {
  it("EpicCreateModule should returns correct values", () => DefaultEpic({
    action: mockedApi => createModule(mockedApi.db.createAndUpdateModule()),
    epic: EpicCreateModule,
    processData: () => [
      {
        type: CREATE_MODULE_ITEM_FULFILLED
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Module Record created" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "Module" }
      },
      setListSelection([]),
      clearListNestedEditRecord(0),
      initialize(LIST_EDIT_VIEW_FORM_NAME, null)
    ]
  }));
});
