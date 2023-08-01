import ArrowDown from "@mui/icons-material/KeyboardArrowDown";
import IconButton from "@mui/material/IconButton";
import Menu from "@mui/material/Menu";
import MenuItem from "@mui/material/MenuItem";
import Typography from "@mui/material/Typography";
import React, { useCallback } from "react";
import { CustomSelectorOption } from "./CustomSelector";

interface Props {
  className?: any;
  caption: string;
  options: CustomSelectorOption[];
  selectedIndex: number;
  setSelectedIndex: (index: number) => void;
  onSelect: (index: number) => void;
  disabled?: boolean;
}

const CustomSelectorHeader = ({
  className,
  options,
  caption,
  selectedIndex,
  setSelectedIndex,
  onSelect,
  disabled
}: Props) => {
  const [anchorEl, setAnchorEl] = React.useState<null | HTMLElement>(null);

  const handleClickListItem = useCallback(event => {
    setAnchorEl(event.currentTarget);
  }, []);

  const handleMenuItemClick = useCallback(
    (event, index) => {
      setAnchorEl(null);
      setSelectedIndex(index);
      onSelect(index);
    },
    [onSelect, setSelectedIndex]
  );

  const handleClose = useCallback(() => {
    setAnchorEl(null);
  }, []);

  return (
    <div className={className}>
      <Typography variant="caption">{caption}</Typography>
      {" "}
      <Typography
        variant="caption"
        className="d-inline-flex-center cursor-pointer text-bold"
        onClick={disabled ? undefined : handleClickListItem}
        color={disabled ? "textSecondary" : "initial"}
      >
        {options[selectedIndex].caption}
        {" "}
        <IconButton className="p-0" disabled={disabled}>
          <ArrowDown />
        </IconButton>
      </Typography>
      <Menu id="lock-menu" anchorEl={anchorEl} keepMounted open={Boolean(anchorEl)} onClose={handleClose}>
        {options.map((option, index) => (
          <MenuItem
            key={option.caption}
            selected={index === selectedIndex}
            onClick={event => handleMenuItemClick(event, index)}
          >
            {option.caption}
          </MenuItem>
        ))}
      </Menu>
    </div>
  );
};

export default CustomSelectorHeader;
