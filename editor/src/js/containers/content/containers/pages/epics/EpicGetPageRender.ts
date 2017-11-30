import {Epic} from "redux-observable";
import "rxjs";
import {success} from 'react-notification-system-redux';

import * as EpicUtils from "../../../../../epics/EpicUtils";
import {GET_PAGE_RENDER_REQUEST, GET_PAGE_RENDER_FULFILLED} from "../actions";
import PageService from "../../../../../services/PageService";

const request: EpicUtils.Request<any, any> = {
  type: GET_PAGE_RENDER_REQUEST,
  getData: (payload, state) => PageService.getPageRender(payload.pageNumber),
  processData: (response: {html: string}, state: any, payload) => {

    return [
      {
        type: GET_PAGE_RENDER_FULFILLED,
        payload: {html: response.html, pageNumber: payload.pageNumber},
      },
    ];
  },
};

export const EpicGetPageRender: Epic<any, any> = EpicUtils.Create(request);
