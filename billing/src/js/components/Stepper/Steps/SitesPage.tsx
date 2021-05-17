import React, {useEffect} from "react";
import {useFormik} from 'formik';
import {useDispatch, useSelector} from "react-redux";
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
import {green} from "@material-ui/core/colors";
import {AddCircle, Delete, ExpandMore} from "@material-ui/icons";
import {Autocomplete} from "@material-ui/lab";
import CustomButton from "../../common/Button";
import * as yup from "yup";
import {stopPropagation} from "../../../utils";
import {SiteDTO} from "@api/model";
import {updateCollegeSites} from "../../../redux/actions";


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
      justifyContent: "space-between",
      paddingLeft: `${theme.spacing(3)}px`,
      "& > :last-child": {
        paddingRight: `${theme.spacing(1)}px`
      }
    },
    summaryRoot: {
      "&$summaryFocused": {
        backgroundColor: "inherit"
      }
    },
    summaryFocused: {},
    zeroMargin: {
      margin: 0
    },
    caption: {
      marginTop: theme.spacing(1)
    }
  }),
);

const validationSchema = yup.object({
  sites: yup.array().of(yup.object().shape({
    key: yup.string().required("Required"),
    name: yup.string().required("Required"),
    domains: yup.array().min(1,"Required")
      .of(yup.string().test("hasSpecial", "Special symbols are forbidden", v => !(/[\\/]/.test(v))))
  }))
});

export const SitesPage: React.FC<any> = (
  {

  }) => {

  const loading = useSelector<State, any>(state => state.loading);
  const { userKey, sites } = useSelector<State, any>(state => state.user);
  const dispatch = useDispatch();

  const { handleSubmit, setValues, dirty, handleChange, values, errors, setFieldValue, isValid, resetForm } = useFormik({
    initialValues: { sites },
    validationSchema,
    onSubmit: values => {
      dispatch(updateCollegeSites(values.sites))
    },
  });

  useEffect(() => {
    if (sites) {
      resetForm({ values: { sites } })
    }
  }, [sites])

  const classes = useStyles();

  return sites ? <div>
    <form
      onSubmit={handleSubmit}
    >
      <div>
        <div className={classes.flexWrapper}>
          <h2 className={classes.coloredHeaderText}>Websites</h2>
          <IconButton onClick={() =>
            setValues({
              sites: [
                {
                  prefix: userKey,
                  domains: []
                },
                ...values.sites
              ] })
          }>
            <AddCircle className={classes.plusButton} />
          </IconButton>
        </div>
          <div>
            {(
              values?.sites?.map((site: SiteDTO, index) => {
                const isNew = typeof site.id !== "number";
                const error = (errors.sites && errors.sites[index]) || {};

                const onClickDelete = e => {
                  stopPropagation(e);
                  const updated = [...values.sites];
                  updated.splice(index,1);
                  setFieldValue("sites", updated);
                }

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
                        root: classes.summaryRoot,
                        focused: classes.summaryFocused,
                        expandIcon: classes.expandIcon,
                        content: classes.summaryContent
                      }}
                    >
                      {isNew
                        ? <TextField
                          name={`sites[${index}].name`}
                          onChange={handleChange}
                          error={Boolean(error.name)}
                          helperText={error.name}
                          variant="standard"
                          label="Site name"
                          InputLabelProps={{ shrink: true }}
                          onClick={stopPropagation}
                        />
                        : <Typography>{site.name}</Typography>
                      }
                      <div>
                        <IconButton className={classes.deleteIcon} onClick={onClickDelete}>
                          <Delete fontSize="inherit" />
                        </IconButton>
                      </div>
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
                                  name={`sites[${index}].key`}
                                  onChange={handleChange}
                                  error={Boolean(error.key)}
                                  helperText={error.key}
                                  InputProps={{
                                    startAdornment:
                                      <InputAdornment
                                        position="start"
                                        classes={{
                                          positionStart: classes.zeroMargin
                                        }}
                                      >
                                        {userKey}-
                                      </InputAdornment>,
                                  }}
                                />
                                : <Typography>{site.key}</Typography>}
                            </div>
                          </FormControl>
                        </Grid>
                        <Grid item xs={6}>
                          <Autocomplete
                            size="small"
                            options={[]}
                            value={site.domains}
                            renderInput={(params) => (
                              <TextField
                                {...params}
                                error={Boolean(error.domains)}
                                helperText={Array.isArray(error.domains) ? error.domains[0] : error.domains}
                                variant="standard"
                                label="Site domains"
                                InputLabelProps={{ shrink: true }}
                              />
                            )}
                            filterSelectedOptions
                            onChange={(e,v) =>
                              setFieldValue(`sites[${index}].domains`,v)
                            }
                            multiple
                            freeSolo
                          />
                          <Typography className={classes.caption} component="p" variant="caption" color="textSecondary">
                            Press "Enter" to add new domains
                          </Typography>
                        </Grid>
                      </Grid>
                    </AccordionDetails>
                  </Accordion>
                )
              })
            )}
          </div>
        <div className={classes.buttonsWrapper}>
          <CustomButton
            variant="contained"
            color="primary"
            type="submit"
            loading={loading}
            disabled={!isValid || !dirty}
          >
            Save
          </CustomButton>
        </div>
      </div>
    </form>
  </div> : <Loading />;
}
