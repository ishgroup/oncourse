/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
import CircularProgress from "@material-ui/core/CircularProgress";
import Tooltip from "@material-ui/core/Tooltip";
import clsx from "clsx";
import React, {
  memo, useCallback, useEffect, useRef, useState
} from "react";
import { FormControlLabel } from "@material-ui/core";
import AutoSizer from "react-virtualized-auto-sizer";
import { areEqual } from "react-window";
import { format } from "date-fns";
import Button from "@material-ui/core/Button";
import Chip from "@material-ui/core/Chip";
import Accordion from "@material-ui/core/Accordion";
import AccordionDetails from "@material-ui/core/AccordionDetails";
import AccordionSummary from "@material-ui/core/AccordionSummary";
import makeStyles from "@material-ui/core/styles/makeStyles";
import CreditCard from "@material-ui/icons/CreditCard";
import Grid from "@material-ui/core/Grid";
import Typography from "@material-ui/core/Typography";
import ExpandMoreIcon from '@material-ui/icons/ExpandMore';
import Done from '@material-ui/icons/Done';
import Close from '@material-ui/icons/Close';
import Zoom from '@material-ui/core/Zoom';
import { connect } from "react-redux";
import { Dispatch } from "redux";
import {
  change, FieldArray, getFormValues, InjectedFormProps, reduxForm
} from "redux-form";
import instantFetchErrorHandler from "../../common/api/fetch-errors-handlers/InstantFetchErrorHandler";
import RouteChangeConfirm from "../../common/components/dialog/confirm/RouteChangeConfirm";
import AppBarHelpMenu from "../../common/components/form/AppBarHelpMenu";
import FormField from "../../common/components/form/form-fields/FormField";
import { Switch } from "../../common/components/form/form-fields/Switch";
import DynamicSizeList from "../../common/components/form/DynamicSizeList";
import { LinkAdornment } from "../../common/components/form/FieldAdornments";
import CustomAppBar from "../../common/components/layout/CustomAppBar";
import LoadingIndicator from "../../common/components/layout/LoadingIndicator";
import EntityService from "../../common/services/EntityService";
import { D_MMM_YYYY } from "../../common/utils/dates/format";
import { formatRelativeDate } from "../../common/utils/dates/formatRelative";
import { openInternalLink } from "../../common/utils/links";
import { decimalPlus } from "../../common/utils/numbers/decimalCalculation";
import { formatCurrency } from "../../common/utils/numbers/numbersNormalizing";
import { BatchPaymentContact } from "../../model/batch-payment";
import { AppTheme } from "../../model/common/Theme";
import { State } from "../../reducers/state";
import CheckoutService from "../checkout/services/CheckoutService";
import { getContactName } from "../entities/contacts/utils";
import { getBachCheckoutModel } from "./utils";

const useStyles = makeStyles((theme: AppTheme) => ({
  checkbox: {
    width: "auto",
    height: "auto"
  },
  list: {
    margin: theme.spacing(-0.5)
  },
  tableTab: {
    padding: theme.spacing(0.5, 0, 0.5, 1),
    borderRadius: theme.shape.borderRadius,
    "&:nth-of-type(odd)": {
      background: theme.table.contrastRow.main
    },
    "&:last-child": {
      background: "inherit",
      "&:hover": {
        background: "inherit"
      }
    },
  },
  panelSummary: {
    "&:hover $openCheckoutButton": {
      visibility: "visible"
    },
    padding: theme.spacing(0, 2, 0, 0)
  },
  panelRoot: {
    "&$panelExpanded:last-child": {
      margin: theme.spacing(2, 0)
    },
    "&$panelExpanded:first-child": {
      margin: theme.spacing(2, 0)
    }
  },
  openCheckoutButton: {
    visibility: "hidden",
    margin: theme.spacing(0, 1)
  },
  amountMargin: {
    marginRight: theme.spacing(4.5)
  },
  panelExpanded: {}
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
 name, style, item, classes, currencySymbol, onContactItemSelect, forwardedRef
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
          mountOnEnter: true
        }}
        classes={{
          root: classes.panelRoot,
          expanded: classes.panelExpanded
        }}

      >
        <AccordionSummary
          expandIcon={<ExpandMoreIcon />}
          classes={{
          root: clsx("m-0", classes.panelSummary),
          content: "m-0"
        }}
        >
          <Grid container className="centeredFlex">
            <div className="centeredFlex flex-fill pl-1">
              <Tooltip
                disableHoverListener={!checkDisabled}
                disableFocusListener={!checkDisabled}
                disableTouchListener={!checkDisabled}
                title="This contact does not have a stored card. You can process the payment manually."
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
                    <Tooltip title="Has stored card">
                      <CreditCard color="action" />
                    </Tooltip>
                  )
                }
                {!item.processing && !item.processed && (
                  <Chip
                    size="small"
                    label="Open in checkout"
                    color="primary"
                    onClick={e => {
                      e.stopPropagation();
                      openInternalLink(`/checkout?contactId=${item.id}`);
                    }}
                    className={classes.openCheckoutButton}
                  />
                )}
                <CircularProgress size={32} className={clsx("ml-1", !item.processing && "d-none")} />
                {item.processing && <Typography variant="caption" className="ml-1" color="primary">Processing</Typography>}
                <Zoom in={item.processed && animate}>
                  {item.error
                      ? <Close className="ml-1 errorColor" />
                      : <Done className="ml-1 successColor" />}
                </Zoom>
                {item.processed && !item.error && <Typography variant="caption" className="ml-1 successColor">Processed</Typography>}
                {item.error && <Typography variant="caption" className="ml-1 errorColor">Failed</Typography>}
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
                    {' '}
                    due
                    {' '}
                    {formatRelativeDate(new Date(i.dateDue), today, D_MMM_YYYY, true, true)}
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
                justify="flex-end"
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
          ...rest
        }}
        >
          {RowRenderer}
        </DynamicSizeList>
    )}
    </AutoSizer>
  );
};

