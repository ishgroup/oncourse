/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import Button from "@mui/material/Button";
import ErrorOutline from "@mui/icons-material/ErrorOutline";
import { IS_JEST } from "../../../constants/EnvironmentConstants";

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
  }) => {
    const buttonProps = IS_JEST ? {
      "data-testid": "appbar-submit-button"
    } : {};
    return (
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
        className={className || ""}
        {...buttonProps}
      >
        {text}
      </Button>
    );
  });

export default FormSubmitButton;

