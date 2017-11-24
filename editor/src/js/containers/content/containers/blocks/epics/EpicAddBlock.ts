import {Epic} from "redux-observable";
import "rxjs";
import {success} from 'react-notification-system-redux';

import * as EpicUtils from "../../../../../epics/EpicUtils";
import {ADD_BLOCK_REQUEST, ADD_BLOCK_FULFILLED} from "../actions";
import {Block} from "../../../../../model";
import BlockService from "../../../../../services/BlockService";
import {notificationParams} from "../../../../../common/utils/NotificationSettings";
import {getHistoryInstance} from "../../../../../history";
import {URL} from "../../../../../routes";

const request: EpicUtils.Request<any, any> = {
  type: ADD_BLOCK_REQUEST,
  getData: (props, state) => BlockService.addBlock(),
  processData: (block: Block, state: any, payload) => {

    getHistoryInstance().push(`${URL.BLOCKS}/${block.id}`);

    return [
      success({...notificationParams, title: 'New Block added'}),
      {
        payload: block,
        type: ADD_BLOCK_FULFILLED,
      },
    ];
  },
};

export const EpicAddBlock: Epic<any, any> = EpicUtils.Create(request);
