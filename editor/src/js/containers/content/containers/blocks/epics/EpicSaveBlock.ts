import {Epic} from "redux-observable";
import "rxjs";
import {success} from 'react-notification-system-redux';

import * as EpicUtils from "../../../../../epics/EpicUtils";
import {SAVE_BLOCK_FULFILLED, SAVE_BLOCK_REQUEST} from "../actions";
import {getPageRender} from "../../pages/actions";
import {Block} from "../../../../../model";
import BlockService from "../../../../../services/BlockService";
import {notificationParams} from "../../../../../common/utils/NotificationSettings";
import {getContentModeId, removeContentMarker} from "../../../utils";

const request: EpicUtils.Request<any, any> = {
  type: SAVE_BLOCK_REQUEST,
  getData: (props, state) => BlockService.saveBlock(props, state),
  processData: (block: Block, state: any, payload) => {
    const contentMode = getContentModeId(block.content);
    const cleanContent = removeContentMarker(block.content);
    const result = [];
    const page = state.page.currentPage;

    result.push(success(notificationParams));
    result.push(
      {
        payload: {
          ...block,
          contentMode,
          content: cleanContent,
        },
        type: SAVE_BLOCK_FULFILLED,
      },
    );

    // get rendered html if raw html has been changed
    if (payload.updatePageRender && page) {
      result.push(getPageRender(page && page.id));
    }

    return result;
  },
};

export const EpicSaveBlock: Epic<any, any> = EpicUtils.Create(request);
