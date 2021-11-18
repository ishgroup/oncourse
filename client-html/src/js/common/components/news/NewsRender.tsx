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
import { utcToZonedTime } from "date-fns-tz";
import CloseIcon from "@mui/icons-material/Close";
import Typography from "@mui/material/Typography";
import { createStyles, withStyles } from '@mui/styles';
import { alpha } from '@mui/material/styles';
import clsx from "clsx";
import ListItemText from "@mui/material/ListItemText";
import Box from "@mui/material/Box";
import { format as formatDate } from "date-fns";
import ListItem from "@mui/material/ListItem";
import { State } from "../../../reducers/state";
import { AppTheme } from "../../../model/common/Theme";
import { D_MMM_YYYY } from "../../utils/dates/format";
import {
  READ_NEWS
} from "../../../constants/Config";
import { setUserPreference } from "../../actions";
import { setReadNewsLocal } from "../list-view/actions";

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
    borderColor: alpha(theme.palette.text.disabled, 0.1),
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
    color: alpha(theme.palette.text.disabled, 0.3),
    borderColor: alpha(theme.palette.text.disabled, 0.1),
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
    classes, post, setReadNews, fullScreenEditView, lastLoginOn, setReadNewsLocal
  } = props;

  const isLatestItem = post.published && (!lastLoginOn || new Date(lastLoginOn).getTime() <= new Date(post.published).getTime());

  const setIdOfReadNews = () => {
    setReadNews(post.id);
    setReadNewsLocal(post.id);
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
      <CloseIcon className={classes.closeIcon} onClick={setIdOfReadNews} />
    </ListItem>
  );
};

const NewsRender = props => {
  const {
    blogPosts, classes, page, preferences, setReadNews, fullScreenEditView, setReadNewsLocal
  } = props;

  const lastLoginOn = localStorage.getItem("lastLoginOn");
  const lastLoginOnWithTimeZone = utcToZonedTime(lastLoginOn || new Date(), Intl.DateTimeFormat().resolvedOptions().timeZone);

  const [postsForRender, setPostsForRender] = useState([]);

  useEffect(() => {
    const readNews = preferences[READ_NEWS] && preferences[READ_NEWS].split(",");

    const filteredPosts = blogPosts.filter(post => (page ? post.page && window.location.pathname.includes(post.page) : true)
      && (!readNews || !readNews.includes(post.id))).reverse();

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
          setReadNews={setReadNews}
          fullScreenEditView={fullScreenEditView}
          lastLoginOn={lastLoginOnWithTimeZone}
          setReadNewsLocal={setReadNewsLocal}
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
  setReadNews: (newsId: string) => dispatch(setUserPreference({ key: READ_NEWS, value: newsId })),
  setReadNewsLocal: (newsId: string) => dispatch(setReadNewsLocal(newsId))
});

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(withStyles(styles)(NewsRender));