import * as React from "react";
import { createStyles, withStyles } from "@material-ui/core/styles";
import Typography from "@material-ui/core/Typography";
import { Menu, MenuItem } from "@material-ui/core";
import AddIcon from "@material-ui/icons/Add";
import Fab from "@material-ui/core/Fab";
import { change } from "redux-form";

const styles = theme =>
  createStyles({
    menuWrapper: {
      marginTop: theme.spacing(10)
    },
    listItemPadding: {
      padding: theme.spacing(0.5, 8, 0.5, 2),
      minHeight: "unset"
    },
    fab: {
      position: "relative",
      left: theme.spacing(-1)
    }
  });

class ScriptAddMenu extends React.Component<any, any> {
  constructor(props) {
    super(props);

    this.state = {
      anchorEl: null
    };
  }

  componentWillReceiveProps(nextProps) {}

  handleAddFieldClick = e => {
    this.setState({ anchorEl: e.currentTarget });
  };

  handleAddFieldClose = () => {
    this.setState({ anchorEl: null });
  };

  addComponent = e => {
    const { addComponent } = this.props;
    addComponent(e.currentTarget.getAttribute("data-component"));
  };

  addImport = () => {
    const { dispatch, form } = this.props;

    dispatch(change(form, "imports", [""]));
  };

  render() {
    const { anchorEl } = this.state;
    const { classes, values, hasUpdateAccess } = this.props;

    const hasImports = Boolean(values && values.imports);

    return (
      <>
        <Menu
          id="field-types-menu"
          anchorEl={anchorEl}
          open={Boolean(anchorEl)}
          onClose={this.handleAddFieldClose}
          className={classes.menuWrapper}
          MenuListProps={{
            classes: {
              root: "p-1"
            }
          }}
        >
          <div className="outline-none mt-1">
            <div className="d-flex">
              {/* <div className={"flex-fill"}> */}
              {/* <Typography className={clsx("heading", "pl-2", "mb-1")}>Trigger</Typography> */}
              {/* <MenuItem className={classes.listItemPadding} button> */}
              {/* <Typography variant="subtitle1" color="textSecondary"> */}
              {/* Schedule */}
              {/* </Typography> */}
              {/* </MenuItem> */}
              {/* <MenuItem className={classes.listItemPadding} button> */}
              {/* <Typography variant="subtitle1" color="textSecondary"> */}
              {/* On create */}
              {/* </Typography> */}
              {/* </MenuItem> */}
              {/* <MenuItem className={classes.listItemPadding} button> */}
              {/* <Typography variant="subtitle1" color="textSecondary"> */}
              {/* On edit */}
              {/* </Typography> */}
              {/* </MenuItem> */}
              {/* <MenuItem className={classes.listItemPadding} button> */}
              {/* <Typography variant="subtitle1" color="textSecondary"> */}
              {/* On delete */}
              {/* </Typography> */}
              {/* </MenuItem> */}
              {/* <MenuItem className={classes.listItemPadding} button> */}
              {/* <Typography variant="subtitle1" color="textSecondary"> */}
              {/* Publish to web */}
              {/* </Typography> */}
              {/* </MenuItem> */}
              {/* <MenuItem className={classes.listItemPadding} button> */}
              {/* <Typography variant="subtitle1" color="textSecondary"> */}
              {/* Enrolment */}
              {/* </Typography> */}
              {/* </MenuItem> */}

              {/* <Typography className={clsx("heading", "pl-2", "mb-1", "mt-3")}> */}
              {/* Search */}
              {/* </Typography> */}
              {/* <MenuItem className={classes.listItemPadding} button disabled> */}
              {/* <Typography variant="subtitle1" color="textSecondary"> */}
              {/* Find records */}
              {/* </Typography> */}
              {/* </MenuItem> */}

              {/* <Typography className={clsx("heading", "pl-2", "mb-1", "mt-3")}> */}
              {/* Join */}
              {/* </Typography> */}
              {/* <MenuItem className={classes.listItemPadding} button disabled> */}
              {/* <Typography variant="subtitle1" color="textSecondary"> */}
              {/* Transform records */}
              {/* </Typography> */}
              {/* </MenuItem> */}
              {/* </div> */}
              <div className="flex-fill">
                <Typography className="heading pl-2 mb-1">Action</Typography>
                <MenuItem
                  button
                  disabled={hasImports || !hasUpdateAccess}
                  className={classes.listItemPadding}
                  data-component="Import"
                  onClick={this.addImport}
                >
                  <Typography variant="subtitle1" color="textSecondary">
                    Import
                  </Typography>
                </MenuItem>

                <MenuItem button className={classes.listItemPadding} data-component="Query" onClick={this.addComponent}>
                  <Typography variant="subtitle1" color="textSecondary">
                    Query
                  </Typography>
                </MenuItem>

                <MenuItem
                  button
                  disabled={!hasUpdateAccess}
                  className={classes.listItemPadding}
                  data-component="Script"
                  onClick={this.addComponent}
                >
                  <Typography variant="subtitle1" color="textSecondary">
                    Script
                  </Typography>
                </MenuItem>

                {/* <MenuItem className={classes.listItemPadding} button disabled> */}
                {/* <Typography variant="subtitle1" color="textSecondary"> */}
                {/* Email */}
                {/* </Typography> */}
                {/* </MenuItem> */}
                {/* <MenuItem className={classes.listItemPadding} button disabled> */}
                {/* <Typography variant="subtitle1" color="textSecondary"> */}
                {/* Email document */}
                {/* </Typography> */}
                {/* </MenuItem> */}
                {/* <MenuItem className={classes.listItemPadding} button disabled> */}
                {/* <Typography variant="subtitle1" color="textSecondary"> */}
                {/* SMS */}
                {/* </Typography> */}
                {/* </MenuItem> */}
                {/* <MenuItem className={classes.listItemPadding} button disabled> */}
                {/* <Typography variant="subtitle1" color="textSecondary"> */}
                {/* Report */}
                {/* </Typography> */}
                {/* </MenuItem> */}
                {/* <MenuItem className={classes.listItemPadding} button disabled> */}
                {/* <Typography variant="subtitle1" color="textSecondary"> */}
                {/* Export */}
                {/* </Typography> */}
                {/* </MenuItem> */}
                {/* <MenuItem className={classes.listItemPadding} button disabled> */}
                {/* <Typography variant="subtitle1" color="textSecondary"> */}
                {/* Store document */}
                {/* </Typography> */}
                {/* </MenuItem> */}

                {/* <Typography className={clsx("heading", "pl-2", "mb-1", "mt-3")}> */}
                {/* Advanced */}
                {/* </Typography> */}
                {/* <MenuItem className={classes.listItemPadding} button> */}
                {/* <Typography variant="subtitle1" color="textSecondary"> */}
                {/* Custom script */}
                {/* </Typography> */}
                {/* </MenuItem> */}
              </div>
            </div>
          </div>
        </Menu>
        <Fab
          type="button"
          size="small"
          color="primary"
          classes={{
            sizeSmall: "appBarFab",
            root: classes.fab
          }}
          aria-owns={anchorEl ? "field-types-menu" : null}
          aria-haspopup="true"
          onClick={this.handleAddFieldClick}
        >
          <AddIcon />
        </Fab>
      </>
    );
  }
}

export default withStyles(styles)(ScriptAddMenu);
