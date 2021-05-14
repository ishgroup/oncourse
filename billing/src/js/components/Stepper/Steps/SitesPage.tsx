import React from "react";
import { Formik, Form, FieldArray } from 'formik';
import {useSelector} from "react-redux";
import {State} from "../../../redux/reducers";
import Loading from "../../common/Loading";
import {createStyles, darken, makeStyles} from "@material-ui/core/styles";
import {AppTheme} from "../../../models/Theme";
import {
  Accordion,
  AccordionDetails,
  AccordionSummary,
  FormControl,
  Grid,
  IconButton,
  InputAdornment,
  InputLabel,
  TextField,
  Typography
} from "@material-ui/core";
import {Site} from "../../../models/Site";
import {green} from "@material-ui/core/colors";
import {AddCircle, Delete, ExpandMore} from "@material-ui/icons";
import {Autocomplete} from "@material-ui/lab";
import CustomButton from "../../common/Button";


export const useStyles = makeStyles((theme: AppTheme) =>
  createStyles({
    coloredHeaderText: {
      color: theme.statistics.coloredHeaderText.color,
    },
    textFieldWrapper: {
      display: "flex",
      alignItems: "baseline",
      marginTop: theme.spacing(2)
    },
    errorMessage: {
      fontSize: "12px",
      color: "#f44336"
    },
    flexWrapper: {
      display: "flex",
      alignItems: "center"
    },
    plusButton: {
      color: darken(green[900], 0.1)
    },
    root: {
      minWidth: "600px"
    },
    loading: {
      height: "25px!important",
      width: "25px!important",
    },
    buttonsWrapper: {
      display: "flex",
      justifyContent: "flex-end",
      marginTop: "30px",
      paddingBottom: "20px"
    },
    expandIcon: {
      right: "unset",
      left: "0px",
      position: "absolute"
    },
    deleteIcon: {
      fontSize: "22px",
      "&:hover": {
        backgroundColor: "transparent",
        color: theme.palette.error.main
      },
      padding: theme.spacing(1),
      margin: `-${theme.spacing(1)}px -${theme.spacing(1)}px -${theme.spacing(1)}px auto`
    },
    summaryContent: {
      paddingLeft: `${theme.spacing(3)}px`,
      "& > :last-child": {
        paddingRight: `${theme.spacing(1)}px`
      }
    }
  }),
);

//TEMP
const domains = ["www.corporate.nida.edu.au", "www.open.nida.edu.au"]


export const SitesPage: React.FC<any> = (
  {

  }) => {

  const loading = useSelector<State, any>(state => state.loading);
  const sites = useSelector<State, any>(state => state.sites);

  const classes = useStyles();

  return loading ? <Loading /> : <div>
    <Formik
      initialValues={{ sites }}
      onSubmit={values => {
          setTimeout(() => {
            alert(JSON.stringify(values, null, 2));
          }, 500)
        }
      }
      render={({ values, setValues }) => (
        <Form>
          <div className={classes.flexWrapper}>
            <h2 className={classes.coloredHeaderText}>Websites</h2>
            <IconButton onClick={() =>
              setValues({
                sites: [
                  {
                    prefix: "nida",
                    domains: []
                  },
                  ...values.sites
                ] })
            }>
              <AddCircle className={classes.plusButton} />
            </IconButton>
          </div>
          <FieldArray
            name="friends"
            render={arrayHelpers => (
              <div>
                {(
                  values.sites.map((site: Site, index) => {
                    const isNew = typeof site.id !== "number";

                    return (
                      <Accordion
                        classes={{
                          root: classes.root
                        }}
                        key={site.id || index}
                        defaultExpanded={isNew}
                      >
                        <AccordionSummary
                          expandIcon={<ExpandMore />}
                          classes={{
                            expandIcon: classes.expandIcon,
                            content: classes.summaryContent
                          }}
                        >
                          {isNew
                            ? <TextField error helperText="Required" variant="standard" label="Site name" InputLabelProps={{ shrink: true }}/>
                            : <Typography>{site.name}</Typography>
                          }
                          <IconButton className={classes.deleteIcon}>
                            <Delete fontSize="inherit" />
                          </IconButton>
                        </AccordionSummary>
                        <AccordionDetails>
                          <Grid container>
                            <Grid item xs={6}>
                              <FormControl fullWidth>
                                <InputLabel shrink>
                                  Site key
                                </InputLabel>
                                <div className={classes.textFieldWrapper}>
                                  {isNew ?
                                    <TextField
                                      variant="standard"
                                      error
                                      helperText="Required"
                                      InputProps={{
                                        startAdornment: <InputAdornment position="start">{site.prefix}-</InputAdornment>,
                                      }}
                                    />
                                    : <Typography>{site.key}</Typography>}
                                </div>
                              </FormControl>
                            </Grid>
                            <Grid item xs={6}>
                              <Autocomplete
                                multiple
                                size="small"
                                options={domains}
                                getOptionLabel={(option) => option}
                                value={site.domains}
                                renderInput={(params) => (
                                  <TextField {...params} error={isNew}
                                             helperText={isNew ? "Required" : null} variant="standard" label="Site domains" InputLabelProps={{ shrink: true }}/>
                                )}
                              />
                            </Grid>
                          </Grid>
                        </AccordionDetails>
                      </Accordion>
                    )
                  })
                )}
              </div>
            )}
          />
          <div className={classes.buttonsWrapper}>
            <CustomButton
              variant="contained"
              color="primary"
              loading={loading}
              disabled
            >
              Save
            </CustomButton>
          </div>
        </Form>
      )}
    />
  </div>;
}
