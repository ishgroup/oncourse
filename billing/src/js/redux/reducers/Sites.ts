/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { SiteDTO } from '@api/model';
import { IAction } from '../../models/IshAction';
import { GET_SITES_FULFILLED } from '../actions/Sites';

const Initial: SiteDTO[] = [];

export const sitesReducer = (state: SiteDTO[] = Initial, action: IAction): SiteDTO[] => {
  switch (action.type) {
    case GET_SITES_FULFILLED:
      return [...action.payload];
    default:
      return [...state];
  }
};
