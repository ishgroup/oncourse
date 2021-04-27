/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useCallback, useEffect, useState } from "react";
import Accordion from "@material-ui/core/Accordion";
import AccordionSummary from "@material-ui/core/AccordionSummary";
import AccordionDetails from "@material-ui/core/AccordionDetails";
import ExpandMoreIcon from "@material-ui/icons/ExpandMore";
import Typography from "@material-ui/core/Typography";
import Delete from "@material-ui/icons/Delete";
import IconButton from "@material-ui/core/IconButton";
import AccordionActions from "@material-ui/core/AccordionActions";
import createStyles from "@material-ui/core/styles/createStyles";
import withStyles from "@material-ui/core/styles/withStyles";
import { getDeepValue } from "../../../utils/common";
import Button from "../../buttons/Button";
import { AppTheme } from "../../../../model/common/Theme";

const styles = (theme: AppTheme) =>
  createStyles({
    root: {
      width: "100%"
    },
    expandIcon: {
      right: "unset",
      left: "0px",
      position: "absolute"
    },
    deleteIcon: {
      fontSize: "22px",
      "&:hover": {
        backgroundColor: "transparent",
        color: theme.palette.error.main
      },
      padding: theme.spacing(1),
      margin: `-${theme.spacing(1)}px -${theme.spacing(1)}px -${theme.spacing(1)}px auto`
    },
    summaryContent: {
      paddingLeft: `${theme.spacing(3)}px`,
      "& > :last-child": {
        paddingRight: `${theme.spacing(1)}px`
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
                expandIcon: classes.expandIcon,
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
                    rootClasses="moreOptions"
                    disabledClasses="moreOptionsDisabled"
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
