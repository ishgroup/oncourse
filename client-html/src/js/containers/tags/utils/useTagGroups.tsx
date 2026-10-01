/*
 * Copyright ish group pty ltd 2024.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { Tag } from '@api/model';
import $t from '@t';
import { stubFunction, TagInputList } from 'ish-ui';
import React, { useMemo } from 'react';
import { Dispatch } from 'redux';
import { change } from 'redux-form';
import { IAction } from '../../../common/actions/IshAction';
import { useAppSelector } from '../../../common/utils/hooks';
import { SPECIAL_TYPES_DISPLAY_KEY } from '../../../constants/Config';
import { COMMON_PLACEHOLDER } from '../../../constants/Forms';
import { getAllFormTags, getAvailableTagIds } from './index';

interface Props {
  tags: Tag[];
  tagsValue: number[];
  dispatch: Dispatch<IAction>;
  form: string;
}

const subjectsFilter = (t: Tag) => t.system && t.name === 'Subjects';

export function useTagGroups({ tagsValue, tags, form, dispatch }: Props) {

  const specialTypesDisabled = useAppSelector(state => state.userPreferences[SPECIAL_TYPES_DISPLAY_KEY] !== 'true');

  const tagsGrouped = useMemo(() => {
    const body = {
      tags,
      tagsValue: [],
      subjects: [],
      subjectsValue: []
    };
    const formTagsValue = tagsValue || [];

    if (tags?.length) {
      const allTags = getAllFormTags(tags);
      const subjects = tags.filter(subjectsFilter)[0]?.childTags || [];

      if (specialTypesDisabled) {
        body.tags = tags;
        body.tagsValue = formTagsValue.filter(id => allTags.some(t => t.id === id));
      } else {
        const menuTags = tags.filter(t => !t.system && t.name !== 'Subjects');
        const subjectIds = new Set(getAllFormTags(subjects).map(t => t.id));
        const menuIds = new Set(getAllFormTags(menuTags).map(t => t.id));

        body.tags = menuTags;
        body.subjects = subjects;
        body.subjectsValue = formTagsValue.filter(id => subjectIds.has(id));
        body.tagsValue = formTagsValue.filter(id => menuIds.has(id) && !subjectIds.has(id));
      }
    }
    return body;
  }, [tags, tagsValue, specialTypesDisabled]);

  // only the tags of the tag field itself - subjects are rendered in their own field, so they are
  // not available for the tag field
  const availableTagIds = useMemo(
    () => (tags ? getAvailableTagIds(tagsGrouped.tags) : undefined),
    [tags, tagsGrouped.tags]
  );

  const subjectsField = <TagInputList
    input={{
      value: tagsGrouped.subjectsValue,
      onChange: updated => {
        dispatch(change(form, 'tags', updated ? Array.from(new Set(tagsGrouped.tagsValue.concat(updated))) : []));
      },
      onBlur: stubFunction
    }}
    meta={{}}
    tags={tagsGrouped.subjects}
    disabled={specialTypesDisabled}
    label={$t('subjects')}
    className="mt-2"
    placeholder={COMMON_PLACEHOLDER}
    allowParentSelect
    hideColor
  />;
  
  return { tagsGrouped, subjectsField, specialTypesDisabled, availableTagIds };
}
