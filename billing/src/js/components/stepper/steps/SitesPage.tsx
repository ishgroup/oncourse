import React, { useEffect } from 'react';
import { useFormik } from 'formik';
import { useDispatch, useSelector } from 'react-redux';
import { createStyles, darken, makeStyles } from '@material-ui/core/styles';
import {
  Accordion,
  AccordionDetails,
  AccordionSummary,
  Chip,
  FormControl,
  Grid,
  IconButton,
  Select,
  TextField,
  Typography,
  MenuItem,
  InputLabel
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
import AutosizeInput from '../../common/AutosizeInput';

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
  },
  textFieldWrapper2: {
    display: 'flex',
    alignItems: 'baseline'
  },
  floatLabel: {
    position: 'relative',
    bottom: theme.spacing(0.5)
  },
  domainsInput: {},
  hasClearIcon: {
    '& $domainsInput': {
      paddingRight: 0
    }
  }
}));

const validationSchema = yup.object({
  sites: yup.array().of(yup.object().shape({
    key: yup.string().nullable()
      .required('Required')
      .max(40, 'Maximum length is 40 characters')
      .test('uniqueKey', 'Key should be unique', (value, context: any) => {
        const { collegeKey, sites } = context.from[1].value;
        return sites.filter((s) => (s.id ? s.key : `${collegeKey}-${s.key}`) === (context.parent.id ? value : `${collegeKey}-${value}`)).length === 1;
      })
      .matches(/^[0-9a-z-]+$/i, 'You can only use letters, numbers and "-"'),
    primaryDomain: yup.string().nullable(),
    name: yup.mixed().required('Required').test(
      'uniqueName',
      'Name should be unique',
      (value, context: any) => context.from[1].value.sites.filter((s) => s.name === value).length === 1
    ),
    webSiteTemplate: yup.string().nullable().when('id', {
      is: (val) => !val,
      then: yup.string().nullable().required('Required'),
    }),
    domains: yup.array().of(yup.string().matches(/\b((?=[a-z0-9-]{1,63}\.)(xn--)?[a-z0-9]+(-[a-z0-9]+)*\.)+[a-z]{2,63}\b/, 'Domain name is invalid'))
  }))
});

