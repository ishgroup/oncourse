/*
 * Copyright ish group pty ltd 2024.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { Tag } from "@api/model";
import { EditInPlaceSearchSelect, stubFunction } from "ish-ui";
import React, { useMemo } from "react";
import { Dispatch } from "redux";
import { change } from "redux-form";
import { useAppSelector } from "../../../common/utils/hooks";
import { SPECIAL_TYPES_DISPLAY_KEY } from "../../../constants/Config";
import { COMMON_PLACEHOLDER } from "../../../constants/Forms";
import { getAllFormTags, getAllTags } from "./index";

interface Props {
  tags: Tag[];
  tagsValue: number[];
  dispatch: Dispatch;
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
    if (!specialTypesDisabled && tags?.length) {
      body.tags = tags.filter(t => !t.system && t.name !== 'Subjects');
      body.subjects = getAllTags(tags.filter(subjectsFilter)).filter(t => !subjectsFilter(t));
      const allTags = getAllFormTags(tags);
      body.subjectsValue = tagsValue.filter(id => {
        const tag = allTags.find(t => t.id === id);
        return tag?.rootTag?.system && tag?.rootTag?.name === 'Subjects';
      });
      body.tagsValue = tagsValue.filter(id => !body.subjectsValue.includes(id));
    }
    return body;
  }, [tags, tagsValue, specialTypesDisabled]);

  const subjectsField = <EditInPlaceSearchSelect
    input={{
      value: tagsGrouped.subjectsValue,
      onChange: updated => {
        dispatch(change(form, 'tags', updated ? Array.from(new Set(tagsGrouped.tagsValue.concat(updated))) : []));
      },
      onBlur: stubFunction
    }}
    meta={{}}
    items={tagsGrouped.subjects}
    disabled={specialTypesDisabled}
    label="Subjects"
    selectValueMark="id"
    selectLabelMark="name"
    className="mt-2"
    placeholder={COMMON_PLACEHOLDER}
    allowEmpty
    multiple
  />;
  
  return { tagsGrouped, subjectsField, specialTypesDisabled };
}