/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useCallback } from "react";
import clsx from "clsx";
import { InjectedFormProps, reduxForm } from "redux-form";
import createStyles from "@material-ui/core/styles/createStyles";
import withStyles from "@material-ui/core/styles/withStyles";
import Tooltip from "@material-ui/core/Tooltip";
import IconButton from "@material-ui/core/IconButton";
import HelpOutline from "@material-ui/icons/HelpOutline";
import Grid from "@material-ui/core/Grid";
import Card from "@material-ui/core/Card";
import CardContent from "@material-ui/core/CardContent";
import MuiButton from "@material-ui/core/Button/Button";
import FormField from "../../../common/components/form/form-fields/FormField";
import CustomAppBar from "../../../common/components/layout/CustomAppBar";
import AppBarActions from "../../../common/components/form/AppBarActions";
import Button from "../../../common/components/buttons/Button";
import Content from "../../../common/components/layout/Content";
import TagsTree from "../components/TagsTree";

const styles = theme =>
  createStyles({
    mainArea: {
      width: "100%",
      background: theme.palette.background.default,
      position: "relative"
    },
    mainContent: {
      display: "flex",
      justifyContent: "center"
    },
    buttons: {
      display: "flex",
      justifyContent: "flex-end",
      "& > *": {
        marginRight: theme.spacing(3)
      },
      "& > *:last-child": {
        marginRight: 0
      }
    }
  });

interface BulkTagViewProps extends InjectedFormProps {
  classes?: any;
}

const FORM: string = "BULK_TAG_VIEW_FORM";

const initialValues: any = {
  tagAction: "Add tag"
};

const tags = [
  {
    id: 1,
    name: "Subject",
    parent: true,
    children: [
      {
        id: 2,
        name: "Art and craft",
        parent: true,
        children: []
      },
      {
        id: 3,
        name: "Computer skills",
        parent: true,
        children: [
          {
            id: 8,
            name: "Operating system",
            parent: true,
            children: [
              {
                id: 10,
                name: "#Linux",
                parent: false,
                children: []
              },
              {
                id: 11,
                name: "#Mac",
                parent: false,
                children: []
              },
              {
                id: 12,
                name: "#Windows",
                parent: false,
                children: []
              }
            ]
          },
          {
            id: 13,
            name: "Hardware",
            parent: true,
            children: [
              {
                id: 10,
                name: "#Physical Connections",
                parent: false,
                children: []
              },
              {
                id: 11,
                name: "#Hard drives",
                parent: false,
                children: []
              },
              {
                id: 12,
                name: "#Network devices",
                parent: false,
                children: []
              }
            ]
          }
        ]
      },
      {
        id: 4,
        name: "Language",
        parent: true,
        children: [
          {
            id: 5,
            name: "#french",
            parent: false,
            children: []
          },
          {
            id: 6,
            name: "#German",
            parent: false,
            children: []
          },
          {
            id: 7,
            name: "#Italian",
            parent: false,
            children: []
          }
        ]
      }
    ]
  }
];

const BulkTagViewForm: React.FC<BulkTagViewProps> = props => {
  const { classes, handleSubmit } = props;

  const onSubmit = useCallback(() => {}, []);

  return (
    <div className="root">
      <div className={classes.mainArea}>
        <Content>
          <form className="container" onSubmit={handleSubmit(onSubmit)}>
            <CustomAppBar fullWidth>
              <Grid container>
                <Grid item xs={12} className="centeredFlex">
                  <div className="flex-fill" />
                  <AppBarActions actions={[]} />
                  <Tooltip title="Additional information">
                    <IconButton onClick={() => {}}>
                      <HelpOutline className="text-white" />
                    </IconButton>
                  </Tooltip>
                  <Button onClick={() => {}} className="whiteAppBarButton">
                    Save
                  </Button>
                </Grid>
              </Grid>
            </CustomAppBar>
            <Grid container className={classes.mainContent}>
              <Grid item sm={5}>
                <Card>
                  <CardContent className="mb-0 p-3">
                    <div className="heading pt-1">Bulk Edit</div>
                    <div className="pt-1 pb-2">
                      <FormField
                        type="select"
                        name="tagAction"
                        items={[
                          { label: "Add tag", value: "Add tag" },
                          { label: "Remove tag", value: "Remove tag" },
                          { label: "Show on web", value: "Show on web" },
                          { label: "Remove from web", value: "Remove from web" }
                        ]}
                      />
                    </div>
                    <div className="heading">Tags</div>
                    <TagsTree tags={tags} />
                    <div className={clsx("pt-2", classes.buttons)}>
                      <MuiButton color="primary" onClick={() => {}}>
                        Cancel
                      </MuiButton>
                      <Button variant="contained" type="submit" color="primary" onClick={() => {}}>
                        Save
                      </Button>
                    </div>
                  </CardContent>
                </Card>
              </Grid>
            </Grid>
          </form>
        </Content>
      </div>
    </div>
  );
};

const BulkTagView = reduxForm({ form: FORM, initialValues })(withStyles(styles)(BulkTagViewForm));

export default BulkTagView;
