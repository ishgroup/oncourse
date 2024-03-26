/*
 * Copyright ish group pty ltd 2024.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { ArgumentTypes } from "ish-ui";
import { showMessage } from "../../../common/actions";
import { Create, Request } from "../../../common/epics/EpicUtils";
import TagsService from "../../tags/services/TagsService";
import { getSpecialTagTypes, POST_SPECIAL_TAG_TYPES } from "../actions";
import { getEntityBySpecialTagType } from "../utils";

const request: Request<any, ArgumentTypes<typeof TagsService.updateSpecial>> = {
  type: POST_SPECIAL_TAG_TYPES,
  getData: ([childTags, specialType]) => TagsService.updateSpecial(childTags, specialType),
  processData: (types, state, [,specialType]) => [
    getSpecialTagTypes(getEntityBySpecialTagType(specialType)),
    showMessage({ success: true, message: `${getEntityBySpecialTagType(specialType)} types was successfully updated` })
  ]
};

export const EpicCreateSpecialTagType = Create(request);