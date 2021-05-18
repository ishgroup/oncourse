import {Epic} from "redux-observable";
import "rxjs";
import * as EpicUtils from "../../../../../epics/EpicUtils";
import {getPageRender, SAVE_PAGE_FULFILLED, SAVE_PAGE_REQUEST} from "../actions";
import {Page} from "../../../../../model";
import PageService from "../../../../../services/PageService";
import {getContentModeId, removeContentMarker} from "../../../utils";
import {SHOW_MESSAGE} from "../../../../../common/components/message/actions";

const request: EpicUtils.Request<any, any> = {
  type: SAVE_PAGE_REQUEST,
  getData: (payload, state) => PageService.savePage(payload, state),
  processData: (page: Page, state: any, payload) => {
    const contentMode = getContentModeId(page.content);
    const cleanContent = removeContentMarker(page.content);
    const result = [];

    result.push(
      {
        type: SHOW_MESSAGE,
        payload: {message: "Save success", success: true},
      },
    );
    result.push(
      {
        payload:  {
          ...page,
          contentMode,
          content: cleanContent,
        },
        type: SAVE_PAGE_FULFILLED,
      },
    );

    // get rendered html if raw html has been changed
    if (payload.updateRender) {
      result.push(getPageRender(page.id));
    }

    return result;
  },
};

export const EpicSavePage: Epic<any, any> = EpicUtils.Create(request);
