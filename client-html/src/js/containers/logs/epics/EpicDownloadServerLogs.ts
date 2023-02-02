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
import { createAndDownloadFile } from "../../../common/utils/common";
import { initialize } from "redux-form";
import { LOGS_FORM_NAME } from "../Logs";

const request: EpicUtils.Request<LogFile[], DatesInterval> = {
  type: DOWNLOAD_LOGS,
  getData: startDate => LogsService.getLogs(startDate),
  processData: logs => {
    
    for (const log of logs) {
      createAndDownloadFile(log.content, "log", log.fileName);
    }
    
    return [
      initialize(LOGS_FORM_NAME, null)
    ];
  },
};

export const EpicDownloadServerLogs: Epic<any, any> = EpicUtils.Create(request);
