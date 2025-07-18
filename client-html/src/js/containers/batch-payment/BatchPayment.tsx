/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
import Close from '@mui/icons-material/Close';
import CreditCard from '@mui/icons-material/CreditCard';
import Done from '@mui/icons-material/Done';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import { FormControlLabel, Grid, Typography } from '@mui/material';
import Accordion from '@mui/material/Accordion';
import AccordionDetails from '@mui/material/AccordionDetails';
import AccordionSummary from '@mui/material/AccordionSummary';
import Chip from '@mui/material/Chip';
import CircularProgress from '@mui/material/CircularProgress';
import Tooltip from '@mui/material/Tooltip';
import Zoom from '@mui/material/Zoom';
import $t from '@t';
import clsx from 'clsx';
import { format } from 'date-fns';
import {
  D_MMM_YYYY,
  decimalPlus,
  DynamicSizeList,
  formatCurrency,
  formatRelativeDate,
  LinkAdornment,
  makeAppStyles,
  openInternalLink,
  Switch
} from 'ish-ui';
import React, { memo, useCallback, useEffect, useRef, useState, } from 'react';
import { connect } from 'react-redux';
import AutoSizer from 'react-virtualized-auto-sizer';
import { areEqual } from 'react-window';
import { Dispatch } from 'redux';
import { change, FieldArray, getFormValues, InjectedFormProps, reduxForm, } from 'redux-form';
import { IAction } from '../../common/actions/IshAction';
import instantFetchErrorHandler from '../../common/api/fetch-errors-handlers/InstantFetchErrorHandler';
import FormField from '../../common/components/form/formFields/FormField';
import AppBarContainer from '../../common/components/layout/AppBarContainer';
import LoadingIndicator from '../../common/components/progress/LoadingIndicator';
import EntityService from '../../common/services/EntityService';
import { getManualLink } from '../../common/utils/getManualLink';
import { getPluralSuffix } from '../../common/utils/strings';
import { BatchPaymentContact } from '../../model/batch-payment';
import { State } from '../../reducers/state';
import CheckoutService from '../checkout/services/CheckoutService';
import { getContactFullName } from '../entities/contacts/utils';
import { getBachCheckoutModel } from './utils';

const useStyles = makeAppStyles<void, 'openCheckoutButton' | 'panelExpanded'>()((theme, p, classes) => ({
  checkbox: {
    width: "auto",
    height: "auto",
  },
  list: {
    margin: theme.spacing(-0.5),
  },
  tableTab: {
    padding: theme.spacing(0.5, 0, 0.5, 1),
    borderRadius: theme.shape.borderRadius,
    "&:nth-of-type(odd)": {
      background: theme.table.contrastRow.main,
    },
    "&:last-child": {
      background: "inherit",
      "&:hover": {
        background: "inherit",
      },
    },
  },
  panelSummary: {
    [`&:hover .${classes.openCheckoutButton}`]: {
      visibility: "visible",
    },
    padding: theme.spacing(0, 2, 0, 0),
  },
  panelRoot: {
    [`&.${classes.panelExpanded}:last-child`]: {
      margin: theme.spacing(2, 0),
    },
    [`&.${classes.panelExpanded}:first-child`]: {
      margin: theme.spacing(2, 0),
    },
  },
  openCheckoutButton: {
    visibility: "hidden",
    margin: theme.spacing(0, 1),
  },
  amountMargin: {
    marginRight: theme.spacing(4.5),
  },
  panelExpanded: {},
}));

const FORM = "Batch payment form";

const listRef = React.createRef<any>();

const today = new Date();

interface ContactItemProps {
  name: string;
  style: any;
  item: BatchPaymentContact;
  classes: any;
  currencySymbol: string;
  onContactItemSelect: any;
  forwardedRef: any;
}

