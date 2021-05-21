import React from "react";
import Button from "@material-ui/core/Button";
import ErrorOutline from "@material-ui/icons/ErrorOutline";

interface Props {
  disabled: boolean;
  invalid: boolean;
  fab?: boolean;
  text?: string;
  errorText?: string;
}

const FormSubmitButton = React.memo<Props>(({
    disabled,
    invalid,
    fab = false,
    text = "Save"
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
    >
      {text}
    </Button>
  ));

export default FormSubmitButton;

