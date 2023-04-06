import React, { useCallback, useRef } from "react";
import { ButtonBase, Typography } from "@mui/material";
import clsx from "clsx";
import { alpha } from "@mui/material/styles";
import { WrappedFieldProps } from "redux-form";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";

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
      className={clsx("centeredFlex cursor-pointer", className)}
      onClick={onClick}
    >
      <input type="checkbox" ref={inputRef} checked={input.value} hidden onChange={input.onChange} />
      <ButtonBase
        component="div"
        disabled={disabled}
        sx={{
          width: theme => theme.spacing(3),
          height: theme => theme.spacing(3),
          border: `2px solid #${color}`,
          borderRadius: "50%",
          fontSize: "10px",
          backgroundColor: input.value ? `#${color}` : alpha(`#${color}`, 0.1),
          color: theme => theme.palette.getContrastText(`#${color}`)
        }}
      >
        {input.value && <FontAwesomeIcon fixedWidth icon="check" />}
      </ButtonBase>
      <Typography 
        variant="caption"
        fontWeight="400"
        sx={{
          marginLeft: 1,
          textDecoration: input.value ? "line-through" : "unset",
          color: theme => (input.value ? theme.palette.text.disabled : "unset"),
        }}
      >
        {label}
      </Typography>
    </div>
  );
};
