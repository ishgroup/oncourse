/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { CourseEnrolmentType, CourseStatus, Tag } from '@api/model';
import { FormControlLabel, Grid } from '@mui/material';
import $t from '@t';
import { LinkAdornment, mapSelectItems, openInternalLink, TimetableButton } from 'ish-ui';
import React, { useCallback, useMemo } from 'react';
import { connect } from 'react-redux';
import { change } from 'redux-form';
import FormField from '../../../../common/components/form/formFields/FormField';
import NestedEntity from '../../../../common/components/form/nestedEntity/NestedEntity';
import FullScreenStickyHeader
  from '../../../../common/components/list-view/components/full-screen-edit-view/FullScreenStickyHeader';
import { EditViewProps } from '../../../../model/common/ListView';
import { CourseExtended } from '../../../../model/entities/Course';
import { State } from '../../../../reducers/state';
import { PreferencesState } from '../../../preferences/reducers/state';
import { EntityChecklists } from '../../../tags/components/EntityChecklists';
import { useTagGroups } from '../../../tags/utils/useTagGroups';
import CustomFields from '../../customFieldTypes/components/CustomFieldsTypes';
import { openFacultyLink } from '../../faculties/utils';
import { courseFilterCondition } from '../utils';
import CourseAvailableClassChart from './CourseAvailableClassChart';
import CourseItemRenderer from './CourseItemRenderer';

const CourseEnrolmentTypes = Object.keys(CourseEnrolmentType).map(mapSelectItems);
const CourseStatusTypes = Object.keys(CourseStatus).map(mapSelectItems);

interface CourseGeneralTabProps extends EditViewProps<CourseExtended> {
  tags: Tag[];
  specialTags: Tag[];
  dataCollectionRules: PreferencesState["dataCollectionRules"];
  dispatch: any;
  form: string;
}

