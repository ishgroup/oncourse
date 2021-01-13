import { initialize } from "redux-form";
import { DefaultEpic } from "../../common/Default.Epic";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import {
  GET_RECORDS_REQUEST,
  setListSelection
} from "../../../js/common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../js/common/components/list-view/constants";
import { EpicDeleteBanking } from "../../../js/containers/entities/bankings/epics/EpicDeleteBanking";
import { DELETE_BANKING_ITEM_FULFILLED, removeBanking } from "../../../js/containers/entities/bankings/actions";

describe("Delete banking epic tests", () => {
  it("EpicDeleteBanking should returns correct values", () => DefaultEpic({
    action: removeBanking("1"),
    epic: EpicDeleteBanking,
    processData: api => {
      const banking = api.db.getBankings();
      return [
        {
          type: DELETE_BANKING_ITEM_FULFILLED,
          payload: { banking }
        },
        {
          type: FETCH_SUCCESS,
          payload: { message: "Banking record deleted" }
        },
        {
          type: GET_RECORDS_REQUEST,
          payload: { entity: "Banking", listUpdate: true }
        },
        setListSelection([]),
        initialize(LIST_EDIT_VIEW_FORM_NAME, null)
      ];
    }
  }));
});
