import {Epic} from "redux-observable";
import "rxjs";
import * as EpicUtils from "../../../../../epics/EpicUtils";
import {GET_BLOCK_FULFILLED, GET_BLOCK_REQUEST} from "../actions";
import {Block} from "../../../../../model";
import BlockService from "../../../../../services/BlockService";

const request: EpicUtils.Request<any, any> = {
  type: GET_BLOCK_REQUEST,
  getData: (id) => BlockService.getBlock(id),
  processData: (block: Block) => {
    const result = [];

    result.push(
      {
        payload: block,
        type: GET_BLOCK_FULFILLED,
      },
    );

    return result;
  },
};

export const EpicGetBlock: Epic<any, any> = EpicUtils.Create(request);
