/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { CustomFieldType } from "@api/model";
import MuiButton from "@material-ui/core/Button/Button";
import Dialog from "@material-ui/core/Dialog";
import DialogActions from "@material-ui/core/DialogActions";
import DialogContent from "@material-ui/core/DialogContent";
import Grid from "@material-ui/core/Grid";
import Typography from "@material-ui/core/Typography";
import React, { useCallback } from "react";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import {
  getFormValues, initialize, InjectedFormProps, reduxForm
} from "redux-form";
import Button from "../../../../../common/components/buttons/Button";
import FormField from "../../../../../common/components/form/form-fields/FormField";
import { stubFunction } from "../../../../../common/utils/common";
import { validateSingleMandatoryField } from "../../../../../common/utils/validation";
import { NoArgFunction } from "../../../../../model/common/CommonFunctions";
import { State } from "../../../../../reducers/state";

interface CustomFieldsDeleteDialogProps {
  value?: string;
  dispatch?: Dispatch;
  item?: CustomFieldType;
  setFieldToDelete?: any;
  onConfirm?: NoArgFunction;
}

const FORM: string = "DELETE_CUSTOM_FIELD_FORM";

const initialValues = {
  customFieldName: ""
};

const validateName = (value, item) => {
  if (!item || !item.name || !value) return undefined;
  return item.name.toLowerCase() === value.toLowerCase() ? undefined : "Type in correct value";
};

const CustomFieldsDeleteDialog = React.memo<CustomFieldsDeleteDialogProps & InjectedFormProps>(props => {
  const {
    handleSubmit,
    item,
    value,
    setFieldToDelete,
    invalid,
    dispatch,
    onConfirm = stubFunction
  } = props;

  React.useEffect(() => {
    dispatch(initialize(FORM, initialValues ));
  }, [item]);

  const onClose = useCallback(() => {
    setFieldToDelete(null);
  }, []);

  const onSubmit = useCallback(() => {
    setFieldToDelete(null);
    onConfirm();
  }, [value]);

  const validateCustomFieldName = useCallback(
    value => validateName(value, item),
    [item]
  );

  return (
    <Dialog open={Boolean(item)} onClose={onClose} fullWidth maxWidth="xs">
      <form autoComplete="off" onSubmit={handleSubmit(onSubmit)}>
        <DialogContent>
          <div className="heading pt-1">Delete custom field</div>
          <div className="centeredFlex pt-1">
            { item && (
              <Typography variant="body2">
                {`You are about to delete the custom field "${item.name}". This is permanent and you will lose all the data in that field.
                Type "${item.name}" in the box below to continue.`}
              </Typography>
            )}
          </div>
          <Grid item xs={12}>
            <FormField
              type="text"
              name="customFieldName"
              label=""
              validate={[validateSingleMandatoryField, validateCustomFieldName]}
            />
          </Grid>
        </DialogContent>
        <DialogActions className="p-3">
          <MuiButton color="primary" onClick={onClose}>
            Cancel
          </MuiButton>
          <Button color="primary" type="submit" disabled={invalid}>
            Delete
          </Button>
        </DialogActions>
      </form>
    </Dialog>
  );
});

const mapStateToProps = (state: State) => ({
  value: getFormValues(FORM)(state)
});

export default reduxForm<any, CustomFieldsDeleteDialogProps>({
  form: FORM
})(connect<any, any, any>(mapStateToProps)(CustomFieldsDeleteDialog));
