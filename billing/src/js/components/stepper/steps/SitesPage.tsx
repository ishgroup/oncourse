import React, { useEffect } from 'react';
import { useFormik } from 'formik';
import { useDispatch, useSelector } from 'react-redux';
import { createStyles, darken, makeStyles } from '@material-ui/core/styles';
import {
  Accordion,
  AccordionDetails,
  AccordionSummary,
  Grid,
  IconButton,
  InputAdornment,
  TextField,
  Typography
} from '@material-ui/core';
import { green } from '@material-ui/core/colors';
import { AddCircle, Delete, ExpandMore } from '@material-ui/icons';
import { Autocomplete } from '@material-ui/lab';
import * as yup from 'yup';
import { SiteDTO } from '@api/model';
import CustomButton from '../../common/Button';
import { stopPropagation } from '../../../utils';
import { AppTheme } from '../../../models/Theme';
import Loading from '../../common/Loading';
import { State } from '../../../redux/reducers';
import { updateCollegeSites } from '../../../redux/actions';
import { TemplateField } from '../../common/TemplateChoser';

export const useStyles = makeStyles((theme: AppTheme) => createStyles({
  container: {
    display: 'flex',
    padding: theme.spacing(0, 20)
  },
  coloredHeaderText: {
    color: theme.statistics.coloredHeaderText.color,
  },
  textFieldWrapper: {
    display: 'flex',
    alignItems: 'baseline',
    marginTop: theme.spacing(2)
  },
  errorMessage: {
    fontSize: '12px',
    color: '#f44336'
  },
  flexWrapper: {
    display: 'flex',
    alignItems: 'center'
  },
  plusButton: {
    color: darken(green[900], 0.1)
  },
  root: {
    minWidth: '600px'
  },
  loading: {
    height: '25px!important',
    width: '25px!important',
  },
  buttonsWrapper: {
    display: 'flex',
    justifyContent: 'flex-end',
    marginTop: '30px',
    paddingBottom: '20px'
  },
  expandIcon: {
    right: 'unset',
    left: '0px',
    position: 'absolute'
  },
  deleteIcon: {
    fontSize: '22px',
    '&:hover': {
      backgroundColor: 'transparent',
      color: theme.palette.error.main
    },
    padding: theme.spacing(1),
    margin: `-${theme.spacing(1)}px -${theme.spacing(1)}px -${theme.spacing(1)}px auto`
  },
  summaryContent: {
    justifyContent: 'space-between',
    paddingLeft: `${theme.spacing(3)}px`,
    '& > :last-child': {
      paddingRight: `${theme.spacing(1)}px`
    }
  },
  summaryRoot: {
    '&$summaryFocused': {
      backgroundColor: 'inherit'
    }
  },
  summaryFocused: {},
  zeroMargin: {
    margin: 0
  },
  caption: {
    marginTop: theme.spacing(1)
  },
  pr: {
    paddingRight: '20px'
  }
}));

const validationSchema = yup.object({
  sites: yup.array().of(yup.object().shape({
    key: yup.mixed().required('Required').test('uniqueKey', 'Key should be unique', (value, context: any) => context.from[1].value.sites.filter((s) => s.key === value).length === 1),
    name: yup.mixed().required('Required').test('uniqueName', 'Name should be unique', (value, context: any) => context.from[1].value.sites.filter((s) => s.name === value).length === 1),
    webSiteTemplate: yup.mixed().required('Required'),
    domains: yup.array().min(1, 'Required')
      .of(yup.string().test('hasSpecial', 'Special symbols are forbidden', (v) => !(/[\\/]/.test(v))))
  }))
});

const getChangedSites = (initial: SiteDTO[], current: SiteDTO[]) => {
  const changed = [];
  current.forEach((site) => {
    const initialSite = initial.find((i) => i.id === site.id);
    if (initialSite) {
      Object.keys(site).forEach((key) => {
        if (site[key] !== initialSite[key]) {
          changed.push(site);
        }
      });
    }
  });
  return changed;
};

