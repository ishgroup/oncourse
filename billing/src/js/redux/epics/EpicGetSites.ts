import { Epic } from 'redux-observable';
import * as EpicUtils from './EpicUtils';
import WebSiteService from '../../api/services/WebSiteService';
import { GET_SITES, GET_SITES_FULFILLED } from '../actions/Sites';

const request: EpicUtils.Request = {
  type: GET_SITES,
  getData: () => WebSiteService.getSites(),
  processData: (sites) => [{ type: GET_SITES_FULFILLED, payload: sites }]
};

export const EpicGetSites: Epic<any, any> = EpicUtils.Create(request);
