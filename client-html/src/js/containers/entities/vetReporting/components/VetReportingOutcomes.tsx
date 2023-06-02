/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React, { useCallback, useMemo } from "react";
import { EditViewProps } from "../../../../model/common/ListView";
import FullScreenStickyHeader from "../../../../common/components/list-view/components/full-screen-edit-view/FullScreenStickyHeader";
import Divider from "@mui/material/Divider";
import FormField from "../../../../common/components/form/formFields/FormField";
import { LinkAdornment } from "../../../../common/components/form/FieldAdornments";
import Collapse from "@mui/material/Collapse";
import { change } from "redux-form";
import { VetReport } from "../../../../model/entities/VetReporting";
import { openOutcomeLink, OutcomeSelectItemRenderer, OutcomeSelectValueRenderer } from "../../outcomes/utils";
import { getEntityItemById } from "../../common/entityItemsService";
import OutcomeEditFields from "../../outcomes/components/OutcomeEditFields";

const getFieldName = name => `outcome.${name}`;

const VetReportingOutcome = (props: EditViewProps<VetReport>) => {
  const {
    twoColumn,
    form,
    dispatch,
    values,
    expanded,
    tabIndex,
    setExpanded
  } = props;

  // useCallback is needed to prevent infinite loop
  const getCustomSearch = useCallback(search => `enrolment.id is ${values.selectedEnrolment?.id} and module.id not is null and (module.title starts with "${search}" or module.nationalCode starts with "${search}")`,
  [values.selectedEnrolment?.id]);

  const onOutcomeSelect = ou => {
    if (ou.id) {
      getEntityItemById("Outcome", ou.id).then(outcome => {
        dispatch(change(form, 'outcome', outcome));
      });

      if (!expanded.includes(tabIndex)) {
        setExpanded([...expanded, tabIndex]);
      }
    }
  };

  const searchField = useMemo(() => (
      <FormField
        preloadEmpty
        type="remoteDataSelect"
        name="selectedOutcome"
        entity="Outcome"
        label="Select an outcome"
        returnType="object"
        selectValueMark="id"
        selectLabelMark="module.nationalCode"
        aqlColumns="module.nationalCode,module.title,status"
        itemRenderer={OutcomeSelectItemRenderer}
        valueRenderer={OutcomeSelectValueRenderer}
        onChange={onOutcomeSelect}
        getCustomSearch={getCustomSearch}
        labelAdornment={
          <LinkAdornment
            linkHandler={openOutcomeLink}
            link={values.selectedOutcome?.id}
            disabled={!values.selectedOutcome?.id}
          />
        }
      />
  ),
  [values.selectedOutcome?.id, values.selectedEnrolment?.id]);

  return (
    <div className="pt-1 pl-3 pr-3">
      <FullScreenStickyHeader
        isFixed={false}
        twoColumn={twoColumn}
        title="Outcomes"
        disableInteraction
      />
      <Divider className="mt-3 mb-3" />
      {searchField}
      <Collapse in={Boolean(values.outcome)} mountOnEnter unmountOnExit>
        {Boolean(values.outcome) && <OutcomeEditFields
          {...props}
          noHeader
          className="mt-2"
          values={values.outcome}
          getFieldName={getFieldName}
          isPriorLearningBinded={values.outcome?.isPriorLearning}
        />}
      </Collapse>
    </div>
  );
};

export default props => props.values
  ? <VetReportingOutcome {...props}/>
  : null;
