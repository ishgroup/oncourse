import {Epic} from "redux-observable";
import "rxjs";
import * as EpicUtils from "../../../../../epics/EpicUtils";
import {ADD_PAGE_REQUEST, ADD_PAGE_FULFILLED} from "../actions";
import {Page} from "../../../../../model";
import PageService from "../../../../../services/PageService";
import {getHistoryInstance} from "../../../../../history";
import {URL} from "../../../../../routes";
import {SHOW_MESSAGE} from "../../../../../common/components/message/actions";

const request: EpicUtils.Request<any, any> = {
  type: ADD_PAGE_REQUEST,
  getData: (props, state) => PageService.addPage(),
  processData: (page: Page, state: any) => {

    getHistoryInstance().push(`${URL.PAGES}/${page.id}`);

    return [
      {
        type: SHOW_MESSAGE,
        payload: {message: "New page added", success: true},
      },
      {
        payload: page,
        type: ADD_PAGE_FULFILLED,
      },
    ];
  },
};

export const EpicAddPage: Epic<any, any> = EpicUtils.Create(request);
