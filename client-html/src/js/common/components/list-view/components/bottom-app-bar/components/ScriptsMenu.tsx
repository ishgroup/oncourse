/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import MenuItem from "@mui/material/MenuItem";
import { Script } from "@api/model";
import { Menu } from "@mui/material";
import { FilterScriptsBy } from "../../../../../../model/common/ListView";

interface ScriptsMenuProps {
  scripts?: Script[];
  classes: any;
  closeAll: () => void;
  entity: string;
  filterScriptsBy?: FilterScriptsBy;
  openScriptModal: (scriptId: number) => void;
  scriptsMenuOpen: any;
  setScriptsMenuOpen: any;
}

const ScriptsMenu = React.memo<ScriptsMenuProps>(props => {
  const {
     scripts, classes, filterScriptsBy, closeAll, openScriptModal, scriptsMenuOpen, setScriptsMenuOpen
  } = props;

  return (
    <>
      {filterScriptsBy
        ? (
          Object.keys(filterScriptsBy).map(k => {
            
            if (!filterScriptsBy[k].scripts.length) return null;
            
            return (
              <MenuItem
                key={k}
                classes={{
                  root: "listItemPadding"
                }}
                onClick={event => setScriptsMenuOpen({ anchor: event.currentTarget, entity: k })}
              >
                Execute script for {filterScriptsBy[k].ids.length} {k.toLowerCase()}{filterScriptsBy[k].ids.length > 1 ? k[k.length - 1] === "s" ? "es" : "s" : ""}
              </MenuItem>
            );
          })
        )
        : (
          <MenuItem
            classes={{
              root: "listItemPadding"
            }}
            onClick={event => setScriptsMenuOpen({ anchor: event.currentTarget })}
          >
            Execute script
          </MenuItem>
        )
      }

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