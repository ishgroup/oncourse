import React, { useCallback, useRef } from "react";
import {
  ButtonBase, Typography
} from "@mui/material";
import CheckIcon from '@mui/icons-material/Check';
import clsx from "clsx";
import { alpha } from "@mui/material/styles";
import { WrappedFieldProps } from "redux-form";

interface Props extends WrappedFieldProps {
  label: string;
  color: string;
  className?: string;
  disabled?: boolean;
}

export const ColoredCheckBox = ({
 input, label, color, className, disabled
}: Props) => {
  const inputRef = useRef<HTMLInputElement>();

  const onClick = useCallback(() => {
    inputRef.current.click();
  }, [inputRef.current]);
  
  return (
    <div
      className={clsx("centeredFlex", className)}
    >
      <input type="checkbox" ref={inputRef} checked={input.value} hidden onChange={input.onChange} />
      <ButtonBase
        onClick={onClick}
        disabled={disabled}
        sx={{
          width: 3,
          height: 3,
          border: `2px solid #${color}`,
          backgroundColor: input.value ? `#${color}` : alpha(`#${color}`, 0.1),
          color: theme => theme.palette.getContrastText(`#${color}`)
        }}
      >
        {input.value && <CheckIcon color="inherit" />}
      </ButtonBase>
      <Typography 
        variant="body2"
        sx={{
          marginLeft: 1,
          textDecoration: input.value ? "line-through" : "unset"
        }}
      >
        {label}
      </Typography>
    </div>
  );
};
