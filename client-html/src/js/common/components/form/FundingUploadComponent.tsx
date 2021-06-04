/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { Component } from "react";
import {
  Typography,
  Button,
  ButtonBase,
  createStyles,
  withStyles,
  FormControl,
  Select,
  MenuItem
} from "@material-ui/core";
import { AvetmissExportSettings, FundingStatus, FundingUpload } from "@api/model";
import clsx from "clsx";
import { format } from "date-fns";
import { KeyboardArrowDown, OpenInNew } from "@material-ui/icons";
import { stubComponent } from "../../utils/common";
import { III_DD_MMM_YYYY_HH_MM_AAAA_SPECIAL } from "../../utils/dates/format";
import { AnyArgFunction } from "../../../model/common/CommonFunctions";
import { openInternalLink } from "../../utils/links";
import { AppTheme } from "../../../model/common/Theme";

const styles = (theme: AppTheme) => createStyles({
    rootPanel: {
      display: "grid",
      gridTemplateColumns: "1fr auto",
      gridColumnGap: "8px",
      marginBottom: theme.spacing(2)
    },
    actionGroup: {
      display: "flex",
      alignItems: "baseline",
      width: "168px",
      justifyContent: "space-between"
    },
    button: {
      whiteSpace: "nowrap",
      minHeight: "auto",
      padding: theme.spacing(0, 1),
      textTransform: "lowercase"
    },
    buttonContained: {
      boxShadow: "none"
    },
    subheadingButton: {
      width: "30px",
      minWidth: "unset"
    },
    buttonIcon: {
      fontSize: "18px"
    },
    inlineBlock: {
      display: "inline-block"
    },
    textInfo: {
      fontSize: `${theme.typography.fontSize - 2}px`,
      margin: theme.spacing(0, -0.5),
      display: "flex",
      alignItems: "baseline",
      flexWrap: "wrap",
      "& > *": {
        padding: theme.spacing(0, 0.5)
      }
    },
    linkButton: {
      height: "1.2em",
      width: "1.2em",
      borderRadius: `${theme.shape.borderRadius}px`,
      margin: theme.spacing(0, 0, 0.25, 0.25)
    },
    linkButtonIcon: { fontSize: "1.2em" },
    rootSelect: {
      textTransform: "uppercase",
      fontSize: `${theme.typography.fontSize - 2}px`,
      fontWeight: 700
    },
    select: {
      paddingRight: `${theme.spacing(2.5)}px`
    }
  });

const fundingStatuses: any[] = [
  { value: FundingStatus.success, label: "Success" },
  { value: FundingStatus.fail, label: "Failed" },
  { value: FundingStatus.unknown, label: "Unknown" }
];

interface Props {
  fundingUpload: FundingUpload;
  onStatusChange?: (id: number, status: FundingStatus) => void;
  classes?: any;
  className?: any;
  setFirstUploadNode?: AnyArgFunction;
  onRunAgainClicked?: (settings: AvetmissExportSettings) => void;
  readOnly?: boolean;
}

class FundingUploadComponent extends Component<Props, any> {
  handleOutcomeLink = (fundingUploadId: number) => {
    openInternalLink(`/outcome?search=fundingUploadOutcomes.fundingUpload.id=${fundingUploadId}`);
  };

  handleChange = event => {
    const { onStatusChange, fundingUpload } = this.props;

    onStatusChange(fundingUpload.id, event.target.value);
  };

  render() {
    const {
 fundingUpload, classes, className, setFirstUploadNode, onRunAgainClicked, readOnly
} = this.props;

    return (
      <div ref={setFirstUploadNode} className={clsx(className, classes.root)}>
        <div className={classes.rootPanel}>
          <div>
            <Typography variant="body2">
              {format(new Date(fundingUpload.created), III_DD_MMM_YYYY_HH_MM_AAAA_SPECIAL).replace(/\./g, "")}
            </Typography>
            <Typography variant="body2" className={classes.textInfo}>
              <span>{fundingUpload.systemUser}</span>
              <span>
                {fundingUpload.outcomesCount}
                {' '}
                outcomes
                <ButtonBase
                  onClick={() => this.handleOutcomeLink(fundingUpload.id)}
                  classes={{
                    root: classes.linkButton
                  }}
                >
                  <OpenInNew
                    color="secondary"
                    classes={{
                      root: classes.linkButtonIcon
                    }}
                  />
                </ButtonBase>
              </span>
            </Typography>
          </div>
          <div className={classes.actionGroup}>
            <FormControl>
              <Select
                IconComponent={readOnly ? stubComponent : KeyboardArrowDown}
                value={fundingUpload.status}
                disableUnderline
                classes={{
                  root: clsx(classes.rootSelect, {
                    "textSecondaryColor": fundingUpload.status === FundingStatus.unknown,
                    "errorColor": fundingUpload.status === FundingStatus.fail,
                    "successColor": fundingUpload.status === FundingStatus.success
                  }),
                  select: classes.select
                }}
                onChange={this.handleChange}
                disabled={readOnly}
              >
                {fundingStatuses.map((value, index) => (
                  <MenuItem key={index} value={value.value}>
                    {value.label}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>

            {!readOnly && fundingUpload.settings && (
              <Button
                variant="contained"
                color="primary"
                size="small"
                onClick={() => onRunAgainClicked(fundingUpload.settings)}
                classes={{
                  root: classes.button,
                  contained: classes.buttonContained
                }}
              >
                Run again
              </Button>
            )}
          </div>
        </div>
      </div>
    );
  }
}

export default withStyles(styles)(FundingUploadComponent);
