import {Epic} from "redux-observable";
import "rxjs";
import * as EpicUtils from "../../../epics/EpicUtils";
import {GET_PAGES_FULFILLED, GET_PAGES_REQUEST} from "../actions";
import {Page} from "../../../model";
import PageService from "../../../services/PageService";

const request: EpicUtils.Request<any, any> = {
  type: GET_PAGES_REQUEST,
  getData: (payload, state) => PageService.getPages(),
  processData: (pages: Page[], state: any) => {
    return [
      {
        type: GET_PAGES_FULFILLED,
        payload: pages,
      },
    ];
  },
};

export const EpicGetPages: Epic<any, any> = EpicUtils.Create(request);
