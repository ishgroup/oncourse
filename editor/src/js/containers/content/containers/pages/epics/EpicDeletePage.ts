import {Epic} from "redux-observable";
import "rxjs";
import {success} from 'react-notification-system-redux';
import * as EpicUtils from "../../../../../epics/EpicUtils";
import {DELETE_PAGE_FULFILLED, DELETE_PAGE_REQUEST} from "../actions";
import PageService from "../../../../../services/PageService";
import {getHistoryInstance} from "../../../../../history";
import {URL} from "../../../../../routes";
import {notificationParams} from "../../../../../common/utils/NotificationSettings";

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
      success({...notificationParams, title: "Page deleted"}),
    ];
  },
};

export const EpicDeletePage: Epic<any, any> = EpicUtils.Create(request);
