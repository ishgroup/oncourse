/**
 * Common component. Currently used as dev dashboard
 * which render links to all existing applications
 */

import { Card, CardContent, Typography } from '@mui/material';
import * as React from 'react';
import { withStyles } from 'tss-react/mui';
import { routeGroups } from '../routes';

const styles: any = theme => ({
  root: {
    display: "flex",
    flexDirection: "column",
    justifyContent: "center",
    width: "100%"
  },
  inner: {
    display: "flex",
    flexWrap: "wrap",
    justifyContent: "center"
  },
  innerGroup: {
    paddingTop: theme.spacing(2),
    display: "flex",
    flexDirection: "column"
  },
  card: {
    minWidth: 275,
    cursor: "pointer",
    maxWidth: 150,
    margin: theme.spacing(1),
    maxHeight: 40
  },
  cardContent: {
    padding: 10
  },
  bullet: {
    display: "inline-block",
    margin: "0 2px",
    transform: "scale(0.8)"
  },
  title: {
    marginBottom: 16,
    fontSize: 14
  },
  pos: {
    marginBottom: 12
  }
});

class Common extends React.Component<any, any> {
  handleClick(url) {
    this.props.history.push(url);
  }

  render() {
    const { classes } = this.props;

    return (
      <div className={classes.root}>
        <div className={classes.inner}>
          {routeGroups.map(g => (
            <div key={g.title} className={classes.innerGroup}>
              <div className="heading ml-1">{g.title}</div>
              {g.routes.map((r, i) => (
                <Card key={g.title + i} className={classes.card} onClick={() => this.handleClick(r.url)}>
                  <CardContent className={classes.cardContent}>
                    <Typography className={classes.title} color="textSecondary">
                      {r.title}
                    </Typography>
                  </CardContent>
                </Card>
              ))}
            </div>
          ))}
        </div>
      </div>
    );
  }
}

export default withStyles(Common, styles);
