import Collapse from "@material-ui/core/Collapse";
import IconButton from "@material-ui/core/IconButton";
import Paper from "@material-ui/core/Paper";
import { ExpandMore } from "@material-ui/icons";
import clsx from "clsx";
import React, { useMemo, useState } from "react";
import {
 Typography, Grid, List, ListItem, createStyles, withStyles
} from "@material-ui/core";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import ExecuteScriptModal from "../../../../../../containers/automation/containers/scripts/components/ExecuteScriptModal";
import { getHighlightedPartLabel } from "../../../../../utils/formatting";
import Button from "../../../../buttons/Button";
import UserSearch from "../UserSearch";

const styles = theme => createStyles({
  scriptHeading: {
    display: "flex",
    justifyContent: "space-between"
  },
  collapseButton: {
    transition: `transform ${theme.transitions.duration.shortest}ms ${theme.transitions.easing.easeInOut}`,
    padding: 0,
    marginLeft: theme.spacing(0.5)
  },
  collapseButtonReversed: {
    transform: "rotate(180deg)"
  },
  expandedPaper: {
    margin: theme.spacing(1)
  },
  collapsedPaper: {
    boxShadow: "none",
    background: "transparent"
  }
});

const SidebarScriptsList: React.FC<any> = props => {
  const { classes, scripts } = props;
  const [scriptIdSelected, setScriptIdSelected] = useState(null);
  const [search, setSearch] = useState("");
  const [execMenuOpened, setExecMenuOpened] = useState(false);

  const renderScripts = useMemo(() => (
    scripts.filter(s => s.name.includes(search.trim())).map(s => {
      const itemSelected = scriptIdSelected === s.id;

      return (
        <>
          <Paper classes={{
            root: itemSelected
              ? classes.expandedPaper
              : classes.collapsedPaper
            }}
          >
            <ListItem
              disableGutters
              button
              onClick={() => setScriptIdSelected(itemSelected ? null : s.id)}
              key={s.id}
              className={clsx("pl-2 pr-2", classes.scriptHeading)}
            >
              <span className="relative">{getHighlightedPartLabel(s.name, search)}</span>
              <IconButton
                className={clsx(classes.collapseButton, "d-inline-flex", { [classes.collapseButtonReversed]: itemSelected })}
              >
                <ExpandMore />
              </IconButton>
            </ListItem>

            <Collapse in={scriptIdSelected === s.id} mountOnEnter unmountOnExit className="pl-2 pr-2">
              <div className="d-flex justify-content-end pt-2 pb-2">
                <Button color="primary" onClick={() => setExecMenuOpened(true)}>
                  Run now
                </Button>
              </div>
            </Collapse>
          </Paper>
        </>
      );
    })
  ), [scripts, search, scriptIdSelected]);

  return (
    <>
      <ExecuteScriptModal
        opened={execMenuOpened}
        onClose={() => setExecMenuOpened(false)}
        scriptId={scriptIdSelected}
      />
      <Grid container justify="space-between">
        <Grid item xs={12}>
          <Typography className="heading pl-2 pr-2 pt-2">ON DEMAND SCRIPTS</Typography>
        </Grid>

        <Grid item xs={12}>
          <UserSearch getSearchResults={setSearch} placeholder="Filter items" />
        </Grid>

        <Grid item xs={12}>
          <List classes={{ root: classes.root }}>{renderScripts}</List>
        </Grid>
      </Grid>
    </>
  );
};

const mapDispatchToProps = (dispatch: Dispatch) => ({
  dispatch
});

export default connect(null, mapDispatchToProps)(withStyles(styles)(SidebarScriptsList));
