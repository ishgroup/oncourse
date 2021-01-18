import { initialize } from "redux-form";
import { DefaultEpic } from "../../common/Default.Epic";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import {
  GET_RECORDS_REQUEST,
  setListSelection
} from "../../../js/common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../js/common/components/list-view/constants";
import { EpicDeleteDiscount } from "../../../js/containers/entities/discounts/epics/EpicDeleteDiscount";
import { DELETE_DISCOUNT_ITEM_FULFILLED, removeDiscount } from "../../../js/containers/entities/discounts/actions";

describe("Delete discount epic tests", () => {
  it("EpicDeleteDiscount should returns correct values", () => DefaultEpic({
    action: removeDiscount("1"),
    epic: EpicDeleteDiscount,
    processData: () => [
      {
        type: DELETE_DISCOUNT_ITEM_FULFILLED
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Discount record deleted" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "Discount", listUpdate: true }
      },
      setListSelection([]),
      initialize(LIST_EDIT_VIEW_FORM_NAME, null)
    ]
  }));
});
