import {Epic} from "redux-observable";
import "rxjs";
import * as EpicUtils from "../../../../../epics/EpicUtils";
import {GET_BLOCKS_FULFILLED, GET_BLOCKS_REQUEST} from "../actions";
import {Block} from "../../../../../model";
import BlockService from "../../../../../services/BlockService";
import {getContentModeId, removeContentMarker} from "../../../utils";

const mapBlocks = block => {
  const contentMode = getContentModeId(block.content);
  const cleanContent = removeContentMarker(block.content);
  return {
    ...block,
    contentMode,
    content: cleanContent,
  };
};

const request: EpicUtils.Request<any, any> = {
  type: GET_BLOCKS_REQUEST,
  getData: (payload, state) => BlockService.getBlocks(),
  processData: (blocks: Block[], state: any) => {
    return [
      {
        type: GET_BLOCKS_FULFILLED,
        payload: blocks
          .map(mapBlocks)
          .sort((b1, b2) => b1.title ? b1.title.localeCompare(b2.title) : 1),
      },
    ];
  },
};

export const EpicGetBlocks: Epic<any, any> = EpicUtils.Create(request);
