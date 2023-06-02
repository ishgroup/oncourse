/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React, { useCallback } from "react";
import { EditViewProps } from "../../../../model/common/ListView";
import FullScreenStickyHeader from "../../../../common/components/list-view/components/full-screen-edit-view/FullScreenStickyHeader";
import Divider from "@mui/material/Divider";
import { LinkAdornment } from "../../../../common/components/form/FieldAdornments";
import FormField from "../../../../common/components/form/formFields/FormField";
import {
  EnrolmentSelectItemRenderer,
  EnrolmentSelectValueRenderer,
  openEnrolmentLink
} from "../../enrolments/utils";
import Collapse from "@mui/material/Collapse";
import { VetReport } from "../../../../model/entities/VetReporting";
import EnrolmentDetails from "../../enrolments/components/EnrolmentDetails";
import { Grid } from "@mui/material";
import EnrolmentVetStudentLoans from "../../enrolments/components/EnrolmentVetStudentLoans";
import { change, FieldArray, FormSection } from "redux-form";
import { getEntityItemById } from "../../common/entityItemsService";
import { useAppSelector } from "../../../../common/utils/hooks";
import ExpandableContainer from "../../../../common/components/layout/expandable/ExpandableContainer";
import EnrolmentSubmissions from "../../enrolments/components/EnrolmentSubmissions";
import { AssessmentClass, Enrolment, GradingType } from "@api/model";

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
      dispatch(change(form, 'selectedOutcome', null));
      dispatch(change(form, 'outcome', null));

      getEntityItemById("Enrolment", en.id).then(enrolment => {
        dispatch(change(form, 'enrolment', enrolment));
      });
    }  
  };

  return (
    <div className="pt-1 pl-3 pr-3">
      <FullScreenStickyHeader
        isFixed={false}
        twoColumn={twoColumn}
        title="Enrolments"
        disableInteraction
      />
      <Divider className="mt-3 mb-3" />
      <FormField
        preloadEmpty
        type="remoteDataSelect"
        name="selectedEnrolment"
        entity="Enrolment"
        label="Select an enrolment"
        returnType="object"
        selectValueMark="id"
        selectLabelMark="courseClass.course.name"
        aqlColumns="courseClass.course.name,courseClass.course.code,courseClass.code,status"
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
      <Collapse in={Boolean(values.enrolment.id)} mountOnEnter unmountOnExit>
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
                twoColumn={twoColumn}
                contracts={contracts}
              />
            </Grid>
          </ExpandableContainer>
          <EnrolmentVetStudentLoans
            {...props}
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
      </Collapse>
    </div>
  );
};

export default props => props.values
  ? <VetReportingEnrolment {...props}/>
  : null;