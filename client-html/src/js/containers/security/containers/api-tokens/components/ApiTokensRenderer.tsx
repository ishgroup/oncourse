/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { ApiToken, User } from '@api/model';
import { Button, Card, Grid, Typography } from '@mui/material';
import $t from '@t';
import { LinkAdornment, NumberArgFunction, openInternalLink } from 'ish-ui';
import React, { useRef } from 'react';
import { Dispatch } from 'redux';
import { WrappedFieldArrayProps } from 'redux-form';
import { showMessage } from '../../../../../common/actions';
import { IAction } from '../../../../../common/actions/IshAction';
import FormField from '../../../../../common/components/form/formFields/FormField';
import { getContactFullName } from '../../../../entities/contacts/utils';
import UserSelectItemRenderer from '../../users/components/UserSelectItemRenderer';

interface RendererProps {
  users: User[];
  onDelete: NumberArgFunction;
  dispatch: Dispatch<IAction>
}

interface ItemsProps extends RendererProps {
  field: ApiToken;
  item: string;
  index: number;
}

const openUserLink = id => openInternalLink(`/security/users/${id}`);

const ApiTokenItem: React.FC<ItemsProps> = (
  {
    item,
    users,
    field,
    onDelete,
    dispatch,
    index
  }
) => {
  const isNew = typeof field.id !== "number";

  const linkInput = useRef<HTMLInputElement>(undefined);

  const onCopy = () => {
    linkInput.current.select();
    document.execCommand("copy");
    linkInput.current.setSelectionRange(0, 0);
    dispatch(showMessage({
      message: "Link copied",
      success: true
    }));
  };

  return (
    <Card className="mb-2 p-3">
      <Grid container columnSpacing={3} rowSpacing={2}>
        <Grid item xs={6}>
          <FormField
            label={$t('act_as_user')}
            type="select"
            selectValueMark="id"
            selectLabelCondition={getContactFullName}
            name={`${item}.userId`}
            items={users || []}
            itemRenderer={UserSelectItemRenderer}
            labelAdornment={(
              <LinkAdornment
                linkHandler={openUserLink}
                link={field.userId}
                disabled={!field.userId}
              />
            )}
            rowHeight={55}
            disabled={!isNew}
            className="pr-1"
            required
          />

          {isNew && (
            <Typography variant="caption" color="textSecondary">
              {$t('this_api_token_will_behave_as_if_this_user_were_lo')}
            </Typography>
          )}
        </Grid>
        <Grid item xs={6} display="flex" justifyContent="space-between" alignItems="flex-start">
          <FormField type="text" name={`${item}.name`} label={$t('token_name')} disabled={!isNew} required />
          <Button
            size="small"
            classes={{
              root: "errorColor"
            }}
            onClick={() => onDelete(index)}
          >
            {isNew ? "Delete" : "Revoke"}
          </Button>
        </Grid>
        {isNew ? (
          <Grid item xs={12}>
            <div className="centeredFlex mt-3">
              <Typography color="textSecondary" className="flex-fill">
                <input readOnly className="codeArea" type="text" ref={linkInput} value={field.secret} />
              </Typography>
              <Button color="primary" className="text-nowrap" onClick={onCopy}>
                {$t('copy_secret')}
              </Button>
            </div>
            <Typography variant="caption" color="textSecondary">
              {$t('secret_this_will_not_be_shown_again_after_you_clos')}
            </Typography>
          </Grid>
      )
      : (
        <Grid item xs={12}>
          <FormField type="dateTime" name={`${item}.lastAccess`} label={$t('last_access')} disabled />
        </Grid>
      )}
      </Grid>
    </Card>
  );
};

const ApiTokensRenderer: React.FC<RendererProps & WrappedFieldArrayProps> = (
  {
    fields,
    onDelete,
    users,
    dispatch
  }
) => (
  <Grid container columnSpacing={3}>
    <Grid item xs={12}>
      {fields.map((item, index) => (
        <ApiTokenItem
          users={users}
          item={item}
          field={fields.get(index)}
          onDelete={onDelete}
          dispatch={dispatch}
          index={index}
        />
      ))}
    </Grid>
  </Grid>
  );

export default ApiTokensRenderer;
