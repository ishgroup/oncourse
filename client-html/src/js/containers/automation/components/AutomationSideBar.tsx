/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import $t from '@t';
import { History } from 'history';
import { makeAppStyles } from 'ish-ui';
import React from 'react';
import SideBarHeader from '../../../common/components/layout/side-bar-list/SideBarHeader';

interface Props {
  history: History;
}

const useStyles = makeAppStyles()(theme => ({
  root: {
    color: theme.palette.secondary.main,
    padding: theme.spacing(3),
    marginTop: theme.spacing(3)
  }
}));

const getSelected = (history: History) => {
  switch (true) {
    case history.location.pathname.startsWith("/automation/script"):
      return 1;
    case history.location.pathname.startsWith("/automation/import-template"):
      return 2;
    case history.location.pathname.startsWith("/automation/export-template"):
      return 3;
    case history.location.pathname.startsWith("/automation/email-template"):
      return 4;
    case history.location.pathname.startsWith("/automation/pdf-background"):
      return 5;
    case history.location.pathname.startsWith("/automation/pdf-report"):
      return 6;
    case history.location.pathname.startsWith("/automation/integration"):
      return 7;
    default:
      return 0;
  }
};

const AutomationSideBar = ({
    history
  }: Props) => {
  const selectHandler = route => () => {
    history.push(route);
  };

  const selected = getSelected(history);

  const { classes } = useStyles();

  return (
    <div className={classes.root}>
      <SideBarHeader
        selected={selected === 1}
        label={$t('automations')}
        onClick={selectHandler("/automation/scripts")}
      />

      <SideBarHeader
        selected={selected === 2}
        label={$t('import_templates')}
        onClick={selectHandler("/automation/import-templates/")}
      />

      <SideBarHeader
        selected={selected === 3}
        label={$t('export_templates')}
        onClick={selectHandler("/automation/export-templates/")}
      />

      <SideBarHeader
        selected={selected === 4}
        label={$t('message_templates2')}
        onClick={selectHandler("/automation/email-templates/")}
      />

      <SideBarHeader
        selected={selected === 5}
        label={$t('pdf_backgrounds')}
        onClick={selectHandler("/automation/pdf-backgrounds/")}
      />

      <SideBarHeader
        selected={selected === 6}
        label={$t('pdf_reports')}
        onClick={selectHandler("/automation/pdf-reports/")}
      />

      <SideBarHeader
        selected={selected === 7}
        label={$t('integrations')}
        onClick={selectHandler("/automation/integrations")}
      />
    </div>
  );
};

export default AutomationSideBar;
