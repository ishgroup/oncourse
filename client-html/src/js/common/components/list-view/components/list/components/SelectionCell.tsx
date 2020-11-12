import * as React from "react";
import Checkbox from "@material-ui/core/Checkbox";
import clsx from "clsx";

const stopPropagation = e => e.stopPropagation();

const SelectionCell = props => {
  const { classes, selected, onToggle } = props;

  const onClick = e => {
    e.stopPropagation();

    onToggle(e);
  };

  return (
    <td className={classes.container}>
      <Checkbox
        onClick={onClick}
        onDoubleClick={stopPropagation}
        checked={selected}
        className={clsx(classes.checkbox, {
          [classes.hidden]: !selected
        })}
      />
    </td>
  );
};

export default SelectionCell;
