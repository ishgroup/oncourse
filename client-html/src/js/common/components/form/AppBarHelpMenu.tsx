import * as React from "react";
import { withStyles, createStyles } from "@mui/styles";
import clsx from "clsx";
import HelpOutline from "@mui/icons-material/HelpOutline";
import IconButton from "@mui/material/IconButton";
import Typography from "@mui/material/Typography";
import Menu from "@mui/material/Menu";
import MenuItem from "@mui/material/MenuItem";
import Tooltip from "@mui/material/Tooltip";
import { format as formatDate } from "date-fns";
import { III_DD_MMM_YYYY_HH_MM_SPECIAL } from "../../utils/dates/format";
import { openInternalLink } from "../../utils/links";

const styles = theme =>
  createStyles({
    button: {
      color: theme.appBarButton.helpMenu.color,
    },
    info: {
      display: "flex",
      flexDirection: "column",
      alignItems: "baseline",
      padding: theme.spacing(1, 2, 0, 2)
    },
    divider: {
      marginTop: theme.spacing(2),
      borderTop: `1px solid ${theme.palette.divider}`
    }
  });

interface Props {
  classes?: any;
  created?: any;
  modified?: any;
  manualUrl?: string;
  auditsUrl?: string;
  iconClasses?: any;
}

class AppBarHelpMenu extends React.Component<Props, any> {
  constructor(props) {
    super(props);

    this.state = {
      anchorEl: null
    };
  }

  handleAddFieldClick = e => {
    this.setState({ anchorEl: e.currentTarget });
  };

  handleClose = () => {
    this.setState({ anchorEl: null });
  };

  openManual = () => {
    window.open(this.props.manualUrl);
    this.handleClose();
  };

  openAudits = () => {
    openInternalLink("/" + this.props.auditsUrl);
    this.handleClose();
  };

  render() {
    const { anchorEl } = this.state;
    const {
      classes, created, modified, iconClasses, auditsUrl
    } = this.props;

    return (
      <>
        <Menu
          id="form-help-menu"
          anchorEl={anchorEl}
          open={Boolean(anchorEl)}
          onClose={this.handleClose}
          marginThreshold={16}
          transformOrigin={{
            vertical: "center",
            horizontal: "right"
          }}
        >
          {created && (
            <MenuItem disabled className={classes.info}>
              <Typography variant="caption">Created</Typography>
              <Typography variant="subtitle1">
                {typeof created === "object" && isFinite(created)
                  ? formatDate(created, III_DD_MMM_YYYY_HH_MM_SPECIAL)
                  : "---- ---- ---- ---- ----"}
              </Typography>
            </MenuItem>
          )}

          {modified && (
            <MenuItem disabled className={classes.info}>
              <Typography variant="caption">Modified</Typography>
              <Typography variant="subtitle1">
                {typeof modified === "object" && isFinite(modified)
                  ? formatDate(modified, III_DD_MMM_YYYY_HH_MM_SPECIAL)
                  : "---- ---- ---- ---- ----"}
              </Typography>
            </MenuItem>
          )}

          <MenuItem className={created || modified ? classes.divider : undefined} onClick={this.openManual}>
            Open manual
          </MenuItem>
          {auditsUrl && (
            <MenuItem onClick={this.openAudits}>
              View audit trail
            </MenuItem>
          )}
        </Menu>

        <Tooltip title="Additional information">
          <div>
            <IconButton
              type="button"
              aria-owns={anchorEl ? "form-help-menu" : null}
              aria-haspopup="true"
              onClick={this.handleAddFieldClick}
              size="large"
              className={clsx({
                [classes.button]: !iconClasses,
                [iconClasses]: !!iconClasses
              })}
            >
              <HelpOutline />
            </IconButton>
          </div>
        </Tooltip>
      </>
    );
  }
}

export default withStyles(styles)(AppBarHelpMenu);
