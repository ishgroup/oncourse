/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { Epic } from "redux-observable";
import { Tag } from "@api/model";
import { SET_LIST_MENU_TAGS } from "src/js/common/components/list-view/actions";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { GET_SALE_MENU_TAGS } from "../actions";
import { GET_ENTITY_TAGS_REQUEST_FULFILLED, GET_LIST_TAGS_FULFILLED } from "../../../tags/actions";
import TagsService from "../../../tags/services/TagsService";
import { getMenuTags } from "../../../../common/components/list-view/utils/listFiltersUtils";
import { MenuTag } from "../../../../model/tags";

const getTags = async () => {
  const articleTags = await TagsService.getTags("Article");
  const voucherTags = await TagsService.getTags("Voucher");
  const membershipTags = await TagsService.getTags("Membership");
  
  const unique = {};
  const result = [];
  
  [
    ...getMenuTags(articleTags, [], null, null, "Article"), 
    ...getMenuTags(voucherTags, [], null, null, "Voucher"), 
    ...getMenuTags(membershipTags, [], null, null, "Membership"),
  ].forEach(tag => {
    if (!unique[tag.tagBody.id]) {
      unique[tag.tagBody.id] = true;
      result.push(tag);
    }
  });
  
  return result;
}; 

const request: EpicUtils.Request = {
  type: GET_SALE_MENU_TAGS,
  getData: () => getTags(),
  processData: (menuTags: MenuTag[]) => [
      {
        type: GET_LIST_TAGS_FULFILLED
      },
      {
        type: SET_LIST_MENU_TAGS,
        payload: { menuTags }
      }
    ]
};

export const EpicGetSaleMenuTags: Epic<any, any> = EpicUtils.Create(request);
