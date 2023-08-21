/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { DatesInterval, LogFile, LogsApi } from "@api/model";
import { DefaultHttpService } from "../../../common/services/HttpService";

class LogsService {
  readonly service = new DefaultHttpService();

  readonly logsApi = new LogsApi(this.service);
  
  public getLogs(startDate: DatesInterval): Promise<LogFile> {
    return this.logsApi.getLogs(startDate);
  }
}

export default new LogsService();