import { DefaultEpic } from "../../common/Default.Epic";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import { GET_RECORDS_REQUEST } from "../../../js/common/components/list-view/actions";
import { EpicUpdateSale } from "../../../js/containers/entities/sales/epics/EpicUpdateSale";
import { GET_SALE, UPDATE_SALE_FULFILLED, updateSale } from "../../../js/containers/entities/sales/actions";

describe("Update sale epic tests", () => {
  it("EpicUpdateSale should returns correct values", () => DefaultEpic({
    action: mockedApi => updateSale("1", mockedApi.db.getProductItem(1)),
    epic: EpicUpdateSale,
    processData: () => [
      {
        type: UPDATE_SALE_FULFILLED
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Sale Record updated" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "ProductItem", listUpdate: true, savedID: "1" }
      },
      {
        type: GET_SALE,
        payload: { id: "1" }
      }
    ]
  }));
});
