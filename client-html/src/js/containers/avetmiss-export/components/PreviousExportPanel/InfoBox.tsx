import React from "react";
import { Typography } from "@material-ui/core";
import Button from "@material-ui/core/Button";
import createStyles from "@material-ui/core/styles/createStyles";
import withStyles from "@material-ui/core/styles/withStyles";
import OpenInNew from "@material-ui/icons/OpenInNew";
import clsx from "clsx";
import ButtonBase from "@material-ui/core/ButtonBase";
import { FundingUpload, FundingStatus } from "@api/model";
import { format } from "date-fns";
import { III_DD_MMM_YYYY_HH_MM_AAAA_SPECIAL } from "../../../../common/utils/dates/format";
import { AppTheme } from "../../../../model/common/Theme";

const styles = (theme: AppTheme) =>
  createStyles({
    buttons: {
      "& > *": {
        marginRight: theme.spacing(2)
      },
      "& > *:last-child": {
        marginRight: 0
      }
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
    linkButton: {
      height: "1.2em",
      width: "1.2em",
      borderRadius: `${theme.shape.borderRadius}px`,
      margin: theme.spacing(0, 0, 0.25, 0.25),
      color: theme.palette.secondary.main
    },
    linkButtonIcon: { fontSize: "1.2em" },

  });

interface Props {
  item: FundingUpload;
  classes?: any;
  onClick: (id: number, status: FundingStatus) => void;
}

const InfoBox = ({
 item: {
   id, outcomesCount, created, systemUser
 }, classes, onClick
}: Props) => (
  <div className="pl-2">
    <Typography className="mb-3">
      {outcomesCount}
      {' '}
      outcomes
      <ButtonBase
        href=""
        classes={{
            root: classes.linkButton
          }}
      >
        <OpenInNew
          classes={{
            root: classes.linkButtonIcon
          }}
        />
      </ButtonBase>
      {created && (
      <span className="pl-1">
        exported on
        {' '}
        {format(new Date(created), III_DD_MMM_YYYY_HH_MM_AAAA_SPECIAL).replace(/\./g, "")}
        {' '}
        by
        {" "}
        {systemUser}
      </span>
        )}
    </Typography>
    <div className={clsx(classes.buttons, "d-flex justify-content-end pb-2")}>
      <Button variant="outlined" className="textSecondaryColor" onClick={() => onClick(id, FundingStatus.unknown)}>
        Unknown
      </Button>
      <Button variant="outlined" className="errorColor" onClick={() => onClick(id, FundingStatus.fail)}>
        Fail
      </Button>
      <Button variant="contained" color="primary" onClick={() => onClick(id, FundingStatus.success)}>
        Success
      </Button>
    </div>
  </div>
);

export default withStyles(styles)(InfoBox);
