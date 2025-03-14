/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { AssessmentClass, Enrolment, GradingType } from '@api/model';
import { Divider, Grid } from '@mui/material';
import CircularProgress from '@mui/material/CircularProgress';
import Collapse from '@mui/material/Collapse';
import $t from '@t';
import { LinkAdornment } from 'ish-ui';
import React, { useCallback, useState } from 'react';
import { change, FieldArray, FormSection } from 'redux-form';
import FormField from '../../../../common/components/form/formFields/FormField';
import ExpandableContainer from '../../../../common/components/layout/expandable/ExpandableContainer';
import FullScreenStickyHeader
  from '../../../../common/components/list-view/components/full-screen-edit-view/FullScreenStickyHeader';
import { useAppSelector } from '../../../../common/utils/hooks';
import { EditViewProps } from '../../../../model/common/ListView';
import { VetReport } from '../../../../model/entities/VetReporting';
import { getEntityItemById } from '../../common/entityItemsService';
import EnrolmentDetails from '../../enrolments/components/EnrolmentDetails';
import EnrolmentSubmissions from '../../enrolments/components/EnrolmentSubmissions';
import EnrolmentVetStudentLoans from '../../enrolments/components/EnrolmentVetStudentLoans';
import { EnrolmentSelectItemRenderer, EnrolmentSelectValueRenderer, openEnrolmentLink } from '../../enrolments/utils';

const VetReportingEnrolment = (props: EditViewProps<VetReport>) => {
  const {
    twoColumn,
    values,
    dispatch,
    form,
    expanded,
    setExpanded,
    syncErrors,
    tabIndex
  } = props;

  const [enrolmentLoading, setEnrolmentLoading] = useState(false);
  
  const contracts = useAppSelector(state => state.export.contracts);
  const gradingTypes = useAppSelector(state => state.preferences.gradingTypes);

  const validateAssesments = useCallback((value: AssessmentClass[], allValues: VetReport) => {
    let error;

    if (Array.isArray(value) && value.length) {
      value.forEach(a => {
        const gradeType: GradingType = gradingTypes?.find(g => g.id === a.gradingTypeId);
        const submission = allValues.enrolment?.submissions.find(s => s.assessmentId === a.id);

        if (gradeType
          && submission
          && typeof submission.grade === "number"
          && (submission.grade > gradeType.maxValue || submission.grade < gradeType.minValue)) {
          error = "Some assessments grades are invalid";
        }
      });
    }
    return error;
  }, [gradingTypes]);

  // useCallback is needed to prevent infinite loop
  const getCustomSearch = useCallback(search => `student.contact.id is ${values.student.id} and (courseClass.course.name starts with "${search}" or courseClass.code starts with "${search}")`,
    [values.student.id]);

  const onEnrolmentSelect = en => {
    if (en.id) {
      setEnrolmentLoading(true);
      dispatch(change(form, 'selectedOutcome', null));
      dispatch(change(form, 'outcome', null));

      getEntityItemById("Enrolment", en.id).then(enrolment => {
        dispatch(change(form, 'enrolment', enrolment));
        setEnrolmentLoading(false);
      });
    }
  };

  return (
    <div className="pt-1 pl-3 pr-3">
      <FullScreenStickyHeader
        isFixed={false}
        twoColumn={twoColumn}
        title={$t('enrolments4')}
        disableInteraction
      />
      <Divider className="mt-3 mb-3" />
      <FormField
        preloadEmpty
        type="remoteDataSelect"
        name="selectedEnrolment"
        entity="Enrolment"
        label={$t('select_an_enrolment')}
        returnType="object"
        selectValueMark="id"
        selectLabelMark="courseClass.course.name"
        aqlColumns="courseClass.course.name,courseClass.course.code,courseClass.code,displayStatus"
        itemRenderer={EnrolmentSelectItemRenderer}
        valueRenderer={EnrolmentSelectValueRenderer}
        getCustomSearch={getCustomSearch}
        onChange={onEnrolmentSelect}
        labelAdornment={
          <LinkAdornment 
            linkHandler={openEnrolmentLink} 
            link={values.selectedEnrolment?.id} 
            disabled={!values.selectedEnrolment?.id}
          />
        }
      />

      {enrolmentLoading
        ? <CircularProgress />
        : (<Collapse in={Boolean(values.enrolment.id)} mountOnEnter unmountOnExit>
        <FormSection name="enrolment">
          <ExpandableContainer
            expanded={expanded}
            setExpanded={setExpanded}
            formErrors={syncErrors}
            header="Details"
            index={tabIndex}
            noDivider
          >
            <Grid container columnSpacing={3} rowSpacing={2}>
              <EnrolmentDetails
                values={values.enrolment}
                twoColumn={twoColumn}
                contracts={contracts}
              />
            </Grid>
          </ExpandableContainer>
          <EnrolmentVetStudentLoans
            {...props}
            namePrefix="enrolment"
            values={values.enrolment}
          />
          {Boolean(values.enrolment?.assessments?.length) && <>
            <ExpandableContainer
              expanded={expanded}
              setExpanded={setExpanded}
              formErrors={syncErrors}
              header="Assessments submissions"
              index="Assessments submissions"
            >
              <FieldArray
                name="assessments"
                namePrefix="enrolment"
                component={EnrolmentSubmissions}
                values={values.enrolment}
                gradingTypes={gradingTypes}
                dispatch={dispatch}
                validate={validateAssesments}
                twoColumn={twoColumn}
              />
            </ExpandableContainer>
          </>}
        </FormSection>
      </Collapse>)
      }
    </div>
  );
};

export default props => props.values
  ? <VetReportingEnrolment {...props}/>
  : null;