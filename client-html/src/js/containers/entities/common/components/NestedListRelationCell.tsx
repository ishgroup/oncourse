import { Sale } from "@api/model";
import { change } from "redux-form";
import React, { useMemo } from "react";
import { stubFunction } from "../../../../common/utils/common";
import EditInPlaceSearchSelect from "../../../../common/components/form/formFields/EditInPlaceSearchSelect";

export default (
  {
    relationTypesFilter,
    relationTypes,
    item,
    dispatch,
    form,
    index
  }
) => {
  const filteredTypes = useMemo(() => {
    if (relationTypesFilter && relationTypesFilter.entities.includes(item.entityName)) {
      return relationTypes.filter(relationTypesFilter.filter);
    }
    return relationTypes;
  }, [relationTypesFilter, relationTypes, item.entityName]);

  const onRelationChange = rel => {
    const entityId = item.entityFromId || item.entityToId;
    const changed: Sale & { tempId: any } = {
      id: item.id,
      name: item.name,
      code: item.code,
      active: item.active,
      type: item.type,
      expiryDate: item.expiryDate,
      entityFromId: rel.combined ? entityId : rel.isReverseRelation ? null : entityId,
      entityToId: rel.isReverseRelation ? entityId : null,
      relationId: rel.id,
      tempId: item.tempId
    };
    dispatch(change(form, `relatedSellables[${index}]`, changed));
  };

  const getSelectedRelation = () => {
    if (!relationTypes.length || typeof item.relationId !== "number") {
      return null;
    }
    return relationTypes.find(t => t.id === item.relationId && (t.combined || (
      typeof item.entityToId === "number"
        ? t.isReverseRelation
        : !t.isReverseRelation
    )));
  };

  const hasError = !filteredTypes.length;

  return (
    <div className="ml-2 mt-0-5">
      {Boolean(relationTypes.length) && (
        <EditInPlaceSearchSelect
          meta={{
            error: hasError && "No available relation types",
            invalid: hasError
          }}
          items={filteredTypes}
          input={{
            onChange: onRelationChange,
            onFocus: stubFunction,
            onBlur: stubFunction,
            value: getSelectedRelation()
          }}
          returnType="object"
          placeholder="Select relation"
          inline
        />
      )}
    </div>
  );
};