const getChangedSites = (initial: SiteDTO[], current: SiteDTO[]) => {
  const changed = [];
  current.forEach((site) => {
    const initialSite = initial.find((i) => i.id === site.id);
    if (initialSite) {
      Object.keys(site).forEach((key) => {
        if (Array.isArray(site[key])) {
          site[key].forEach((i, index) => {
            if (site[key][index] !== initialSite[key][index]) {
              changed.push(site);
            }
          });
        } else if (site[key] !== initialSite[key]) {
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
    handleSubmit, setValues, setFieldError, initialValues, dirty, handleChange, values, errors, setFieldValue, isValid, resetForm
  } = useFormik({
    initialValues: { sites, collegeKey },
    validationSchema,
    onSubmit: (submitted) => {
      const parsed = submitted.sites.map((s) => ({ ...s, domains: s.domains.map((d) => d.replace(/https?:\/\//, '')) }));
      dispatch(updateCollegeSites({
        changed: getChangedSites(initialValues.sites, parsed.filter((s) => s.id)),
        created: parsed.filter((s) => !s.id).map((s) => ({ ...s, key: `${collegeKey}-${s.key}` })),
        removed: initialValues.sites.filter((s) => s.id && !parsed.some((c) => c.id === s.id))
      }));
    },
  });

  useEffect(() => {
    if (sites || collegeKey) {
      resetForm({ values: { sites, collegeKey } });
    }
  }, [sites, collegeKey]);

  const classes = useStyles();

  const onKeyChange: any = (e, index, isNew, initial, initialMatchPattern) => {
    const { value } = e.target;
    const name = `sites[${index}].key`;
    const newValue = initialMatchPattern ? `${collegeKey}-${value}` : value;
    let shouldValidte = true;
    if (initial && initial.key !== newValue && !newValue.match(`${collegeKey}-`)) {
      shouldValidte = false;
      setFieldError(name, `Location should start with ${collegeKey}-`);
    }
    if (initial && initial.key !== newValue && newValue.match(`${collegeKey}-`) && !newValue.match(new RegExp(`${collegeKey}-[^\\s]`))) {
      shouldValidte = false;
      setFieldError(name, 'Location has invalid format');
    }
    setFieldValue(name, newValue, shouldValidte);
  };

  const onSetPrimaryDomain = (domain, index) => {
    setFieldValue(`sites[${index}].primaryDomain`, domain);
  }

  const onAddDomain = (site, index, params) => {
    setFieldValue(`sites[${index}].domains`, [...site.domains, params.inputProps.value]);
    if (!site.domains.length && !site.primaryDomain) onSetPrimaryDomain(params.inputProps.value, index);
  }

  const renderSelectItems = domains => domains.map(domain => (
    <MenuItem key={domain} value={domain}>
      {domain}
    </MenuItem>
  ))

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
                      primaryDomain: null,
                      webSiteTemplate: null,
                      domains: []
                    },
                    ...values.sites
                  ],
                  collegeKey,
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
                const initial = site.id && initialValues.sites.find((i) => i.id === site.id);
                const initialMatchPattern = initial && initial.key.match(`${collegeKey}-`);

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
                        {isNew ? `${collegeKey}-` : ''}
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
                        <Grid item xs={6} className={classes.pr}>
                          <TextField
                            name={`sites[${index}].name`}
                            value={values?.sites[index]?.name}
                            onChange={handleChange}
                            error={Boolean(error.name)}
                            helperText={error.name}
                            variant="standard"
                            label="Website page title prefix"
                            InputLabelProps={{ shrink: true }}
                            onClick={stopPropagation}
                            fullWidth
                          />
                        </Grid>
                        <Grid item xs={6}>
                          <Typography variant="caption" color={error.key ? 'error' : 'textSecondary'} className={classes.floatLabel}>Site location</Typography>
                          <div className={classes.textFieldWrapper2}>
                            <Typography>
                              https://
                              {isNew || initialMatchPattern ? `${collegeKey}-` : ''}
                            </Typography>
                            <Typography variant="body1" component="span">
                              <AutosizeInput
                                value={initialMatchPattern ? site.key.replace(`${collegeKey}-`, '') : site.key}
                                error={Boolean(error.key)}
                                onChange={(e) => onKeyChange(e, index, isNew, initial, initialMatchPattern)}
                                disabled={!isNew}
                              />
                            </Typography>
                            <Typography>.oncourse.cc</Typography>
                          </div>
                          {Boolean(error.key) && (<Typography className={classes.errorMessage}>{error.key}</Typography>)}
                        </Grid>
                        <Grid item xs={6} className={classes.pr}>
                          <Autocomplete
                            size="small"
                            options={[]}
                            value={site.domains}
                            classes={{
                              inputRoot: classes.domainsInput,
                              // @ts-ignore
                              hasClearIcon: classes.hasClearIcon
                            }}
                            renderInput={(params: any) => (
                              <TextField
                                {...params}
                                error={Boolean(error.domains)}
                                InputProps={{
                                  ...params.InputProps,
                                  margin: 'none',
                                  endAdornment: params.inputProps.value
                                    ? (
                                      <Chip
                                        size="small"
                                        label="Add"
                                        clickable
                                        onClick={() => onAddDomain(site, index, params)}
                                      />
                                    )
                                    : null
                                }}
                                helperText={Array.isArray(error.domains) ? error.domains.find((d) => d) : error.domains}
                                variant="standard"
                                label="Site domains"
                                margin="normal"
                                InputLabelProps={{ shrink: true }}
                                multiline
                              />
                            )}
                            onChange={(e, v) => setFieldValue(`sites[${index}].domains`, v)}
                            filterSelectedOptions
                            multiple
                            freeSolo
                          />
                        </Grid>
                        <Grid item xs={6}>
                          <FormControl fullWidth margin="normal">
                            <InputLabel>Primary hostname</InputLabel>
                            <Select
                              autoWidth
                              value={site.primaryDomain || ""}
                              onChange={e => onSetPrimaryDomain(e.target.value, index)}
                            >
                              {renderSelectItems(site.domains)}
                            </Select>
                          </FormControl>
                        </Grid>
                        {isNew && (
                          <Grid item xs={6} className={classes.pr}>
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
                              fullWidth
                            />
                          </Grid>
                        )}
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
