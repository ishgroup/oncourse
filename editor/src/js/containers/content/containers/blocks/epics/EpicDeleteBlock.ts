import {Epic} from "redux-observable";
import "rxjs";
import * as EpicUtils from "../../../../../epics/EpicUtils";
import {DELETE_BLOCK_FULFILLED, DELETE_BLOCK_REQUEST} from "../actions";
import {getHistoryInstance} from "../../../../../history";
import {URL} from "../../../../../routes";
import BlockService from "../../../../../services/BlockService";
import {SHOW_MESSAGE} from "../../../../../common/components/message/actions";

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
      {
        type: SHOW_MESSAGE,
        payload: {message: "Block Deleted", success: true},
      },
    ];
  },
};

export const EpicDeleteBlock: Epic<any, any> = EpicUtils.Create(request);
