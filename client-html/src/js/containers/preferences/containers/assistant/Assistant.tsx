/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import * as React from 'react';
import { useAppSelector } from '../../../../common/utils/hooks';
import { Categories } from '../../../../model/preferences';
import FormContainer from '../FormContainer';
import AssistantForm, { ASSISTANT_FORM_NAME } from './components/AssistantForm';

const Assistant = () => {
  const assistant = useAppSelector(state => state.preferences.assistant);

  return (
    <FormContainer
      data={assistant}
      category={Categories.assistant}
      form={formRoleName => <AssistantForm formRoleName={formRoleName} />}
      formName={ASSISTANT_FORM_NAME}
    />
  );
};

export default Assistant;
