import { initialize } from "redux-form";
import { DefaultEpic } from "../../common/Default.Epic";
import { SET_LIST_EDIT_RECORD } from "../../../js/common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../js/common/components/list-view/constants";
import { EpicGetDiscount } from "../../../js/containers/entities/discounts/epics/EpicGetDiscount";
import { GET_DISCOUNT_ITEM_FULFILLED, getDiscount } from "../../../js/containers/entities/discounts/actions";

describe("Get discount epic tests", () => {
  it("EpicGetDiscount should returns correct values", () => DefaultEpic({
    action: getDiscount("1"),
    epic: EpicGetDiscount,
    processData: mockedApi => {
      const discount = mockedApi.db.getDiscount(1);
      discount.discountMemberships.forEach(el => {
        el.contactRelations = el.contactRelations.sort();
      });
      return [
        {
          type: GET_DISCOUNT_ITEM_FULFILLED
        },
        {
          type: SET_LIST_EDIT_RECORD,
          payload: { editRecord: discount, name: discount.name }
        },
        initialize(LIST_EDIT_VIEW_FORM_NAME, discount)
      ];
    }
  }));
});
