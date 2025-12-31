/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import Button from '@mui/material/Button';
import Dialog from '@mui/material/Dialog';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import DialogTitle from '@mui/material/DialogTitle';
import Typography from '@mui/material/Typography';
import $t from '@t';
import React from 'react';
import { InjectedFormProps, reduxForm } from 'redux-form';
import FormField from '../../../common/components/form/formFields/FormField';
import { validateKeycode } from '../utils';

interface Props {
  opened: any;
  onClose: any;
  onSave: any;
  validateNameField: any;
}

const SaveAsNewAutomationModal = React.memo<Props & InjectedFormProps>(
  ({
     opened, handleSubmit, invalid, onClose, onSave, validateNameField
    }) => (
      <Dialog open={opened} onClose={onClose} maxWidth="md" scroll="body">
        <form autoComplete="off" onSubmit={handleSubmit(onSave)}>
          <DialogTitle>{$t('your_new_custom_automation_requires_a_unique_key_c')}</DialogTitle>

          <DialogContent>
            <Typography variant="body2" color="textSecondary" className="pb-2">
              {$t('your_new_custom_automation_requires_you_to_create')}
            </Typography>

            <FormField
              type="text"
              label={$t('keycode')}
              name="keyCode"
              validate={validateKeycode}
              required
                          />

            <FormField
              type="text"
              label={$t('name')}
              name="name"
              validate={validateNameField || undefined}
              className="mt-2"
              required
                          />

          </DialogContent>

          <DialogActions className="p-3">
            <Button color="primary" onClick={onClose}>
              {$t('cancel')}
            </Button>

            <Button variant="contained" color="primary" type="submit" disabled={invalid}>
              {$t('save2')}
            </Button>
          </DialogActions>
        </form>
      </Dialog>
    )
);

export default reduxForm<any, Props>({
  form: "SaveAsNewAutomationForm"
})(SaveAsNewAutomationModal);