/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Note } from "@api/model";

export interface NoteExtended extends Note {
  temporaryId: string;
}
