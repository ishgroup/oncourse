/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useEffect, useState } from "react";
import MenuItem from "@material-ui/core/MenuItem";
import { connect } from "react-redux";
import { Script } from "@api/model";
import { Menu } from "@material-ui/core";
import { Dispatch } from "redux";
import { getScripts } from "../../../../../actions";

interface ScriptsMenuProps {
  scripts?: Script[];
  classes: any;
  getScripts: (entity: string) => void;
  executeScript: (scriptId: number, entity: string, entityIds: string[]) => void;
  closeAll: () => void;
  entity: string;
  openScriptModal: (scriptId: number) => void;
}

const ScriptsMenu = React.memo<ScriptsMenuProps>(props => {
  const {
    getScripts, entity, scripts, classes, closeAll, openScriptModal
  } = props;

  const [scriptsMenuOpen, setScriptsMenuOpen] = useState(null);

  useEffect(() => {
    if (!scripts) {
      getScripts(entity);
    }
  }, []);

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


const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  getScripts: (entity: string) => dispatch(getScripts(entity))
});

export default connect<any, any, any>(null, mapDispatchToProps)(ScriptsMenu);
