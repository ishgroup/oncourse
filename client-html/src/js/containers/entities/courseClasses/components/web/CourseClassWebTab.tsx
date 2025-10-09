/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import Grid from '@mui/material/Grid';
import $t from '@t';
import * as React from 'react';
import { FormEditorField } from '../../../../../common/components/form/formFields/FormEditor';

class CourseClassWebTab extends React.Component<any, any> {
  render() {
    return (
      <>
        <div className="pl-3 pr-3 centeredFlex">
          <div className="heading mt-2 mb-2">{$t('web2')}</div>
        </div>

        <Grid container columnSpacing={3} className="pl-3 pr-3 pb-3">
          <Grid item xs={12}>
            <FormEditorField name="webDescription" label={$t('class_specific_web_description')} />
          </Grid>
        </Grid>
      </>
    );
  }
}

export default CourseClassWebTab;
