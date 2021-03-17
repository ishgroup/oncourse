import { DefaultEpic } from "../../common/Default.Epic";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import { GET_RECORDS_REQUEST } from "../../../js/common/components/list-view/actions";
import {
  UPDATE_DISCOUNT_ITEM_FULFILLED,
  updateDiscount
} from "../../../js/containers/entities/discounts/actions";
import { EpicUpdateDiscount } from "../../../js/containers/entities/discounts/epics/EpicUpdateDiscount";

describe("Update discount epic tests", () => {
  it("EpicUpdateDiscount should returns correct values", () => {
    const id = "2";
    return DefaultEpic({
      action: mockedApi => updateDiscount(id, mockedApi.db.createAndUpdateDiscount(id)),
      epic: EpicUpdateDiscount,
      processData: () => [
        {
          type: UPDATE_DISCOUNT_ITEM_FULFILLED
        },
        {
          type: FETCH_SUCCESS,
          payload: { message: "Discount Record updated" }
        },
        {
          type: GET_RECORDS_REQUEST,
          payload: { entity: "Discount", listUpdate: true, savedID: id }
        }
      ]
    });
  });
});
