/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useState } from "react";
import MenuItem from "@material-ui/core/MenuItem";
import { Script } from "@api/model";
import { Menu } from "@material-ui/core";

interface ScriptsMenuProps {
  scripts?: Script[];
  classes: any;
  closeAll: () => void;
  entity: string;
  openScriptModal: (scriptId: number) => void;
}

const ScriptsMenu = React.memo<ScriptsMenuProps>(props => {
  const {
     scripts, classes, closeAll, openScriptModal
  } = props;

  const [scriptsMenuOpen, setScriptsMenuOpen] = useState(null);

  return (
    <>
      <MenuItem
        classes={{
          root: "listItemPadding"
        }}
        onClick={event => setScriptsMenuOpen(event.currentTarget)}
      >
        Execute script
      </MenuItem>

      <Menu
        id="scripts"
        anchorEl={scriptsMenuOpen}
        open={Boolean(scriptsMenuOpen)}
        onClose={() => setScriptsMenuOpen(null)}
        classes={{
          paper: classes.cogWheelMenuOffset
        }}
      >
        {scripts.map((s: Script) => (
          <MenuItem
            classes={{
              root: "listItemPadding"
            }}
            key={s.id}
            disabled={scripts.length === 0}
            onClick={() => {
              openScriptModal(s.id);
              closeAll();
            }}
          >
            {s.name}
          </MenuItem>
        ))}
      </Menu>
    </>
  );
});

export default ScriptsMenu;
