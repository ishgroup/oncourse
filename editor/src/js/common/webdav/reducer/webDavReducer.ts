import { WebDAVClientOptions } from "webdav/dist/node/types";
import { IAction } from "../../../actions/IshAction";
import {
  CLEAR_WEBDAV_FILE_CONTENT,
  GET_WEBDAV_DIRECTORY_CONTENT_FULFILLED,
  GET_WEBDAV_FILE_CONTENT_FULFILLED,
  SET_WEBDAV_CLIENT
} from "../actions/webDavActions";

export interface WebDav {
  client: { remoteURL: string, options?: WebDAVClientOptions };
  fileContent: any;
  directoryContents: any;
}

const initial = {
  client: null,
  fileContent: "",
  directoryContents: [],
};

export const webDavReducer = (state: WebDav = initial, action: IAction<any>): any => {
  switch (action.type) {
    case SET_WEBDAV_CLIENT: {
      const { remoteURL, options } = action.payload;
      return {
        ...state,
        client: { remoteURL, options },
      };
    }

    case GET_WEBDAV_FILE_CONTENT_FULFILLED: {
      return {
        ...state,
        fileContent: action.payload.fileContent,
      };
    }

    case CLEAR_WEBDAV_FILE_CONTENT: {
      return {
        ...state,
        fileContent: "",
      };
    }

    case GET_WEBDAV_DIRECTORY_CONTENT_FULFILLED: {
      return {
        ...state,
        directoryContents: action.payload.directoryContents,
      };
    }

    default:
      return state;
  }
};
