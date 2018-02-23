import {Epic} from "redux-observable";
import "rxjs";
import {success, error} from 'react-notification-system-redux';
import * as EpicUtils from "../../../../../epics/EpicUtils";
import {GET_PAGE_BY_URL_FULFILLED, GET_PAGE_BY_URL_REQUEST} from "../actions";
import {Page} from "../../../../../model";
import PageService from "../../../../../services/PageService";
import {getHistoryInstance} from "../../../../../history";
import {LOG_OUT_REQUEST} from "../../../../auth/actions/index";

const request: EpicUtils.Request<any, any> = {
  type: GET_PAGE_BY_URL_REQUEST,
  getData: (payload, state) => PageService.getPages(payload),
  processData: (page: Page, state: any) => {

    if (page && page.serialNumber) {
      getHistoryInstance().push(`/pages/${page.serialNumber}`);
    }

    return [
      {
        type: GET_PAGE_BY_URL_FULFILLED,
        payload: page,
      },
    ];
  },
  processError: data => {

    if (data.status && 403 === data.status) {
      return [];
    }

    if (data.status && 401 === data.status) {
      return [{type: LOG_OUT_REQUEST}];
    }

    return [
      EpicUtils.errorMessage(data),
    ];
  },
};

export const EpicGetPageByUrl: Epic<any, any> = EpicUtils.Create(request);
