import {Epic} from "redux-observable";
import * as EpicUtils from "../../../epics/EpicUtils";
import {getWebDavFileContent, UPDATE_WEBDAV_FILE_CONTENT} from "../actions/webDavActions";
import {webDavClient} from "../constants/webDavConfig";
import {SHOW_MESSAGE} from "../../components/message/actions";

const request: EpicUtils.Request<any, any> = {
  type: UPDATE_WEBDAV_FILE_CONTENT,
  getData: ({filename, data, options}, state) => webDavClient(state.webDav.client).putFileContents(filename, data, options),
  processData: (v, s, {filename}) => {
    return [
      getWebDavFileContent(filename, {format: "text"}),
      {
        type: SHOW_MESSAGE,
        payload: {message: "Settings updated successfully.", success: true},
      },
    ];
  },
};

export const EpicPutFileContents: Epic<any, any> = EpicUtils.Create(request);
