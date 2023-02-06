/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { Epic } from "redux-observable";
import * as EpicUtils from "../../../common/epics/EpicUtils";
import { DOWNLOAD_LOGS } from "../actions";
import { DatesInterval, LogFile } from "@api/model";
import LogsService from "../services/LogsService";
import { createAndDownloadBase64File } from "../../../common/utils/common";
import { initialize } from "redux-form";
import { LOGS_FORM_NAME } from "../Logs";
import { showMessage } from "../../../common/actions";

const request: EpicUtils.Request<LogFile, DatesInterval> = {
  type: DOWNLOAD_LOGS,
  getData: startDate => LogsService.getLogs(startDate),
  processData: log => {
    if (log) {
      createAndDownloadBase64File(log.content, log.fileName, "gzip");
    }
    return [
      initialize(LOGS_FORM_NAME, null),
      ...log ? [] : [showMessage({ message: "No logs was found" })]
    ];
  },
};

export const EpicDownloadServerLogs: Epic<any, any> = EpicUtils.Create(request);
