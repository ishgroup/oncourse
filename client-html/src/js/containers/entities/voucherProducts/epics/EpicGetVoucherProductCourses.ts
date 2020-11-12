import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { SEARCH_VOUCHER_PRODUCT_COURSES, SEARCH_VOUCHER_PRODUCT_COURSES_FULFILLED } from "../actions";
import { DataResponse } from "@api/model";
import { Epic } from "redux-observable";
import EntityService from "../../../../common/services/EntityService";

const request: EpicUtils.Request<any, any, string> = {
  type: SEARCH_VOUCHER_PRODUCT_COURSES,
  getData: search => {
    return EntityService.getPlainRecords("Course", "code,name", search);
  },
  processData: (response: DataResponse) => {
    return [
      {
        type: SEARCH_VOUCHER_PRODUCT_COURSES_FULFILLED,
        payload: {
          foundCourses: response.rows.map(({ id, values }) => ({ id: Number(id), code: values[0], name: values[1] })),
          pendingCourses: false
        }
      }
    ];
  }
};

export const EpicGetVoucherProductCourses: Epic<any, any> = EpicUtils.Create(request);
