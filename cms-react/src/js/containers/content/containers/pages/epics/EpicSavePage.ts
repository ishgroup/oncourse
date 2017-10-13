import {Epic} from "redux-observable";
import "rxjs";
import {success} from 'react-notification-system-redux';

import * as EpicUtils from "../../../../../epics/EpicUtils";
import {SAVE_PAGE_FULFILLED, SAVE_PAGE_REQUEST} from "../actions";
import {Page} from "../../../../../model";
import PageService from "../../../../../services/PageService";
import {notificationParams} from "../../../../../common/utils/NotificationSettings";
import {getHistoryInstance} from "../../../../../history";
import {URL} from "../../../../../routes";

const request: EpicUtils.Request<any, any> = {
  type: SAVE_PAGE_REQUEST,
  getData: (props, state) => PageService.savePage(props, state),
  processData: (page: Page, state: any) => {

    getHistoryInstance().push(`${URL.PAGES}/${page.id}`);

    return [
      success(notificationParams),
      {
        payload: page,
        type: SAVE_PAGE_FULFILLED,
      },
    ];
  },
};

export const EpicSavePage: Epic<any, any> = EpicUtils.Create(request);
