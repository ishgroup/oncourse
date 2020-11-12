/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import * as React from "react";
import { withRouter, RouteComponentProps } from "react-router-dom";
import { Collapse, Typography, withStyles } from "@material-ui/core";
import { Field, change } from "redux-form";
import createStyles from "@material-ui/core/styles/createStyles";
import clsx from "clsx";
import FormField from "../../../../../common/components/form/form-fields/FormField";
import { Switch } from "../../../../../common/components/form/form-fields/Switch";
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

const ContentBase = props => {
  const {
    classes,
    item: { permissions, headers }
  } = props;

  return (
    <div className="d-flex">
      <div className="mt-auto">
        {permissions.map((item, index) => (
          <Typography variant="body2" key={index} className={clsx("centeredFlex", classes.stepperLabel)}>
            {item.name}
          </Typography>
        ))}
      </div>

      <div className="flex-fill">
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
        {permissions.map((i, index) => (i.checkbox ? (
          <FormField
            type="checkbox"
            name={i.name}
            key={index}
            classes={{ root: classes.checkboxColor }}
            className={classes.checkbox}
            color="primary"
            stringValue
          />
          ) : (
            <Field
              name={i.name}
              component={SliderStepperField}
              key={index}
              item={i}
              headers={headers}
              className="relative d-flex"
            />
          )))}
      </div>
    </div>
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
