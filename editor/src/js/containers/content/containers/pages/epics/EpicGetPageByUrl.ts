import {Epic} from "redux-observable";
import "rxjs";
import * as EpicUtils from "../../../../../epics/EpicUtils";
import {GET_PAGE_BY_URL_FULFILLED, GET_PAGE_BY_URL_REQUEST} from "../actions";
import {Page} from "../../../../../model";
import PageService from "../../../../../services/PageService";
import {getHistoryInstance} from "../../../../../history";

const request: EpicUtils.Request<any, any> = {
  type: GET_PAGE_BY_URL_REQUEST,
  getData: (payload, state) => PageService.getPageByUrl(payload),
  processData: (page: Page, state: any) => {

    if (page && page.number && !page.reservedURL) {
      getHistoryInstance().push(`/pages/${page.number}`);
    }

    return [
      {
        type: GET_PAGE_BY_URL_FULFILLED,
        payload: page,
      },
    ];
  },
};

export const EpicGetPageByUrl: Epic<any, any> = EpicUtils.Create(request);
