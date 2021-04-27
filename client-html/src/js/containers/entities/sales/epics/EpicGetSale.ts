import { ProductItem } from "@api/model";
import { Epic } from "redux-observable";
import { initialize } from "redux-form";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { GET_SALE, GET_SALE_FULFILLED } from "../actions";
import SaleService from "../services/SaleService";
import { SET_LIST_EDIT_RECORD } from "../../../../common/components/list-view/actions";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";

const request: EpicUtils.Request<any, { id: string }> = {
  type: GET_SALE,
  getData: payload => SaleService.getSale(Number(payload.id)),
  processData: (productItem: ProductItem) => [
      {
        type: GET_SALE_FULFILLED,
        payload: { productItem }
      },
      {
        type: SET_LIST_EDIT_RECORD,
        payload: { editRecord: productItem, name: productItem.productName }
      },
      initialize(LIST_EDIT_VIEW_FORM_NAME, productItem)
    ],
  processError: response => [...FetchErrorHandler(response), initialize(LIST_EDIT_VIEW_FORM_NAME, null)]
};

export const EpicGetSale: Epic<any, any> = EpicUtils.Create(request);
