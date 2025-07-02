/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { CourseClass } from '@api/model';
import { reset } from 'redux-form';
import { Epic } from 'redux-observable';
import { showMessage } from '../../../../common/actions';
import FetchErrorHandler from '../../../../common/api/fetch-errors-handlers/FetchErrorHandler';
import { getRecords } from '../../../../common/components/list-view/actions';
import { LIST_EDIT_VIEW_FORM_NAME } from '../../../../common/components/list-view/constants';
import { Create, Request } from '../../../../common/epics/EpicUtils';
import history from '../../../../constants/History';
import { QueuedAction } from '../../../../model/common/ActionsQueue';
import { processCustomFields } from '../../customFieldTypes/utils';
import { CREATE_COURSE_CLASS } from '../actions';
import CourseClassService from '../services/CourseClassService';
import { processCourseClassApiActions } from '../utils';

const request: Request<{ actions: QueuedAction[], createdClassId: number }, { courseClass: CourseClass }> = {
  type: CREATE_COURSE_CLASS,
  getData: async ({ courseClass }, s) => {
    processCustomFields(courseClass);
    const createdClassId = await CourseClassService.createCourseClass(courseClass);

    const actions = await processCourseClassApiActions(s, createdClassId);

    return { actions, createdClassId };
  },
  processData: ({ actions, createdClassId }) => {

    const { pathname, search } = window.location;

    history.push({
      pathname: pathname.replace("new", String(createdClassId)),
      search
    });

    return [
      ...actions.map(a => a.actionBody),
      reset(LIST_EDIT_VIEW_FORM_NAME),
      getRecords({ entity: 'CourseClass', listUpdate: true }),
      showMessage({ success: true, message: "Class created" })
    ];
  },
  processError: response => FetchErrorHandler(response, "Class was not created")
};

export const EpicCreateCourseClass: Epic<any, any> = Create(request);