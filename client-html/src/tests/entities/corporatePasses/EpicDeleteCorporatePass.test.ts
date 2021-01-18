import { initialize } from "redux-form";
import { DefaultEpic } from "../../common/Default.Epic";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import {
  GET_RECORDS_REQUEST,
  setListSelection
} from "../../../js/common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../js/common/components/list-view/constants";
import { EpicDeleteCorporatePass } from "../../../js/containers/entities/corporatePasses/epics/EpicDeleteCorporatePass";
import {
  DELETE_CORPORATE_PASS_ITEM_FULFILLED,
  removeCorporatePass
} from "../../../js/containers/entities/corporatePasses/actions";

describe("Delete corporate pass epic tests", () => {
  it("EpicDeleteCorporatePass should returns correct values", () => DefaultEpic({
    action: removeCorporatePass("1"),
    epic: EpicDeleteCorporatePass,
    processData: () => [
      {
        type: DELETE_CORPORATE_PASS_ITEM_FULFILLED
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "CorporatePass record deleted" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "CorporatePass", listUpdate: true }
      },
      setListSelection([]),
      initialize(LIST_EDIT_VIEW_FORM_NAME, null)
    ]
  }));
});
