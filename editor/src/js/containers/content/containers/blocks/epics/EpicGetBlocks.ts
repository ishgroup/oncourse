import {Epic} from "redux-observable";
import "rxjs";
import * as EpicUtils from "../../../../../epics/EpicUtils";
import {GET_BLOCKS_FULFILLED, GET_BLOCKS_REQUEST} from "../actions";
import {Block} from "../../../../../model";
import BlockService from "../../../../../services/BlockService";

const request: EpicUtils.Request<any, any> = {
  type: GET_BLOCKS_REQUEST,
  getData: (payload, state) => BlockService.getBlocks(),
  processData: (blocks: Block[], state: any) => {
    return [
      {
        type: GET_BLOCKS_FULFILLED,
        payload: blocks,
      },
    ];
  },
};

export const EpicGetBlocks: Epic<any, any> = EpicUtils.Create(request);
