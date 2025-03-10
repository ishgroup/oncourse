/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Course } from '@api/model';
import Grid from '@mui/material/Grid';
import $t from '@t';
import React from 'react';
import { FieldArray } from 'redux-form';
import DocumentsRenderer from '../../../../common/components/form/documents/DocumentsRenderer';
import { FormEditorField } from '../../../../common/components/form/formFields/FormEditor';
import FormField from '../../../../common/components/form/formFields/FormField';
import { EditViewProps } from '../../../../model/common/ListView';
import { EntityRelationTypeRendered } from '../../../../model/entities/EntityRelations';
import RelationsCommon from '../../common/components/RelationsCommon';

const relationTypesFilter = {
  entities: ["Module" as const],
  filter: (rel: EntityRelationTypeRendered) => rel.isReverseRelation
    && rel.considerHistory
    && rel.shoppingCart === "Add but do not allow removal"
};

const CourseMarketingTab: React.FC<EditViewProps<Course>> = props => {
  const {
    twoColumn,
    dispatch,
    form,
    showConfirm,
    values,
    rootEntity,
    submitSucceeded
  } = props;

  return (
    <Grid container columnSpacing={3} rowSpacing={2} className="pl-3 pr-3">
      <Grid item xs={12}>
        <div className="heading mt-3">{$t('marketing')}</div>
      </Grid>

      <Grid item xs={12}>
        <FormEditorField name="webDescription" label={$t('web_description')} />
      </Grid>
      <Grid item xs={12}>
        <FormEditorField name="shortWebDescription" label={$t('short_web_description')} />
      </Grid>

      <Grid item xs={12}>
        <FormField
          type="multilineText"
          name="brochureDescription"
          label={$t('print_brochure_description')}
        />
      </Grid>

      <Grid item xs={12}>
        <FieldArray
          name="documents"
          label={$t('documents')}
          entity="Course"
          component={DocumentsRenderer}
          xsGrid={12}
          mdGrid={twoColumn ? 4 : 12}
          lgGrid={twoColumn ? 3 : 12}
          dispatch={dispatch}
          form={form}
          showConfirm={showConfirm}
          rerenderOnEveryChange
        />
      </Grid>

      <Grid item xs={12} className="mt-3">
        <RelationsCommon
          name="relatedSellables"
          values={values}
          dispatch={dispatch}
          form={form}
          submitSucceeded={submitSucceeded}
          rootEntity={rootEntity}
          relationTypesFilter={relationTypesFilter}
        />
      </Grid>
    </Grid>
  );
};

export default CourseMarketingTab;
