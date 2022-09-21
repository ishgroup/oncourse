/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React from "react";
import Button from "@mui/material/Button";
import ErrorOutline from "@mui/icons-material/ErrorOutline";
import { IS_JEST } from "../../../constants/EnvironmentConstants";
import { Collapse } from "@mui/material";

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
        variant="contained"
        color="primary"
        startIcon={
          <Collapse
            in={invalid}
            orientation="horizontal"
            classes={{
              wrapperInner: 'd-flex'
            }}>
            <ErrorOutline color="error" fontSize="inherit" />
          </Collapse>
        }
        className={className || ""}
        {...buttonProps}
      >
        {text}
      </Button>
    );
  });

export default FormSubmitButton;

