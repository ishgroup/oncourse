import { Sale } from "@api/model";
import { change } from "redux-form";
import React from "react";
import EditInPlaceField from "../../../../common/components/form/form-fields/EditInPlaceField";
import { stubFunction } from "../../../../common/utils/common";

export default (
  {
    relationTypes,
    item,
    dispatch,
    form,
    index
  }
) => {
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

  return (
    <div className="ml-2">
      {relationTypes.length && (
        <EditInPlaceField
          meta={{}}
          items={relationTypes}
          input={{
            onChange: onRelationChange,
            onFocus: stubFunction,
            onBlur: stubFunction,
            value: getSelectedRelation()
          }}
          formatting="inline"
          returnType="object"
          placeholder="Select relation"
          select
        />
      )}
    </div>
  );
};
