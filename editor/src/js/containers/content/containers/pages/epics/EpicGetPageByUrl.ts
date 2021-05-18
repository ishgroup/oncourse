import {Epic} from "redux-observable";
import "rxjs";
import * as EpicUtils from "../../../../../epics/EpicUtils";
import {GET_PAGE_BY_URL_FULFILLED, GET_PAGE_BY_URL_REQUEST, setCurrentPage} from "../actions";
import {Page} from "../../../../../model";
import PageService from "../../../../../services/PageService";
import {getHistoryInstance} from "../../../../../history";
import {LOG_OUT_REQUEST} from "../../../../auth/actions/index";

const request: EpicUtils.Request<any, any> = {
  type: GET_PAGE_BY_URL_REQUEST,
  getData: (payload, state) => PageService.getPages(payload),
  processData: (response: Page[], state: any, payload) => {
    const page = response[0];

    if (page && page.id && payload) {
      getHistoryInstance().push(`/page/${page.id}`);
    }

    return [
      {
        type: GET_PAGE_BY_URL_FULFILLED,
        payload: page,
      },
      setCurrentPage(page),
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
