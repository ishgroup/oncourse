import {Epic} from "redux-observable";
import "rxjs";
import * as EpicUtils from "../../../epics/EpicUtils";
import {SAVE_PAGE_SETTINGS_REQUEST, SAVE_PAGE_SETTINGS_FULFILLED} from "../actions";
import {Page} from "../../../model";
import PageService from "../../../services/PageService";

const request: EpicUtils.Request<any, any> = {
  type: SAVE_PAGE_SETTINGS_REQUEST,
  getData: (payload, state) => PageService.savePageSettings(payload),
  processData: (response: Page[], state: any, payload) => {
    console.log(payload);
    return [
      {
        payload,
        type: SAVE_PAGE_SETTINGS_FULFILLED,
      },
    ];
  },
};

export const EpicSavePageSettings: Epic<any, any> = EpicUtils.Create(request);
