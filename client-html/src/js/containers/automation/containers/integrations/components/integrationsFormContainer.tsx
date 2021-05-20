/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import * as React from "react";
import clsx from "clsx";
import Grid from "@material-ui/core/Grid";
import {
   initialize, isDirty, isInvalid, SubmissionError
} from "redux-form";
import { withStyles } from "@material-ui/core/styles";
import { RouteComponentProps, withRouter } from "react-router-dom";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import DeleteForever from "@material-ui/icons/DeleteForever";
import createStyles from "@material-ui/core/styles/createStyles";
import { Integration, IntegrationProp } from "@api/model";
import FormField from "../../../../../common/components/form/form-fields/FormField";
import FormSubmitButton from "../../../../../common/components/form/FormSubmitButton";
import IntegrationDescription from "./IntegrationDescription";
import { IntegrationSchema } from "../../../../../model/automation/integrations/IntegrationSchema";
import * as IntegrationTypes from "../../../../../model/automation/integrations/IntegrationTypes";
import * as IntegrationForms from "./forms/index";
import IntegrationImages from "../IntegrationImages";
import { State } from "../../../../../reducers/state";
import AppBarActions from "../../../../../common/components/form/AppBarActions";
import AppBarHelpMenu from "../../../../../common/components/form/AppBarHelpMenu";
import { getManualLink } from "../../../../../common/utils/getManualLink";
import {
  updateIntegration,
  createIntegration,
  deleteIntegrationItem
} from "../../../actions";
import { setNextLocation, showConfirm } from "../../../../../common/actions";
import { getByType } from "../utils";
import { ShowConfirmCaller } from "../../../../../model/common/Confirm";

const styles = theme => createStyles({
    root: {
      padding: theme.spacing(3),
      height: `calc(100% - ${theme.spacing(8)}px)`,
      marginTop: theme.spacing(8)
    },
    image: {
      "align-self": "flex-end"
    },
    label: {
      margin: theme.spacing(2, 1, 0, 1)
    }
  });

const getAuditsUrl = (id: string) => `audit?search=~"Integration" and entityId == ${id}`;

interface Props {
  integrations: IntegrationSchema[];
  onUpdate: (id: string, item: Integration) => void;
  onCreate: (item: Integration) => void;
  onDelete: (id: string) => void;
  openConfirm?: ShowConfirmCaller;
  classes: any;
  dispatch: any;
  formName: any;
  dirty: boolean;
  invalid: boolean;
  nextLocation: string;
  setNextLocation: (nextLocation: string) => void
}

class FormContainer extends React.Component<Props & RouteComponentProps<any>, any> {
  private resolvePromise;

  private rejectPromise;

  private isPending: boolean = false;

  constructor(props) {
    super(props);

    this.state = {
      canSave: true,
      integrationItem:
        props.match.params.action === "new"
          ? { id: "", type: props.match.params.type, fields: {} }
          : props.integrations
          ? this.getIntegrationItem(props)
          : null
    };
  }

  setCanSave = (flag: boolean) => {
    this.setState({ canSave: flag });
  };

  // eslint-disable-next-line camelcase
  UNSAFE_componentWillReceiveProps(nextProps) {
    if (nextProps.match.params.action === "edit" && nextProps.integrations) {
      this.setState({
        integrationItem: this.getIntegrationItem(nextProps)
      });
    }

    if (!this.isPending) {
      return;
    }
    if (nextProps.fetch && nextProps.fetch.success === false) {
      this.rejectPromise(nextProps.fetch.formError);
    }
    if (nextProps.fetch && nextProps.fetch.success) {
      this.resolvePromise();
      this.isPending = false;
    }
  }

  getIntegrationItem(source) {
    return source.integrations.find(item => item.name === decodeURIComponent(source.location.pathname.split("/")[5]));
  }

  handleDelete = () => {
    const {
     onDelete, history, integrations, openConfirm
    } = this.props;

    const item = this.state.integrationItem;

    const onConfirm = () => {
      this.isPending = true;

      return new Promise((resolve, reject) => {
        this.resolvePromise = resolve;
        this.rejectPromise = reject;

        onDelete(item.id);
      })
        .then(() => {
          if (integrations[0].id === item.id) {
            integrations.length > 1
              ? history.push(
                  `/automation/integrations/edit/${integrations[1].type}/${encodeURIComponent(integrations[1].name)}`
                )
              : history.push("/automation/integrations");
            return;
          }

          history.push(
            `/automation/integrations/edit/${integrations[0].type}/${encodeURIComponent(integrations[0].name)}`
          );
        })
        .catch(() => {
          this.isPending = false;
        });
    };

    openConfirm({
      onConfirm,
      confirmMessage: item && `${item.name} will be removed from integrations list`,
      confirmButtonText: "DELETE"
    });
  };

