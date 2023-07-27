import React, { useCallback, useState } from "react";
import { change } from "redux-form";
import clsx from "clsx";
import createStyles from "@mui/styles/createStyles";
import withStyles from "@mui/styles/withStyles";
import Typography from "@mui/material/Typography";
import Button from "@mui/material/Button";
import Grid from "@mui/material/Grid";
import IconButton from "@mui/material/IconButton";
import Paper from "@mui/material/Paper";
import CloseIcon from "@mui/icons-material/Close";
import UploadIcon from "@mui/icons-material/Upload";
import HelpOutlineIcon from "@mui/icons-material/HelpOutline";
import CodeIcon from "@mui/icons-material/Code";
import EmailOutlinedIcon from "@mui/icons-material/EmailOutlined";
import StackedLineChartIcon from "@mui/icons-material/StackedLineChart";
import AddCircleOutlineIcon from "@mui/icons-material/AddCircleOutline";
import AddIcon from "@mui/icons-material/Add";
import Collapse from "@mui/material/Collapse";
import { transition } from "ish-ui";

const styles = createStyles(theme => ({
  addActionButton: {
    position: "relative",
    marginLeft: theme.spacing(-9),
    paddingLeft: theme.spacing(9),
    zIndex: 1,
  },
  addActionButtonHover: {
    "&:hover": {
      "& $cardLeftIcon": {
        color: theme.heading.color
      },
      "& $buttonRoot": {
        height: "auto",
        padding: theme.spacing(3.5, 2),
        border: `2px dashed ${theme.palette.divider}`,
        visibility: "visible",
        opacity: 1,
      },
      "&:not($active) $addActionWrapper": {
        margin: theme.spacing(3, 0),
      }
    },
  },
  addActionWrapper: {
    minHeight: theme.spacing(5.75),
    ...transition(theme, "all"),
  },
  buttonRoot: {
    height: 0,
    padding: 0,
    border: 0,
    textAlign: "center",
    ...transition(theme, "visibility, opacity, height, padding, border"),
    visibility: "hidden",
    opacity: 0,
  },
  activeStep: {
    marginTop: theme.spacing(3),
    "& $buttonRoot": {
      height: "auto",
      padding: theme.spacing(3.5, 2),
      border: `2px dashed ${theme.palette.divider}`,
      visibility: "visible",
      opacity: 1,
    }
  },
  actionPaper: {
    margin: theme.spacing(3, 0),
  },
  heading: {
    borderBottom: `1px solid ${theme.palette.divider}`,
    "& .heading": {
      margin: "20px 0",
    }
  },
  actionItem: {
    textTransform: "initial",
    lineHeight: "initial",
  },
  itemTitle: {
    fontWeight: "bold"
  },
  importIcon: {
    backgroundColor: "#f6ecf5",
    color: "#9e5193"
  },
  queryIcon: {
    backgroundColor: "#fef4e8",
    color: "#f7941d"
  },
  scriptIcon: {
    backgroundColor: "#fef4e8",
    color: "#f7941d"
  },
  messageIcon: {
    backgroundColor: "#eeeff4",
    color: "#4b6390"
  },
  reportIcon: {
    backgroundColor: "#f6ecf5",
    color: "#9e5193"
  },
  cardLeftIcon: {
    position: "absolute",
    left: 13,
    width: 20,
    height: 20,
    zIndex: 1,
    top: "50%",
    transform: "translateY(-50%)",
    backgroundColor: `${theme.appBar.header.background}`,
    color: theme.palette.divider,
    "&:hover": {
      cursor: "inherit"
    }
  },
  cardPlusIconActive: {
    color: theme.heading.color
  },
  activeLeftButton: {
    left: -3,
    top: `calc(50% + ${theme.spacing(1.5)})`,
    width: 50,
    height: 50,
    backgroundColor: theme.palette.mode === "light" ? "#eaebe6" : theme.palette.background.paper,
    color: theme.heading.color
  },
  active: {},
}));

interface ScriptActionProps {
  icon: any;
  title: string;
  detail?: any;
  classes?: any;
  iconClass?: any;
  addAction?: (title: string) => void;
  disabled?: boolean;
}

