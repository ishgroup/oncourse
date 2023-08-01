/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { AnyArgFunction, PromiseReturnFunction } from "ish-ui";
import { Dispatch } from "redux";
import { addActionToQueue } from "../actions";
import { IAction } from "../actions/IshAction";
import { asyncValidateFieldArrayFieldCallback, getFieldArrayFieldMeta } from "./validation";

export const fieldUpdateHandler = (
  values: any,
  dispatch: Dispatch,
  props: any,
  blurredField: string,
  entity: string,
  valuePath: string,
  validateCreate: PromiseReturnFunction,
  validateUpdate: PromiseReturnFunction,
  createAction: AnyArgFunction<IAction>,
  updateAction: AnyArgFunction<IAction>,
  bindedActionId?: string
) => {
  if (!blurredField) {
    return Promise.resolve();
  }

  const meta = getFieldArrayFieldMeta(blurredField);

  const item = {...values[valuePath][meta.index]};

  const temporaryId = item.temporaryId;

  if (item.hasOwnProperty("index")) {
    delete item.index;
  }

  if (temporaryId) {
    delete item.temporaryId;

    return validateCreate(item)
      .then(() => {
        dispatch(addActionToQueue(createAction(item), "POST", entity, temporaryId, bindedActionId));
      })
      .catch(res => asyncValidateFieldArrayFieldCallback(res, meta, dispatch));
  }
  return validateUpdate(item)
    .then(() => {
      dispatch(addActionToQueue(updateAction(item), "PUT", entity, temporaryId || item.id, bindedActionId));
    })
    .catch(res => asyncValidateFieldArrayFieldCallback(res, meta, dispatch));
};
