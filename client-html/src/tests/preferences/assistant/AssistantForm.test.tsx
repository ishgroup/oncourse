/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import * as React from 'react';
import Assistant from '../../../js/containers/preferences/containers/assistant/Assistant';
import { preferencesFormRole } from '../../../js/containers/preferences/containers/FormContainer';
import { AssistantAdminOnly, AssistantEnabled, AssistantStudentDataAllowed } from '../../../js/model/preferences';
import { dashedCase } from '../../common/utils';
import { mockedAPI, TestEntry } from '../../TestEntry';

const formValues = (enabled: boolean, adminOnly: boolean, studentDataAllowed: boolean) => dashedCase({
  [AssistantEnabled.uniqueKey]: enabled,
  [AssistantAdminOnly.uniqueKey]: adminOnly,
  [AssistantStudentDataAllowed.uniqueKey]: studentDataAllowed
});

const renderAssistant = async () => {
  render(
    <TestEntry>
      <Assistant />
    </TestEntry>
  );

  // the mocked server has the defaults: disabled, admins only, no student data
  await waitFor(() => expect(screen.getByRole(preferencesFormRole)).toHaveFormValues(formValues(false, true, false)));
};

describe('Assistant preferences form', () => {
  it('shows the assistant settings', async () => {
    await renderAssistant();

    expect(screen.getAllByText('onCourse Assistant').length).toBeGreaterThan(0);
    expect(screen.getByLabelText('Enable onCourse Assistant')).not.toBeChecked();
    expect(screen.getByText('Shows the Ask onCourse AI button to permitted users')).toBeInTheDocument();
    expect(screen.getByLabelText('Available to')).toBeChecked();
    expect(screen.getByText('Users with the Admin toggle on')).toBeInTheDocument();
    expect(screen.getByText('Pilot')).toBeInTheDocument();
    expect(screen.getByLabelText('Allow student data to be sent to the AI model')).not.toBeChecked();
  });

  it('describes who can use the assistant when it is not limited to admins', async () => {
    await renderAssistant();

    fireEvent.click(screen.getByLabelText('Available to'));

    expect(screen.getByLabelText('Available to')).not.toBeChecked();
    expect(screen.getByText('All users, within their existing permissions')).toBeInTheDocument();
    expect(screen.queryByText('Users with the Admin toggle on')).not.toBeInTheDocument();
  });

  it('saves changed settings', async () => {
    await renderAssistant();

    expect(screen.getByTestId('appbar-submit-button')).toBeDisabled();

    fireEvent.click(screen.getByLabelText('Enable onCourse Assistant'));
    fireEvent.click(screen.getByLabelText('Allow student data to be sent to the AI model'));
    fireEvent.click(screen.getByTestId('appbar-submit-button'));

    await waitFor(() => expect(mockedAPI.db.preference[AssistantEnabled.uniqueKey]).toBe('true'));
    expect(mockedAPI.db.preference[AssistantStudentDataAllowed.uniqueKey]).toBe('true');
    expect(mockedAPI.db.preference[AssistantAdminOnly.uniqueKey]).toBe('true');
  });
});
