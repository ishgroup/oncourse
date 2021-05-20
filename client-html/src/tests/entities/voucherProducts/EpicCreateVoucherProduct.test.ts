import { initialize } from "redux-form";
import { DefaultEpic } from "../../common/Default.Epic";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import {
  clearListNestedEditRecord,
  GET_RECORDS_REQUEST,
  setListSelection
} from "../../../js/common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../js/common/components/list-view/constants";
import { EpicCreateVoucherProduct } from "../../../js/containers/entities/voucherProducts/epics/EpicCreateVoucherProduct";
import {
  CREATE_VOUCHER_PRODUCT_ITEM_FULFILLED,
  createVoucherProduct
} from "../../../js/containers/entities/voucherProducts/actions";

describe("Create voucherProduct epic tests", () => {
  it("EpicCreateVoucherProduct should returns correct values", () => DefaultEpic({
    action: mockedApi => createVoucherProduct(mockedApi.db.getVoucherProduct(1)),
    epic: EpicCreateVoucherProduct,
    processData: () => [
      {
        type: CREATE_VOUCHER_PRODUCT_ITEM_FULFILLED
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Voucher Product Record created" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "VoucherProduct" }
      },
      setListSelection([]),
      clearListNestedEditRecord(0),
      initialize(LIST_EDIT_VIEW_FORM_NAME, null)
    ]
  }));
});
