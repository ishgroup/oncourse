/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { AssessmentClass, Enrolment, FundingSource, GradingType, Tag } from '@api/model';
import Divider from '@mui/material/Divider';
import Grid from '@mui/material/Grid';
import $t from '@t';
import clsx from 'clsx';
import React, { useCallback, useMemo } from 'react';
import { connect } from 'react-redux';
import { Dispatch } from 'redux';
import { FieldArray } from 'redux-form';
import { HeaderContactTitle } from '../../../../common/components/form/formFields/FieldAdornments';
import FormField from '../../../../common/components/form/formFields/FormField';
import Uneditable from '../../../../common/components/form/formFields/Uneditable';
import NestedEntity from '../../../../common/components/form/nestedEntity/NestedEntity';
import ExpandableContainer from '../../../../common/components/layout/expandable/ExpandableContainer';
import FullScreenStickyHeader
  from '../../../../common/components/list-view/components/full-screen-edit-view/FullScreenStickyHeader';
import { useAppSelector } from '../../../../common/utils/hooks';
import { EditViewProps } from '../../../../model/common/ListView';
import { EnrolmentExtended } from '../../../../model/entities/Enrolment';
import { State } from '../../../../reducers/state';
import { EntityChecklists } from '../../../tags/components/EntityChecklists';
import CustomFields from '../../customFieldTypes/components/CustomFieldsTypes';
import { setSelectedContact } from '../../invoices/actions';
import { paymentSourceItems } from '../constants';
import EnrolmentDetails from './EnrolmentDetails';
import EnrolmentSubmissions from './EnrolmentSubmissions';

interface Props extends EditViewProps<EnrolmentExtended> {
  contracts?: FundingSource[];
  tags?: Tag[];
  gradingTypes?: GradingType[];
}

const EnrolmentGeneralTab: React.FC<Props> = props => {
  const {
    isNew,
    form,
    showConfirm,
    tags,
    twoColumn,
    values,
    dispatch,
    contracts,
    dirty,
    gradingTypes,
    expanded,
    setExpanded,
    syncErrors
  } = props;

  const validateAssesments = useCallback((value: AssessmentClass[], allValues: Enrolment) => {
    let error;

    if (Array.isArray(value) && value.length) {
      value.forEach(a => {
        const gradeType: GradingType = gradingTypes?.find(g => g.id === a.gradingTypeId);
        const submission = allValues.submissions.find(s => s.assessmentId === a.id);

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

  const invoiceTypes = useMemo(
    () => (values.id
      ? [
          {
            name: "Invoices",
            count: values.invoicesCount,
            link: `/invoice?search=invoiceLines.enrolment.id=${values?.id}`
          }
        ]
      : []),
    [values.invoicesCount, values?.id]
  );

  const outcomeTypes = useMemo(
    () => (values.id
      ? [
          {
            name: "Outcomes",
            count: values.outcomesCount,
            link: `/outcome?search=enrolment.id=${values?.id}`
          }
        ]
      : []),
    [values.outcomesCount, values?.id]
  );

  const outcomesAddLink = useMemo(() => `/outcome/new?search=enrolment.id=${values?.id}`, [values?.id]);

  const hideAUSReporting = useAppSelector(state => state.location.countryCode !== 'AU');

  return (
    <Grid container columnSpacing={3} rowSpacing={2} className={clsx("pl-3 pr-3", twoColumn ? "pt-2" : "pt-3")}>
      <Grid item xs={12}>
        <FullScreenStickyHeader
          disableInteraction
          twoColumn={twoColumn}
          title={(
            <HeaderContactTitle name={values?.studentName} id={values?.studentContactId} />
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
          entity="Enrolment"
          form={form}
          entityId={values.id}
          checked={values.tags}
        />
      </Grid>

      <Grid item xs={twoColumn ? 8 : 12}>
        <Uneditable
          value={values && values.courseClassName}
          label={$t('class')}
          url={`/class/${values.courseClassId}`}
        />
      </Grid>

      <Grid item xs={twoColumn ? 4 : 12}>
        <FormField type="text" name="displayStatus" label={$t('status')} disabled />
      </Grid>

      <Grid item xs={twoColumn ? 8 : 12}>
        <FormField
          type="select"
          name="source"
          label={$t('source')}
          items={paymentSourceItems}
          disabled={!isNew}
        />
      </Grid>

      {!hideAUSReporting && <>
        <Grid item xs={12} className="pt-2 pb-3">
          <Divider />
        </Grid>
          <EnrolmentDetails
            values={values}
            twoColumn={twoColumn}
            contracts={contracts}
          />
        <Grid item xs={12} className="pt-2 pb-3">
          <Divider />
        </Grid>
      </> }

      <Grid item xs={12}>
        <NestedEntity
          entityTypes={invoiceTypes}
          dirty={dirty}
          showConfirm={showConfirm}
          twoColumn={twoColumn}
          isNew={isNew}
        />
      </Grid>
      <Grid item xs={12}>
        <NestedEntity
          entityTypes={outcomeTypes}
          dirty={dirty}
          showConfirm={showConfirm}
          twoColumn={twoColumn}
          isNew={isNew}
          addLink={outcomesAddLink}
        />
      </Grid>

      <Grid item xs={12} className="pb-3">
        <Divider />
      </Grid>
      <CustomFields
        entityName="Enrolment"
        fieldName="customFields"
        entityValues={values}
        form={form}
        gridItemProps={{
          xs: twoColumn ? 6 : 12,
          lg: twoColumn ? 4 : 12
        }}
      />

      <Grid item xs={12}>
        {Boolean(values?.assessments?.length) && <>
          <ExpandableContainer
            expanded={expanded}
            setExpanded={setExpanded}
            formErrors={syncErrors}
            header="Assessments submissions"
            index="Assessments submissions"
          >
            <FieldArray
              name="assessments"
              component={EnrolmentSubmissions}
              values={values}
              gradingTypes={gradingTypes}
              dispatch={dispatch}
              validate={validateAssesments}
              twoColumn={twoColumn}
            />
          </ExpandableContainer>
        </>}
      </Grid>
    </Grid>
  );
};

const mapStateToProps = (state: State) => ({
  tags: state.tags.entityTags["Enrolment"],
  contracts: state.export.contracts,
  gradingTypes: state.preferences.gradingTypes
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  setSelectedContact: (selectedContact: any) => dispatch(setSelectedContact(selectedContact)),
});

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(EnrolmentGeneralTab);
