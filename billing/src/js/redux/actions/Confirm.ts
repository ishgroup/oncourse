/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { ShowConfirmCaller } from '../../models/Confirm';

export const OPEN_CONFIRM = 'common/confirm/open';
export const CLOSE_CONFIRM = 'common/confirm/close';

export const showConfirm: ShowConfirmCaller = (payload) => ({
  type: OPEN_CONFIRM,
  payload
});

export const closeConfirm = () => ({
  type: CLOSE_CONFIRM
});
