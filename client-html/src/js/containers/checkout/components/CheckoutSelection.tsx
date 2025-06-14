/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { CheckoutSaleRelation, ColumnWidth } from '@api/model';
import Button from '@mui/material/Button';
import $t from '@t';
import clsx from 'clsx';
import { AppTheme, NoArgFunction, ResizableWrapper, ShowConfirmCaller } from 'ish-ui';
import debounce from 'lodash.debounce';
import React, { useCallback, useEffect, useMemo, useState } from 'react';
import { connect } from 'react-redux';
import { Dispatch } from 'redux';
import { change, getFormValues, initialize, isDirty, isInvalid, reduxForm } from 'redux-form';
import { withStyles } from 'tss-react/mui';
import { FETCH_FINISH, openDrawer, showConfirm } from '../../../common/actions';
import {
  clearCommonPlainRecords,
  getCommonPlainRecords,
  setCommonPlainSearch
} from '../../../common/actions/CommonPlainRecordsActions';
import AppBarContainer from '../../../common/components/layout/AppBarContainer';
import Drawer from '../../../common/components/layout/Drawer';
import { setListEditRecord } from '../../../common/components/list-view/actions';
import LoadingIndicator from '../../../common/components/progress/LoadingIndicator';
import { useAppSelector } from '../../../common/utils/hooks';
import { latestActivityStorageHandler } from '../../../common/utils/storage';
import uniqid from '../../../common/utils/uniqid';
import { PLAIN_LIST_MAX_PAGE_SIZE } from '../../../constants/Config';
import history from '../../../constants/History';
import {
  CheckoutContact,
  CheckoutCourse,
  CheckoutCourseClass,
  CheckoutItem,
  CheckoutSummary
} from '../../../model/checkout';
import { CheckoutFundingInvoice } from '../../../model/checkout/fundingInvoice';
import { EditViewProps } from '../../../model/common/ListView';
import { State } from '../../../reducers/state';
import {
  getContactsConcessionTypes,
  getContactsRelationTypes,
  getContactsTaxTypes,
  getContactTags
} from '../../entities/contacts/actions';
import { studentInitial } from '../../entities/contacts/components/ContactsGeneral';
import { ContactInitial } from '../../entities/contacts/Contacts';
import { getContactFullName } from '../../entities/contacts/utils';
import { getCountries, getLanguages, updateColumnsWidth } from '../../preferences/actions';
import {
  addContact,
  addItem,
  checkoutUpdateRelatedItems,
  removeContact,
  removeItem,
  updateClassItem,
  updateContact
} from '../actions';
import { checkoutClearContactEditRecord, checkoutGetContact, getRelatedContacts } from '../actions/checkoutContact';
import { checkoutClearPaymentStatus, checkoutGetActivePaymentMethods } from '../actions/checkoutPayment';
import { checkoutUpdateSummaryClassesDiscounts } from '../actions/checkoutSummary';
import {
  checkoutClearCourseClassList,
  checkoutGetClassPaymentPlans,
  checkoutGetCourseClassList,
  checkoutGetMembership,
  checkoutGetProduct,
  checkoutGetVoucher,
  clearCheckoutItemRecord
} from '../actions/chekoutItem';
import {
  CHECKOUT_CONTACT_COLUMNS,
  CHECKOUT_MEMBERSHIP_COLUMNS,
  CHECKOUT_PRODUCT_COLUMNS,
  CHECKOUT_VOUCHER_COLUMNS,
  CheckoutCurrentStep,
  CheckoutCurrentStepType,
  CheckoutPage,
  CheckoutPageType,
  titles
} from '../constants';
import {
  checkoutCourseMap,
  checkoutProductMap,
  checkoutVoucherMap,
  getCheckoutCurrentStep,
  processCheckoutContactId,
  processCheckoutCourseClassId,
  processCheckoutEnrolmentId,
  processCheckoutInvoiceId,
  processCheckoutLeadId,
  processCheckoutSale,
  processCheckoutStripePaymentRedirect,
  processCheckoutWaitingListIds,
  processChekoutCartIds
} from '../utils';
import CheckoutAppBar from './CheckoutAppBar';
import CheckoutSectionExpandableRenderer from './CheckoutSectionExpandableRenderer';
import CheckoutContactEditView, { CHECKOUT_CONTACT_EDIT_VIEW_FORM_NAME } from './contact/CheckoutContactEditView';
import CheckoutContactSearch from './contact/CheckoutContactSearch';
import EnrolContactListView from './contact/EnrolContactListView';
import CheckoutFundingInvoiceForm from './fundingInvoice/CheckoutFundingInvoiceForm';
import { CHECKOUT_FUNDING_INVOICE_SUMMARY_LIST_FORM } from './fundingInvoice/CheckoutFundingInvoiceSummaryList';
import CheckoutFundingThisInvoice from './fundingInvoice/CheckoutFundingThisInvoice';
import HeaderField from './HeaderField';
import CheckoutItemView from './items/components/CheckoutItemView';
import { CHECKOUT_ITEM_EDIT_VIEW_FORM } from './items/components/CkecoutItemViewForm';
import EnrolCourseClassView from './items/components/EnrolCourseClassView';
import SelectedItemRenderer from './items/components/SelectedItemRenderer';
import EnrolItemListView from './items/EnrolItemListView';
import CheckoutPaymentPage from './payment/CheckoutPaymentPage';
import CheckoutPaymentHeaderField from './payment/components/CheckoutPaymentHeaderField';
import CheckoutSummaryComp from './summary/CheckoutSummary';
import CheckoutSummaryHeaderField from './summary/CheckoutSummaryHeaderField';
import { CHECKOUT_SUMMARY_FORM as SUMMARRY_FORM } from './summary/CheckoutSummaryList';
import CheckoutPromoCodesHeaderField from './summary/promocode/CheckoutPromoCodesHeaderField';

export const CHECKOUT_SELECTION_FORM_NAME: string = "CHECKOUT_SELECTION_FORM";
const SIDEBAR_DEFAULT_WIDTH: number = 320;

