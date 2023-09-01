import { FilterGroup } from "../../../model/common/ListView";
import { FormMenuTag } from "../../../model/tags";
import { getActiveTags } from "../../components/list-view/utils/listFiltersUtils";

export const getFiltersString = (filterGroups: FilterGroup[], tags: FormMenuTag[]) => {
  const activeFiltersString = filterGroups
    .map(group => {
      const activeFilters = group.filters.filter(i => i.active).map(j => j.expression);

      let activeFiltersFormattedString = activeFilters.map(v => `(${v})`).join(" or ");

      if (activeFilters.length > 1) {
        activeFiltersFormattedString = `( ${activeFiltersFormattedString} )`;
      }

      return activeFiltersFormattedString;
    })
    .filter(v => v.trim())
    .join(" and ");

  const activeTagsString = tags
    .map(t => {
      const activeTags = getActiveTags(t.children).map(i => "#" + ` "${i.tagBody.name}"`);

      let activeTagsFormattedString = activeTags.map(v => `(${v})`).join(" or ");

      if (activeTags.length > 1) {
        activeTagsFormattedString = `( ${activeTagsFormattedString} )`;
      }

      return activeTagsFormattedString;
    })
    .filter(v => v.trim())
    .join(" and ");

  return activeFiltersString
    ? activeTagsString ? `${activeFiltersString} and ${activeTagsString}` : activeFiltersString
    : activeTagsString;
};
