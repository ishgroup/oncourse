import { initialize } from "redux-form";
import { DefaultEpic } from "../../common/Default.Epic";
import { SET_LIST_EDIT_RECORD } from "../../../js/common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../js/common/components/list-view/constants";
import { EpicGetSale } from "../../../js/containers/entities/sales/epics/EpicGetSale";
import { GET_SALE_FULFILLED, getSale } from "../../../js/containers/entities/sales/actions";

describe("Get sale epic tests", () => {
  it("EpicGetSale should returns correct values", () => DefaultEpic({
    action: getSale("1"),
    epic: EpicGetSale,
    processData: mockedApi => {
      const productItem = mockedApi.db.getProductItem(1);
      return [
        {
          type: GET_SALE_FULFILLED,
          payload: { productItem }
        },
        {
          type: SET_LIST_EDIT_RECORD,
          payload: { editRecord: productItem, name: productItem.productName }
        },
        initialize(LIST_EDIT_VIEW_FORM_NAME, productItem)
      ];
    }
  }));
});
