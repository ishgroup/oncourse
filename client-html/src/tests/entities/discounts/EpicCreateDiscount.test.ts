import { initialize } from "redux-form";
import { DefaultEpic } from "../../common/Default.Epic";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import {
  clearListNestedEditRecord,
  GET_RECORDS_REQUEST,
  setListSelection
} from "../../../js/common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../js/common/components/list-view/constants";
import { EpicCreateDiscount } from "../../../js/containers/entities/discounts/epics/EpicCreateDiscount";
import { CREATE_DISCOUNT_ITEM_FULFILLED, createDiscount } from "../../../js/containers/entities/discounts/actions";

describe("Create discount epic tests", () => {
  it("EpicCreateDiscount should returns correct values", () => DefaultEpic({
    action: mockedApi => createDiscount(mockedApi.db.createAndUpdateDiscount()),
    epic: EpicCreateDiscount,
    processData: () => [
      {
        type: CREATE_DISCOUNT_ITEM_FULFILLED
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "New Discount created" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "Discount" }
      },
      setListSelection([]),
      clearListNestedEditRecord(0),
      initialize(LIST_EDIT_VIEW_FORM_NAME, null)
    ]
  }));
});
