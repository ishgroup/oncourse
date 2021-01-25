import { initialize } from "redux-form";
import { DefaultEpic } from "../../common/Default.Epic";
import { SET_LIST_EDIT_RECORD } from "../../../js/common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../js/common/components/list-view/constants";
import { EpicGetMembershipProduct } from "../../../js/containers/entities/membershipProducts/epics/EpicGetMembershipProduct";
import {
  GET_MEMBERSHIP_PRODUCT_ITEM_FULFILLED,
  getMembershipProduct
} from "../../../js/containers/entities/membershipProducts/actions";

describe("Get membership product epic tests", () => {
  it("EpicGetMembershipProduct should returns correct values", () => DefaultEpic({
    action: getMembershipProduct("1"),
    epic: EpicGetMembershipProduct,
    processData: mockedApi => {
      const membershipProduct = mockedApi.db.getMembershipProduct(1);
      return [
        {
          type: GET_MEMBERSHIP_PRODUCT_ITEM_FULFILLED,
          payload: { membershipProduct }
        },
        {
          type: SET_LIST_EDIT_RECORD,
          payload: { editRecord: membershipProduct, name: membershipProduct.name }
        },
        initialize(LIST_EDIT_VIEW_FORM_NAME, membershipProduct)
      ];
    }
  }));
});
