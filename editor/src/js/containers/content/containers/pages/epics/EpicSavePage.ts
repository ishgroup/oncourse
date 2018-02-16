import {Epic} from "redux-observable";
import "rxjs";
import {success} from 'react-notification-system-redux';

import * as EpicUtils from "../../../../../epics/EpicUtils";
import {getPageRender, SAVE_PAGE_FULFILLED, SAVE_PAGE_REQUEST} from "../actions";
import {Page} from "../../../../../model";
import PageService from "../../../../../services/PageService";
import {notificationParams} from "../../../../../common/utils/NotificationSettings";

const request: EpicUtils.Request<any, any> = {
  type: SAVE_PAGE_REQUEST,
  getData: (payload, state) => PageService.savePage(payload, state),
  processData: (page: Page, state: any, payload) => {

    const result = [];
    result.push(success(notificationParams));
    result.push(
      {
        payload: page,
        type: SAVE_PAGE_FULFILLED,
      },
    );

    // get rendered html if raw html has been changed
    if (payload.updateRender) {
      result.push(getPageRender(page.serialNumber));
    }

    return result;
  },
};

export const EpicSavePage: Epic<any, any> = EpicUtils.Create(request);
