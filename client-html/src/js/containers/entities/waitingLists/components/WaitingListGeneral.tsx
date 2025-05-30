/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { Grid } from '@mui/material';
import $t from '@t';
import { LinkAdornment } from 'ish-ui';
import * as React from 'react';
import { connect } from 'react-redux';
import { change } from 'redux-form';
import {
  ContactLinkAdornment,
  HeaderContactTitle
} from '../../../../common/components/form/formFields/FieldAdornments';
import FormField from '../../../../common/components/form/formFields/FormField';
import FullScreenStickyHeader
  from '../../../../common/components/list-view/components/full-screen-edit-view/FullScreenStickyHeader';
import { State } from '../../../../reducers/state';
import { EntityChecklists } from '../../../tags/components/EntityChecklists';
import ContactSelectItemRenderer from '../../contacts/components/ContactSelectItemRenderer';
import { getContactFullName } from '../../contacts/utils';
import CourseItemRenderer from '../../courses/components/CourseItemRenderer';
import { courseFilterCondition, openCourseLink } from '../../courses/utils';
import CustomFields from '../../customFieldTypes/components/CustomFieldsTypes';

class WaitingListGeneral extends React.PureComponent<any, any> {
  handlerCourseChange = courseId => {
    const { coursesItems, dispatch, form } = this.props;
    const course = coursesItems.find(c => c.id === courseId);

    if (course) dispatch(change(form, "courseName", `${course.name} ${course.code}`));
  };

  render() {
    const {
      values,
      tags,
      form,
      twoColumn,
      isNew,
      syncErrors
    } = this.props;

    const gridItemProps: any = {
      xs: twoColumn ? 6 : 12
    };

    return (
      <Grid container columnSpacing={3} rowSpacing={2} className="p-3">
        <Grid item xs={12}>
          <FullScreenStickyHeader
            opened={isNew || Object.keys(syncErrors).includes("contactId")}
            disableInteraction={!isNew}
            twoColumn={twoColumn}
            title={(
              <HeaderContactTitle name={values?.studentName} id={values?.contactId} />
            )}
            fields={(
              <Grid item {...gridItemProps}>
                <FormField
                  type="remoteDataSelect"
                  entity="Contact"
                  aqlFilter="isStudent is true"
                  name="contactId"
                  label={$t('student')}
                  selectValueMark="id"
                  selectLabelCondition={getContactFullName}
                  defaultValue={values && values.studentName}
                  labelAdornment={
                    <ContactLinkAdornment id={values?.contactId} />
                  }
                  itemRenderer={ContactSelectItemRenderer}
                  rowHeight={55}
                  required
                />
              </Grid>
            )}
          />
        </Grid>
        <Grid item xs={twoColumn ? 8 : 12}>
          <FormField
            type="tags"
            name="tags"
            tags={tags}
          />
        </Grid>
        <Grid item xs={twoColumn ? 4 : 12}>
          <EntityChecklists
            entity="WaitingList"
            form={form}
            entityId={values.id}
            checked={values.tags}
          />
        </Grid>
        <Grid item xs={12}>
          <FormField type="number" name="studentCount" label={$t('number_of_students')} />
        </Grid>
        <Grid item xs={12}>
          <FormField
            type="remoteDataSelect"
            entity="Course"
            aqlFilter="allowWaitingLists is true"
            name="courseId"
            label={$t('course')}
            selectValueMark="id"
            selectLabelCondition={v => v.name}
            selectFilterCondition={courseFilterCondition}
            defaultValue={values && values.courseName}
            labelAdornment={<LinkAdornment link={values.courseId} linkHandler={openCourseLink} />}
            itemRenderer={CourseItemRenderer}
            onChange={this.handlerCourseChange}
            debounced={false}
            rowHeight={55}
            required
          />
        </Grid>
        <CustomFields
          entityName="WaitingList"
          fieldName="customFields"
          entityValues={values}
          form={form}
          gridItemProps={gridItemProps}
        />
      </Grid>
    );
  }
}

const mapStateToProps = (state: State) => ({
  tags: state.tags.entityTags["WaitingList"],
  coursesItems: state.plainSearchRecords["Course"]?.items,
});

export default connect<any, any, any>(mapStateToProps)(WaitingListGeneral);
