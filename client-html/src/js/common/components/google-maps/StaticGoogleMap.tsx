import Typography from "@mui/material/Typography";
import withStyles from "@mui/styles/withStyles";
import * as React from "react";
import { GOOGLE_MAPS_API_KEY } from "../../../constants/Config";

const styles = theme => ({
  noAddressContainer: {
    minHeight: "250px",
    height: "100%",
    width: "100%",
    color: theme.palette.grey[400],
    justifyContent: "center",
    alignItems: "center",
    display: "flex"
  }
});

interface Props {
  markerLetter?: string;
  size?: [number, number];
  latitude?: number;
  longitude?: number;
  classes?: any;
  hasAddress?: boolean;
}

const StaticGoogleMap = (props: Props) => {
  const { markerLetter = "G", latitude, longitude, size = [600, 280], classes } = props;

  return latitude && longitude ? (
    <img
      style={{ maxWidth: size[0], width: "100%" }}
      src={`https://maps.googleapis.com/maps/api/staticmap?zoom=13&size=${size[0]}x${size[1]}&scale=2&maptype=roadmap
&markers=color:green%7Clabel:${markerLetter}%7C${latitude},${longitude}&key=${atob(GOOGLE_MAPS_API_KEY)}`}
    />
  ) : (
    <div className={classes.noAddressContainer}>
      <Typography variant="h6" color="inherit" align="center">
        Enter street and suburb to see a map of the site location
      </Typography>
    </div>
  );
};

export default withStyles(styles)(StaticGoogleMap as React.FunctionComponent<Props>);
