/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from 'redux-observable';
import { Request, Create } from '../EpicUtils';
import WebSiteService from '../../../api/services/WebSiteService';
import { GET_SITES, GET_SITES_FULFILLED } from '../../actions/Sites';
import history from '../../../constant/History';

const request: Request = {
  type: GET_SITES,
  getData: () => WebSiteService.getSites(),
  processData: (sites, state) => {
    const newSite = state.sites.length && sites.find((s) => !state.sites.find((site) => site.id === s.id));
    if (newSite) {
      history.push(`/websites/${newSite.id}/urls?openSettings=true`);
    }
    return [{ type: GET_SITES_FULFILLED, payload: sites }];
  }
};

export const EpicGetSites: Epic<any, any> = Create(request);
