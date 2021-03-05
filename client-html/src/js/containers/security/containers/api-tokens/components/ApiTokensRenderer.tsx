/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React, { useRef } from "react";
import Grid from "@material-ui/core/Grid";
import { Card, Typography } from "@material-ui/core";
import Button from "@material-ui/core/Button";
import { ApiToken, User } from "@api/model";
import { WrappedFieldArrayProps } from "redux-form";
import { Dispatch } from "redux";
import FormField from "../../../../../common/components/form/form-fields/FormField";
import { NumberArgFunction } from "../../../../../model/common/CommonFunctions";
import { LinkAdornment } from "../../../../../common/components/form/FieldAdornments";
import { openInternalLink } from "../../../../../common/utils/links";
import { contactLabelCondition } from "../../../../entities/contacts/utils";
import UserSelectItemRenderer from "../../users/components/UserSelectItemRenderer";
import { showMessage } from "../../../../../common/actions";

interface RendererProps {
  users: User[];
  onDelete: NumberArgFunction;
  dispatch: Dispatch;
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

  const linkInput = useRef<HTMLInputElement>();

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
    <Card className="mb-2">
      <Grid container className="p-3">
        <Grid xs={6}>
          <FormField
            label="Act as user"
            type="searchSelect"
            selectValueMark="id"
            selectLabelCondition={contactLabelCondition}
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
            fullWidth
            required
          />

          {isNew && (
            <Typography variant="caption" color="textSecondary">
              This API token will behave as if this user were logged in.
              You should try to restrict that user`s access rights as much as possible to safeguard your data.
            </Typography>
          )}
        </Grid>
        <Grid xs={5}>
          <FormField type="text" name={`${item}.name`} label="Token name" disabled={!isNew} required fullWidth />
        </Grid>
        <Grid item xs={1}>
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
          <Grid xs={12}>
            <div className="centeredFlex mt-3">
              <Typography color="textSecondary" className="flex-fill">
                <input readOnly className="codeArea" type="text" ref={linkInput} value={field.secret} />
              </Typography>
              <Button color="primary" className="text-nowrap" onClick={onCopy}>
                Copy Secret
              </Button>
            </div>
            <Typography variant="caption" color="textSecondary">
              Secret (this will not be shown again after you close this window)
            </Typography>
          </Grid>
      )
      : (
        <Grid xs={12}>
          <FormField type="dateTime" name={`${item}.lastAccess`} label="Last access" disabled />
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
  <Grid container>
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
