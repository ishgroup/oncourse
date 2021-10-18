import { FULFILLED } from './ActionUtils';
import { SitesUpdateRequest } from '../../models/Sites';

export const GET_SITES = 'GET_SITES';
export const GET_SITES_FULFILLED = FULFILLED(GET_SITES);
export const UPDATE_COLLEGE_SITES = 'UPDATE_COLLEGE_SITES';

export const updateCollegeSites = (sites: SitesUpdateRequest) => ({
  type: UPDATE_COLLEGE_SITES,
  payload: sites
});

export const getSites = () => ({
  type: GET_SITES
});
