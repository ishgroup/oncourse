/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import Typography from "@mui/material/Typography";
import React, { useCallback, useEffect, useState } from "react";
import DuplicateCourseClassModal from "./DuplicateCourseClassModal";

export const DuplicateCourseClassSwingWrapper: React.FC<any> = () => {
  const [showCompleteMessage, setShowCompleteMessage] = useState(false);
  const [dialogOpened, setDialogOpened] = useState(false);
  const [selection, setSelection] = useState([]);

  const onClose = useCallback(() => {
    setShowCompleteMessage(true);
  }, []);

  useEffect(() => {
    if (window.location.search) {
      const search = new URLSearchParams(window.location.search);

      if (search.has("ids")) {
        const ids = search.get("ids").split(",");
        setSelection(ids);
        setDialogOpened(true);
      }

      document.title = "Course class duplicate";
    }
  }, [window.location.search]);

  return (
    <>
      <DuplicateCourseClassModal
        selection={selection}
        opened={dialogOpened}
        setDialogOpened={setDialogOpened}
        closeMenu={onClose}
        disableClose
      />

      {showCompleteMessage && (
        <div className="root">
          <div className="noRecordsMessage">
            <Typography variant="h6" color="inherit" align="center">
              Duplicate process completed. You can close the window
            </Typography>
          </div>
        </div>
      )}
    </>
  );
};
