/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import * as React from "react";
import { withRouter, RouteComponentProps } from "react-router-dom";
import { Collapse, Grid, Typography } from "@mui/material";
import { withStyles } from "@mui/styles";
import { Field, change } from "redux-form";
import createStyles from "@mui/styles/createStyles";
import clsx from "clsx";
import FormField from "../../../../../common/components/form/formFields/FormField";
import { Switch } from  "ish-ui";
import SliderStepperField from "./SliderStepperField";

const styles = theme =>
  createStyles({
    stepperLabel: {
      height: "32px",
      paddingBottom: "2px",
      paddingRight: "10px",
      minWidth: "240px"
    },
    stepperHeader: {
      position: "absolute",
      left: "-50%",
      bottom: "50%",
      transform: "translateY(50%)",
      width: "100%",
      textAlign: "center"
    },
    stepperHeadersWrapper: {
      padding: theme.spacing(2, 0),
      "& .flex-fill:first-child $stepperHeader": {
        paddingLeft: "10px"
      }
    },
    checkbox: {
      height: "32px",
      display: "flex",
      margin: "0 0 0 -10px"
    },
    checkboxColor: {
      color: `${theme.palette.primary.main}`
    }
  });

const getLabel = (category: string) => {
  switch (category) {
    case "EmailTemplate":
      return "Message templates";
    case "ExportTemplate":
      return "Export templates";
    default:
      return category;
  }
}

const ContentBase = props => {
  const {
    classes,
    item: { permissions, headers }
  } = props;

  return (
    <Grid container>
      <Grid item xs={3}/>
      <Grid item xs={9}>
        <div className={clsx("d-flex", classes.stepperHeadersWrapper)}>
          {headers
          && headers.map((i, index) => (
            <Typography variant="body2" key={index} component="div" className="flex-fill">
              <div className="relative">
                <div className={classes.stepperHeader}>{i}</div>
              </div>
            </Typography>
          ))}
        </div>
      </Grid>
      <Grid item container xs={12}>
        {permissions.map((item, index) => (
          <Grid item key={index} xs={12} container>
            <Grid item xs={3}>
              <Typography variant="body2" className={clsx("centeredFlex", classes.stepperLabel)}>
                {getLabel(item.name)}
              </Typography>
            </Grid>
            <Grid item xs={9}>
              {item.checkbox ? (
                <FormField
                  type="checkbox"
                  name={item.name}
                  key={index}
                  className={clsx(classes.checkbox, classes.checkboxColor)}
                  color="primary"
                  stringValue
                />
                ) : (
                  <Field
                    name={item.name}
                    component={SliderStepperField}
                    key={index}
                    item={item}
                    headers={headers}
                    className="relative d-flex"
                  />
                )}
            </Grid>
          </Grid>
          ))}
      </Grid>
    </Grid>
  );
};

const Content = withStyles(styles)(ContentBase);

interface Props {
  item?;
  dispatch?;
  initial?;
  values;
}

class UserRolePreference extends React.PureComponent<Props & RouteComponentProps<any>, any> {
  private unlisten: any;

  constructor(props) {
    super(props);

    this.state = {
      checked: false,
      valuesChecked: false
    };
  }

  changeSwitchStatus = (e, checked) => {
    this.setState({
      checked
    });

    if (!checked) {
      const {
        dispatch,
        item: { permissions }
      } = this.props;

      permissions.forEach(p => {
        dispatch(change("UserRolesForm", p.name, p.checkbox ? "false" : "Hide"));
      });
    }
  };

  checkValues = values => {
    const {
      item: { permissions }
    } = this.props;

    this.setState({
      checked: permissions.some(i => (i.checkbox ? values[i.name] !== "false" : values[i.name] !== "Hide")),
      valuesChecked: true
    });
  };

  onHistoryChange = () => {
    this.setState({
      valuesChecked: false
    });
  };

  componentWillReceiveProps({ values }) {
    if (!this.state.valuesChecked) {
      this.checkValues(values);
    }
  }

  componentDidMount() {
    this.checkValues(this.props.values);
    this.unlisten = this.props.history.listen(() => {
      this.onHistoryChange();
    });
  }

  componentWillUnmount() {
    this.unlisten();
  }

  render() {
    const { item, dispatch, initial } = this.props;
    const { checked } = this.state;

    return (
      <>
        <div className="centeredFlex">
          <Typography className={clsx("heading", "labelOffset")}>{item.type}</Typography>

          <Switch className="ml-1" checked={checked} onChange={this.changeSwitchStatus} />
        </div>

        <Collapse in={checked} className="mb-2">
          <Content item={item} dispatch={dispatch} initial={initial} />
        </Collapse>
      </>
    );
  }
}

export default withRouter(UserRolePreference as any) as any;
