import React from "react";
import AddCircleOutlineOutlinedIcon from "@mui/icons-material/AddCircleOutlineOutlined";
import IconButton, {IconButtonProps} from "@mui/material/IconButton";

interface Props extends Partial<IconButtonProps>{
  onClick?: any;
  className?: any;
  disabled?: boolean;
  iconClassName?: any;
  iconFontSize?: 'inherit' | 'large' | 'medium' | 'small';
}

const AddIcon: React.FC<Props> = (props => {
  const { onClick, className, disabled, iconClassName, iconFontSize } = props;
  return (
    <IconButton onClick={onClick} className={className} disabled={disabled}>
      <AddCircleOutlineOutlinedIcon className={iconClassName || "addButtonColor"} fontSize={iconFontSize || "small"} />
    </IconButton>
  );
});

export default AddIcon;