const ContactItem = memo<ContactItemProps>(({
 name, style, item, classes, currencySymbol, onContactItemSelect, forwardedRef,
}) => {
  const [expanded, setExpanded] = useState(false);
  const [animate, setAnimate] = useState(false);

  const total = formatCurrency(item.total, currencySymbol);

  useEffect(() => {
    if (item.processed) {
      setTimeout(() => {
        setAnimate(true);
      }, 300);
    }
  }, [item.processed]);

  const checkDisabled = !item.hasStoredCard || item.processing || item.processed;

  return (
    <div
      style={style}
      ref={forwardedRef}
      className="p-0-5"
    >
      <Accordion
        expanded={expanded || item.processing}
        onChange={() => setExpanded(!expanded)}
        TransitionProps={{
          unmountOnExit: true,
          mountOnEnter: true,
        }}
        classes={{
          root: classes.panelRoot,
          expanded: classes.panelExpanded,
        }}

      >
        <AccordionSummary
          expandIcon={<ExpandMoreIcon />}
          classes={{
          root: clsx("m-0", classes.panelSummary),
          content: "m-0",
        }}
        >
          <Grid container className="centeredFlex">
            <div className="centeredFlex flex-fill pl-1 pr-2">
              <Tooltip
                disableHoverListener={!checkDisabled}
                disableFocusListener={!checkDisabled}
                disableTouchListener={!checkDisabled}
                title={$t('this_contact_does_not_have_a_stored_card_you_can_p')}
              >
                <div>
                  <FormField
                    type="checkbox"
                    name={`${name}.checked`}
                    disabled={checkDisabled}
                    onChange={onContactItemSelect}
                    stopPropagation
                  />
                </div>
              </Tooltip>
              <div className="flex-fill centeredFlex">
                <div className="heading mr-1">
                  {item.name}
                </div>
                {
                  item.hasStoredCard && (
                    <Tooltip title={$t('has_stored_card')}>
                      <CreditCard color="action" />
                    </Tooltip>
                  )
                }
                {!item.processing && !item.processed && (
                  <Chip
                    size="small"
                    label={$t('open_in_checkout')}
                    color="primary"
                    onClick={e => {
                      e.stopPropagation();
                      openInternalLink(`/checkout?contactId=${item.id}`);
                    }}
                    className={classes.openCheckoutButton}
                  />
                )}
                <CircularProgress size={32} className={clsx("ml-1", !item.processing && "d-none")} />
                {item.processing && <Typography variant="caption" className="ml-1" color="primary">{$t('processing')}</Typography>}
                <Zoom in={item.processed && animate}>
                  {item.error
                      ? <Close className="ml-1 errorColor" />
                      : <Done className="ml-1 successColor" />}
                </Zoom>
                {item.processed && !item.error && <Typography variant="caption" className="ml-1 successColor">{$t('processed')}</Typography>}
                {item.error && <Typography variant="caption" className="ml-1 errorColor">{$t('failed')}</Typography>}
              </div>

              {!expanded && <span className="money">{total}</span>}
            </div>
          </Grid>
        </AccordionSummary>
        <AccordionDetails>
          <Grid container>
            {item.items.map((i, index) => (
              <Grid key={i.id} item xs={12} className={clsx("d-flex", classes.tableTab)}>
                <div className="centeredFlex flex-fill">
                  <FormField
                    type="checkbox"
                    name={`${name}.items[${index}].checked`}
                    className={clsx("p-0 m-0", classes.checkbox)}
                    onChange={onContactItemSelect}
                    disabled={checkDisabled}
                  />
                  <Typography className="ml-1 mr-1">
                    {i.invoiceNumber}
                  </Typography>
                  <LinkAdornment
                    linkHandler={() => openInternalLink(`/invoice/${i.id}`)}
                    link={item.id}
                    className="appHeaderFontSize"
                  />
                </div>
                <Tooltip title={format(new Date(i.dateDue), D_MMM_YYYY)}>
                  <Typography className="mr-1">
                    {$t('due',[formatRelativeDate(new Date(i.dateDue), today, D_MMM_YYYY, true, true)])}
                  </Typography>
                </Tooltip>

                <span className={clsx("money centeredFlex", classes.amountMargin)}>
                  {formatCurrency(i.amountOwing, currencySymbol)}
                </span>
              </Grid>
            ))}

            <Grid item xs={12} container direction="row" className="mt-1">
              <Grid item xs={8} />
              <Grid
                item
                xs={4}
                container
                justifyContent="flex-end"
                className="money pt-1 summaryTopBorder"
              >
                <Typography variant="body2" className={classes.amountMargin}>{total}</Typography>
              </Grid>
            </Grid>
          </Grid>
        </AccordionDetails>
      </Accordion>
    </div>
);
}, areEqual);

const RowRenderer = React.forwardRef<any, any>(({ data, index, style }, ref) => {
  const { items, ...rest } = data;
  return (
    <ContactItem
      key={items[index].id}
      name={`contacts[${items[index].index}]`}
      item={items[index]}
      style={style}
      forwardedRef={ref}
      {...rest}
    />
);
});

