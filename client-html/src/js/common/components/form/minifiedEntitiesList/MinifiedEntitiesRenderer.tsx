/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useCallback, useEffect, useState } from "react";
import Accordion from "@mui/material/Accordion";
import AccordionSummary from "@mui/material/AccordionSummary";
import AccordionDetails from "@mui/material/AccordionDetails";
import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
import Typography from "@mui/material/Typography";
import Delete from "@mui/icons-material/Delete";
import IconButton from "@mui/material/IconButton";
import AccordionActions from "@mui/material/AccordionActions";
import createStyles from "@mui/styles/createStyles";
import withStyles from "@mui/styles/withStyles";
import Button from "@mui/material/Button";
import { getDeepValue } from "../../../utils/common";
import { AppTheme } from "../../../../model/common/Theme";

const styles = (theme: AppTheme) =>
  createStyles({
    root: {
      width: "100%"
    },
    expandIcon: {
      right: "unset",
      left: theme.spacing(1),
      position: "absolute"
    },
    deleteIcon: {
      fontSize: "22px",
      "&:hover": {
        backgroundColor: "transparent",
        color: theme.palette.error.main
      },
      padding: theme.spacing(1),
      margin: `-${theme.spacing(1)} -${theme.spacing(1)} -${theme.spacing(1)} auto`
    },
    summaryContent: {
      paddingLeft: `${theme.spacing(3)}`,
      "& > :last-child": {
        paddingRight: `${theme.spacing(1)}`
      }
    }
  });

let prevFieldsLength = 0;

const MinifiedEntitiesRenderer: React.FC<any> = props => {
  const {
    entity,
    fields,
    namePath = "name",
    FieldsContent,
    HeaderContent,
    classes,
    onDelete,
    onViewMore,
    accordion,
    twoColumn,
    syncErrors
  } = props;

  const onDeleteHandler = (e, index, id) => {
    e.stopPropagation();
    onDelete(index, id);
  };

  const [expanded, setExpanded] = useState({});

  const rows = fields.getAll();

  const onChangeBase = useCallback(
    index => {
      if (accordion) {
        setExpanded(prev => (prev[index] ? {} : { [index]: true }));
      } else {
        setExpanded(prev => ({ ...prev, [index]: !prev[index] }));
      }
    },
    [accordion]
  );

  useEffect(() => {
    if (fields.length > prevFieldsLength) {
      const noIdFields = fields.getAll().filter(el => !el.id).map((e, ind) => ind);
      if (noIdFields.length) {
        setExpanded(prev => ({
          ...prev,
          ...noIdFields.reduce((p, c) => {
          p[c] = true;
          return p;
        }, {})
        }));
      }
    }
    prevFieldsLength = fields.length;
  }, [fields.length]);

  const fieldsWithError = syncErrors ? (getDeepValue(syncErrors, fields.name) || {}) : {};

  return (
    <div>
      {fields.map((item, index) => {
        const field = fields.get(index);

        return (
          <Accordion
            key={field.id || index}
            expanded={Boolean(fieldsWithError[index]) || Boolean(expanded[index])}
            onChange={() => onChangeBase(index)}
            defaultExpanded={!field.id}
            TransitionProps={{ unmountOnExit: true }}
          >
            <AccordionSummary
              classes={{
                expandIconWrapper: classes.expandIcon,
                content: classes.summaryContent
              }}
              expandIcon={<ExpandMoreIcon />}
            >
              {HeaderContent ? (
                <HeaderContent item={item} row={field} />
              ) : (
                <Typography variant="subtitle2" className="money">
                  {field[namePath]}
                </Typography>
              )}
              {onDelete && (
                <IconButton onClick={e => onDeleteHandler(e, index, field.id)} className={classes.deleteIcon}>
                  <Delete fontSize="inherit" />
                </IconButton>
              )}
            </AccordionSummary>
            <AccordionDetails className="pb-0">
              <FieldsContent item={item} classes={classes} row={field} rows={rows} twoColumn={twoColumn} />
            </AccordionDetails>
            {onViewMore && (
              <AccordionActions>
                {field.id && (
                  <Button
                    className="moreOptions"
                    classes={{
                      disabled: "moreOptionsDisabled"
                    }}
                    onClick={() => onViewMore(entity, field.id, field)}
                  >
                    More options
                  </Button>
                )}
              </AccordionActions>
            )}
          </Accordion>
        );
      })}
    </div>
  );
};

export default withStyles(styles)(MinifiedEntitiesRenderer) as React.FC<any>;
