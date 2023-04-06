import React, { useCallback } from "react";
import Chip from "@mui/material/Chip";
import { createStyles, withStyles } from "@mui/styles";
import { Theme } from "@mui/material/styles";
import { CommonListFilter } from "../../../../../model/common/sidebar";

interface Props {
  filters: CommonListFilter[];
  activeFilters: boolean[];
  setActveFilters: any;
  classes?: any;
}

const styles = createStyles((theme: Theme) => ({
  list: {
    padding: theme.spacing(0.5, 3)
  },
  chipRoot: {
    height: "auto",
    fontSize: "11px",
    margin: theme.spacing(0.5, 0.5, 0, 0),
    padding: theme.spacing(0.3125, 1),
    "&$chipOutlined $chipIcon": {
      marginLeft: theme.spacing(-0.5),
      marginRight: 0
    }
  },
  chipIcon: {
    fontSize: "12px",
    margin: theme.spacing(-0.125, 0.25, 0, -0.5)
  },
  chipBorder: {
    border: "1px solid transparent"
  },
  chipOutlined: {},
  clickable: {
    "&$chipOutlined:focus": {
      backgroundColor: "transparent"
    }
  }
}));

const FiltersList = React.memo<Props>(({
 filters, activeFilters, setActveFilters, classes 
}) => {
  const onClick = useCallback(e => {
    const index = Number(e.currentTarget.getAttribute("role"));

    setActveFilters(prev => {
      const changed = [...prev];

      changed[index] = !prev[index];

      return changed;
    });
  }, []);

  return (
    <div className={classes.list}>
      {filters.map((f, i) => (
        <Chip
          key={i + f.name}
          classes={{
            root: `${classes.chipRoot} ${activeFilters[i] ? classes.chipBorder : ""}`,
            icon: classes.chipIcon,
            outlined: classes.chipOutlined,
            clickable: classes.clickable,
            label: "p-0"
          }}
          label={f.name}
          icon={f.icon}
          color={activeFilters[i] ? "secondary" : undefined}
          variant={activeFilters[i] ? "filled" : "outlined"}
          onClick={onClick}
          role={i.toString()}
        />
      ))}
    </div>
  );
});

export default withStyles(styles)(FiltersList);
