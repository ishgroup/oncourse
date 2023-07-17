import React, { useCallback, useRef } from "react";
import { ButtonBase, Typography } from "@mui/material";
import clsx from "clsx";
import { alpha } from "@mui/material/styles";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { getCheckboxValue } from "../../utils/common";
import { useAppTheme } from "../../themes/ishTheme";
import { rgbToHex } from "@mui/system/colorManipulator";
import { ColoredCheckboxFieldProps, FieldInputProps, FieldMetaProps } from "../../ish-ui/model/Fields";

export function ColoredCheckBox<IP extends FieldInputProps, MP extends FieldMetaProps>({
 input, label, color, className, disabled
}: ColoredCheckboxFieldProps<IP, MP>) {
  const theme = useAppTheme();
  
  const inputRef = useRef<HTMLInputElement>();

  const onClick = useCallback(() => {
    inputRef.current.click();
  }, [inputRef.current]);
  
  if (disabled) {
    color = rgbToHex(theme.palette.divider);
  }

  return (
    <div
      className={clsx("centeredFlex cursor-pointer", className)}
      onClick={onClick}
    >
      <input
        ref={inputRef}
        type="checkbox"
        {...input}
        checked={getCheckboxValue(input.value, false)}
        hidden
      />
      <ButtonBase
        component="div"
        disabled={disabled}
        sx={{
          width: theme => theme.spacing(3),
          height: theme => theme.spacing(3),
          border: `2px solid ${color}`,
          borderRadius: "50%",
          fontSize: "10px",
          backgroundColor: input.value ? `${color}` : alpha(`${color}`, 0.1),
          color: theme => theme.palette.getContrastText(`${color}`)
        }}
      >
        {input.value && <FontAwesomeIcon fixedWidth icon="check" />}
      </ButtonBase>
      {label && <Typography
        variant="caption"
        fontWeight="400"
        sx={{
          marginLeft: 1,
          textDecoration: input.value ? "line-through" : "unset",
          color: theme => (input.value ? theme.palette.text.disabled : "unset"),
        }}
      >
        {label}
      </Typography>}
    </div>
  );
}
