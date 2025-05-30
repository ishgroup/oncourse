/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Outcome, PriorLearning, Qualification } from '@api/model';
import { Grid } from '@mui/material';
import $t from '@t';
import { LinkAdornment } from 'ish-ui';
import React, { useCallback, useEffect, useMemo } from 'react';
import { arrayInsert, arrayRemove, change, FieldArray } from 'redux-form';
import DocumentsRenderer from '../../../../common/components/form/documents/DocumentsRenderer';
import { ContactLinkAdornment } from '../../../../common/components/form/formFields/FieldAdornments';
import FormField from '../../../../common/components/form/formFields/FormField';
import Uneditable from '../../../../common/components/form/formFields/Uneditable';
import MinifiedEntitiesList from '../../../../common/components/form/minifiedEntitiesList/MinifiedEntitiesList';
import FullScreenStickyHeader
  from '../../../../common/components/list-view/components/full-screen-edit-view/FullScreenStickyHeader';
import EntityService from '../../../../common/services/EntityService';
import { EditViewProps } from '../../../../model/common/ListView';
import ContactSelectItemRenderer from '../../contacts/components/ContactSelectItemRenderer';
import { getContactFullName } from '../../contacts/utils';
import { OutcomeInitial } from '../../outcomes/Outcomes';
import { openQualificationLink } from '../../qualifications/utils';
import { OutcomesContentLine, OutcomesHeaderLine } from './OutcomesLines';

interface PriorLearningEditViewProps extends EditViewProps<PriorLearning> {
  classes?: any;
}

const validateOutcomes = (values: Outcome[]) => {
  if (Array.isArray(values) && values.length && values.some(v => !v.startDate || !v.endDate)) {
    return "Some outcome items are missing mandatory fields";
  }
  return undefined;
};

