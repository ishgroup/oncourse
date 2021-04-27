/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React from "react";

import Grid from "@material-ui/core/Grid";
import Fab from "@material-ui/core/Fab";
import AddIcon from "@material-ui/icons/Add";
import Typography from "@material-ui/core/Typography";
import {
  arrayInsert, arrayRemove, FieldArray, getFormValues, InjectedFormProps, reduxForm
} from "redux-form";
import { Dispatch } from "redux";
import Alert from "@material-ui/lab/Alert";
import { AlertTitle } from "@material-ui/lab";
import { connect } from "react-redux";
import { User } from "@api/model";
import { v4 as uuidv4 } from 'uuid';
import FormSubmitButton from "../../../../common/components/form/FormSubmitButton";
import { onSubmitFail } from "../../../../common/utils/highlightFormClassErrors";
import CustomAppBar from "../../../../common/components/layout/CustomAppBar";
import RouteChangeConfirm from "../../../../common/components/dialog/confirm/RouteChangeConfirm";
import ApiTokensRenderer from "./components/ApiTokensRenderer";
import { State } from "../../../../reducers/state";
import { deleteApiToken, updateApiTokens } from "../../actions";
import { showConfirm } from "../../../../common/actions";

interface Props extends InjectedFormProps {
  security: any;
  dispatch?: Dispatch;
  values?: any;
  users?: User[];
}

const ApiTokensBase:React.FC<Props> = (
  {
    handleSubmit,
    dirty,
    invalid,
    values,
    dispatch,
    form,
    users
}
) => {
  const onAdd = () => {
    dispatch(arrayInsert(form, "tokens", 0, {
      name: null,
      userId: null,
      secret: "b25Db3Vyc2U" + uuidv4().replaceAll("-", "")
    }));

    const domNode = document.getElementById("tokens[0].userId");
    if (domNode) domNode.scrollIntoView({ behavior: "smooth" });
  };

  const onSave = ({ tokens }) => {
    dispatch(updateApiTokens(tokens.filter(t => typeof t.id !== "number")));
  };

  const onDeleteToken = index => {
    const item = values.tokens[index];
    if (typeof item.id !== "number") {
      dispatch(arrayRemove(form, "tokens", index));
    } else {
      dispatch(
        showConfirm(
          () => dispatch(deleteApiToken(item.id)),
          "API token will be deleted permanently"
        )
      );
    }
  };

  return (
    <form className="mt-2" noValidate autoComplete="off" onSubmit={handleSubmit(onSave)}>
      <RouteChangeConfirm form={form} when={dirty} />
      <CustomAppBar>
        <Grid container>
          <Grid item xs={12} className="centeredFlex relative">
            <Fab
              type="button"
              size="small"
              color="primary"
              classes={{
                sizeSmall: "appBarFab"
              }}
              onClick={onAdd}
            >
              <AddIcon />
            </Fab>
            <Typography color="inherit" noWrap className="appHeaderFontSize pl-2">
              API Tokens
            </Typography>

            <div className="flex-fill" />

            {/* {values && ( */}
            {/*  <AppBarHelpMenu */}
            {/*    created={created} */}
            {/*    modified={modified} */}
            {/*    auditsUrl={`audit?search=~"UnavailableRule" and entityId in (${idsToString(values.holidays)})`} */}
            {/*    manualUrl={manualUrl} */}
            {/*  /> */}
            {/* )} */}

            <FormSubmitButton
              disabled={!dirty}
              invalid={invalid}
            />
          </Grid>
        </Grid>
      </CustomAppBar>

      <Grid container>
        <Grid item xs={12} md={10}>
          <Alert severity="info" className="mb-2">
            <AlertTitle>
              API tokens can be used by third party tools
            </AlertTitle>
            Caution: these tokens will allow an attacker to access, change or delete your data, so take care of where you distribute the token secret.
          </Alert>
        </Grid>
        <Grid item xs={12} md={10}>
          <FieldArray
            name="tokens"
            users={users}
            component={ApiTokensRenderer}
            onDelete={onDeleteToken}
            dispatch={dispatch}
            rerenderOnEveryChange
          />
        </Grid>
      </Grid>
    </form>
  );
};

const mapStateToProps = (state: State) => ({
  values: getFormValues("ApiTokensForm")(state),
  users: state.security.users,
});

const ApiTokens = reduxForm({
  onSubmitFail,
  form: "ApiTokensForm",
  initialValues: {
    tokens: []
  }
})(connect(mapStateToProps)((props: any) => (props.values ? <ApiTokensBase {...props} /> : null)));

export default ApiTokens;
