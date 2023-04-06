/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React, { useCallback } from "react";
import ExecuteScriptModal from "../../../containers/automation/containers/scripts/components/ExecuteScriptModal";
import { BooleanArgFunction, StringArgFunction } from "../../../model/common/CommonFunctions";
import navigation from "./data/navigation.json";
import SideBarHeader from "../layout/side-bar-list/SideBarHeader";

interface Props {
  selected: string | number,
  execMenuOpened: boolean,
  scriptIdSelected: number,
  setSelected: StringArgFunction,
  setExecMenuOpened: BooleanArgFunction,
  setScriptIdSelected: BooleanArgFunction,
}

const Navigation = (
  {
    selected,
    setSelected,
    execMenuOpened,
    setExecMenuOpened,
    setScriptIdSelected,
    scriptIdSelected,
  }: Props
) => {
  const getSelect = useCallback((category: string) => () => setSelected(selected === category ? null : category), [selected]);

  return (
    <div>
      <div className="secondaryColor pt-2 pr-2 pl-2 pb-1">
        {navigation.categories.map(category => (
          <SideBarHeader
            key={category.key}
            selected={selected === category.key}
            label={category.title}
            onClick={getSelect(category.key)}
          />
        ))}
      </div>
      <ExecuteScriptModal
        opened={execMenuOpened}
        onClose={() => {
          setExecMenuOpened(false);
          setScriptIdSelected(null);
        }}
        scriptId={scriptIdSelected}
      />
    </div>
  );
};

export default Navigation;