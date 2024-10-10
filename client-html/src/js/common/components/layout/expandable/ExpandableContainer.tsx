/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import ExpandMore from "@mui/icons-material/ExpandMore";
import Collapse from "@mui/material/Collapse";
import Divider from "@mui/material/Divider";
import IconButton from "@mui/material/IconButton";
import Typography from "@mui/material/Typography";
import { createStyles, withStyles } from "@mui/styles";
import clsx from "clsx";
import { AddButton, AppTheme } from "ish-ui";
import React, { useEffect, useMemo, useRef, useState } from "react";
import { findDOMNode } from "react-dom";
import { FormErrors } from "redux-form";
import { IS_JEST } from "../../../../constants/EnvironmentConstants";
import { animateFormErrors } from "../../../utils/highlightFormErrors";
import { getFirstErrorNodePath } from "../../../utils/validation";
import FormField from "../../form/formFields/FormField";

const styles = (theme: AppTheme) =>
  createStyles({
    expandButton: {
      position: "absolute",
      right: 0,
      transition: `transform ${theme.transitions.duration.shortest}ms ${theme.transitions.easing.easeInOut}`
    },
    expandButtonExpanded: {
      transform: "rotate(180deg)"
    },
    controls: {
      position: "relative",
      paddingRight: theme.spacing(5)
    },
    expanded: {}
  });

interface Props {
  children: React.ReactNode;
  header: React.ReactNode;
  expanded: any[];
  index: any;
  name?: string;
  formErrors?: FormErrors;
  setExpanded: (arg: number[]) => void;
  headerAdornment?: React.ReactNode;
  onChange?: (e: Event, expanded: number[]) => void;
  onAdd?: any;
  classes?: any;
  noDivider?: boolean;
  className?: string;
}

const ExpandableContainer: React.FC<Props> = ({
  children,
  header,
  headerAdornment,
  onAdd,
  onChange,
  classes,
  expanded,
  setExpanded,
  index,
  noDivider,
  formErrors,
  className,
  name
}) => {
  const [hasErrors, setHasErrors] = useState(false);

  const childrenRef = useRef<HTMLDivElement>();

  const headerRef = useRef<any>();

  const isExpanded = useMemo(() => expanded.includes(index), [expanded, index]);

  const toggleExpand = e => {
    const updated = [...expanded];

    if (isExpanded) {
      updated.splice(
        updated.findIndex(i => i === index),
        1
      );
    } else {
      updated.push(index);
    }

    if (onChange) {
      onChange(e, updated);
    }
    if (!e?.isDefaultPrevented()) {
      setExpanded(updated);
    }
  };

  const buttonId = `expand-button-${index}`;
  const iconButtonProps = IS_JEST ? {
    'data-testid': buttonId,
  } : {};

  useEffect(() => {
    if (formErrors && childrenRef.current) {
      const domNode = findDOMNode(childrenRef.current) as HTMLDivElement;

      let childrenError = false;

      if (domNode?.querySelector(`[name="${getFirstErrorNodePath(formErrors)}"]`)) {
        childrenError = true;
      }
      setHasErrors(childrenError);
    }
  }, [childrenRef.current, formErrors]);

  useEffect(() => {
    if (hasErrors && !isExpanded) {
      toggleExpand(null);
    }
  }, [hasErrors, isExpanded]);

  const clickHandler = hasErrors ? () => animateFormErrors(childrenRef.current) : toggleExpand;

  const nameError = useMemo(
    () =>
      formErrors && formErrors[name] && (
        <div className="shakingError mb-2">
          <Typography color="error" variant="body2" className="text-pre-wrap">
            {formErrors[name]}
          </Typography>
        </div>
      ),
    [formErrors, name]
  );

  return (
    <div className={className}>
      <Divider className={clsx("mb-2 mb-3", noDivider && "invisible")}/>
      <div ref={headerRef}>
        <div className={clsx("centeredFlex mb-2", classes.controls)}>
          <div className="centeredFlex">
            <div
              className={clsx("heading headingHover", isExpanded && classes.expanded, hasErrors && 'errorColor')}
              onClick={clickHandler}>
              {header}
            </div>
            <AddButton className={onAdd ? null : "invisible"} onClick={onAdd} />
          </div>
          {headerAdornment}
          <IconButton
            onClick={clickHandler}
            className={clsx(classes.expandButton, isExpanded && classes.expandButtonExpanded)}
            id={buttonId}
            {...iconButtonProps}
          >
            <ExpandMore/>
          </IconButton>
        </div>
      </div>

      {nameError}

      <div ref={childrenRef} id={name}>
        {name && <FormField type="stub" name={name} />}
        <Collapse in={isExpanded}>
          {children}
        </Collapse>
      </div>
    </div>
  );
};

export default withStyles(styles)(ExpandableContainer);