const CourseGeneralTab = React.memo<CourseGeneralTabProps>(
  ({
    showConfirm,
    tags = [],
     specialTags = [],
    dataCollectionRules,
    twoColumn,
    values,
    isNew,
    dirty,
     syncErrors,
    dispatch,
    form
  }) => {
    const { tagsGrouped, subjectsField, specialTypesDisabled } = useTagGroups({ tags, tagsValue: values.tags, dispatch, form });

    const onCalendarClick = useCallback(() => {
      openInternalLink(`/timetable?search=courseClass.course.id=${values?.id}`);
    }, [values?.id]);

    const onIsTraineeshipChange = useCallback(
      (e, value) => {
        if (value) {
          dispatch(change(form, "isSufficientForQualification", value));
        }
      },
      [form]
    );

    const waitingListTypes = useMemo(
      () => [
        {
          name: `${values?.studentWaitingListCount ? `S` : "No s"}tudents on waiting list`,
          count: values?.studentWaitingListCount,
          disabled: !values.studentWaitingListCount,
          link: `/waitingList?search=course.id is ${values?.id}`
        }
      ],
      [values?.studentWaitingListCount, values?.id]
    );

    return (
      <Grid container columnSpacing={3} rowSpacing={2} className="pt-3 pl-3 pr-3">
        <Grid item xs={12}>
          <FullScreenStickyHeader
            opened={isNew || Object.keys(syncErrors).some(k => ['code', 'name'].includes(k))}
            twoColumn={twoColumn}
            title={twoColumn ? (
              <div className="d-inline-flex-center">
                <span>
                  {values && values.code}
                </span>
                <span className="ml-2">
                  {values && values.name}
                </span>
              </div>
            ) : (
              <div>
                <div>
                  {values && values.code}
                </div>
                <div className="mt-2">
                  {values && values.name}
                </div>
              </div>
            )}
            fields={(
              <Grid container columnSpacing={3} rowSpacing={2}>
                <Grid item xs={twoColumn ? 2 : 12}>
                  <FormField
                    type="text"
                    label={$t('code')}
                    name="code"
                    placeholder={twoColumn ? "Code" : undefined}
                    required
                  />
                </Grid>
                <Grid item xs={twoColumn ? 4 : 12}>
                  <FormField
                    type="text"
                    label={$t('name')}
                    name="name"
                    placeholder={twoColumn ? "Name" : undefined}
                    required
                  />
                </Grid>
              </Grid>
            )}
          />
        </Grid>

        <Grid item xs={twoColumn ? 8 : 12}>
          <FormField
            type="tags"
            name="tags"
            tags={tagsGrouped.tags}
            className="mb-2"
          />

          <FormField
            type="select"
            items={specialTags}
            disabled={specialTypesDisabled}
            name="specialTagId"
            label={$t('type')}
            selectValueMark="id"
            selectLabelMark="name"
            allowEmpty
          />

          <FormField
            selectFilterCondition={courseFilterCondition}
            selectLabelCondition={courseFilterCondition}
            labelAdornment={(
              <LinkAdornment
                linkHandler={openFacultyLink}
                link={values?.facultyId}
                disabled={!values?.facultyId}
              />
            )}
            itemRenderer={CourseItemRenderer}
            rowHeight={55}
            name='facultyId'
            type='remoteDataSelect'
            preloadEmpty={true}
            label={$t('faculty')}
            entity='Faculty'
            aqlColumns='name,code'
            selectValueMark='id'
            selectLabelMark='name'
            className="mt-2"
            disabled={values.isTraineeship || specialTypesDisabled}
            allowEmpty
          />

          {subjectsField}
        </Grid>

        <Grid item xs={twoColumn ? 4 : 12}>
          <EntityChecklists
            className={twoColumn ? "mr-4" : null}
            entity="Course"
            form={form}
            entityId={values.id}
            checked={values.tags}
          />
        </Grid>

        <CourseAvailableClassChart courseId={values.id} isNew={isNew} />

        <Grid item xs={12} className="mb-2">
          <TimetableButton onClick={onCalendarClick} />
        </Grid>

        {!values.isTraineeship && (
          <>
            <Grid item xs={twoColumn ? 4 : 12}>
              <FormField
                type="select"
                name="enrolmentType"
                label={$t('enrolment_type')}
                items={CourseEnrolmentTypes}
                disabled={values.isTraineeship}
              />
            </Grid>
            <Grid item xs={twoColumn ? 4 : 12}>
              <FormField
                type="select"
                name="status"
                label={$t('status')}
                items={CourseStatusTypes}
                disabled={values.isTraineeship}
              />
            </Grid>
          </>
        )}
        <Grid item xs={twoColumn ? 4 : 12}>
          <FormField
            type="select"
            name="dataCollectionRuleId"
            defaultValue={values.dataCollectionRuleName}
            label={$t('data_collection_rule')}
            selectValueMark="id"
            selectLabelMark="name"
            items={dataCollectionRules || []}
            sort
            required
          />
        </Grid>

        <Grid item xs={12} className="centeredFlex">
          <FormControlLabel
            className="checkbox"
            control={(
              <FormField
                type="checkbox"
                name="isTraineeship"
                disabled={!isNew}
                onChange={onIsTraineeshipChange}
              />
            )}
            label={$t('traineeship')}
          />
        </Grid>

        <Grid item xs={12} className="centeredFlex">
          <FormControlLabel
            className="checkbox"
            control={<FormField type="checkbox" name="allowWaitingLists" />}
            label={$t('allows_waiting_lists')}
          />
        </Grid>

        {values.isTraineeship && (
          <Grid item xs={12} className="centeredFlex">
            <FormControlLabel
              className="checkbox"
              control={<FormField type="checkbox" name="currentlyOffered"/>}
              label={$t('currently_offered')}
            />
          </Grid>
        )}

        <Grid item xs={12}>
          <NestedEntity
            entityTypes={waitingListTypes}
            dirty={dirty}
            showConfirm={showConfirm}
            twoColumn={twoColumn}
          />
        </Grid>

        <CustomFields
          entityName="Course"
          fieldName="customFields"
          entityValues={values}
          form={form}
          gridItemProps={{
            xs: twoColumn ? 6 : 12,
            lg: twoColumn ? 4 : 12
          }}
        />
      </Grid>
    );
  }
);

const mapStateToProps = (state: State) => ({
  tags: state.tags.entityTags.Course,
  specialTags: state.tags.entitySpecialTags.Course,
  dataCollectionRules: state.preferences.dataCollectionRules
});

export default connect<any, any, any>(mapStateToProps, null)(CourseGeneralTab);