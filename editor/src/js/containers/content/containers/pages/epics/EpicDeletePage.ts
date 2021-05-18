import {Epic} from "redux-observable";
import "rxjs";
import * as EpicUtils from "../../../../../epics/EpicUtils";
import {DELETE_PAGE_FULFILLED, DELETE_PAGE_REQUEST} from "../actions";
import PageService from "../../../../../services/PageService";
import {getHistoryInstance} from "../../../../../history";
import {URL} from "../../../../../routes";
import {SHOW_MESSAGE} from "../../../../../common/components/message/actions";

const request: EpicUtils.Request<any, any> = {
  type: DELETE_PAGE_REQUEST,
  getData: (payload, state) => PageService.deletePage(payload),
  processData: (page: any, state: any, payload) => {

    getHistoryInstance().push(URL.PAGES);

    return [
      {
        payload,
        type: DELETE_PAGE_FULFILLED,
      },
      {
        type: SHOW_MESSAGE,
        payload: {message: "Page deleted", success: true},
      },
    ];
  },
};

export const EpicDeletePage: Epic<any, any> = EpicUtils.Create(request);
