/*
 * Copyright ish group pty ltd 2024.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */


import { Tag } from "@api/model";
import { useMemo } from "react";
import { useAppSelector } from "../../../common/utils/hooks";
import { SPECIAL_TYPES_DISPLAY_KEY } from "../../../constants/Config";
import { getAllFormTags, getAllTags } from "./index";

interface Props {
  tags: Tag[];
  tagsValue: number[];
}

export function useTagGroups({ tagsValue, tags }: Props) {

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
      body.subjects = getAllTags(tags.filter(t => t.system && t.name === 'Subjects'));
      const allTags = getAllFormTags(tags);
      body.subjectsValue = tagsValue.filter(id => {
        const tag = allTags.find(t => t.id === id);
        return tag?.rootTag?.system && tag?.rootTag?.name === 'Subjects';
      });
      body.tagsValue = tagsValue.filter(id => !body.subjectsValue.includes(id));
    }
    return body;
  }, [tags, tagsValue, specialTypesDisabled]);
  
  return { specialTypesDisabled, tagsGrouped };
}