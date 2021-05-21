import { Epic } from "redux-observable";
import * as EpicUtils from "../../../epics/EpicUtils";
import { GET_WEBDAV_FILE_CONTENT, GET_WEBDAV_FILE_CONTENT_FULFILLED } from "../actions/webDavActions";
import { webDavClient } from "../constants/webDavConfig";

const request: EpicUtils.Request<any, any> = {
  type: GET_WEBDAV_FILE_CONTENT,
  getData: ({ file, options }, state) => webDavClient(state.webDav.client).getFileContents(file, options),
  processData: response => [{
    type: GET_WEBDAV_FILE_CONTENT_FULFILLED,
    payload: {
      fileContent: response,
    },
  }],
};

export const EpicGetFileContents: Epic<any, any> = EpicUtils.Create(request);