interface Props {
  dispatch?: Dispatch;
  currencySymbol?: string;
  values?: {
    contacts: BatchPaymentContact[]
  };
}

const getContacts = (dispatch, setContactsLoading, onComplete?) => {
  EntityService.getPlainRecords(
    "Invoice",
    "contact.id,contact.firstName,contact.lastName,amountOwing,dateDue,contact.hasSavedCC,invoiceNumber",
    `overdue > 0`,
    null,
    null,
    "dateDue",
    false
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
        name: getContactName({ firstName: res.rows[0].values[1], lastName: res.rows[0].values[2] }),
        hasStoredCard,
        checked: hasStoredCard,
        total: 0,
        items: [],
        index: 0,
        processed: false,
        processing: false,
        error: null
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
            name: getContactName({ firstName: r.values[1], lastName: r.values[2] }),
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
              checked: hasStoredCard
            }]
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
            checked: contacts[matchIndex].hasStoredCard
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

const BatchPayment: React.FC<Props & InjectedFormProps> = ({
  handleSubmit,
  dirty,
  invalid,
  dispatch,
  currencySymbol,
  values,
  form
}) => {
  const [contactsLoading, setContactsLoading] = useState(false);
  const [filterByStoreCard, setFilterByStoreCard] = useState(true);
  const [processing, setProcessing] = useState(false);
  const cancel = useRef(false);

  const classes = useStyles();

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

  const checkedContacts = values.contacts.filter(c => c.checked);
  const total = checkedContacts.reduce(
    (p, c) => decimalPlus(p, c.items.reduce((p, c) => decimalPlus(p, c.checked ? c.amountOwing : 0), 0) ),
    0
  );

  const onSave = () => {
    cancel.current = false;
    setProcessing(true);
    setContactsLoading(true);
    getContacts(dispatch, setContactsLoading,
      async () => {
        await checkedContacts.map((c, index) => () => new Promise(resolve => {
          if (cancel.current) {
            resolve();
            return;
          }
          listRef.current.scrollToItem(filterByStoreCard ? index : c.index, "start");
          setTimeout(() => {
            dispatch(change(FORM, `contacts[${c.index}].processing`, true ));
            CheckoutService.checkoutSubmitPayment(getBachCheckoutModel(c), null, null, null)
              .then(() => {
                dispatch(change(FORM, `contacts[${c.index}]`, {
                  ...c, processing: false, processed: true
                } ));
                setTimeout(resolve, 200);
              })
              .catch(res => {
                instantFetchErrorHandler(dispatch, res, `Payment for ${c.name} failed`);
                dispatch(change(FORM, `contacts[${c.index}]`, {
                  ...c, processing: false, processed: true, error: true
                } ));
                setTimeout(resolve, 200);
              });
          }, 300);
        })).reduce(async (a, b) => {
          await a;
          await b();
        }, Promise.resolve());
      });
  };

  return (
    <form className="appBarContainer" noValidate autoComplete="off" onSubmit={handleSubmit(onSave)}>
      <RouteChangeConfirm form={form} when={dirty} />
      <LoadingIndicator appBarOffset transparentBackdrop customLoading={contactsLoading} />

      <CustomAppBar>
        <Grid container>
          <Grid item xs={12} className="centeredFlex">
            <Typography color="inherit" noWrap className="appHeaderFontSize">
              Batch payment in (showing
              {' '}
              {values.contacts.length}
              {' '}
              contact
              {values.contacts.length !== 1 ? "s" : ""}
              {' '}
              with amounts due or overdue)
            </Typography>

            <div className="flex-fill" />

            <AppBarHelpMenu
              manualUrl=""
            />

            <Button
              className="closeAppBarButton"
              onClick={() => {
              cancel.current = true;
              setProcessing(false);
            }}
            >
              cancel
            </Button>

            <Button
              type="submit"
              classes={{
                root: "whiteAppBarButton",
                disabled: "whiteAppBarButtonDisabled"
              }}
              disabled={invalid || processing}
            >
              Process
              {' '}
              {checkedContacts.length}
              {' '}
              payment
              {checkedContacts.length === 1 ? "" : "s"}
            </Button>
          </Grid>
        </Grid>
      </CustomAppBar>

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
              labelPlacementStart: "m-0"
            }}
            label="Only show contacts with a stored card"
            labelPlacement="start"
            disabled={processing}
          />
        </div>

        <div className={clsx("overflow-auto flex-fill", classes.list)}>
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
            justify="flex-end"
            className="money p-2 summaryTopBorder"
          >
            <Typography variant="body2" className={classes.amountMargin}>
              <strong>{formatCurrency(total, currencySymbol)}</strong>
            </Typography>
          </Grid>
        </Grid>
      </div>
      )}
    </form>
);
};

const mapStateToProps = (state: State) =>
  ({
    currencySymbol: state.currency.shortCurrencySymbol,
    values: getFormValues(FORM)(state)
  });

export default reduxForm<any, Props>({
  form: FORM,
  initialValues: {
    contacts: []
  }
})(connect<any, any, any>(mapStateToProps)(BatchPayment));
