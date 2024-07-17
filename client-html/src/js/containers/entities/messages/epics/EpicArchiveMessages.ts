/*
 * Copyright ish group pty ltd 2024.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { ArchiveParam } from "@api/model";
import { showMessage } from "../../../../common/actions";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { getRecords } from "../../../../common/components/list-view/actions";
import { Create, Request } from "../../../../common/epics/EpicUtils";
import { Categories, MessageDateArchived } from "../../../../model/preferences";
import { getPreferencesByKeys } from "../../../preferences/actions";
import { ARCHIVE_MESSAGES } from "../actions";
import MessageService from "../services/MessageService";

const request: Request<any, ArchiveParam> = {
  type: ARCHIVE_MESSAGES,
  getData: archiveDate => MessageService.archiveMessages(archiveDate),
  processData: () => {
    return [
      getPreferencesByKeys([MessageDateArchived.uniqueKey], Categories.messaging),
      showMessage({
        success: true,
        message: "Archive request submitted"
      }),
      getRecords({ entity: 'Message', listUpdate: true })
    ];
  },
  processError: response => FetchErrorHandler(response, "Archive request failed")
};

export const EpicArchiveMessages = Create(request);