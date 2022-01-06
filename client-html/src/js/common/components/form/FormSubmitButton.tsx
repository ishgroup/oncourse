/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import Button from "@mui/material/Button";
import ErrorOutline from "@mui/icons-material/ErrorOutline";
import React from "react";

interface Props {
  disabled: boolean;
  invalid: boolean;
  fab?: boolean;
  text?: string;
  errorText?: string;
  className?: any;
}

const FormSubmitButton = React.memo<Props>(({
    disabled,
    invalid,
    fab = false,
    text = "Save",
    className,
  }) => (
    <Button
      type="submit"
      classes={{
        root: fab ? "saveButtonEditView" : "whiteAppBarButton",
        disabled: fab ? "saveButtonEditViewDisabled" : "whiteAppBarButtonDisabled"
      }}
      disabled={disabled}
      startIcon={invalid && <ErrorOutline color="error" />}
      variant="contained"
      color="primary"
      className={className}
      data-testid="appbar-submit-button"
    >
      {text}
    </Button>
  ));

export default FormSubmitButton;

