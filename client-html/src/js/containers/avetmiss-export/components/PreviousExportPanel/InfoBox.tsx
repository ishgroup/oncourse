import { FundingStatus, FundingUpload } from '@api/model';
import OpenInNew from '@mui/icons-material/OpenInNew';
import Button from '@mui/material/Button';
import ButtonBase from '@mui/material/ButtonBase';
import Typography from '@mui/material/Typography';
import $t from '@t';
import clsx from 'clsx';
import { format } from 'date-fns';
import { AppTheme, III_DD_MMM_YYYY_HH_MM_AAAA_SPECIAL } from 'ish-ui';
import React from 'react';
import { withStyles } from 'tss-react/mui';

const styles = (theme: AppTheme) =>
  ({
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
      {$t('outcomes2')}
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
        {$t('exported_on_by', [format(new Date(created), III_DD_MMM_YYYY_HH_MM_AAAA_SPECIAL).replace(/\./g, "")])}
        {" "}
        {systemUser}
      </span>
        )}
    </Typography>
    <div className={clsx(classes.buttons, "d-flex justify-content-end pb-2")}>
      <Button variant="outlined" className="textSecondaryColor" onClick={() => onClick(id, FundingStatus.unknown)}>
        {$t('unknown')}
      </Button>
      <Button variant="outlined" className="errorColor" onClick={() => onClick(id, FundingStatus.fail)}>
        {$t('fail')}
      </Button>
      <Button variant="contained" color="primary" onClick={() => onClick(id, FundingStatus.success)}>
        {$t('success')}
      </Button>
    </div>
  </div>
);

export default withStyles(InfoBox, styles);
