/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { GTMContainer } from '../models/Google';
import { State } from '../models/State';

export const getTokenString = (state: State) => `Bearer ${state.google.token?.access_token}`;

export const renderContainerLabel = (con: GTMContainer) => `${con.name} (${con.publicId})`;
