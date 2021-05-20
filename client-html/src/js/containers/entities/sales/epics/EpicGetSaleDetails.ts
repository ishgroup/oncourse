import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { GET_SALE_DETAILS, SET_SALE_DETAILS } from "../actions";
import { DataResponse, ProductItem, ProductType } from "@api/model";
import { Epic } from "redux-observable";
import EntityService from "../../../../common/services/EntityService";

const request: EpicUtils.Request<any, { id: string }> = {
  type: GET_SALE_DETAILS,
  getData: ({ id }) => {
    return EntityService.getPlainRecords("ProductItem", "status,type", `id == ${id}`);
  },

  processData: (response: DataResponse) => {
    const [selectedSaleStatus, index] = response.rows[0].values;

    return [
      {
        type: SET_SALE_DETAILS,
        payload: {
          selectedSaleStatus,
          selectedSaleType: Object.keys(ProductType)[Number(index) - 1]
        }
      }
    ];
  }
};

export const EpicGetSaleDetails: Epic<any, any> = EpicUtils.Create(request);
