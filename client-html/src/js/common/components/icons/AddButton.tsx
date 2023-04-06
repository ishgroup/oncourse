import React from "react";
import AddCircleOutlineOutlinedIcon from "@mui/icons-material/AddCircleOutlineOutlined";
import IconButton, { IconButtonProps } from "@mui/material/IconButton";
import { OverridableStringUnion } from "@mui/types";
import { IconButtonPropsColorOverrides, IconButtonPropsSizeOverrides } from "@mui/material/IconButton";

interface Props extends Partial<IconButtonProps>{
  onClick?: any;
  className?: any;
  disabled?: boolean;
  iconClassName?: any;
  iconFontSize?: 'inherit' | 'large' | 'medium' | 'small';
  color?: OverridableStringUnion<
    'inherit' | 'default' | 'primary' | 'secondary' | 'error' | 'info' | 'success' | 'warning',
    IconButtonPropsColorOverrides
    >,
  size?: OverridableStringUnion<'small' | 'medium' | 'large', IconButtonPropsSizeOverrides>;
}

const AddButton = React.forwardRef<any, Props>((props, ref) => {
  const {
   onClick, className, disabled, iconClassName, iconFontSize, color, size
  } = props;
  return (
    <IconButton color={color} size={size} onClick={onClick} className={className} disabled={disabled} ref={ref}>
      <AddCircleOutlineOutlinedIcon className={iconClassName || "addButtonColor"} fontSize={iconFontSize || "small"} />
    </IconButton>
  );
});

export default AddButton;
