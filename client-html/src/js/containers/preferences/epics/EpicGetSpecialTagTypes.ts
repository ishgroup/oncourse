/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Tag } from "@api/model";
import { initialize } from "redux-form";
import { Create, Request } from "../../../common/epics/EpicUtils";
import { CLASS_TYPES_FORM_NAME } from "../../../constants/Forms";
import { EntityName } from "../../../model/entities/common";
import { SpecialTypeTagsFormValues } from "../../../model/tags";
import TagsService from "../../tags/services/TagsService";
import { GET_SPECIAL_TAG_TYPES } from "../actions";

const request: Request<Tag[], EntityName> = {
  type: GET_SPECIAL_TAG_TYPES,
  getData: entity => TagsService.getSpecialTags(entity),
  processData: (types, state, entity) => {
    const actions = [];

    switch (entity) {
      case "CourseClass": {
        actions.push(initialize(CLASS_TYPES_FORM_NAME, { types } satisfies SpecialTypeTagsFormValues));
        break;
      }
    }

    return actions;
  }
};

export const EpicGetSpecialTagTypes = Create(request);