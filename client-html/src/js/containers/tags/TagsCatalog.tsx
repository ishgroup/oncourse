import * as React from "react";
import { RouteComponentProps } from "react-router";
import { useAppSelector } from "../../common/utils/hooks";
import CatalogWithSearch from "../../common/components/layout/catalog/CatalogWithSearch";

export const TagsCatalog = ({ history }: RouteComponentProps) => {
  const allTags = useAppSelector(state => state.tags.allTags);
  
  const onOpen = id => {
    history.push(`/tags/tagGroup/${id}`);
  };

  const onClickNew = () => {
    history.push("/tags/tagGroup/new");
  };

  return (
    <CatalogWithSearch
      items={allTags}
      title="Tag groups"
      itemsListTitle="Tag groups"
      onOpen={onOpen}
      customAddNew={onClickNew}
    />
  );
};

export default TagsCatalog;