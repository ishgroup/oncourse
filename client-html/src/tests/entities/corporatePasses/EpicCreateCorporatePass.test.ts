import { initialize } from "redux-form";
import { DefaultEpic } from "../../common/Default.Epic";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import {
  clearListNestedEditRecord,
  GET_RECORDS_REQUEST,
  setListSelection
} from "../../../js/common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../js/common/components/list-view/constants";
import { EpicCreateCorporatePass } from "../../../js/containers/entities/corporatePasses/epics/EpicCreateCorporatePass";
import {
  CREATE_CORPORATE_PASS_ITEM_FULFILLED,
  createCorporatePass
} from "../../../js/containers/entities/corporatePasses/actions";

describe("Create corporate pass epic tests", () => {
  it("EpicCreateCorporatePass should returns correct values", () => DefaultEpic({
    action: mockedApi => createCorporatePass(mockedApi.db.createNewCorporatePasses()),
    epic: EpicCreateCorporatePass,
    processData: () => [
      {
        type: CREATE_CORPORATE_PASS_ITEM_FULFILLED
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "New CorporatePass created" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "CorporatePass" }
      },
      setListSelection([]),
      clearListNestedEditRecord(0),
      initialize(LIST_EDIT_VIEW_FORM_NAME, null)
    ]
  }));
});
