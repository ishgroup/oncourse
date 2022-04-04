/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import * as React from "react";
import clsx from "clsx";
import Grid from "@mui/material/Grid";
import {
  getFormSyncErrors,
  initialize, isDirty, isInvalid, SubmissionError
} from "redux-form";
import { withStyles } from "@mui/styles";
import { RouteComponentProps, withRouter } from "react-router-dom";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import DeleteForever from "@mui/icons-material/DeleteForever";
import createStyles from "@mui/styles/createStyles";
import { Integration, IntegrationProp } from "@api/model";
import FormField from "../../../../../common/components/form/formFields/FormField";
import IntegrationDescription from "./IntegrationDescription";
import { IntegrationSchema } from "../../../../../model/automation/integrations/IntegrationSchema";
import IntegrationTypes from "../IntegrationTypes";
import { State } from "../../../../../reducers/state";
import AppBarActions from "../../../../../common/components/form/AppBarActions";
import { getManualLink } from "../../../../../common/utils/getManualLink";
import { createIntegration, deleteIntegrationItem, updateIntegration } from "../../../actions";
import { setNextLocation, showConfirm } from "../../../../../common/actions";
import { ShowConfirmCaller } from "../../../../../model/common/Confirm";
import AppBarContainer from "../../../../../common/components/layout/AppBarContainer";

const styles = theme => createStyles({
  root: {
    height: `calc(100% - ${theme.spacing(8)})`,
    marginTop: theme.spacing(8)
  },
  image: {
    "align-self": "flex-end"
  },
  label: {
    margin: theme.spacing(2, 1, 0, 1)
  }
});

const getAuditsUrl = (id: number) => `audit?search=~"Integration" and entityId == ${id}`;

interface Props {
  syncErrors: any;
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
    
    const {
     match, integrations
    } = props;

    const type = match.params.type;

    this.state = {
      isPending: false,
      integrationItem:
        match.params.id === "new"
          ? { id: "", type, fields: {} }
          : integrations
            ? this.getIntegrationItem(props)
            : null
    };
  }

  // eslint-disable-next-line camelcase
  UNSAFE_componentWillReceiveProps(nextProps) {
    if (nextProps.match.params.id !== "new" && nextProps.integrations) {
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
      this.setState({
        isPending: false
      });
    }
  }

  getIntegrationItem(source) {
    return source.integrations.find(item => String(item.id) === this.props.match.params.id);
  }

  handleDelete = () => {
    const {
     onDelete, history, openConfirm
    } = this.props;

    const item = this.state.integrationItem;

    const onConfirm = () => {
      this.isPending = true;
      this.setState({
        isPending: true
      });

      return new Promise((resolve, reject) => {
        this.resolvePromise = resolve;
        this.rejectPromise = reject;

        onDelete(item.id);
      })
        .then(() => {
          history.push("/automation/integrations")
        })
        .catch(() => {
          this.isPending = false;
          this.setState({
            isPending: false
          });
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
    if (this.props.match.params.id === "new") {
      match = this.props.integrations.find(item => item.name === value.trim());
    } else {
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
    this.setState({
      isPending: true
    });

    const {
     onUpdate, onCreate, history, dispatch, formName, nextLocation, setNextLocation
    } = this.props;
    const encodedID = encodeURIComponent(integration.id);
    const data: Integration = parseIntegrationSchema(integration);

    return new Promise((resolve, reject) => {
      this.resolvePromise = resolve;
      this.rejectPromise = reject;

      if (this.props.match.params.id === "new") {
        onCreate(data);
      } else {
        onUpdate(encodedID, data);
      }
    })
      .then(() => {
        dispatch(initialize(formName, this.state.integrationItem));
        if (nextLocation) {
          history.push(nextLocation);
          setNextLocation('');
        } else {
          history.push(`/automation/integration/${integration.id}`);
        }
      })
      .catch(error => {
        this.isPending = false;
        this.setState({
          isPending: false
        });
        const errors: any = {
          fields: {}
        };

        if (error) {
          errors.fields[error.propertyName] = error.errorMessage;
        }

        throw new SubmissionError(errors);
      });
  };

  renderAppBar = ({ disableName, children }) => {
    const {
      match, dirty, invalid, syncErrors
    } = this.props;
    const item = this.state.integrationItem;
    const isNew = match.params.id === "new";

    const { isPending } = this.state;

    return (
      <AppBarContainer
        values={item}
        manualUrl={getManualLink("externalintegrations", item.type)}
        getAuditsUrl={getAuditsUrl}
        disabled={!dirty || isPending}
        invalid={invalid}
        title={isNew && (!item.name || item.name.trim().length === 0) ? "New" : item.name.trim()}
        disableInteraction={disableName}
        hideHelpMenu={!isNew && item}
        opened={isNew || Object.keys(syncErrors).includes("name")}
        disabledScrolling
        fields={(
          <Grid item xs={12}>
            <FormField
              name="name"
              label="Name"
              validate={this.validateNameField}
              disabled={disableName}
              fullWidth
            />
          </Grid>
        )}
        actions={!isNew && (
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
      >
        {children}
      </AppBarContainer>
    );
  };

  render() {
    const { classes } = this.props;

    const item = this.state.integrationItem;

    if (!item) {
      return null;
    }

    const typeItem = IntegrationTypes[item.type];
    const TypeForm = typeItem.form;
    const image = typeItem.image;

    return (
      <Grid container className={classes.root}>
        <Grid item xs={12} sm={6} lg={5}>
          {item && TypeForm && (
            <TypeForm
              onSubmit={this.submitForm}
              validateNameField={this.validateNameField}
              item={item}
              AppBarContent={this.renderAppBar}
              {...this.props}
            />
          )}
        </Grid>
        <Grid item xs={12} sm={5} lg={4} className="flex-column pr-3 text-end">
          <div>
            <img
              alt="integrationLogo"
              src={image}
              width={200}
              className={clsx("mb-2", classes.image)}
            />
          </div>
          <IntegrationDescription item={typeItem} />
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
    id: String(schema.id),
    type: Number(schema.type),
    name: schema.name,
    verificationCode: schema.verificationCode,
    props: properties
  };
};

const getFormName = form => form && Object.keys(form)[0];

const mapStateToProps = (state: State) => {
  const formName = getFormName(state.form);
  return {
    integrations: state.automation.integration.integrations,
    formName,
    dirty: isDirty(formName)(state),
    invalid: isInvalid(formName)(state),
    syncErrors: getFormSyncErrors(formName)(state),
    fetch: state.fetch,
    nextLocation: state.nextLocation
  };
};

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
