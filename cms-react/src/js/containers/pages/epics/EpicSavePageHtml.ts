import {Epic} from "redux-observable";
import "rxjs";
import Notifications, {success} from 'react-notification-system-redux';

import * as EpicUtils from "../../../epics/EpicUtils";
import {SAVE_PAGE_HTML_FULFILLED, SAVE_PAGE_HTML_REQUEST} from "../actions";
import {Page} from "../../../model";
import PageService from "../../../services/PageService";

const request: EpicUtils.Request<any, any> = {
  type: SAVE_PAGE_HTML_REQUEST,
  getData: (payload, state) => PageService.savePageHtml(payload),
  processData: (pages: Page[], state: any, payload) => {
    return [
      success({
        // uid: 'once-please', // you can specify your own uid if required
        title: 'Saved successfully',
        position: 'tr',
        autoDismiss: 2,
      }),
      {
        payload,
        type: SAVE_PAGE_HTML_FULFILLED,
      },
    ];
  },
};

export const EpicSavePageHtml: Epic<any, any> = EpicUtils.Create(request);