const styles = (theme: AppTheme) => ({
  sideBar: {
    [theme.breakpoints.up("md")]: {
      paddingRight: 0,
      borderRight: 0
    }
  },
  drawerPaper: {
    [theme.breakpoints.up("md")]: {
      overflowX: "hidden",
      "&::after": {
        content: "''",
        position: "absolute",
        top: 0,
        right: 0,
        bottom: 0,
        borderRight: `1px solid ${theme.palette.divider}`,
        zIndex: -1
      }
    }
  },
  fieldCardRoot: {
    overflow: "visible",
    border: `1px solid ${theme.palette.divider}`
  },
  headerRoot: {
    background: "transparent"
  },
  contentRoot: {
    "&:last-child": {
      padding: 0
    }
  }
});

interface Props extends Partial<EditViewProps> {
  openSidebarDrawer?: () => void;
  updateColumnsWidth?: (columnsWidth: ColumnWidth) => void;
  classes?: any;
  value?: any;
  contactEditRecord?: any;
  showConfirm?: ShowConfirmCaller;
  selectedContacts?: CheckoutContact[];
  addSelectedContact?: (contact: any) => void;
  removeContact?: (index: number) => void;
  updateContact?: (contact: any, id: number) => void;
  contacts?: any[];
  getContactRecord?: (id: string) => void;
  onContactInit?: () => void;
  contactsSearch?: any;
  contactsLoading?: boolean;
  contactsRowsCount?: number;
  resetContactEditView?: NoArgFunction;
  isContactEditViewDirty?: boolean;
  courses?: any[];
  getCourses?: any;
  products?: any[];
  getProducts?: any;
  vouchers?: any[];
  getVouchers?: any;
  membershipProducts?: any[];
  getMembershipProducts?: any;
  selectedItems?: CheckoutItem[];
  addSelectedItem?: (item: any) => void;
  removeItem?: (itemId: number, itemType: string) => void;
  onChangeStep?: (step: CheckoutCurrentStepType) => void;
  checkoutStep?: number;
  courseClasses?: any[];
  itemEditRecord?: any;
  getMemberShipRecord?: (item: any) => void;
  getProductRecord?: (id: string) => void;
  getVoucherRecord?: (item: any) => void;
  clearItemRecord?: () => void;
  getActivePaymentMethods?: () => void;
  clearContactsSearch?: NoArgFunction;
  getRelatedContacts?: (search: string) => void;
  relatedContacts?: any[];
  checkoutGetCourseClassList?: (search: string) => void;
  checkoutClearCourseClassList?: NoArgFunction;
  checkoutUpdateSummaryClassesDiscounts?: NoArgFunction;
  paymentProcessStatus?: any;
  onCheckoutClearPaymentStatus?: () => void;
  updateSelectedClass?: any;
  getClassPaymentPlans?: any;
  hasErrors?: boolean;
  summarryInvalid?: boolean;
  fundingInvoiceInvalid?: boolean;
  finalTotal?: number;
  summary?: CheckoutSummary;
  fundingInvoiceValues?: {
    fundingInvoices: CheckoutFundingInvoice[];
  };
  salesRelations?: CheckoutSaleRelation[];
}

const createConfirmMessage = "Please first save or cancel the new contact you are creating.";

