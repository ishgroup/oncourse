/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { User } from '@api/model';
import { AlertTitle } from '@mui/lab';
import Alert from '@mui/lab/Alert';
import Grid from '@mui/material/Grid';
import $t from '@t';
import React from 'react';
import { connect } from 'react-redux';
import { Dispatch } from 'redux';
import { arrayInsert, arrayRemove, FieldArray, Form, getFormValues, InjectedFormProps, reduxForm } from 'redux-form';
import { v4 as uuidv4 } from 'uuid';
import { showConfirm } from '../../../../common/actions';
import { IAction } from '../../../../common/actions/IshAction';
import RouteChangeConfirm from '../../../../common/components/dialog/RouteChangeConfirm';
import AppBarContainer from '../../../../common/components/layout/AppBarContainer';
import { onSubmitFail } from '../../../../common/utils/highlightFormErrors';
import { State } from '../../../../reducers/state';
import { deleteApiToken, updateApiTokens } from '../../actions';
import ApiTokensRenderer from './components/ApiTokensRenderer';

interface Props extends InjectedFormProps {
  security: any;
  dispatch?: Dispatch<IAction>;
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
    users,
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
        showConfirm({
          onConfirm: () => dispatch(deleteApiToken(item.id)),
          confirmMessage: "API token will be deleted permanently",
          confirmButtonText: "Delete"
        })
      );
    }
  };

  return (
    <Form className="mt-2" noValidate autoComplete="off" onSubmit={handleSubmit(onSave)}>
      <RouteChangeConfirm form={form} when={dirty} />

      <AppBarContainer
        values={values}
        disabled={!dirty}
        invalid={invalid}
        title={$t('api_tokens')}
        disableInteraction
        hideHelpMenu
        onAddMenu={onAdd}
      >
        <Grid container className="mt-2">
          <Grid item xs={12} md={10}>
            <Alert severity="info" className="mb-2">
              <AlertTitle>
                {$t('api_tokens_can_be_used_by_third_party_tools')}
              </AlertTitle>
              {$t('caution_these_tokens_will_allow_an_attacker_to_acc')}
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
      </AppBarContainer>
    </Form>
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
})(connect(mapStateToProps)(
  (props: any) => (props.values ? <ApiTokensBase {...props} /> : null)
));

export default ApiTokens;
