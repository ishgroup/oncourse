/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import { arrayInsert, arrayRemove, FieldArray } from "redux-form";
import { Holiday, RepeatEndEnum, RepeatEnum } from "@api/model";
import { addHours } from "date-fns";
import AvailabilityRenderer from "./AvailabilityRenderer";
import { getLabelWithCount } from "../../../utils/strings";
import { ShowConfirmCaller } from "../../../../model/common/Confirm";
import AddButton from "../../icons/AddButton";
import { EditViewProps } from "../../../../model/common/ListView";

const addRule = (dispatch: any, form: string) => {
  const item = {} as Holiday;
  item.id = null;
  item.description = undefined;
  item.startDateTime = new Date().toISOString();
  item.endDateTime = addHours(new Date(), 1).toISOString();
  item.repeatEnd = RepeatEndEnum.never;
  item.repeat = RepeatEnum.none;
  item.repeatEndAfter = 0;
  item.repeatOn = undefined;

  dispatch(arrayInsert(form, "rules", 0, item));
  const domNode = document.getElementById("rules[0].description");
  if (domNode) {
    domNode.scrollIntoView({ behavior: "smooth" });
  }
};

const deleteRule = (dispatch: any, showConfirm: ShowConfirmCaller, form: string, field: any, index: number) => {
  showConfirm({
    onConfirm: () => dispatch(arrayRemove(form, "rules", index)),
    confirmMessage: "This item will be removed from Availability Rules list",
    confirmButtonText: "DELETE"
  });
};

interface Props extends EditViewProps {
  timezone?: string;
  name?: string;
  className?: string;
}

const AvailabilityFormComponent = React.memo<Props>(
  ({
     values,
     dispatch,
     showConfirm,
     twoColumn,
     form,
     className,
     name = "rules",
     timezone,
}) => (
  <div className={`${className} pl-3 pr-3 pb-1`}>
    <div className="centeredFlex">
      <div className="heading mt-2 mb-2">
        {getLabelWithCount("Availability Rule", values[name] ? values[name].length : 0)}
      </div>
      <AddButton onClick={addRule.bind(null, dispatch, form, name)} />
    </div>

    <FieldArray
      name={name}
      component={AvailabilityRenderer}
      onDelete={deleteRule.bind(null, dispatch, showConfirm, form)}
      threeColumn={!twoColumn}
      dispatch={dispatch}
      timezone={timezone}
    />
  </div>
    )
);

export default AvailabilityFormComponent;
