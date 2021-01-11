/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Site } from "@api/model";
import { _toRequestType, FULFILLED } from "../../../../common/actions/ActionUtils";

export const GET_SITE_ITEM = _toRequestType("get/site");
export const GET_SITE_ITEM_FULFILLED = FULFILLED(GET_SITE_ITEM);

export const DELETE_SITE_ITEM = _toRequestType("delete/site");
export const DELETE_SITE_ITEM_FULFILLED = FULFILLED(DELETE_SITE_ITEM);

export const UPDATE_SITE_ITEM = _toRequestType("put/site");
export const UPDATE_SITE_ITEM_FULFILLED = FULFILLED(UPDATE_SITE_ITEM);

export const CREATE_SITE_ITEM = _toRequestType("post/site");
export const CREATE_SITE_ITEM_FULFILLED = FULFILLED(CREATE_SITE_ITEM);

export const GET_ADMINISTRATION_SITES = _toRequestType("get/site/administration");
export const GET_ADMINISTRATION_SITES_FULFILLED = FULFILLED(GET_ADMINISTRATION_SITES);

export const GET_VIRTUAL_SITES = _toRequestType("get/site/virtual");
export const GET_VIRTUAL_SITES_FULFILLED = FULFILLED(GET_VIRTUAL_SITES);

export const getSite = (id: string) => ({
  type: GET_SITE_ITEM,
  payload: id
});

export const removeSite = (id: string) => ({
  type: DELETE_SITE_ITEM,
  payload: id
});

export const updateSite = (id: string, site: Site) => ({
  type: UPDATE_SITE_ITEM,
  payload: { id, site }
});

export const createSite = (site: Site) => ({
  type: CREATE_SITE_ITEM,
  payload: { site }
});

export const getAdministrationSites = () => ({
  type: GET_ADMINISTRATION_SITES
});

export const getVirtualSites = () => ({
  type: GET_VIRTUAL_SITES
});

