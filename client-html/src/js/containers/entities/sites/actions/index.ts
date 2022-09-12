/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
import { _toRequestType, FULFILLED } from "../../../../common/actions/ActionUtils";

export const GET_SITE_ITEM = _toRequestType("get/site");
export const GET_SITE_ITEM_FULFILLED = FULFILLED(GET_SITE_ITEM);

export const GET_ADMINISTRATION_SITES = _toRequestType("get/site/administration");
export const GET_ADMINISTRATION_SITES_FULFILLED = FULFILLED(GET_ADMINISTRATION_SITES);

export const GET_VIRTUAL_SITES = _toRequestType("get/site/virtual");
export const GET_VIRTUAL_SITES_FULFILLED = FULFILLED(GET_VIRTUAL_SITES);

export const getSite = (id: string) => ({
  type: GET_SITE_ITEM,
  payload: id
});

export const getAdministrationSites = () => ({
  type: GET_ADMINISTRATION_SITES
});

export const getVirtualSites = () => ({
  type: GET_VIRTUAL_SITES
});