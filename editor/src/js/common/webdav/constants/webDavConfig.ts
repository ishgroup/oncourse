import { createClient } from "webdav";
import { WebDAVClient, WebDAVClientOptions } from "webdav/dist/node/types";

export const webDavPath: string = "/editor/webdav";
export const webDavSrcSitePath: string = `${webDavPath}/s/stylesheets/src/site`;

export const settingFileName: string = "_settings.scss";

export const webDavClient = (client: { remoteURL: string, options?: WebDAVClientOptions }): WebDAVClient => {
  const { remoteURL, options } = client;
  return createClient(remoteURL, options);
};
