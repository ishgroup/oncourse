/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React, { useEffect, useState } from "react";
import { Dispatch } from "redux";
import { connect } from "react-redux";
import CloseIcon from "@material-ui/icons/Close";
import withStyles from "@material-ui/core/styles/withStyles";
import createStyles from "@material-ui/core/styles/createStyles";
import Typography from "@material-ui/core/Typography";
import { fade } from "@material-ui/core/styles/colorManipulator";
import clsx from "clsx";
import ListItemText from "@material-ui/core/ListItemText";
import Box from "@material-ui/core/Box";
import { format as formatDate } from "date-fns";
import ListItem from "@material-ui/core/ListItem";
import { State } from "../../../reducers/state";
import { AppTheme } from "../../../model/common/Theme";
import { D_MMM_YYYY } from "../../utils/dates/format";
import {
  DASHBOARD_NEWS_LATEST_READ,
  READED_NEWS
} from "../../../constants/Config";
import { setUserPreference } from "../../actions";

const styles = (theme: AppTheme) => createStyles({
  postsWrapper: {
    padding: theme.spacing(3),
  },
  postWrapper: {
    background: theme.palette.background.paper,
    position: "relative",
    padding: theme.spacing(3),
    borderRadius: theme.spacing(1),
    border: "2px solid",
    borderColor: fade(theme.palette.text.disabled, 0.1),
    "&:not(:last-child)": {
      marginBottom: theme.spacing(2),
    },
  },
  closeIcon: {
    position: "absolute",
    top: "-15px",
    right: "-15px",
    borderRadius: "50%",
    padding: theme.spacing(0.5),
    background: theme.palette.background.paper,
    border: "2px solid",
    color: fade(theme.palette.text.disabled, 0.3),
    borderColor: fade(theme.palette.text.disabled, 0.1),
    width: "30px",
    height: "30px",
    "&:hover": {
      cursor: "pointer",
      color: theme.palette.primary.main,
      borderColor: theme.palette.primary.main,
    },
  },
  newsTitle: {
    fontSize: "16px",
    fontColor: theme.palette.text.primary,
    fontWeight: 600,
  },
  postContentExpanded: {
    maxHeight: "none",
    fontWEight: 400,
    "&::after": {
      content: "none",
    },
  },
  newCaption: {
    borderRadius: 16,
    padding: "1px 6px",
    fontSize: 12,
    top: 0,
    fontWeight: 600,
    color: "#45AA05",
    backgroundColor: "rgba(69, 170, 5, 0.1)",
  },
  videoWrapper: {
    maxWidth: "100%",
  },
});

const NewsItemRender = props => {
  const {
    classes, latestReadDate, post, setReadedNews, fullScreenEditView
  } = props;

  const isLatestItem = latestReadDate === "" || new Date(latestReadDate).getTime() < new Date(post.published).getTime();

  const setIdOfReadedNews = () => {
    setReadedNews(post.id);
  };

  return (
    <ListItem
      id={`post-${post.id}`}
      alignItems="flex-start"
      className={classes.postWrapper}
    >
      <div className={clsx("w-100 d-block", fullScreenEditView && post.video && "d-flex")}>
        {post.video && (
          <iframe
            allow="fullscreen"
            width={fullScreenEditView ? "220px" : "100%"}
            height="150"
            src={`https://www.youtube.com/embed/${post.video}`}
            title="video"
            className={clsx(classes.videoWrapper, fullScreenEditView && "mr-2")}
          />
        )}
        <ListItemText
          primary={(
            <>
              {isLatestItem && (
                <Typography
                  component="span"
                  variant="caption"
                  className={clsx(
                    "boldText relative left-0 text-uppercase mr-1",
                    classes.newCaption,
                  )}
                >
                  NEW
                </Typography>
              )}
              <Typography component="span" variant="body1" className={classes.newsTitle}>
                {post.title}
              </Typography>
            </>
          )}
          secondary={(
            <Box component="span" display="block">
              <Box component="span" display="block" position="relative">
                <Typography
                  component="span"
                  variant="body2"
                  color="textPrimary"
                  className={clsx(
                    "blog-post-content d-block overflow-hidden", classes.postContentExpanded
                  )}
                >
                  <Box component="span" display="block" dangerouslySetInnerHTML={{ __html: post.content }} />
                </Typography>
                {" "}
              </Box>
              <Box component="span" display="block" textAlign="left">
                <Typography
                  component="span"
                  variant="caption"
                  color="textSecondary"
                  className="d-block"
                >
                  {post.published && `Posted ${post.published && formatDate(new Date(post.published), D_MMM_YYYY)}`}
                </Typography>
              </Box>
            </Box>
          )}
        />
      </div>
      <CloseIcon className={classes.closeIcon} onClick={setIdOfReadedNews} />
    </ListItem>
  );
};

const NewsRender = props => {
  const {
    blogPosts, classes, page, preferences, setReadedNews, fullScreenEditView
  } = props;

  const [postsForRender, setPostsForRender] = useState([]);

  useEffect(() => {
    const readedNews = preferences[READED_NEWS] && preferences[READED_NEWS].split(",");

    const filteredPosts = blogPosts.filter(post => ((!post.page && !page) || window.location.pathname.includes(post.page))
      && (!readedNews || !readedNews.includes(post.id))).reverse();

    const newsWithoutDate = filteredPosts.filter(post => !post.published);
    const newsWithDate = filteredPosts.filter(post => post.published);
    const newsForRender = newsWithoutDate.concat(newsWithDate);
    setPostsForRender(newsForRender);
  }, [blogPosts, page, preferences]);

  return postsForRender.length ? (
    <div className={classes.postsWrapper}>
      {postsForRender.map(post => (
        <NewsItemRender
          key={post.id}
          post={post}
          classes={classes}
          latestReadDate={preferences[DASHBOARD_NEWS_LATEST_READ]}
          setReadedNews={setReadedNews}
          fullScreenEditView={fullScreenEditView}
        />
      ))}
    </div>
  ) : null;
};

const mapStateToProps = (state: State) => ({
  blogPosts: state.dashboard.blogPosts,
  fullScreenEditView: state.list.fullScreenEditView,
  preferences: state.userPreferences,
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  setReadedNews: (newsId: string) => dispatch(setUserPreference({ key: READED_NEWS, value: newsId })),
});

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(withStyles(styles)(NewsRender));