  validateNameField = value => {
    if (!value) {
      return "Field is mandatory";
    }
    if (value.includes("%")) {
      return "Special symbols not allowed";
    }
    if (!this.props.integrations) {
      return undefined;
    }
    let match;
    if (this.props.match.params.action === "new") {
      match = this.props.integrations.find(item => item.name === value.trim());
    }
    if (this.props.match.params.action === "edit") {
      match = this.props.integrations
        .filter(item => item.name !== this.state.integrationItem.name)
        .find(item => item.name === value.trim());
    }
    if (match) {
      return "Name value must be unique";
    }
    return undefined;
  };

  submitForm = integration => {
    this.isPending = true;

    const {
     onUpdate, onCreate, history, dispatch, formName, nextLocation, setNextLocation
    } = this.props;
    const encodedID = encodeURIComponent(integration.id);
    const data: Integration = parseIntegrationSchema(integration);

    return new Promise((resolve, reject) => {
      this.resolvePromise = resolve;
      this.rejectPromise = reject;

      if (this.props.match.params.action === "new") {
        onCreate(data);
      }

      if (this.props.match.params.action === "edit") {
        onUpdate(encodedID, data);
      }
    })
      .then(() => {
        dispatch(initialize(formName, this.state.integrationItem));
        if (nextLocation) {
          history.push(nextLocation);
          setNextLocation('')
        } else {
          history.push(`/automation/integrations/edit/${integration.type}/${encodeURIComponent(integration.name)}`);
        }
      })
      .catch(error => {
        this.isPending = false;
        const errors: any = {
          fields: {}
        };

        if (error) {
          errors.fields[error.propertyName] = error.errorMessage;
        }

        throw new SubmissionError(errors);
      });
  };

  render() {
    const {
     classes, match, dirty, invalid
    } = this.props;
    const TypeForm = getByType(match.params.type, IntegrationForms);
    const item = this.state.integrationItem;
    const isNew = match.params.action === "new";
    const appBarContent = (
      <Grid
        container
        classes={{
          container: classes.fitSmallWidth
        }}
      >
        <Grid item xs={6} className="centeredFlex">
          <FormField
            type="headerText"
            name="name"
            label="Name"
            validate={this.validateNameField}
            fullWidth
          />
        </Grid>
        <Grid item xs={6} className="centeredFlex">
          <div className="flex-fill" />
          {!isNew && (
            <AppBarActions
              actions={[
                {
                  action: this.handleDelete,
                  icon: <DeleteForever />,
                  tooltip: "Delete Integration",
                  confirmButtonText: "DELETE"
                }
              ]}
            />
          )}

          {!isNew && item && (
            <AppBarHelpMenu
              created={item.created ? new Date(item.created) : null}
              modified={item.modified ? new Date(item.modified) : null}
              auditsUrl={getAuditsUrl(item.id)}
              manualUrl={getManualLink("externalintegrations", item.type)}
            />
          )}

          <FormSubmitButton
            disabled={!dirty || !this.state.canSave}
            invalid={invalid}
          />
        </Grid>
      </Grid>
    );

    const descriptionItem = getByType(match.params.type, IntegrationTypes);

    return (
      <Grid container className={classes.root}>
        <Grid item xs={12} sm={6} lg={5} className={classes.formPadding}>
          {item && TypeForm && (
            <TypeForm
              onSubmit={this.submitForm}
              validateNameField={this.validateNameField}
              item={item}
              appBarContent={appBarContent}
              canSave={this.setCanSave}
              {...this.props}
            />
          )}
        </Grid>
        <Grid item xs={12} sm={5} lg={4} className="flex-column pr-3 text-end">
          <div>
            <img
              alt="integrationLogo"
              src={getByType(match.params.type, IntegrationImages)}
              width={200}
              className={clsx("mb-2", classes.image)}
            />
          </div>
          {descriptionItem && (
            <IntegrationDescription item={descriptionItem} />
          )}
        </Grid>
      </Grid>
    );
  }
}

export const parseIntegrationSchema = (schema: IntegrationSchema): Integration => {
  const properties: IntegrationProp[] = [];
  for (const k in schema.fields) {
    if (schema.fields.hasOwnProperty(k)) {
      properties.push({ key: k, value: schema.fields[k] } as IntegrationProp);
    }
  }

  return {
    id: schema.id,
    type: Number(schema.type),
    name: schema.name,
    verificationCode: schema.verificationCode,
    props: properties
  };
};

const getFormName = form => form && Object.keys(form)[0];

const mapStateToProps = (state: State) => ({
  integrations: state.automation.integration.integrations,
  formName: getFormName(state.form),
  dirty: isDirty(getFormName(state.form))(state),
  invalid: isInvalid(getFormName(state.form))(state),
  fetch: state.fetch,
  nextLocation: state.nextLocation
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
    dispatch,
    onUpdate: (id: string, item: Integration) => dispatch(updateIntegration(id, item)),
    onCreate: (item: Integration) => dispatch(createIntegration(item)),
    onDelete: (id: string) => dispatch(deleteIntegrationItem(id)),
    openConfirm: props => dispatch(showConfirm(props)),
    setNextLocation: (nextLocation: string) => dispatch(setNextLocation(nextLocation)),
  });

export default connect<any, any, any>(
  mapStateToProps,
  mapDispatchToProps
)(withStyles(styles)(withRouter(FormContainer)));