const PriorLearningEditView: React.FC<PriorLearningEditViewProps> = props => {
  const {
    classes,
    twoColumn,
    values,
    dispatch,
    form,
    showConfirm,
    isNew,
    syncErrors
  } = props;

  useEffect(() => {
    if (isNew && window.location.search) {
      const searchParam = new URLSearchParams(window.location.search);

      let contactId: any = searchParam.get("contactId");
      contactId = Number(contactId);

      if (contactId && !isNaN(contactId)) {
        EntityService.getPlainRecords("Contact", "firstName,lastName,email,birthDate", `id is ${contactId}`, 1).then(
          res => {
            if (res.rows.length > 0) {
              dispatch(change(form, "contactId", contactId));
              dispatch(
                change(
                  form,
                  "contactName",
                  getContactFullName({ firstName: res.rows[0].values[0], lastName: res.rows[0].values[1] })
                )
              );
            }
          }
        );
      }
    }
  }, [isNew]);

  const outcomesCount = useMemo(() => (values && values.outcomes ? values.outcomes.length : 0), [
    values && values.outcomes
  ]);

  const addNewOutcome = useCallback(() => {
    dispatch(arrayInsert(form, "outcomes", 0, { ...OutcomeInitial }));
  }, [values && values.id, form]);

  const deleteOutcome = useCallback(
    index => {
      dispatch(arrayRemove(form, "outcomes", index));
    },
    [values && values.id, form]
  );

  const OutcomesContent = useCallback(
    outcomesProps => <OutcomesContentLine {...outcomesProps} twoColumn={twoColumn} dispatch={dispatch} form={form} />,
    [values && values.id, form, twoColumn]
  );

  const onQualificationCodeChange = useCallback(
    (q: Qualification) => {
      dispatch(change(form, "qualificationId", q ? q.id : null));
      dispatch(change(form, "qualificationLevel", q ? q.qualLevel : null));
      dispatch(change(form, "qualificationName", q ? q.title : null));
    },
    [form]
  );

  const onQualificationTitleChange = useCallback(
    (q: Qualification) => {
      dispatch(change(form, "qualificationId", q ? q.id : null));
      dispatch(change(form, "qualificationLevel", q ? q.qualLevel : null));
      dispatch(change(form, "qualificationNationalCode", q ? q.nationalCode : null));
    },
    [form]
  );

  return values ? (
    <div className="p-3">
      <Grid container columnSpacing={3} rowSpacing={2}>
        <Grid item xs={12}>
          <FullScreenStickyHeader
            opened={isNew || Object.keys(syncErrors).includes("title")}
            twoColumn={twoColumn}
            title={(
              <div className="d-inline-flex-center">
                {values.title}
              </div>
            )}
            fields={(
              <Grid item xs={twoColumn ? 6 : 12}>
                <FormField type="text" name="title" label={$t('title')} required />
              </Grid>
            )}
          />
        </Grid>
        <Grid item xs={twoColumn ? 6 : 12}>
          <FormField
            type="remoteDataSelect"
            name="contactId"
            label={$t('student')}
            entity="Contact"
            aqlFilter="isStudent is true"
            selectValueMark="id"
            selectLabelCondition={getContactFullName}
            defaultValue={values?.contactName}
            labelAdornment={
              <ContactLinkAdornment id={values?.contactId} />
            }
            itemRenderer={ContactSelectItemRenderer}
            required
            rowHeight={55}
          />
        </Grid>

        <Grid item xs={twoColumn ? 3 : 12}>
          <FormField
            type="remoteDataSelect"
            name="qualificationName"
            label={$t('qualification')}
            entity="Qualification"
            selectValueMark="title"
            selectLabelMark="title"
            onInnerValueChange={onQualificationTitleChange}
            labelAdornment={(
              <LinkAdornment
                linkHandler={openQualificationLink}
                link={values.qualificationId}
                disabled={!values.qualificationId}
              />
            )}
            rowHeight={36}
          />
        </Grid>

        <Grid item xs={twoColumn ? 3 : 12}>
          <FormField
            type="remoteDataSelect"
            name="qualificationNationalCode"
            label={$t('national_code')}
            selectValueMark="nationalCode"
            selectLabelMark="nationalCode"
            entity="Qualification"
            onInnerValueChange={onQualificationCodeChange}
            labelAdornment={(
              <LinkAdornment
                linkHandler={openQualificationLink}
                link={values.qualificationNationalCode}
                disabled={!values.qualificationNationalCode}
              />
            )}
            rowHeight={36}
          />
        </Grid>

        <Grid item xs={twoColumn ? 6 : 12}>
          <FormField type="text" name="externalReference" label={$t('external_reference')} />
        </Grid>
        <Grid item xs={twoColumn ? 6 : 12}>
          <Uneditable value={values.qualificationLevel} label={$t('level')} />
        </Grid>

        <Grid item xs={twoColumn ? 6 : 12}>
          <FormField
            type="text"
            name="outcomeIdTrainingOrg"
            label={$t('outcome_identifier_training_organization')}
          />
        </Grid>
        <Grid item xs={12}>
          <FormField type="multilineText" name="notes" label={$t('private_notes')} />
        </Grid>

        <Grid item xs={12} className="pb-2 pt-2">
          <MinifiedEntitiesList
            name="outcomes"
            header="Outcomes"
            oneItemHeader="Outcome"
            count={outcomesCount}
            FieldsContent={OutcomesContent}
            HeaderContent={OutcomesHeaderLine}
            onAdd={addNewOutcome}
            onDelete={deleteOutcome}
            syncErrors={syncErrors}
            validate={validateOutcomes}
            accordion
          />
        </Grid>
        <Grid item xs={12}>
          <FieldArray
            name="documents"
            label={$t('documents')}
            entity="PriorLearning"
            classes={classes}
            component={DocumentsRenderer}
            xsGrid={12}
            mdGrid={twoColumn ? 6 : 12}
            lgGrid={twoColumn ? 4 : 12}
            dispatch={dispatch}
            form={form}
            showConfirm={showConfirm}
            rerenderOnEveryChange
          />
        </Grid>
      </Grid>
    </div>
  ) : null;
};

export default PriorLearningEditView;
