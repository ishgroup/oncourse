import React from "react";
import { Tag } from "@api/model";
import Typography from "@material-ui/core/Typography";
import Delete from "@material-ui/icons/Delete";
import Tooltip from "@material-ui/core/Tooltip";
import clsx from "clsx";
import ButtonBase from "@material-ui/core/ButtonBase";
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