const ContactRenderer = ({
 fields, filterByStoreCard, ...rest
}) => {
  const items = filterByStoreCard ? fields.getAll().filter(i => i.hasStoredCard) : fields.getAll();

  // @ts-ignore
  // @ts-ignore
  return (
    <AutoSizer>
      {({ width, height }) => (
        <DynamicSizeList
          height={height}
          width={width}
          listRef={listRef}
          itemCount={items.length}
          estimatedItemSize={56}
          itemData={{
          items,
            ...rest,
          }}
        >
          {RowRenderer as any}
        </DynamicSizeList>
    )}
    </AutoSizer>
  );
};

interface Props extends Partial<InjectedFormProps> {
  dispatch?: Dispatch<IAction>;
  currencySymbol?: string;
  values?: {
    contacts: BatchPaymentContact[]
  };
}

const manualUrl = getManualLink("batch-payments-in");

const getContacts = (dispatch, setContactsLoading, onComplete?) => {
  EntityService.getPlainRecords(
    "Invoice",
    "contact.id,contact.firstName,contact.lastName,amountOwing,dateDue,contact.hasSavedCC,invoiceNumber",
    `overdue > 0`,
    null,
    null,
    "dateDue",
    false,
  )
    .then(res => {
      setContactsLoading(false);

      if (!res.rows.length) {
        return;
      }

      let counter = 0;

      const hasStoredCard = res.rows[0].values[5] === "true";

      const contacts:BatchPaymentContact[] = [{
        id: Number(res.rows[0].values[0]),
        name: getContactFullName({ firstName: res.rows[0].values[1], lastName: res.rows[0].values[2] }),
        hasStoredCard,
        checked: hasStoredCard,
        total: 0,
        items: [],
        index: 0,
        processed: false,
        processing: false,
        error: null,
      }];

      res.rows.forEach(r => {
        const id = Number(r.values[0]);
        const amountOwing = parseFloat(r.values[3]);

        const matchIndex = contacts.findIndex(c => c.id === id);

        if (matchIndex === -1) {
          const hasStoredCard = r.values[5] === "true";

          contacts.push({
            id,
            hasStoredCard,
            index: counter + 1,
            name: getContactFullName({ firstName: r.values[1], lastName: r.values[2] }),
            checked: hasStoredCard,
            total: amountOwing,
            processed: false,
            processing: false,
            error: null,
            items: [{
              id: r.id,
              amountOwing,
              dateDue: r.values[4],
              invoiceNumber: r.values[6],
              checked: hasStoredCard,
            }],
          });
          counter++;
        } else {
          if (!contacts[matchIndex].hasStoredCard && r.values[5] === "true") {
            contacts[matchIndex].hasStoredCard = true;
          }
          contacts[matchIndex].total = decimalPlus(contacts[matchIndex].total, amountOwing);

          contacts[matchIndex].items.push({
            id: r.id,
            amountOwing,
            dateDue: r.values[4],
            invoiceNumber: r.values[6],
            checked: contacts[matchIndex].hasStoredCard,
          });
        }
      });

      dispatch(change(FORM, "contacts", contacts));

      if (onComplete) {
        onComplete();
      }
    })
    .catch(res => instantFetchErrorHandler(dispatch, res, "Failed to get batch payment contacts"));
};