const parseContactSearch = (search: string) => {
  const contact = {
    firstName: null,
    lastName: null
  };

  const searchName = search.length > 0 ? search.replace(/[~"]/g, "").trim() : "";

  if (!searchName) return contact;

  const separator = searchName.indexOf(',');

  if (separator >= 0) {
    contact.firstName = (searchName.slice(separator + 1)).trim();
    contact.lastName = (searchName.slice(0, separator)).trim();
  } else {
    const searchPart = searchName.split(" ");
    if (searchPart.length > 0) {
      contact.firstName = (searchPart.slice(0, -1)).length ? (searchPart.slice(0, -1)).join(" ").trim() : null;
      contact.lastName = (searchPart.slice(searchPart.length - 1)).join("").trim();
    } else {
      contact.firstName = null;
      contact.lastName = (searchName).trim();
    }
  }

  return contact;
};

const CheckoutSelectionForm = React.memo<Props>(props => {
  const {
    openSidebarDrawer,
    form,
    dispatch,
    reset,
    updateColumnsWidth,
    classes,
    handleSubmit,
    value,
    contactEditRecord,
    showConfirm,
    getContactRecord,
    selectedContacts,
    addSelectedContact,
    removeContact,
    updateContact,
    contacts,
    onContactInit,
    contactsSearch,
    resetContactEditView,
    isContactEditViewDirty,
    courses,
    getCourses,
    products,
    getProducts,
    vouchers,
    getVouchers,
    membershipProducts,
    getMembershipProducts,
    selectedItems,
    addSelectedItem,
    removeItem,
    onChangeStep,
    checkoutStep,
    courseClasses,
    contactsLoading,
    itemEditRecord,
    getMemberShipRecord,
    getProductRecord,
    getVoucherRecord,
    clearItemRecord,
    getActivePaymentMethods,
    clearContactsSearch,
    getRelatedContacts,
    relatedContacts,
    checkoutGetCourseClassList,
    checkoutClearCourseClassList,
    checkoutUpdateSummaryClassesDiscounts,
    paymentProcessStatus,
    onCheckoutClearPaymentStatus,
    updateSelectedClass,
    getClassPaymentPlans,
    hasErrors,
    summarryInvalid,
    summary,
    invalid,
    fundingInvoiceValues,
    fundingInvoiceInvalid,
    salesRelations
  } = props;

  const [sidebarWidth, setSidebarWidth] = useState(SIDEBAR_DEFAULT_WIDTH);
  const [activeField, setActiveField] = useState<CheckoutPageType>(CheckoutPage.default);
  const [listContacts, setListContacts] = useState(contacts);
  const [selectedContact, setSelectedContact] = useState(undefined);
  const [openContactEditView, setOpenContactEditView] = useState(false);
  const [createNewContact, setCreateNewContact] = useState(false);
  const [selectedCourse, setSelectedCourse] = useState(undefined);
  const [openClassListView, setOpenClassListView] = useState(false);
  const [checkDirtyContactViewOnFocus, setCheckDirtyContactViewOnFocus] = useState(true);
  const [checkDirtyContactViewOnFocusCancel, setCheckDirtyContactViewOnFocusCancel] = useState(false);
  const [openedItem, setOpenedItem] = useState(undefined);
  const [openItemEditView, setOpenItemEditView] = useState(false);
  const [selectedDiscount, setSelectedDiscount] = useState(undefined);
  const [openDiscountView, setOpenDiscountView] = useState(false);
  const [customLoading, setCustomLoading] = useState(false);
  const [disablePayment, setDisablePayment] = useState(false);
  const [itemsSearch, setItemsSearch] = useState<string>(null);

  const hideAUSReporting = useAppSelector(state => state.location.countryCode !== 'AU');

  const mapedProducts = useMemo(() => products.map(checkoutProductMap), [products]);
  const mapedMbershipProducts = useMemo(() => membershipProducts.map(checkoutProductMap), [membershipProducts]);
  const mapedVouchers = useMemo(() => vouchers.map(checkoutVoucherMap), [vouchers]);

  const filterItems = item => item.name.trim().toLowerCase().includes(itemsSearch) || item.code.trim().toLowerCase().includes(itemsSearch);

  const filteredItems = useMemo(() => ({
    courses: itemsSearch ? courses.filter(filterItems) : courses,
    products: itemsSearch ? mapedProducts.filter(filterItems) : mapedProducts,
    vouchers: itemsSearch ? mapedVouchers.filter(filterItems) : mapedVouchers,
    membershipProducts: itemsSearch ? mapedMbershipProducts.filter(filterItems) : mapedMbershipProducts
  }), [
    itemsSearch,
    courses,
    mapedProducts,
    mapedVouchers,
    mapedMbershipProducts
  ]);

  const onCloseItemView = useCallback(() => {
    setOpenedItem(undefined);
    setOpenItemEditView(false);
    clearItemRecord();
    setActiveField(CheckoutPage.default);
  }, []);

  const clearDiscountView = useCallback(() => {
    setSelectedDiscount(undefined);
    setOpenDiscountView(false);
  }, []);

  const handleChangeStep = useCallback(
    (step: CheckoutCurrentStepType) => {
      const currentStep = getCheckoutCurrentStep(step);
      onChangeStep(step);
      if (currentStep > 0) {
        setActiveField(CheckoutPage.default);
        setOpenContactEditView(false);
        setSelectedContact(undefined);
        resetContactEditView();
        setOpenClassListView(false);
        setSelectedCourse(undefined);
        checkoutClearCourseClassList();
        onCloseItemView();
        clearDiscountView();
      }
    },
    [checkoutStep]
  );

  const onCloseClassList = useCallback(() => {
    setSelectedCourse(undefined);
    setOpenClassListView(false);
    checkoutClearCourseClassList();
    setActiveField(CheckoutPage.default);
  }, []);

  const getItemRecord = useCallback((item, type) => {
    switch (type) {
      case "membership":
        getMemberShipRecord(item);
        break;
      case "product":
        getProductRecord(item.id);
        break;
      case "voucher":
        getVoucherRecord(item);
        break;
      default:
        break;
    }
  }, []);

  const openItem = useCallback(
    item => {
      if (checkoutStep > 0) handleChangeStep(CheckoutCurrentStep.shoppingCart);
      switch (item.type) {
        case "course":
          if (selectedCourse && selectedCourse.id === item.id) {
            return;
          }
          setSelectedCourse(item);
          if (
            !selectedCourse
            || (selectedCourse && typeof selectedCourse.courseId === "number" && selectedCourse.courseId !== item.courseId)
          ) {
            checkoutGetCourseClassList(`course.id is ${item.courseId} and isCancelled is false and isActive is true`);
          }
          setOpenClassListView(true);
          onCloseItemView();
          break;
        case "voucher":
        case "product":
        case "membership":
          if (openedItem && openedItem.id === item.id && openedItem.type === item.type) {
            return;
          }
          dispatch(initialize(CHECKOUT_ITEM_EDIT_VIEW_FORM, {
            items: ""
          }));
          getItemRecord(item, item.type);
          setOpenItemEditView(true);
          setOpenedItem(item);
          onCloseClassList();
          openSidebarDrawer();
          break;
        default:
          return;
      }
      setOpenContactEditView(false);
      setSelectedContact(undefined);
      resetContactEditView();
    },
    [selectedCourse, openClassListView, selectedItems, checkoutStep, openedItem]
  );

  const openContactRow = useCallback(
    (item, checkDirty = true) => {
      if (selectedContact && selectedContact.id === item.id) {
        return;
      }

      if ((isContactEditViewDirty || createNewContact) && checkDirty) {
        showConfirm({
          onConfirm: () => openContactRow(item, false),
          confirmMessage: createNewContact ? createConfirmMessage : "",
          ...(createNewContact ? { title: null } : {})
          });
        return;
      }

      setCreateNewContact(false);
      resetContactEditView();
      getContactRecord(item.id);
      setOpenContactEditView(true);
      setSelectedContact(item);
      setListContacts([]);
      onCloseClassList();
      onCloseItemView();
      openSidebarDrawer();
    },
    [openContactEditView, contactEditRecord, selectedContact, isContactEditViewDirty, checkoutStep, createNewContact]
  );

  const onClearItemsSearch = useCallback((clearActive = false) => {
    if (clearActive) {
      setActiveField(CheckoutPage.default);
    }
  }, []);

  const onSelectHandler = (row, type, skipEditView?: boolean) => {
    setItemsSearch(null);

    switch (type) {
      case "contact":
        if (row.type === "create") {
          const parsedContact = parseContactSearch(contactsSearch);
          const { firstName, lastName } = parsedContact;
          setCreateNewContact(true);
          setOpenContactEditView(true);
          setSelectedContact({ id: null, ...parsedContact });
          ContactInitial.firstName = firstName;
          ContactInitial.lastName = lastName;
          ContactInitial.student = { ...studentInitial };
          reset();
          setListContacts([]);
        } else {
          dispatch(change(form, "contacts", null));
          const check = selectedContacts.filter(c => c.id === row.id);
          if (check.length === 0) {
            row.relations = [];
            addSelectedContact(row);

            if (!skipEditView) {
              openContactRow(row);
            }
          }
        }
        onContactInit();
        break;
      case "course": {
        if (!skipEditView) {
          const course = checkoutCourseMap(row);
          course.id = uniqid();
          openItem(course);
          addSelectedItem(course);
        }
        dispatch(change(CHECKOUT_SELECTION_FORM_NAME, "items", ""));
        onClearItemsSearch(true);
        break;
      }
      case "voucher":
      case "product":
      case "membership":
        if (selectedItems.filter(i => i.id === row.id && i.type === type).length === 0) {
          processCheckoutSale(row, type);
          openItem(row);
          addSelectedItem(row);
        }
        dispatch(change(CHECKOUT_SELECTION_FORM_NAME, "items", ""));
        onClearItemsSearch(true);
        break;
    }
    if (type !== 'course') {
      setTimeout(() => {
        checkoutUpdateSummaryClassesDiscounts();
      }, 500);
    }
  };

  useEffect(() => {
    dispatch(setCommonPlainSearch("Course", "currentlyOffered is true"));
    dispatch(setCommonPlainSearch("ArticleProduct", "isOnSale is true"));
    dispatch(setCommonPlainSearch("VoucherProduct", "isOnSale is true"));
    dispatch(setCommonPlainSearch("MembershipProduct", "isOnSale is true"));
    getCourses();
    getProducts();
    getVouchers();
    getMembershipProducts();
    getActivePaymentMethods();

    const query = new URLSearchParams(window.location.search);
    const invoiceId = query.get("invoiceId");
    const enrolmentId = query.get("enrolmentId");
    const contactId = query.get("contactId");
    const leadId = query.get("leadId");
    const courseClassId = query.get("courseClassId");
    const waitingListIds = query.get("waitingListIds");
    const cartId = query.get("cartId");
    const transactionId = query.get("payment_intent");
    const onCourseSessionId = query.get("onCourseSessionId");

    if (!transactionId && window.location.search) {
      history.replace("/checkout");
    }

    if (transactionId && onCourseSessionId) {
      processCheckoutStripePaymentRedirect(
        transactionId,
        onCourseSessionId,
        dispatch
      );
    }

    if (cartId) {
      processChekoutCartIds(
        cartId,
        onChangeStep,
        setActiveField,
        setCustomLoading,
        dispatch
      );
    }
    if (leadId) {
      processCheckoutLeadId(
        leadId,
        onChangeStep,
        setActiveField,
        setCustomLoading,
        dispatch
      );
    }
    if (waitingListIds) {
      processCheckoutWaitingListIds(
        waitingListIds.split(","),
        onChangeStep,
        setActiveField,
        setCustomLoading,
        dispatch
      );
    }
    if (courseClassId) {
      processCheckoutCourseClassId(
        courseClassId,
        onSelectHandler,
        setSelectedCourse,
        addSelectedItem,
        updateSelectedClass,
        getClassPaymentPlans,
        openItem,
        dispatch
      );
    }
    if (contactId) {
      processCheckoutContactId(contactId, onSelectHandler, dispatch);
    }
    if (invoiceId) {
      processCheckoutInvoiceId(
        invoiceId,
        onSelectHandler,
        onChangeStep,
        dispatch
      );
    }
    if (enrolmentId) {
      processCheckoutEnrolmentId(
        enrolmentId,
        onSelectHandler,
        dispatch
      );
    }
  }, []);

  useEffect(() => {
    if (checkoutStep === getCheckoutCurrentStep(CheckoutCurrentStep.shoppingCart) && contactsSearch && contactsSearch.length > 0) {
      setCheckDirtyContactViewOnFocus(true);
      contacts.splice(0, 0, {
        type: "create",
        name: "Create new student..."
      });
      setListContacts(contacts);
    }
  }, [contacts, contactsSearch]);

  useEffect(() => {
    if (contactEditRecord) {
      setListContacts(prev => {
        const updated = [...prev];
        const findIndex = updated.findIndex(c => c.id === contactEditRecord.id);

        if (findIndex !== -1) {
          latestActivityStorageHandler(
            { name: contactEditRecord.name, date: new Date().toISOString(), id: contactEditRecord.id },
            "Contacts"
          );

          updated[findIndex] = contactEditRecord;
        }
        return updated;
      });
    }
  }, [contactEditRecord]);

  useEffect(() => {
    if (selectedContacts.length > 0) {
      const ids = [];
      selectedContacts.forEach(sc => {
        if (sc.relations && sc.relations.length > 0) {
          sc.relations.forEach(r => {
            ids.push(r.relatedContactId);
          });
        }
      });
      if (ids.length > 0) {
        getRelatedContacts(`id in (${ids.join(",")})`);
      }
    }
  }, [selectedContacts]);

  const handleResizeCallback = useCallback(
    (...props) => {
      setSidebarWidth(props[2].getClientRects()[0].width);
    },
    []
  );

  const handleResizeStopCallback = useCallback(
    (...props) => {
      updateColumnsWidth(props[2].getClientRects()[0].width);
    },
    []
  );

  const onClearContactsSearch = useCallback(() => {
    clearContactsSearch();
    setListContacts([]);
  }, []);

  const handleFocusCallback = useCallback(
    (props, name) => {
      if (checkDirtyContactViewOnFocusCancel) {
        setCheckDirtyContactViewOnFocusCancel(false);
        setCheckDirtyContactViewOnFocus(true);
        props.target.blur();
        reset();
        return;
      }

      if ((isContactEditViewDirty || createNewContact) && checkDirtyContactViewOnFocus) {
        setCheckDirtyContactViewOnFocus(false);
        showConfirm(
          {
            onConfirm: () => {
              handleFocusCallback(props, name);
              onClearContactsSearch();
            },
            confirmMessage: createNewContact ? createConfirmMessage : "",
            onCancel: () => {
              setCheckDirtyContactViewOnFocusCancel(true);
            },
            ...(createNewContact ? { title: null } : {})
          }
        );
        return;
      }

      setActiveField(props.target.name);
      setOpenContactEditView(false);
      setSelectedContact(undefined);
      setCreateNewContact(false);
      resetContactEditView();
      setOpenClassListView(false);
      setSelectedCourse(undefined);
      checkoutClearCourseClassList();
      setOpenedItem(undefined);
      setOpenItemEditView(false);
      clearItemRecord();
      clearDiscountView();

      if (
        checkoutStep > 0
        && ![CheckoutPage.promocodes, CheckoutPage.fundingInvoiceCompanies].includes(name)
      ) {
        handleChangeStep(CheckoutCurrentStep.shoppingCart);
      }
    },
    [
      activeField,
      openContactEditView,
      selectedContact,
      checkoutStep,
      isContactEditViewDirty,
      createNewContact,
      checkDirtyContactViewOnFocus,
      checkDirtyContactViewOnFocusCancel
    ]
  );

  const onClose = useCallback(
    (props?: any, checkDirty = true) => {
      if ((isContactEditViewDirty || createNewContact) && checkDirty) {
        showConfirm({
          onConfirm: () => onClose(props, false)
        });
        return;
      }
      setActiveField(CheckoutPage.default);
      onClearContactsSearch();
      setOpenContactEditView(false);
      setCreateNewContact(false);
      setSelectedContact(undefined);
      resetContactEditView();
      reset();
    },
    [isContactEditViewDirty, createNewContact]
  );

  const onSelectDisabledHandler = useCallback(
    (row, type) => {
      switch (type) {
        case "contact":
          return selectedContacts.filter(c => c.id === row.id).length === 1;
        case "course":
          return false;
        case "product":
        case "voucher":
        case "membership":
          return selectedItems.filter(c => c.id === row.id && c.type === type).length === 1;
        default:
          return false;
      }
    },
    [selectedContacts, selectedItems]
  );

  const onContactDeleteHandler = useCallback(
    (e, i, row) => {
      removeContact(i);

      if (selectedContact && selectedContact.id === row.id) {
        setOpenContactEditView(false);
        setActiveField(CheckoutPage.default);
        setSelectedContact(undefined);
        resetContactEditView();
      }
    },
    [contactEditRecord, listContacts, selectedContact]
  );

  const onContactSave = useCallback(
    (value, dispatch, formProps) => {
      if (formProps.creatingNew) {
        setActiveField(CheckoutPage.default);
        setCreateNewContact(false);
        setOpenContactEditView(false);
        setSelectedContact(undefined);
      } else {
        setListContacts(prev => {
          const updated = [...prev];
          const findIndex = updated.findIndex(c => c.id === value.id);

          updated[findIndex] = value;
          return updated;
        });

        updateContact(value, value.id);
      }
    },
    [listContacts, selectedContacts]
  );

  const onSubmit = useCallback(() => { }, []);

  const onItemDeleteHandler = useCallback(
    (e, i, row) => {
      const onRemove = () => {
        removeItem(row.id, row.type);

        if (selectedCourse && selectedCourse.id === row.id) {
          onCloseClassList();
        }
        if (openedItem && openedItem.id === row.id && openedItem.type === row.type) {
          onCloseItemView();
        }
        if (row.cartAction && selectedItems.length) {
          const id = row.type === "course" ? row.courseId : row.id;
          row.id = id;

          dispatch(checkoutUpdateRelatedItems(
            [],
            [...salesRelations,
                {
                  cartAction: row.cartAction,
                  toItem: {
                    id,
                    cartItem: row,
                    type: row.type.capitalize(),
                    link: `/${row.type.toLowerCase()}/${id}`
                  },
                  fromItem: { id: selectedItems[0].id }
                }
              ]
          ));
        }

        const addedRelations = selectedItems.filter(s => s.fromItemRelation
          && (s.fromItemRelation.id === row.id || s.fromItemRelation.id === row.courseId)
          && s.fromItemRelation.type === row.type);

        if (addedRelations.length) {
          addedRelations.forEach(rel => {
            removeItem(rel.id, rel.type);
          });
        }

        setTimeout(() => {
          checkoutUpdateSummaryClassesDiscounts();
        }, 500);
      };

      if (row.cartAction === "Add but do not allow removal") {
        showConfirm({
          onConfirm: onRemove,
          confirmMessage: "The item you are removing is required by another item in the shopping cart.",
        });
      } else {
        onRemove();
      }
    },
    [selectedCourse, salesRelations, selectedItems, openedItem]
  );

  const debouncedSearchUpdate = useCallback<any>(debounce((name, val) => setItemsSearch(val.trim().toLowerCase()), 800), []);

  const onClearItemsSearchField = () => {
    setItemsSearch(null);
  };

  const noItemMsg = useMemo(() => {
    if (
      value
      && value.items
      && !filteredItems.courses.length
      && !filteredItems.products.length
      && !filteredItems.vouchers.length
      && !filteredItems.membershipProducts.length
    ) {
      return `No Items found for "${value.items}"`;
    }
    return null;
  }, [
    value,
    filteredItems
  ]);

  const noContactMsg = useMemo(() => {
    if (value && value.contacts && !contacts.length && !contactsLoading) {
      return `No contacts found for "${value.contacts}"`;
    }
    return null;
  }, [value, contacts, contactsLoading]);

  const handleSummmayClick = () => {
    handleChangeStep(CheckoutCurrentStep.summary);
    setActiveField(CheckoutPage.summary);
    dispatch({ type: FETCH_FINISH });
  };

  const handlePaymentClick = () => {
    handleChangeStep(CheckoutCurrentStep.payment);
    onCheckoutClearPaymentStatus();
    dispatch({ type: FETCH_FINISH });
  };

  const handleShoppingCartExpand = () => {
    handleChangeStep(CheckoutCurrentStep.shoppingCart);
    setActiveField(CheckoutPage.default);
    dispatch({ type: FETCH_FINISH });
  };

  const openDiscount = useCallback(
    row => {
      if (selectedDiscount && selectedDiscount.id === row.id && selectedDiscount.type === row.type) {
        return;
      }
      setSelectedDiscount(row);
      setOpenDiscountView(true);
      setActiveField(CheckoutPage.promocodes);
    },
    [selectedDiscount]
  );

  const onClassSelect = (selectedClass: CheckoutCourseClass) => {
    const updatedCourse: CheckoutCourse = {
      ...selectedCourse,
      price: selectedClass.price,
      animate: true,
      discount: null,
      discounts: [],
      discountExTax: 0,
      studyReason: "Not stated",
      class: { ...selectedClass }
    };

    if (selectedItems.some(i => i.id === updatedCourse.id)) {
      updateSelectedClass(updatedCourse);
    } else {
      addSelectedItem(updatedCourse);
    }

    setSelectedCourse(updatedCourse);
    getClassPaymentPlans(updatedCourse);
    openSidebarDrawer();
    setTimeout(() => {
      checkoutUpdateSummaryClassesDiscounts();
    }, 500);
  };

  const handleFundingInvoiceClick = () => {
    handleChangeStep(CheckoutCurrentStep.fundingInvoice);
    setActiveField(CheckoutPage.fundingInvoiceSummary);
    dispatch(change(CHECKOUT_FUNDING_INVOICE_SUMMARY_LIST_FORM,
      "fundingInvoices",
      fundingInvoiceValues.fundingInvoices.map((f, ind) => ({
        ...f,
        active: ind === 0
      }))));
  };
  
  const isSummaryAvailable = useMemo(() => selectedContacts.length > 0
    && ((selectedItems.length > 0 && selectedItems.some(i => i.type !== "course" || i.class))
      || Boolean(summary.previousOwing.invoices.length)), [selectedContacts, selectedItems, summary.previousOwing.invoices.length]);
  
  const isCreateLeadAvailable = useMemo(() => selectedContacts.length === 1
    && selectedItems.length > 0
    && !summary.previousOwing.invoices.length
    && !selectedItems.some(i => i.type !== "course" || i.class),
    [selectedItems, selectedContacts, summary.previousOwing.invoices.length]);
  
  const createLeadButton = useMemo(() => (
    <div className="centeredFlex justify-content-center mt-3 mb-3">
      <Button
        variant="outlined"
        color="primary"
        size="large"
        href={selectedContacts[0]
          ? `/lead/new?courseIds=${selectedItems.map(s => s.courseId)}&contactId=${selectedContacts[0]?.id}&contactName=${getContactFullName(selectedContacts[0] as any)}`
          : ""}
        target="_blank"
      >
        {$t('create_lead')}
      </Button>
    </div>
    ),
  [selectedItems, selectedContacts]);

  return (
    <div className={clsx("root", classes.root)}>
      <ResizableWrapper
        maxWidth="60%"
        onResizeStop={handleResizeStopCallback}
        onResize={handleResizeCallback}
        sidebarWidth={sidebarWidth || SIDEBAR_DEFAULT_WIDTH}
        className={classes.sideBar}
        classes={{
          sideBarWrapper: classes.sideBarWrapper
        }}
      >
        <Drawer classes={{ drawerPaper: classes.drawerPaper }}>
          <form onSubmit={handleSubmit(onSubmit)} autoComplete="off">
            <div className="pl-2 pr-2 pt-3">
              <CheckoutSectionExpandableRenderer
                title={$t('shopping_cart')}
                expanded={checkoutStep === getCheckoutCurrentStep(CheckoutCurrentStep.shoppingCart)}
                onExpanded={handleShoppingCartExpand}
                disabled={paymentProcessStatus === "success" || summarryInvalid}
              >
                <CheckoutContactSearch
                  form={form}
                  dispatch={dispatch}
                  value={value}
                  setActiveField={setActiveField}
                  handleFocusCallback={handleFocusCallback}
                  onClearContactsSearch={onClearContactsSearch}
                  onContactDeleteHandler={onContactDeleteHandler}
                  selectedContacts={selectedContacts}
                  selectedItems={selectedItems}
                  selectedContact={selectedContact}
                  openContactRow={openContactRow}
                  summary={summary}
                />

                <HeaderField
                  heading="Items"
                  name={CheckoutPage.items}
                  placeholder={$t('find_course_or_item')}
                  onFocus={handleFocusCallback}
                  SelectedItemView={(
                    <SelectedItemRenderer
                      selectedCourse={selectedCourse}
                      openedItem={openedItem}
                      selectedContacts={selectedContacts}
                      items={selectedItems}
                      openRow={openItem}
                      onDelete={onItemDeleteHandler}
                    />
                  )}
                  form={form}
                  dispatch={dispatch}
                  onSearch={debouncedSearchUpdate}
                  onClearSearch={onClearItemsSearchField}
                  value={value}
                />
              </CheckoutSectionExpandableRenderer>

              {isCreateLeadAvailable && createLeadButton}

              {isSummaryAvailable
                && (
                <>
                  <CheckoutSectionExpandableRenderer
                    title={$t('summary')}
                    expanded={checkoutStep === getCheckoutCurrentStep(CheckoutCurrentStep.summary)}
                    onExpanded={handleSummmayClick}
                    disabled={paymentProcessStatus === "success"}
                  >
                    <CheckoutPromoCodesHeaderField
                      setActiveField={setActiveField}
                      handleFocusCallback={handleFocusCallback}
                      openDiscountRow={openDiscount}
                      selectedDiscount={selectedDiscount}
                      clearDiscountView={clearDiscountView}
                      dispatch={dispatch}
                      disabled={summarryInvalid}
                    />

                    <CheckoutSummaryHeaderField
                      activeField={activeField}
                      setActiveField={setActiveField}
                      selectedContacts={selectedContacts}
                      clearDiscountView={clearDiscountView}
                    />
                  </CheckoutSectionExpandableRenderer>

                  {!hideAUSReporting && selectedContacts?.length === 1 && Boolean(fundingInvoiceValues?.fundingInvoices?.length && selectedItems.filter(i => i.type === 'course' && i.checked).length === 1) && (
                    <CheckoutSectionExpandableRenderer
                      title={$t('funding_invoices')}
                      expanded={checkoutStep === getCheckoutCurrentStep(CheckoutCurrentStep.fundingInvoice)}
                      onExpanded={handleFundingInvoiceClick}
                      disabled={paymentProcessStatus === "success"}
                    >
                      <CheckoutFundingThisInvoice dispatch={dispatch} />
                    </CheckoutSectionExpandableRenderer>
                 )}

                  <CheckoutSectionExpandableRenderer
                    title={$t('Payment')}
                    expanded={checkoutStep === getCheckoutCurrentStep(CheckoutCurrentStep.payment)}
                    onExpanded={handlePaymentClick}
                    disabled={
                    paymentProcessStatus === "success"
                    || hasErrors
                    || summarryInvalid
                    || fundingInvoiceInvalid
                    || (
                      !summary.list.some(l => l.items.some(li => li.checked))
                      && !summary.voucherItems.some(i => i.checked)
                      && !summary.previousOwing.invoices.length)
                  }
                  >
                    <CheckoutPaymentHeaderField
                      form={form}
                      dispatch={dispatch}
                      activeField={activeField}
                      setActiveField={setActiveField}
                      selectedDiscount={selectedDiscount}
                      setSelectedDiscount={setSelectedDiscount}
                      setDisablePayment={setDisablePayment}
                      formInvalid={invalid}
                    />
                  </CheckoutSectionExpandableRenderer>
                </>
                )}
            </div>
          </form>
        </Drawer>
      </ResizableWrapper>

      <div style={{ width: `calc(100% - ${sidebarWidth}px)` }}>
        <div className={clsx({ "d-none": checkoutStep !== getCheckoutCurrentStep(CheckoutCurrentStep.shoppingCart) })}>
          <div className="appFrame flex-fill root">
            <LoadingIndicator
              customLoading={customLoading}
            />
            { !openItemEditView
              && !openedItem
              && !openClassListView
              && !selectedCourse
              && !openContactEditView
              && !selectedContact && (
                <>
                  <AppBarContainer
                    hideHelpMenu
                    hideSubmitButton
                    disableInteraction
                    title={(
                      <CheckoutAppBar
                        title={
                          activeField === CheckoutPage.contacts && noContactMsg !== null
                            ? noContactMsg
                            : activeField === CheckoutPage.items && noItemMsg !== null
                              ? noItemMsg
                              : titles[activeField]
                        }
                      />
                    )}
                  >
                    <div className="w-100">
                      {!openContactEditView && activeField === CheckoutPage.contacts && (
                        <div>
                          <EnrolContactListView
                            title={`${listContacts.length > 1 ? "Contacts" : "Contact"}`}
                            contacts={listContacts}
                            relatedContacts={relatedContacts}
                            onChangeHandler={onSelectHandler}
                            disabledHandler={onSelectDisabledHandler}
                            searchString={value && value.contacts}
                            selectedContacts={selectedContacts}
                            contactsLoading={contactsLoading}
                          />
                        </div>
                      )}

                      {!openItemEditView && !openClassListView && activeField === CheckoutPage.items && (
                        <div>
                          <EnrolItemListView
                            courses={filteredItems.courses}
                            products={filteredItems.products}
                            vouchers={filteredItems.vouchers}
                            membershipProducts={filteredItems.membershipProducts}
                            onChangeHandler={onSelectHandler}
                            disabledHandler={onSelectDisabledHandler}
                            searchString={itemsSearch}
                            selectedItems={selectedItems}
                            salesRelations={salesRelations}
                          />
                        </div>
                      )}
                    </div>
                  </AppBarContainer>
                </>
              )}

            {openContactEditView && (contactEditRecord || createNewContact) && (
              <CheckoutContactEditView
                creatingNew={createNewContact}
                showConfirm={showConfirm}
                onSave={onContactSave}
                onClose={onClose}
                leftOffset={sidebarWidth}
              />
            )}

            {openClassListView && Boolean(courseClasses.length) && (
              <EnrolCourseClassView
                course={selectedCourse}
                onClose={onCloseClassList}
                onClassSelect={onClassSelect}
                selectedItems={selectedItems}
                courseClasses={courseClasses}
              />
            )}

            {openItemEditView && itemEditRecord && (
              <CheckoutItemView openedItem={openedItem} onClose={onCloseItemView} summary={summary} />
            )}
          </div>
        </div>
        <div className={clsx("root", { "d-none": checkoutStep !== getCheckoutCurrentStep(CheckoutCurrentStep.summary) })}>
          <CheckoutSummaryComp
            checkoutStep={checkoutStep}
            onChangeStep={onChangeStep}
            activeField={activeField}
            openDiscountView={openDiscountView}
            selectedDiscount={selectedDiscount}
            selectedContacts={selectedContacts}
            summaryList={summary.list}
          />
        </div>
        <div className={clsx("root", { "d-none": checkoutStep !== getCheckoutCurrentStep(CheckoutCurrentStep.fundingInvoice) })}>
          <CheckoutFundingInvoiceForm
            activeField={activeField}
            titles={titles}
          />
        </div>
        <div className={clsx("root", { "d-none": checkoutStep !== getCheckoutCurrentStep(CheckoutCurrentStep.payment) })}>
          <CheckoutPaymentPage
            activeField={activeField}
            titles={titles}
            selectedDiscount={selectedDiscount}
            disablePayment={disablePayment}
          />
        </div>
      </div>
    </div>

  );
});

const mapStateToProps = (state: State) => ({
  value: getFormValues(CHECKOUT_SELECTION_FORM_NAME)(state),
  isContactEditViewDirty: isDirty(CHECKOUT_CONTACT_EDIT_VIEW_FORM_NAME)(state),
  contactEditRecord: state.checkout.contactEditRecord,
  itemEditRecord: state.checkout.itemEditRecord,
  salesRelations: state.checkout.salesRelations,
  contacts: state.plainSearchRecords["Contact"].items,
  contactsSearch: state.plainSearchRecords["Contact"].search,
  contactsLoading: state.plainSearchRecords["Contact"].loading,
  courses: state.plainSearchRecords["Course"].items,
  coursesSearch: state.plainSearchRecords["Course"].search,
  contactsRowsCount: state.plainSearchRecords["Contact"].rowsCount,
  products: state.plainSearchRecords["ArticleProduct"].items,
  vouchers: state.plainSearchRecords["VoucherProduct"].items,
  membershipProducts: state.plainSearchRecords["MembershipProduct"].items,
  selectedContacts: state.checkout.contacts,
  selectedItems: state.checkout.items,
  courseClasses: state.checkout.courseClasses,
  relatedContacts: state.checkout.relatedContacts,
  paymentProcessStatus: state.checkout.payment.process.status,
  summary: state.checkout.summary,
  hasErrors: state.checkout.hasErrors,
  summarryInvalid: isInvalid(SUMMARRY_FORM)(state),
  fundingInvoiceInvalid: isInvalid(CHECKOUT_FUNDING_INVOICE_SUMMARY_LIST_FORM)(state),
  fundingInvoiceValues: getFormValues(CHECKOUT_FUNDING_INVOICE_SUMMARY_LIST_FORM)(state)
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  openSidebarDrawer: () => dispatch(openDrawer()),
  updateColumnsWidth: (preferenceLeftColumnWidth: number) => {
    dispatch(updateColumnsWidth({ preferenceLeftColumnWidth }));
  },
  getContactTags: () => dispatch(getContactTags()),
  onContactInit: () => {
    dispatch(setListEditRecord(ContactInitial));
    dispatch(initialize(CHECKOUT_CONTACT_EDIT_VIEW_FORM_NAME, ContactInitial));
  },
  resetContactEditView: () => {
    dispatch(initialize(CHECKOUT_CONTACT_EDIT_VIEW_FORM_NAME, null));
    dispatch(setListEditRecord(null));
    dispatch(checkoutClearContactEditRecord());
  },
  clearContactsSearch: () => dispatch(clearCommonPlainRecords("Contact")),
  getContactRecord: (id: number) => dispatch(checkoutGetContact(id)),
  getCountries: () => dispatch(getCountries()),
  getLanguages: () => dispatch(getLanguages()),
  getContactsRelationTypes: () => dispatch(getContactsRelationTypes()),
  getContactsConcessionTypes: () => dispatch(getContactsConcessionTypes()),
  getTaxTypes: () => dispatch(getContactsTaxTypes()),
  showConfirm: props =>
    dispatch(showConfirm(props)),
  getCourses: (offset?: number) => dispatch(
    getCommonPlainRecords("Course", offset, "code,name,isTraineeship", true, null, PLAIN_LIST_MAX_PAGE_SIZE)
  ),
  getProducts: (offset?: number) =>
    dispatch(getCommonPlainRecords("ArticleProduct", offset, CHECKOUT_PRODUCT_COLUMNS, true, null, PLAIN_LIST_MAX_PAGE_SIZE)),
  getVouchers: (offset?: number) =>
    dispatch(getCommonPlainRecords("VoucherProduct", offset, CHECKOUT_VOUCHER_COLUMNS, true, null, PLAIN_LIST_MAX_PAGE_SIZE)),
  getMembershipProducts: (offset?: number) => dispatch(
    getCommonPlainRecords("MembershipProduct", offset, CHECKOUT_MEMBERSHIP_COLUMNS, true, null, PLAIN_LIST_MAX_PAGE_SIZE)
  ),
  addSelectedContact: contact => dispatch(addContact(contact)),
  removeContact: index => dispatch(removeContact(index)),
  updateContact: (contact, id) => dispatch(updateContact(contact, id)),
  addSelectedItem: (item: any) => dispatch(addItem(item)),
  removeItem: (itemId: number, itemType: string) => dispatch(removeItem(itemId, itemType)),
  checkoutGetCourseClassList: (search: string) => dispatch(checkoutGetCourseClassList(search)),
  checkoutClearCourseClassList: () => dispatch(checkoutClearCourseClassList()),
  getMemberShipRecord: (item: any) => dispatch(checkoutGetMembership(item.id)),
  getProductRecord: (id: number) => dispatch(checkoutGetProduct(id)),
  getVoucherRecord: (item: any) => dispatch(checkoutGetVoucher(item)),
  clearItemRecord: () => {
    dispatch(initialize(CHECKOUT_ITEM_EDIT_VIEW_FORM, null));
    dispatch(clearCheckoutItemRecord());
  },
  getActivePaymentMethods: () => dispatch(checkoutGetActivePaymentMethods()),
  onCheckoutClearPaymentStatus: () => dispatch(checkoutClearPaymentStatus()),
  getRelatedContacts: (search: string) => dispatch(getRelatedContacts(search, CHECKOUT_CONTACT_COLUMNS, true, "lastName,firstName")),
  checkoutUpdateSummaryClassesDiscounts: () => dispatch(checkoutUpdateSummaryClassesDiscounts()),
  updateSelectedClass: item => dispatch(updateClassItem(item)),
  getClassPaymentPlans: item => dispatch(checkoutGetClassPaymentPlans(item))
});

export default reduxForm<any, Props>({
  form: CHECKOUT_SELECTION_FORM_NAME
})(connect<any, any, any>(mapStateToProps, mapDispatchToProps)(withStyles(CheckoutSelectionForm, styles)));