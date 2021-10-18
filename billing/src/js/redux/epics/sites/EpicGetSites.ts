/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from 'redux-observable';
import { Request, Create } from '../EpicUtils';
import WebSiteService from '../../../api/services/WebSiteService';
import { GET_SITES, GET_SITES_FULFILLED } from '../../actions/Sites';

const request: Request = {
  type: GET_SITES,
  getData: () => WebSiteService.getSites(),
  processData: (sites) => [{ type: GET_SITES_FULFILLED, payload: sites }]
};

export const EpicGetSites: Epic<any, any> = Create(request);