const BatchPayment: React.FC<Props> = ({
  handleSubmit,
  dispatch,
  currencySymbol,
  values,
}) => {
  const [contactsLoading, setContactsLoading] = useState(false);
  const [filterByStoreCard, setFilterByStoreCard] = useState(true);
  const [processing, setProcessing] = useState(false);
  const cancel = useRef(false);

  const { classes } = useStyles();

  useEffect(() => {
    setContactsLoading(true);
    getContacts(dispatch, setContactsLoading);
  }, []);

  const onContactItemSelect = useCallback((event, newValue, previousValue, name) => {
    const parsed = name.match(/\[(\d+)]/g).map(m => Number(m.replace(/[^\d.]/g, "")));
    let changed = [];
    if (parsed.length === 1) {
      changed = values.contacts[parsed[0]].items.map(i => ({ ...i, checked: newValue }));
      dispatch(change(FORM, `contacts[${parsed[0]}].items`, changed ));
    }

    if (parsed.length === 2) {
      changed = values.contacts[parsed[0]].items.map((i, index) => (index === parsed[1] ? { ...i, checked: newValue } : i));
      if ((!values.contacts[parsed[0]].checked && changed.some(c => c.checked))
      || (values.contacts[parsed[0]].checked && !changed.some(c => c.checked))
      ) {
        dispatch(change(FORM, `contacts[${parsed[0]}].checked`, newValue ));
      }
    }
    dispatch(change(FORM, `contacts[${parsed[0]}].total`,
      changed.reduce((p, i) => (i.checked ? decimalPlus(p, i.amountOwing) : p), 0) ));
  }, [values]);

  const checkedContacts = values.contacts.filter(c => c.checked && !c.processed);

  const total = checkedContacts.reduce(
    (p, c) => decimalPlus(p, c.items.reduce((p, c) => decimalPlus(p, c.checked ? c.amountOwing : 0), 0) ),
    0,
  );

  const onSave = () => {
    cancel.current = false;
    setProcessing(true);
    setContactsLoading(true);
    getContacts(dispatch, setContactsLoading,
      async () => {
        await checkedContacts.map((c, index) => () => new Promise<void>(resolve => {
          if (cancel.current) {
            resolve();
            return;
          }
          listRef.current.scrollToItem(filterByStoreCard ? index : c.index, "start");
          setTimeout(async () => {
            dispatch(change(FORM, `contacts[${c.index}].processing`, true ));
            const model = getBachCheckoutModel(c);
            await CheckoutService.createSession(model);
            CheckoutService.submitPayment(model)
              .then(() => {
                dispatch(change(FORM, `contacts[${c.index}]`, {
                  ...c, processing: false, processed: true,
                } ));
                setTimeout(resolve, 200);
              })
              .catch(res => {
                instantFetchErrorHandler(dispatch, res, `Payment for ${c.name} failed`);
                dispatch(change(FORM, `contacts[${c.index}]`, {
                  ...c, processing: false, processed: true, error: true,
                }));
                setTimeout(resolve, 200);
              });
          }, 300);
        })).reduce(async (a, b) => {
          await a;
          await b();
        }, Promise.resolve());

        setProcessing(false);
      });
  };

  return (
    <form className="appBarContainer" noValidate autoComplete="off" onSubmit={handleSubmit(onSave)}>
      <LoadingIndicator appBarOffset transparentBackdrop customLoading={contactsLoading} />
      <AppBarContainer
        disabledScrolling
        disableInteraction
        disabled={processing || contactsLoading || checkedContacts.length === 0}
        title={(
          <div>
            {$t('Batch payment in (showing')}
            {' '}
            {values.contacts.length}
            {' '}
            {$t('contact')}
            {values.contacts.length !== 1 ? "s" : ""}
            {' '}
            {$t('with amounts due or overdue')})
          </div>
        )}
        manualUrl={manualUrl}
        onCloseClick={processing ? () => {
          cancel.current = true;
          setProcessing(false);
        } : null}
        closeButtonText="Cancel"
        submitButtonText={processing ? "Processing..." : `Process ${checkedContacts.length} payment${getPluralSuffix(checkedContacts.length)}`}
        containerClass="flex-column p-3 h-100"
      >
        {!contactsLoading && (
          <div className="flex-column p-3 h-100">
            <div className="mb-3 d-flex justify-content-end">
              <FormControlLabel
                control={(
                  <Switch
                    checked={filterByStoreCard}
                    onChange={() => setFilterByStoreCard(!filterByStoreCard)}
                  />
                )}
                classes={{
                  labelPlacementStart: "m-0",
                }}
                label={$t('only_show_contacts_with_a_stored_card')}
                labelPlacement="start"
                disabled={processing}
              />
            </div>

            <div className={clsx("flex-fill", classes.list)}>
              <FieldArray
                name="contacts"
                component={ContactRenderer}
                classes={classes}
                currencySymbol={currencySymbol}
                filterByStoreCard={filterByStoreCard}
                onContactItemSelect={onContactItemSelect}
              />
            </div>

            <Grid container className="pt-3 d-flex justify-content-end">
              <Grid item xs={12} sm={8} />
              <Grid
                item
                xs={12}
                sm={4}
                container
                justifyContent="flex-end"
                className="money p-2 summaryTopBorder"
              >
                <Typography variant="body2" className={classes.amountMargin}>
                  <strong>{formatCurrency(total, currencySymbol)}</strong>
                </Typography>
              </Grid>
            </Grid>
          </div>
        )}
      </AppBarContainer>
    </form>
);
};

const mapStateToProps = (state: State) =>
  ({
    currencySymbol: state.location.currency.shortCurrencySymbol,
    values: getFormValues(FORM)(state),
  });

export default reduxForm<any, Props>({
  form: FORM,
  initialValues: {
    contacts: [],
  },
})(connect(mapStateToProps)((props: Props) => props.values ? <BatchPayment {...props} /> : null));
