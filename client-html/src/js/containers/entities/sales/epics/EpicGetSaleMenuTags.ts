/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { getMenuTags } from 'ish-ui';
import { Epic } from 'redux-observable';
import { setListMenuTags } from '../../../../common/components/list-view/actions';
import * as EpicUtils from '../../../../common/epics/EpicUtils';
import TagsService from '../../../tags/services/TagsService';
import { GET_SALE_MENU_TAGS } from '../actions';

const getTags = async () => {
  const [
    articleTags, voucherTags, membershipTags, articleChecklists, voucherChecklists, membershipChecklists
  ] = await Promise.all([
    TagsService.getTags("Article"),
    TagsService.getTags("Voucher"),
    TagsService.getTags("Membership"),
    TagsService.getChecklists("Article"),
    TagsService.getChecklists("Voucher"),
    TagsService.getChecklists("Membership")
  ]);

  const uniqueChecklists = {};
  
  const checkedChecklists = getMenuTags(articleChecklists, [], null, null, "Article")
    .concat(getMenuTags(voucherChecklists, [], null, null, "Voucher"))
    .concat(getMenuTags(membershipChecklists, [], null, null, "Membership"))
    .filter(tag => {
      if (!uniqueChecklists[tag.tagBody.id]) {
        uniqueChecklists[tag.tagBody.id] = true;
        tag.entity = "ProductItem";
        return true;
      }
      return false;
    });

  const uncheckedChecklists = [...checkedChecklists];
  
  const unique = {};

  const tags = [
    ...getMenuTags(articleTags, [], null, null, "Article"), 
    ...getMenuTags(voucherTags, [], null, null, "Voucher"), 
    ...getMenuTags(membershipTags, [], null, null, "Membership"),
  ].filter(tag => {
    if (!unique[tag.tagBody.id]) {
      unique[tag.tagBody.id] = true;
      tag.entity = "ProductItem";
      return true;
    }
    return false;
  });
  
  return { tags, checkedChecklists, uncheckedChecklists };
}; 

const request: EpicUtils.Request = {
  type: GET_SALE_MENU_TAGS,
  getData: () => getTags(),
  processData: ({ tags, checkedChecklists, uncheckedChecklists }) => [
    setListMenuTags(
      tags,
      checkedChecklists,
      uncheckedChecklists
    ),
  ]
};

export const EpicGetSaleMenuTags: Epic<any, any> = EpicUtils.Create(request);