export const SitesPage: React.FC<any> = () => {
  const loading = useSelector<State, any>((state) => state.loading);
  const sites = useSelector<State, any>((state) => state.sites);
  const collegeKey = useSelector<State, any>((state) => state.collegeKey);
  const dispatch = useDispatch();

  const {
    handleSubmit, setValues, initialValues, dirty, handleChange, values, errors, setFieldValue, isValid, resetForm
  } = useFormik({
    initialValues: { sites },
    validationSchema,
    onSubmit: (submitted) => {
      dispatch(updateCollegeSites({
        changed: getChangedSites(initialValues.sites, submitted.sites.filter((s) => s.id)),
        created: submitted.sites.filter((s) => !s.id),
        removed: initialValues.sites.filter((s) => s.id && !submitted.sites.some((c) => c.id === s.id))
      }));
    },
  });

  useEffect(() => {
    if (sites) {
      resetForm({ values: { sites } });
    }
  }, [sites]);

  const classes = useStyles();

  const onKeyChange: any = (e) => {
    const updated = e.target.value.match(`${collegeKey}-`) ? e.target.value : `${collegeKey}-`;
    setFieldValue(e.target.name, updated);
  };

  return (
    <div className={classes.container}>
      {sites
        ? (
          <form
            onSubmit={handleSubmit}
          >
            <div>
              <div className={classes.flexWrapper}>
                <h2 className={classes.coloredHeaderText}>Websites</h2>
                <IconButton onClick={() => setValues({
                  sites: [
                    {
                      id: null,
                      key: null,
                      name: null,
                      webSiteTemplate: null,
                      prefix: collegeKey,
                      domains: []
                    },
                    ...values.sites
                  ]
                })}
                >
                  <AddCircle className={classes.plusButton} />
                </IconButton>
              </div>
              <div>
                {(
              values?.sites?.map((site: SiteDTO, index) => {
                const isNew = typeof site.id !== 'number';
                const error = (errors.sites && errors.sites[index]) || {};

                const onClickDelete = (e) => {
                  stopPropagation(e);
                  const updated = [...values.sites];
                  updated.splice(index, 1);
                  setFieldValue('sites', updated);
                };

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
                        focusVisible: classes.summaryFocused,
                        expandIcon: classes.expandIcon,
                        content: classes.summaryContent
                      }}
                    >
                      <Typography variant="body1">
                        https://
                        {site.key}
                        .oncourse.cc
                      </Typography>
                      <div>
                        <IconButton className={classes.deleteIcon} onClick={onClickDelete}>
                          <Delete fontSize="inherit" />
                        </IconButton>
                      </div>
                    </AccordionSummary>
                    <AccordionDetails>
                      <Grid container>
                        <Grid item xs={6}>
                          <TextField
                            name={`sites[${index}].name`}
                            value={values?.sites[index]?.name}
                            onChange={handleChange}
                            error={Boolean(error.name)}
                            helperText={error.name}
                            variant="standard"
                            label="Site name"
                            InputLabelProps={{ shrink: true }}
                            onClick={stopPropagation}
                          />
                        </Grid>
                        <Grid item xs={6}>
                          <TextField
                            label="Site location"
                            name={`sites[${index}].key`}
                            value={site.key}
                            error={Boolean(error.key)}
                            onChange={onKeyChange}
                            helperText={error.key}
                            InputLabelProps={{ shrink: true }}
                            inputProps={{
                              style: {
                                width: `${site.key?.length || 2}ch`
                              }
                            }}
                            InputProps={{
                              startAdornment: (
                                <InputAdornment
                                  position="start"
                                  classes={{
                                    positionStart: classes.zeroMargin
                                  }}
                                >
                                  https://
                                </InputAdornment>
                              ),
                              endAdornment: (
                                <InputAdornment
                                  position="end"
                                  classes={{
                                    positionEnd: classes.zeroMargin
                                  }}
                                >
                                  .oncourse.cc
                                </InputAdornment>
                              )
                            }}
                          />
                        </Grid>
                        <Grid item xs={6}>
                          <TemplateField
                            label="Site template"
                            name={`sites[${index}].webSiteTemplate`}
                            onChange={(val) => setFieldValue(`sites[${index}].webSiteTemplate`, val)}
                            value={site.webSiteTemplate}
                            helperText={error.webSiteTemplate}
                            error={Boolean(error.webSiteTemplate)}
                            variant="standard"
                            margin="normal"
                            InputLabelProps={{ shrink: true }}
                          />
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
                                InputProps={{
                                  ...params.InputProps,
                                  margin: 'none'
                                }}
                                helperText={Array.isArray(error.domains) ? error.domains[0] : error.domains}
                                variant="standard"
                                label="Site domains"
                                margin="normal"
                                InputLabelProps={{ shrink: true }}
                              />
                            )}
                            filterSelectedOptions
                            onChange={(e, v) => setFieldValue(`sites[${index}].domains`, v)}
                            multiple
                            freeSolo
                          />
                        </Grid>
                      </Grid>
                    </AccordionDetails>
                  </Accordion>
                );
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
        )
        : <Loading />}
    </div>
  );
};
