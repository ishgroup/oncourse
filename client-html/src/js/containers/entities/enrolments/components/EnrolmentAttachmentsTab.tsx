/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Grid } from '@mui/material';
import $t from '@t';
import * as React from 'react';
import { FieldArray } from 'redux-form';
import DocumentsRenderer from '../../../../common/components/form/documents/DocumentsRenderer';
import OwnApiNotes from '../../../../common/components/form/notes/OwnApiNotes';

const EnrolmentAttachmentsTab: React.FC<any> = props => {
  const {
 form, showConfirm, twoColumn, classes, dispatch
} = props;

  return (
    <Grid container rowSpacing={2} className="pl-3 pr-3 pb-3">
      <FieldArray
        name="documents"
        label={$t('documents')}
        entity="Enrolment"
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

      <Grid item xs={12}>
        <OwnApiNotes {...props} />
      </Grid>
    </Grid>
  );
};

export default EnrolmentAttachmentsTab;
