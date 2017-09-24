import {Epic} from "redux-observable";
import "rxjs";
import Notifications, {success} from 'react-notification-system-redux';

import * as EpicUtils from "../../../epics/EpicUtils";
import {SAVE_PAGE_FULFILLED, SAVE_PAGE_REQUEST} from "../actions";
import {Page} from "../../../model";
import PageService from "../../../services/PageService";

const request: EpicUtils.Request<any, any> = {
  type: SAVE_PAGE_REQUEST,
  getData: (props, state) => PageService.savePage(props, state),
  processData: (page: Page[], state: any) => {
    return [
      success({
        // uid: 'once-please', // you can specify your own uid if required
        title: 'Save success',
        message: null,
        position: 'tr',
        autoDismiss: 3,
      }),
      {
        payload: page,
        type: SAVE_PAGE_FULFILLED,
      },
    ];
  },
};

export const EpicSavePage: Epic<any, any> = EpicUtils.Create(request);
