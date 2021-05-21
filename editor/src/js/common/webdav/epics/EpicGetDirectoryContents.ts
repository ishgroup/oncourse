import { Epic } from "redux-observable";
import * as EpicUtils from "../../../epics/EpicUtils";
import {
  GET_WEBDAV_DIRECTORY_CONTENT,
  GET_WEBDAV_DIRECTORY_CONTENT_FULFILLED
} from "../actions/webDavActions";
import { webDavClient } from "../constants/webDavConfig";

const request: EpicUtils.Request<any, any> = {
  type: GET_WEBDAV_DIRECTORY_CONTENT,
  getData: ({ path, options }, state) => webDavClient(state.webDav.client).getDirectoryContents(path, options),
  processData: response => [{
    type: GET_WEBDAV_DIRECTORY_CONTENT_FULFILLED,
    payload: {
      directoryContents: response,
    },
  }],
};

export const EpicGetDirectoryContents: Epic<any, any> = EpicUtils.Create(request);
