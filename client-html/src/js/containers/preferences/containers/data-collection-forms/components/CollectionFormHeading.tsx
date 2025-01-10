/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import clsx from "clsx";
import * as React from "react";
import FormField from "../../../../../common/components/form/formFields/FormField";
import { validateSingleMandatoryField } from "../../../../../common/utils/validation";

const validateUniqueNames = (value, allValues) => {
  const match = allValues.items.filter(
    item => item.baseType === "heading" && (item.name && item.name.trim() === value.trim())
  );
  return match.length > 1 ? "Heading name must be unique" : undefined;
};

const CollectionFormHeading = props => {
  const {
    item
  } = props;

  return (
    <div
      className={clsx({
        [clsx("centeredFlex")]: true,
      })}
    >
      <div className={clsx("align-items-center flex-fill mw-100 overflow-hidden")}>
        <FormField
          type="text"
          name={`items[${item.id}].name`}
          label="Heading"
          validate={[validateSingleMandatoryField, validateUniqueNames]}
          fieldClasses={{
            label: "heading"
          }}
        />

        <FormField
          type="multilineText"
          name={`items[${item.id}].description`}
          label="Description"
          className="mt-1"
          truncateLines={4}
        />
      </div>
    </div>
  );
};

export default CollectionFormHeading;