const ScriptAction = React.memo<ScriptActionProps>(props => {
  const {
    icon, title, detail, classes, iconClass, addAction, disabled
  } = props;
  return (
    <Button
      className={clsx("d-flex-start p-1", classes.actionItem)}
      component="div"
      color="inherit"
      onClick={() => addAction(title)}
      disabled={disabled}
      disableRipple
    >
      <IconButton size="large" centerRipple className={iconClass} disabled={disabled} disableRipple>
        {icon}
      </IconButton>
      <div className="pl-2 mt-0-5">
        <Typography variant="body1" gutterBottom className={classes.itemTitle}>
          {title}
        </Typography>
        {!!detail && (
          <Typography variant="caption" gutterBottom>{detail}</Typography>
        )}
      </div>
    </Button>
  );
});

const AddScriptAction: React.FC<any> = props => {
  const {
    classes, index, values, dispatch, form, addComponent, hasUpdateAccess, active, disabled
  } = props;

  const [open, setOpen] = useState<boolean>(false);

  const addImport = useCallback(() => {
    dispatch(change(form, "imports", [""]));
    setOpen(false);
  }, [form]);

  const addComponentHandler = useCallback(title => {
    addComponent(title, index);
    setOpen(false);
  }, [index]);

  const hasImports = Boolean(values && values.imports);

  return (
    <div className={clsx(classes.addActionButton, { [classes.addActionButtonHover]: !disabled, [classes.active]: active })}>
      <IconButton
        size={active ? "large" : "small"}
        className={clsx(classes.cardLeftIcon, { [classes.cardPlusIconActive]: open && !disabled, [classes.activeLeftButton]: active })}
        onClick={() => setOpen(true)}
        disableRipple
      >
        {active ? <AddIcon /> : <AddCircleOutlineIcon />}
      </IconButton>
      <Collapse in={!open || disabled}>
        <div className={clsx(classes.addActionWrapper, { [classes.activeStep]: active })}>
          <Button
            component="div"
            variant="outlined"
            classes={{ root: classes.buttonRoot }}
            className="w-100 heading"
            onClick={() => setOpen(true)}
          >
            Add step / action
          </Button>
        </div>
      </Collapse>

      <Collapse in={open && !disabled}>
        <Paper className={classes.actionPaper}>
          <div className={clsx('centeredFlex pl-2 pr-1', classes.heading)}>
            <Typography className="heading flex-fill">
              Select an action
            </Typography>
            <IconButton onClick={() => setOpen(false)}>
              <CloseIcon />
            </IconButton>
          </div>
          <Grid container className="p-2 pl-1 pr-1">
            <Grid item xs={12} md={6}>
              <ScriptAction
                icon={<UploadIcon />}
                title="Import"
                detail="The Import block allows you to import external Java and Groovy libraries to be used in your script. Libraries can give you access to certain methods or classes to be used in an Advanced script block."
                classes={classes}
                iconClass={classes.importIcon}
                addAction={addImport}
                disabled={hasImports || !hasUpdateAccess}
              />
            </Grid>
            <Grid item xs={12} md={6}>
              <ScriptAction
                icon={<HelpOutlineIcon />}
                title="Query"
                detail="The Query block allows you to retrieve records from your database. You must specify what entity type is to be returned from your query, as well as provide a name to reference the returned objects."
                classes={classes}
                iconClass={classes.queryIcon}
                addAction={addComponentHandler}
              />
            </Grid>
            <Grid item xs={12} md={6}>
              <ScriptAction
                icon={<CodeIcon />}
                title="Script"
                detail="The script block contains the core code that powers your script. Write code directly into this block and it will be executed when the script is triggered."
                classes={classes}
                iconClass={classes.scriptIcon}
                addAction={addComponentHandler}
              />
            </Grid>
            <Grid item xs={12} md={6}>
              <ScriptAction
                icon={<EmailOutlinedIcon />}
                title="Message"
                detail="The message block allows you to set a message template to be sent out by the script, or create a message within the script itself to be emailed to set a email address."
                classes={classes}
                iconClass={classes.messageIcon}
                addAction={addComponentHandler}
              />
            </Grid>
            <Grid item xs={12} md={6}>
              <ScriptAction
                icon={<StackedLineChartIcon />}
                title="Report"
                detail="The report block allows you to set a report that is generated by the running of a script. The reports to choose from in this list come from the PDF reports section of your Automation window."
                classes={classes}
                iconClass={classes.reportIcon}
                addAction={addComponentHandler}
              />
            </Grid>
          </Grid>
        </Paper>
      </Collapse>
    </div>
  );
};

export default withStyles(styles)(AddScriptAction);