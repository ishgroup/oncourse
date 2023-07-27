/*
* Copyright ish group pty ltd 2022.
*
* This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
*
*  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
*/

import React, { useEffect, useRef } from "react";
import Button from "@mui/material/Button";
import ErrorOutline from "@mui/icons-material/ErrorOutline";
import { IS_JEST } from "../../../constants/EnvironmentConstants";
import { Collapse } from "@mui/material";
import { useAppSelector } from "../../utils/hooks";

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

  const isFieldProcessing = useAppSelector(state => Boolean(Object.keys(state.fieldProcessing).length));

  const buttonProps = IS_JEST ? {
    "data-testid": "appbar-submit-button"
  } : {};

  // process blur events
  const onClick = e => {
    if (isFieldProcessing) {
      e.preventDefault();
      defaultPrevented.current = true;
      return;
    }
  };

  useEffect(() => {
    if (defaultPrevented.current && !isFieldProcessing) {
      ref.current?.click();
      defaultPrevented.current = false;
    }
  }, [defaultPrevented.current, isFieldProcessing]);

  return (
    <Button
      type="submit"
      ref={ref}
      classes={{
        root: fab ? "saveButtonEditView" : "whiteAppBarButton",
        disabled: fab ? "saveButtonEditViewDisabled" : "whiteAppBarButtonDisabled",
        startIcon: !invalid && "m-0"
      }}
      disabled={disabled}
      onClick={onClick}
      variant="contained"
      color="primary"
      startIcon={
        <Collapse
          in={invalid}
          orientation="horizontal"
          classes={{
            wrapperInner: 'd-flex'
          }}>
          <ErrorOutline color="error" fontSize="inherit"/>
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