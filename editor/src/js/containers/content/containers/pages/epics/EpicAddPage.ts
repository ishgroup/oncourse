import {Epic} from "redux-observable";
import "rxjs";
import {success} from 'react-notification-system-redux';

import * as EpicUtils from "../../../../../epics/EpicUtils";
import {ADD_PAGE_REQUEST, ADD_PAGE_FULFILLED} from "../actions";
import {Page} from "../../../../../model";
import PageService from "../../../../../services/PageService";
import {notificationParams} from "../../../../../common/utils/NotificationSettings";
import {getHistoryInstance} from "../../../../../history";
import {URL} from "../../../../../routes";

const request: EpicUtils.Request<any, any> = {
  type: ADD_PAGE_REQUEST,
  getData: (props, state) => PageService.addPage(),
  processData: (page: Page, state: any) => {

    getHistoryInstance().push(`${URL.PAGES}/${page.number}`);

    return [
      success({...notificationParams, title: 'New page added'}),
      {
        payload: page,
        type: ADD_PAGE_FULFILLED,
      },
    ];
  },
};

export const EpicAddPage: Epic<any, any> = EpicUtils.Create(request);
