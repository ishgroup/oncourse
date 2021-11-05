import React from "react";
import AddCircleOutlineOutlinedIcon from "@mui/icons-material/AddCircleOutlineOutlined";
import IconButton, { IconButtonProps } from "@mui/material/IconButton";
import { OverridableStringUnion } from "@mui/types";
import { IconButtonPropsColorOverrides, IconButtonPropsSizeOverrides } from "@mui/material/IconButton/IconButton";

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

const AddIcon: React.FC<Props> = (props => {
  const {
 onClick, className, disabled, iconClassName, iconFontSize, color, size
} = props;
  return (
    <IconButton color={color} size={size} onClick={onClick} className={className} disabled={disabled}>
      <AddCircleOutlineOutlinedIcon className={iconClassName || "addButtonColor"} fontSize={iconFontSize || "small"} />
    </IconButton>
  );
});

export default AddIcon;
