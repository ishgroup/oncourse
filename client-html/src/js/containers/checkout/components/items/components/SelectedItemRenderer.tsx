/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { memo, useEffect, useState } from "react";
import clsx from "clsx";
import { format as formatDate } from "date-fns";
import createStyles from "@material-ui/core/styles/createStyles";
import withStyles from "@material-ui/core/styles/withStyles";
import { fade } from "@material-ui/core/styles/colorManipulator";
import Typography from "@material-ui/core/Typography";
import IconButton from "@material-ui/core/IconButton";
import Delete from "@material-ui/icons/Delete";
import { appendTimezone } from "../../../../../common/utils/dates/formatTimezone";
import { AppTheme } from "../../../../../model/common/Theme";
import { III_DD_MMM_YYYY_HH_MM_AAAA } from "../../../../../common/utils/dates/format";
import CheckoutAlertTextMessage from "../../CheckoutAlertTextMessage";

const styles = (theme: AppTheme) => createStyles({
  root: {
    paddingTop: "3px",
    paddingBottom: "3px",
    alignItems: "flex-start",
    "&:hover $deleteIcon": {
      visibility: "visible"
    }
  },
  deleteIcon: {
    color: fade(theme.palette.text.primary, 0.2),
    padding: 5,
    fontSize: 16,
    marginTop: -3,
    visibility: "hidden",
    marginLeft: 5
  }
});

const CourseItemRenderer: React.FC<any> = props => {
  const {
   item, index, classes, onDelete, openRow, selected
  } = props;

  return (
    <>
      <div className="flex-fill text-truncate cursor-pointer">
        <div className="flex-fill text-truncate text-nowrap" onClick={openRow ? () => openRow(item) : undefined}>
          <Typography variant="body2" className={clsx({ "fontWeight600": selected })} noWrap>
            {item.code}
            <span className="mr-1" />
            {item.name}
          </Typography>
          <div className="align-items-center text-truncate">
            <Typography variant="caption">
              {item.class.courseCode}
              -
              {item.class.code}
              {item.class.startDateTime !== null
                ? " ( "
                  + formatDate(item.class.siteTimezone
                  ? appendTimezone(new Date(item.class.startDateTime), item.class.siteTimezone)
                  : new Date(item.class.startDateTime), III_DD_MMM_YYYY_HH_MM_AAAA)
                    .replace("a.m.", "am")
                    .replace("p.m.", "pm")
                  + " )"
                : ""}
            </Typography>
          </div>
        </div>
      </div>
      {onDelete && (
        <IconButton className={classes.deleteIcon} color="secondary" onClick={e => onDelete(e, index, item)}>
          <Delete fontSize="inherit" color="inherit" />
        </IconButton>
      )}
    </>
  );
};

export const StyledCourseItemRenderer = withStyles(styles)(CourseItemRenderer);

const CommonItemRenderer: React.FC<any> = props => {
  const {
 item, index, classes, onDelete, openRow, selected
} = props;

  return (
    <>
      <div className="flex-fill text-truncate cursor-pointer" onClick={() => openRow(item)}>
        <Typography variant="body2" noWrap className={clsx({ "fontWeight600": selected })}>
          {item.name}
        </Typography>
      </div>
      <IconButton className={classes.deleteIcon} color="secondary" onClick={e => onDelete(e, index, item)}>
        <Delete fontSize="inherit" color="inherit" />
      </IconButton>
    </>
  );
};

const Item = props => {
  const {
 index, classes, selectedCourse, openedItem, item
} = props;

  const [mounted, setMounted] = useState(false);

  const selected = React.useMemo(
    () => (
      (selectedCourse && selectedCourse.id === item.id)
      || (openedItem && openedItem.id === item.id && openedItem.type === item.type)
    ),
    [selectedCourse, openedItem]
  );

  useEffect(() => {
    setTimeout(() => {
      setMounted(true);
    }, 500);
  }, []);

  return (
    <div
      id={`animateSelectedItem-${index}`}
      className={clsx(
      classes.root,
      "relative",
      !mounted && "bounceInRight animated",
      selected && "selectedItemArrow"
    )}
    >
      <div className="centeredFlex">
        {item.type === "course" ? (
          <CourseItemRenderer item={item} {...props} selected={selected} />
      ) : (
        <CommonItemRenderer item={item} {...props} selected={selected} />
      )}
      </div>
      {mounted && item.type === "course" && item.class.message && (
        <CheckoutAlertTextMessage message={item.class.message} />
      )}
    </div>
);
};

const SelectedItemRenderer = memo<any>(({
 items, ...rest
}) => {
  return (
    <div className="relative">
      {items && items.map((item, index) => <Item item={item} index={index} {...rest} />)}
    </div>
  );
});

export default withStyles(styles)(SelectedItemRenderer);
