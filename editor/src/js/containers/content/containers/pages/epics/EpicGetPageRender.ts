import {Epic} from "redux-observable";
import "rxjs";

import * as EpicUtils from "../../../../../epics/EpicUtils";
import {GET_PAGE_RENDER_REQUEST, GET_PAGE_RENDER_FULFILLED, GET_PAGE_RENDER_REJECTED} from "../actions";
import PageService from "../../../../../services/PageService";

const request: EpicUtils.Request<any, any> = {
  type: GET_PAGE_RENDER_REQUEST,
  getData: (payload, state) => PageService.getPageRender(payload.id),
  processData: (response: {html: string}, state: any, payload) => {
    return [{
      type: GET_PAGE_RENDER_FULFILLED,
      payload: {html: response.html, id: payload.id},
    }];
  },
  processError: error => {
    console.error(`Page render failed: ${error.statusText}`);
    return [{
      type: GET_PAGE_RENDER_REJECTED,
    }];
  },
};

export const EpicGetPageRender: Epic<any, any> = EpicUtils.Create(request);
