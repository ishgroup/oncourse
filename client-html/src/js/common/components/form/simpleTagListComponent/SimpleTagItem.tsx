import React from "react";
import { Tag } from "@api/model";
import Typography from "@mui/material/Typography";
import Delete from "@mui/icons-material/Delete";
import Tooltip from "@mui/material/Tooltip";
import clsx from "clsx";
import ButtonBase from "@mui/material/ButtonBase";
import { NumberArgFunction } from "../../../../model/common/CommonFunctions";

interface Props {
  item: Tag;
  classes: any;
  index: number;
  onDelete: NumberArgFunction;
}

const SimpleTagItem = (props: Props) => {
  const {
    item, classes, onDelete, index
  } = props;

  return (
    <Tooltip title="Delete Tag">
      <ButtonBase className={clsx("centeredFlex", classes.tagBody)} onClick={() => onDelete(index)}>
        <Typography variant="body2" className={classes.tagBodyTypography}>
          #
          {item.name}
        </Typography>

        <div className={classes.tagDeleteButton}>
          <Delete className={classes.tagDeleteIcon} />
        </div>
      </ButtonBase>
    </Tooltip>
  );
};

export default SimpleTagItem;
