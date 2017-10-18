import {Epic} from "redux-observable";
import "rxjs";
import {success} from 'react-notification-system-redux';

import * as EpicUtils from "../../../../../epics/EpicUtils";
import {DELETE_BLOCK_FULFILLED, DELETE_BLOCK_REQUEST} from "../actions";
import {getHistoryInstance} from "../../../../../history";
import {URL} from "../../../../../routes";
import BlockService from "../../../../../services/BlockService";
import {notificationParams} from "../../../../../common/utils/NotificationSettings";

const request: EpicUtils.Request<any, any> = {
  type: DELETE_BLOCK_REQUEST,
  getData: (payload, state) => BlockService.deleteBlock(payload),
  processData: (block: any, state: any, payload) => {

    getHistoryInstance().push(URL.BLOCKS);

    return [
      {
        payload,
        type: DELETE_BLOCK_FULFILLED,
      },
      success({...notificationParams, title: "Block Deleted"}),
    ];
  },
};

export const EpicDeleteBlock: Epic<any, any> = EpicUtils.Create(request);
