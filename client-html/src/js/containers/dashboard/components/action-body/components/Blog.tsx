/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useEffect, useCallback, useState } from "react";
import { connect } from "react-redux";
import clsx from "clsx";
import createStyles from "@material-ui/core/styles/createStyles";
import withStyles from "@material-ui/core/styles/withStyles";
import Typography from "@material-ui/core/Typography";
import CircularProgress from "@material-ui/core/CircularProgress";
import List from "@material-ui/core/List";
import ListItem from "@material-ui/core/ListItem";
import ListItemText from "@material-ui/core/ListItemText";
import Box from "@material-ui/core/Box";
import Paper from "@material-ui/core/Paper";
import { format as formatDate } from "date-fns";
import { D_MMM_YYYY } from "../../../../../common/utils/dates/format";
import { AppTheme } from "../../../../../model/common/Theme";
import { State } from "../../../../../reducers/state";
import { LSGetItem } from "../../../../../common/utils/storage";
import { APPLICATION_THEME_STORAGE_NAME } from "../../../../../constants/Config";

const styles = (theme: AppTheme) => createStyles({
  postContent: {
    maxHeight: 62,
    "&::after": {
      background: theme.palette.background.paper,
      bottom: 0,
      content: `""`,
      display: "block",
      height: 20,
      right: 0,
      position: "absolute",
      width: "100%"
    }
  },
  postContentExpanded: {
    maxHeight: "none",
    paddingBottom: 20,
    "&::after": {
      content: "none",
    }
  },
  moreLink: {
    color: theme.palette.primary.dark,
    position: "absolute",
    bottom: 2,
    right: 0,
    zIndex: 2
  },
  popperArrowRight: {
    bottom: -6,
    left: 30,
    width: 14,
    height: 14,
    marginRight: 0,
    position: "absolute",
    fontSize: 7,
    backgroundColor: "inherit",
    transform: "rotate(45deg)",
    "&::before": {
      width: 14,
      height: 14,
      margin: "auto",
      content: `""`,
      display: "block",
      backgroundColor: "inherit"
    }
  },
  highlightedPaper: {
    backgroundColor: "#292929"
  },
  gotItButton: {
    color: LSGetItem(APPLICATION_THEME_STORAGE_NAME) === "highcontrast" ? "#fff" : theme.palette.primary.main
  },
  newCaption: {
    borderRadius: 16,
    padding: "1px 6px",
    fontSize: 10,
    top: 5
  }
});

const BlogPostListItem = React.memo<any>(props => {
  const {
    classes, post, latestReadDate
  } = props;

  const isLatestItem = latestReadDate === "" || new Date(latestReadDate).getTime() < new Date(post.published).getTime();

  useEffect(() => {
    const links = document.querySelectorAll(`#post-${post.id} .blog-post-content a`);
    if (links) {
      links.forEach(link => {
        link.setAttribute("target", "_blank");
      });
    }
  }, [post.id]);

  return (
    <ListItem
      id={`post-${post.id}`}
      alignItems="flex-start"
    >
      <div className="w-100 d-block">
        {isLatestItem && (
          <Typography
            component="span"
            variant="caption"
            className={clsx("errorDarkBackgroundColor errorContrastColor boldText relative left-0 text-uppercase", classes.newCaption)}
          >
            NEW
          </Typography>
        )}
        <ListItemText
          primary={(
            <Typography component="span" variant="body1" className="text-bold heading">
              {post.title}
            </Typography>
          )}
          secondary={(
            <Box component="span" display="block" mt={1}>
              <Box component="span" display="block" position="relative">
                <Typography
                  component="span"
                  variant="body2"
                  color="textPrimary"
                  className={clsx(
                    "blog-post-content d-block overflow-hidden", classes.postContent, classes.postContentExpanded
                  )}
                >
                  <Box component="span" display="block" dangerouslySetInnerHTML={{ __html: post.content }} />
                </Typography>
                {" "}
                <Typography
                  component="span"
                  variant="body2"
                  color="textPrimary"
                  className={clsx("linkDecoration", classes.moreLink)}
                />
              </Box>
              <Box component="span" display="block" fontStyle="italic" textAlign="right">
                <Typography
                  component="span"
                  variant="caption"
                  color="textSecondary"
                  className="mt-1 d-block"
                >
                  {`posted ${post.published && formatDate(new Date(post.published), D_MMM_YYYY)}`}
                </Typography>
              </Box>
            </Box>
          )}
        />
      </div>
    </ListItem>
  );
});

const Blog = ({
  blogPosts, classes, preferencesNewsLatestReadDate, setDashboardNewsLatestReadDate
}) => {
  const [openPopper, setOpenPopper] = useState(false);
  const [latestPost, setLatestPost] = useState([]);

  useEffect(() => {
    if (blogPosts.length > 0) {
      setLatestPost(blogPosts);
    }
  }, [blogPosts, preferencesNewsLatestReadDate]);

  const onGotIt = useCallback(() => {
    setOpenPopper(false);
    if (latestPost.length > 0) {
      const firstPost = latestPost[0];
      setDashboardNewsLatestReadDate(firstPost.published);
    }
  }, [latestPost, openPopper]);

  useEffect(() => {
    if (latestPost.length > 0) {
      const firstPost = latestPost[0];
      const element = document.getElementById(`post-${firstPost.id}`);
      if (element) {
        if (
          preferencesNewsLatestReadDate === ""
          || new Date(preferencesNewsLatestReadDate).getTime() < new Date(firstPost.published).getTime()
        ) {
          setOpenPopper(true);
        } else {
          setOpenPopper(false);
        }
      }
    }
  }, [latestPost]);

  return (
    <div className="d-flex p-0 align-content-start justify-content-center">
      {latestPost.length ? (
        <>
          <div>
            {openPopper && (
              <div className="relative zIndex1">
                <Paper square elevation={0} className={clsx("p-2 relative w-100 text-white", classes.highlightedPaper)}>
                  <span className={classes.popperArrowRight} />
                  <Typography variant="caption" component="div" className="mb-1 fontWeight600">
                    Unread item
                  </Typography>
                  <Typography variant="caption" component="div" className="mb-1">
                    Take a look at this news item for information which might be important to you.
                  </Typography>
                  <Typography
                    variant="caption"
                    color="primary"
                    onClick={onGotIt}
                    className={clsx("cursor-pointer text-uppercase fontWeight600", classes.gotItButton)}
                    component="span"
                  >
                    Got it
                  </Typography>
                </Paper>
              </div>
            )}
            <List className="w-100 paperBackgroundColor p-0">
              {latestPost.filter(post => !post.page).map((post: any, i) => (
                <React.Fragment key={post.id}>
                  <BlogPostListItem
                    index={i}
                    post={post}
                    classes={classes}
                    openPopper={openPopper}
                    latestReadDate={preferencesNewsLatestReadDate}
                  />
                </React.Fragment>
              ))}
            </List>
          </div>
        </>
      ) : (
        <CircularProgress />
      )}
    </div>
  );
};

const mapStateToProps = (state: State) => ({
  blogPosts: state.dashboard.blogPosts
});

export default connect<any, any, any>(mapStateToProps, null)(withStyles(styles)(Blog));
