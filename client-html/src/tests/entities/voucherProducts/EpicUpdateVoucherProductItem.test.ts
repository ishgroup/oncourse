import { DefaultEpic } from "../../common/Default.Epic";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import { GET_RECORDS_REQUEST } from "../../../js/common/components/list-view/actions";
import { EpicUpdateVoucherProductItem } from "../../../js/containers/entities/voucherProducts/epics/EpicUpdateVoucherProductItem";
import {
  UPDATE_VOUCHER_PRODUCT_ITEM_FULFILLED,
  updateVoucherProduct
} from "../../../js/containers/entities/voucherProducts/actions";

describe("Update voucherProduct epic tests", () => {
  it("EpicUpdateVoucherProductItem should returns correct values", () => DefaultEpic({
    action: mockedApi => updateVoucherProduct("1", mockedApi.db.getVoucherProduct(1)),
    epic: EpicUpdateVoucherProductItem,
    processData: () => [
      {
        type: UPDATE_VOUCHER_PRODUCT_ITEM_FULFILLED
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Voucher Product Record updated" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "VoucherProduct", listUpdate: true, savedID: "1" }
      }
    ]
  }));
});
