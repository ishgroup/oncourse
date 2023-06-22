import React, { useCallback, useRef } from "react";
import { ButtonBase, Typography } from "@mui/material";
import clsx from "clsx";
import { alpha } from "@mui/material/styles";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { ColoredCheckboxFieldProps } from "../../../model/common/Fields";

export const ColoredCheckBox = ({
 input, label, color, className, disabled
}: ColoredCheckboxFieldProps) => {
  const inputRef = useRef<HTMLInputElement>();

  const onClick = useCallback(() => {
    inputRef.current.click();
  }, [inputRef.current]);

  return (
    <div
      className={clsx("centeredFlex cursor-pointer", className)}
      onClick={onClick}
    >
      <input
        ref={inputRef}
        type="checkbox"
        onChange={input.onChange}
        hidden
      />
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
};
