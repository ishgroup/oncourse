import { initialize } from "redux-form";
import { DefaultEpic } from "../../common/Default.Epic";
import { SET_LIST_EDIT_RECORD } from "../../../js/common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../js/common/components/list-view/constants";
import { EpicGetVoucherProduct } from "../../../js/containers/entities/voucherProducts/epics/EpicGetVoucherProduct";
import {
  GET_VOUCHER_PRODUCT_ITEM_FULFILLED,
  getVoucherProduct
} from "../../../js/containers/entities/voucherProducts/actions";

describe("Get voucherProduct epic tests", () => {
  it("EpicGetVoucherProduct should returns correct values", () => DefaultEpic({
    action: getVoucherProduct("1"),
    epic: EpicGetVoucherProduct,
    processData: mockedApi => {
      const voucherProduct = mockedApi.db.getVoucherProduct(1);
      return [
        {
          type: GET_VOUCHER_PRODUCT_ITEM_FULFILLED,
          payload: { voucherProduct }
        },
        {
          type: SET_LIST_EDIT_RECORD,
          payload: { editRecord: voucherProduct, name: voucherProduct.name }
        },
        initialize(LIST_EDIT_VIEW_FORM_NAME, voucherProduct)
      ];
    }
  }));
});
