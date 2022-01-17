/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from 'redux-observable';
import { Create, Request } from '../EpicUtils';
import { showMessage } from '../../actions';
import WebSiteService from '../../../api/services/WebSiteService';
import { getSites, UPDATE_COLLEGE_SITES } from '../../actions/Sites';
import { SitesUpdateRequest } from '../../../models/Sites';

const request: Request<any, SitesUpdateRequest> = {
  type: UPDATE_COLLEGE_SITES,
  getData: async (
    {
      changed = [],
      created = [],
      removed = [],
    },
    state
  ) => {
    await changed.map((site) => () => WebSiteService.updateSite(site))
      .reduce(async (a, b) => {
        await a;
        await b();
      }, Promise.resolve());

    await created.map((site) => () => WebSiteService.crateSite({ ...site, key: `${state.college.collegeKey}-${site.key}` }))
      .reduce(async (a, b) => {
        await a;
        await b();
      }, Promise.resolve());

    await removed.map((id) => () => WebSiteService.deleteSite(id))
      .reduce(async (a, b) => {
        await a;
        await b();
      }, Promise.resolve());

    return Promise.resolve(Boolean(removed.length || created.length));
  },
  processData: (updateSites) => [
    showMessage({
      message: 'Websites updated', success: true
    }),
    ...updateSites ? [getSites()] : []
  ]
};

export const EpicUpdateCollegeSites: Epic<any, any> = Create(request);
