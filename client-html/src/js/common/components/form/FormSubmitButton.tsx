/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useRef } from "react";
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
    const ref = useRef<HTMLButtonElement>();
    const defaultPrevented = useRef(false);
  
    const buttonProps = IS_JEST ? {
      "data-testid": "appbar-submit-button"
    } : {};

    // timeout to process blur events
    const onClick = e => {
      if (!defaultPrevented.current) {
        e.preventDefault();
        defaultPrevented.current = true;
        setTimeout(() => {
          ref.current.click();
        }, 600);
        return;
      }
      defaultPrevented.current = false;
    };

    return (
      <Button
        type="submit"
        ref={ref}
        classes={{
          root: fab ? "saveButtonEditView" : "whiteAppBarButton",
          disabled: fab ? "saveButtonEditViewDisabled" : "whiteAppBarButtonDisabled"
        }}
        disabled={disabled}
        onClick={onClick}
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

