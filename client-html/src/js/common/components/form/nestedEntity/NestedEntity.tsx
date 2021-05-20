/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useCallback, useMemo } from "react";
import Typography from "@material-ui/core/Typography";
import IconButton from "@material-ui/core/IconButton";
import AddCircle from "@material-ui/icons/AddCircle";
import Button from "@material-ui/core/Button";
import Badge from "@material-ui/core/Badge";
import ButtonGroup from "@material-ui/core/ButtonGroup";
import DateRange from "@material-ui/icons/DateRange";
import Launch from "@material-ui/icons/Launch";
import Grid from "@material-ui/core/Grid";
import { ShowConfirmCaller } from "../../../../model/common/Confirm";
import { EntityType } from "../../../../model/common/NestedEntity";
import { openInternalLink } from "../../../utils/links";

interface Props {
  entityName?: string;
  entityTypes: EntityType[];
  dirty: boolean;
  goToLink?: string;
  showConfirm: ShowConfirmCaller;
  twoColumn?: boolean;
  addLink?: string;
  isNew?: boolean;
  preventAddMessage?: string;
}

const NestedEntity: React.FC<Props> = ({
  entityName,
  entityTypes,
  addLink,
  dirty,
  showConfirm,
  twoColumn,
  isNew,
  preventAddMessage,
  goToLink
}) => {
  const openEntityLink = useCallback(
    link => {
      openInternalLink(link);
    },
    [dirty]
  );

  const saveAlert = () =>
  showConfirm(
    {
      confirmMessage: preventAddMessage,
      cancelButtonText: "OK"
    },
  );

  const openAddLink = useCallback(() => {
    openInternalLink(addLink);
  }, [dirty, addLink]);

  const openTimetableLink = useCallback(
    link => {
      openInternalLink(link);
    },
    [dirty]
  );

  const addAction = useMemo(
    () =>
      (addLink ? (
        <IconButton onClick={isNew ? saveAlert : openAddLink}>
          <AddCircle className="addButtonColor" />
        </IconButton>
      ) : null),
    [isNew, addLink]
  );

  return (
    <div>
      {entityName && (
        <div className="centeredFlex">
          <Typography className="heading pt-2 pb-2">{entityName}</Typography>
          {goToLink && (
            <IconButton style={{ marginRight: "-8px" }} color="secondary" onClick={() => openInternalLink(goToLink)}>
              <Launch />
            </IconButton>
          )}
          {addAction}
        </div>
      )}

      <Grid container>
        {entityTypes.map((t, i) => (
          <Grid key={i} item xs={twoColumn ? "auto" : 12} className="mb-2">
            <Badge
              color="primary"
              classes={{
                badge: "mr-2"
              }}
              badgeContent={t.count}
              max={999}
            >
              <ButtonGroup variant="contained" className="mr-2" disabled={t.disabled}>
                {t.timetableLink && (
                  <Button size="small" onClick={() => openTimetableLink(t.timetableLink)}>
                    <DateRange />
                  </Button>
                )}
                <Button size="small" onClick={t.linkHandler ? t.linkHandler : () => openEntityLink(t.link)}>
                  <Typography variant="button" color={t.grayOut ? "textSecondary" : "inherit"}>
                    {t.name}
                  </Typography>
                </Button>
              </ButtonGroup>
            </Badge>
            {!entityName && entityTypes.length === 1 && addAction}
          </Grid>
        ))}
      </Grid>
    </div>
  );
};

export default NestedEntity;
