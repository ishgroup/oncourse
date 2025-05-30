/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import CircularProgress from '@mui/material/CircularProgress';
import Collapse from '@mui/material/Collapse';
import Divider from '@mui/material/Divider';
import $t from '@t';
import { LinkAdornment } from 'ish-ui';
import React, { useCallback, useState } from 'react';
import { change } from 'redux-form';
import FormField from '../../../../common/components/form/formFields/FormField';
import FullScreenStickyHeader
  from '../../../../common/components/list-view/components/full-screen-edit-view/FullScreenStickyHeader';
import { EditViewProps } from '../../../../model/common/ListView';
import { VetReport } from '../../../../model/entities/VetReporting';
import { getEntityItemById } from '../../common/entityItemsService';
import OutcomeEditFields from '../../outcomes/components/OutcomeEditFields';
import { openOutcomeLink, OutcomeSelectItemRenderer, OutcomeSelectValueRenderer } from '../../outcomes/utils';

const getFieldName = name => `outcome.${name}`;

const VetReportingOutcome = (props: EditViewProps<VetReport>) => {
  const {
    twoColumn,
    form,
    dispatch,
    values
  } = props;

  const [outcomeLoading, setOutcomeLoading] = useState(false);

  // useCallback is needed to prevent infinite loop
  const getCustomSearch = useCallback(search => `enrolment.id is ${values.selectedEnrolment?.id} and module.id not is null and (module.title starts with "${search}" or module.nationalCode starts with "${search}")`,
  [values.selectedEnrolment?.id]);

  const onOutcomeSelect = ou => {
    if (ou.id) {
      setOutcomeLoading(true);

      getEntityItemById("Outcome", ou.id).then(outcome => {
        dispatch(change(form, 'outcome', outcome));
        setOutcomeLoading(false);
      });
    }
  };

  return (
    <div className="pt-1 pl-3 pr-3">
      <FullScreenStickyHeader
        isFixed={false}
        twoColumn={twoColumn}
        title={$t('outcomes3')}
        disableInteraction
      />
      <Divider className="mt-3 mb-3" />
      <FormField
        preloadEmpty
        type="remoteDataSelect"
        name="selectedOutcome"
        entity="Outcome"
        label={$t('select_an_outcome')}
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
      {outcomeLoading
        ? <CircularProgress />
        : <Collapse in={Boolean(values.outcome)} mountOnEnter unmountOnExit>
          {Boolean(values.outcome) && <OutcomeEditFields
            {...props}
            noHeader
            className="mt-2"
            values={values.outcome}
            getFieldName={getFieldName}
            isPriorLearningBinded={values.outcome?.isPriorLearning}
          />}
        </Collapse>
      }
    </div>
  );
};

export default props => props.values
  ? <VetReportingOutcome {...props}/>
  : null;
