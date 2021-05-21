import Stream from "stream";
import {
  BufferLike,
  GetDirectoryContentsOptions,
  GetFileContentsOptions, PutFileContentsOptions,
  WebDAVClientOptions
} from "webdav/dist/node/types";
import { FULFILLED } from "../../actions/ActionUtils";

export const SET_WEBDAV_CLIENT: string = "set/webDav/client";

export const GET_WEBDAV_FILE_CONTENT: string = "get/webDav/file/content";
export const GET_WEBDAV_FILE_CONTENT_FULFILLED: string = FULFILLED(GET_WEBDAV_FILE_CONTENT);
export const CLEAR_WEBDAV_FILE_CONTENT: string = "clear/webDav/file/content";

export const UPDATE_WEBDAV_FILE_CONTENT: string = "update/webDav/file/content";

export const GET_WEBDAV_DIRECTORY_CONTENT: string = "get/webDav/directory/content";
export const GET_WEBDAV_DIRECTORY_CONTENT_FULFILLED: string = FULFILLED(GET_WEBDAV_DIRECTORY_CONTENT);

export const setWevDavClient = (remoteURL: string, options?: WebDAVClientOptions) => ({
  type: SET_WEBDAV_CLIENT,
  payload: { remoteURL, options },
});

export const getWebDavFileContent = (file, options?: GetFileContentsOptions) => ({
  type: GET_WEBDAV_FILE_CONTENT,
  payload: { file, options },
});

export const clearWebDavFileContents = () => ({
  type: CLEAR_WEBDAV_FILE_CONTENT
});

export const updateWebDavFileContents = (filename: string, data: string | BufferLike | Stream.Readable, options?: PutFileContentsOptions) => ({
  type: UPDATE_WEBDAV_FILE_CONTENT,
  payload: { filename, data, options }
});

export const getWevDavDirectoryContents = (path: string, options?: GetDirectoryContentsOptions) => ({
  type: GET_WEBDAV_DIRECTORY_CONTENT,
  payload: { path, options }
});

