import { Epic } from 'redux-observable';
import { SiteDTO } from '@api/model';
import * as EpicUtils from './EpicUtils';
import { getSites, setLoadingValue, showMessage, UPDATE_COLLEGE_SITES } from '../actions';
import WebSiteService from '../../api/services/WebSiteService';
import FetchErrorHandler from '../../api/fetch-errors-handlers/FetchErrorHandler';

const request: EpicUtils.Request<any, { changed: SiteDTO[], created: SiteDTO[], removed: SiteDTO[] }> = {
  type: UPDATE_COLLEGE_SITES,
  getData: async (sites) => {
    await sites.changed.map((site) => () => WebSiteService.updateSite(site))
      .reduce(async (a, b) => {
        await a;
        await b();
      }, Promise.resolve());

    await sites.created.map((site) => () => WebSiteService.crateSite(site))
      .reduce(async (a, b) => {
        await a;
        await b();
      }, Promise.resolve());

    await sites.removed.map((site) => () => WebSiteService.deleteSite(site.id))
      .reduce(async (a, b) => {
        await a;
        await b();
      }, Promise.resolve());

    return Promise.resolve();
  },
  processData: () => [
    showMessage({
      message: 'Websites updated', success: true
    }),
    getSites(),
    setLoadingValue(false)
  ],
  processError: (response) => [
    setLoadingValue(false),
    ...FetchErrorHandler(response)
  ]
};

export const EpicUpdateCollegeSites: Epic<any, any> = EpicUtils.Create(request);
