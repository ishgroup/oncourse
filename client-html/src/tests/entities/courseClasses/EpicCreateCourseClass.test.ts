/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { reset } from 'redux-form';
import { showMessage } from '../../../js/common/actions';
import { getRecords } from '../../../js/common/components/list-view/actions';
import { LIST_EDIT_VIEW_FORM_NAME } from '../../../js/common/components/list-view/constants';
import { createCourseClass } from '../../../js/containers/entities/courseClasses/actions';
import { EpicCreateCourseClass } from '../../../js/containers/entities/courseClasses/epics/EpicCreateCourseClass';
import { DefaultEpic } from '../../common/Default.Epic';

describe("Create course class epic tests", () => {
  it("EpicCreateCourseClass should returns correct values", () => DefaultEpic({
    action: mockedApi => createCourseClass(mockedApi.db.getCourseClass(1)),
    epic: EpicCreateCourseClass,
    store: () => ({
      form: { [LIST_EDIT_VIEW_FORM_NAME]: { values: { tutors: [] } } }
    }),
    processData: ({ actions }) => [
      ...actions?.map(a => a.actionBody) || [],
      reset(LIST_EDIT_VIEW_FORM_NAME),
      getRecords({ entity: 'CourseClass', listUpdate: true }),
      showMessage({ success: true, message: "Class created" })
    ]
  }));
});