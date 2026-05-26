/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from 'redux-observable';
import * as EpicUtils from '../../../../common/epics/EpicUtils';
import EntityService from '../../../../common/services/EntityService';
import { CHECKOUT_GET_COURSE_CLASS_LIST, checkoutGetCourseClassListFulfilled } from '../../actions/chekoutItem';
import { CHECKOUT_COURSE_CLASS_COLUMNS, CHECKOUT_ITEMS_PER_PAGE } from '../../constants';
import { checkoutCourseClassMap } from '../../utils';

const request: EpicUtils.Request = {
  type: CHECKOUT_GET_COURSE_CLASS_LIST,
  getData: ({
              search, offset
            }) => EntityService.getPlainRecords(
    "CourseClass",
    CHECKOUT_COURSE_CLASS_COLUMNS,
    search,
    CHECKOUT_ITEMS_PER_PAGE,
    offset,
    "startDateTime",
    true
  ),
  processData: ({ rows }, s) => [checkoutGetCourseClassListFulfilled(rows.map(checkoutCourseClassMap))]
};

export const EpicGetCourseClassList: Epic<any, any> = EpicUtils.Create(request);
