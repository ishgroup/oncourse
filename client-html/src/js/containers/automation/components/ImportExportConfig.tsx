/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */
import React from "react";
import FileUploadIcon from '@mui/icons-material/FileUpload';
import FileDownloadIcon from '@mui/icons-material/FileDownload';
import { useAppDispatch } from "../../../common/utils/hooks";
import { AutomationEntity } from "../../../model/automation/common";
import { AppBarAction } from  "ish-ui";
import { importAutomationConfig, exportAutomationConfig } from "../actions";
import { uploadAndGetFile } from "../../../common/utils/common";
import { showMessage } from "../../../common/actions";

const getConfigActions = (
  automation: AutomationEntity,
  name: string,
  id: number
): AppBarAction[] => {

  const dispatch = useAppDispatch();

  const onImport = () => {
    uploadAndGetFile().then(f => {
      if (f.type !== "application/x-yaml") {
        dispatch(showMessage({message: "Config file should have .yaml format"}));
        return;
      }
      const reader = new FileReader();
      reader.readAsText(f);
      reader.onload = function (evt) {
        dispatch(importAutomationConfig(automation, id, {config: evt.target.result as string}));
      };
    });
  };

  return [
    {
      action: onImport,
      icon: <FileUploadIcon/>,
      tooltip: "Import config"
    },
    {
      action: () => dispatch(exportAutomationConfig(automation, name, id)),
      icon: <FileDownloadIcon/>,
      tooltip: "Export config"
    }
  ];
};

export default getConfigActions;