/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import * as React from "react";
import clsx from "clsx";
import Grid from "@mui/material/Grid";
import {
  getFormSyncErrors, isDirty, isInvalid
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
import { showConfirm } from "../../../../../common/actions";
import { ShowConfirmCaller } from "../../../../../model/common/Confirm";
import AppBarContainer from "../../../../../common/components/layout/AppBarContainer";
import RouteChangeConfirm from "../../../../../common/components/dialog/confirm/RouteChangeConfirm";

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
  onUpdate: (id: string, item: Integration, form: string) => void;
  onCreate: (item: Integration, form: string) => void;
  onDelete: (id: number) => void;
  openConfirm?: ShowConfirmCaller;
  classes: any;
  dispatch: any;
  formName: any;
  dirty: boolean;
  invalid: boolean;
  nextLocation: string;
}

class FormContainer extends React.Component<Props & RouteComponentProps<any>, any> {
  getIntegrationItem() {
    const { match, integrations } = this.props;

    if (match.params.id === "new") {
      return { id: "", type: match.params.type, fields: {} } as any;
    }
    return integrations.find(item => String(item.id) === match.params.id);
  }

  handleDelete = () => {
    const {
     onDelete, openConfirm
    } = this.props;

    const item = this.getIntegrationItem();

    openConfirm({
      onConfirm: () => onDelete(item.id),
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
        .filter(item => item.name !== this.getIntegrationItem()?.name)
        .find(item => item.name === value.trim());
    }
    if (match) {
      return "Name value must be unique";
    }
    return undefined;
  };

  submitForm = integration => {
    const {
     onUpdate, onCreate
    } = this.props;

    const encodedID = encodeURIComponent(integration.id);
    const data: Integration = parseIntegrationSchema(integration);

    const typeItem = IntegrationTypes[integration.type];
    const TypeForm = typeItem.formName;

    if (this.props.match.params.id === "new") {
      onCreate(data, TypeForm);
    } else {
      onUpdate(encodedID, data, TypeForm);
    }
  };

  renderAppBar = ({ disableName, children }) => {
    const {
      match, dirty, invalid, syncErrors
    } = this.props;
    const item = this.getIntegrationItem();
    const isNew = match.params.id === "new";

    return (
      <AppBarContainer
        values={item}
        manualUrl={getManualLink("externalintegrations")}
        getAuditsUrl={getAuditsUrl}
        disabled={!dirty}
        invalid={invalid}
        title={isNew && (!item.name || item.name.trim().length === 0) ? "New" : item.name.trim()}
        disableInteraction={disableName}
        hideHelpMenu={Boolean(!isNew && item)}
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
    const { classes, dirty } = this.props;

    const item = this.getIntegrationItem();

    if (!item) {
      return null;
    }

    const typeItem = IntegrationTypes[item.type];
    const TypeForm = typeItem.form;
    const image = typeItem.image;

    return (
      <Grid container className={classes.root}>
        <Grid item xs={12} sm={6} lg={5}>
          <RouteChangeConfirm form={typeItem.formName} when={dirty} />
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

const mapStateToProps = (state: State, { match }) => {
  const typeItem = IntegrationTypes[match.params.type];
  const formName = typeItem.formName;
  
  return {
    integrations: state.automation.integration.integrations,
    formName,
    dirty: isDirty(formName)(state),
    invalid: isInvalid(formName)(state),
    syncErrors: getFormSyncErrors(formName)(state),
    fetch: state.fetch
  };
};

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  dispatch,
  onUpdate: (id: string, item: Integration, form) => dispatch(updateIntegration(id, item, form)),
  onCreate: (item: Integration, form) => dispatch(createIntegration(item, form)),
  onDelete: (id: string) => dispatch(deleteIntegrationItem(id)),
  openConfirm: props => dispatch(showConfirm(props))
});

export default withRouter(connect<any, any, any>(
  mapStateToProps,
  mapDispatchToProps
)(withStyles(styles)(FormContainer)));
