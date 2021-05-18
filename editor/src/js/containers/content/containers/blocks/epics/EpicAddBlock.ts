import {Epic} from "redux-observable";
import "rxjs";
import * as EpicUtils from "../../../../../epics/EpicUtils";
import {ADD_BLOCK_REQUEST, ADD_BLOCK_FULFILLED} from "../actions";
import {Block} from "../../../../../model";
import BlockService from "../../../../../services/BlockService";
import {getHistoryInstance} from "../../../../../history";
import {URL} from "../../../../../routes";
import {SHOW_MESSAGE} from "../../../../../common/components/message/actions";

const request: EpicUtils.Request<any, any> = {
  type: ADD_BLOCK_REQUEST,
  getData: (props, state) => BlockService.addBlock(),
  processData: (block: Block, state: any, payload) => {

    getHistoryInstance().push(`${URL.BLOCKS}/${block.id}`);

    return [
      {
        type: SHOW_MESSAGE,
        payload: {message: "New Block added", success: true},
      },
      {
        payload: block,
        type: ADD_BLOCK_FULFILLED,
      },
    ];
  },
};

export const EpicAddBlock: Epic<any, any> = EpicUtils.Create(request);
