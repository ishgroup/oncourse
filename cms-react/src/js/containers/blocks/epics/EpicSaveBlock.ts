import {Epic} from "redux-observable";
import "rxjs";
import {success} from 'react-notification-system-redux';

import * as EpicUtils from "../../../epics/EpicUtils";
import {SAVE_BLOCK_FULFILLED, SAVE_BLOCK_REQUEST} from "../actions";
import {Block} from "../../../model";
import BlockService from "../../../services/BlockService";
import {notificationParams} from "../../../common/utils/NotificationSettings";
import {getHistoryInstance} from "../../../history";
import {URL} from "../../../routes";

const request: EpicUtils.Request<any, any> = {
  type: SAVE_BLOCK_REQUEST,
  getData: (props, state) => BlockService.saveBlock(props, state),
  processData: (block: Block, state: any) => {

    getHistoryInstance().push(`${URL.BLOCKS}/${block.id}`);

    return [
      success(notificationParams),
      {
        payload: block,
        type: SAVE_BLOCK_FULFILLED,
      },
    ];
  },
};

export const EpicSaveBlock: Epic<any, any> = EpicUtils.Create(request);
