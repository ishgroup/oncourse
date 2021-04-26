/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { PreferenceEnum } from "@api/model";

export const SIMPLE_SEARCH_REGEX = /(^[^~\s."#]+$)/;

export const SIMPLE_SEARCH_QUOTES_REGEX = /(^"([^"])+"\s?)/;

export const SIMPLE_SEARCH_QUOTES_AND_NO_WHITESPACE_REGEX = /(^"([^"\s])+"$)/;

export const FILTER_TAGS_REGEX = /(\W@|^)@[^\s]+/g;

export const TAGS_REGEX = /#[^\s]+/g;

export const DEFAULT_CONFIG = {
  CONTAINER_ID: "client"
};

export const DRAWER_WIDTH = 240;

export const APP_BAR_HEIGHT = 64;

export const LIST_PAGE_SIZE = 50;

export const PLAIN_LIST_MAX_PAGE_SIZE = 65000;

export const APPLICATION_THEME_STORAGE_NAME = "theme";

export const DASHBOARD_ACTIVITY_STORAGE_NAME = "dashboardActivityState";

export const ENTITY_AQL_STORAGE_NAME = "entityAqlState";

// // Google constants

// Google Maps

export const GOOGLE_MAPS_API_KEY = "QUl6YVN5Q1lnMlIzMnc4TVhtNXBwNEJjalNlLTRuUDFHYTNaa0p3";

// Google tag manager
export const GOOGLE_TAG_MANAGER = "GTM-PQ23P93";

export const GOOGLE_TAG_MANAGER_DEFAULT_APP_NAME = "onCourse Client";

// User Preferences keys

export const DEFAULT_TIMEZONE_KEY: PreferenceEnum = "timezone.default";

export const DASHBOARD_CATEGORY_WIDTH_KEY: PreferenceEnum = "html.dashboard.category.width";
export const DASHBOARD_NEWS_LATEST_READ: PreferenceEnum = "dashboard.news.last_read";

export const ACCOUNT_INVOICE_TERMS: PreferenceEnum = "account.invoice.terms";

export const DASHBOARD_THEME_KEY: PreferenceEnum = "html.global.theme";

export const SYSTEM_USER_ADMINISTRATION_CENTER: PreferenceEnum = "systemUser.defaultAdministrationCentre.name";

export const GOOGLE_ANALYTICS_CLIENT_ID_KEY: PreferenceEnum = "google.analytics.cid";

export const GOOGLE_ANALYTICS_COMPAIN_ID_KEY: PreferenceEnum = "google.analytics.ci";

export const GOOGLE_ANALYTICS_COMPAIN_NAME_KEY: PreferenceEnum = "google.analytics.cn";

export const GOOGLE_ANALYTICS_USER_ID_KEY: PreferenceEnum = "google.analytics.uid";

export const LICENSE_SCRIPTING_KEY: PreferenceEnum = "license.scripting";

export const LICENSE_ACCESS_CONTROL_KEY: PreferenceEnum = "license.accesscontrol";

export const ACCOUNT_DEFAULT_STUDENT_ENROLMENTS_ID: PreferenceEnum = "account.default.studentEnrolments.id";

export const ACCOUNT_DEFAULT_VOUCHER_LIABILITY_ID: PreferenceEnum = "account.default.voucherLiability.id";

export const REPLICATION_ENABLED_KEY: PreferenceEnum = "replication.enabled";

export const FAVORITE_SCRIPTS_KEY: PreferenceEnum = "dashboard.scripts.favorite.id";

export const AVETMIS_ID_KEY: PreferenceEnum = "avetmiss.identifier";

export const EMAIL_FROM_KEY: PreferenceEnum = "email.from";

export const DEFAULT_DELIVERY_MODE_KEY: PreferenceEnum = "courseclass_default_deliveryMode";
export const DEFAULT_FUNDING_SOURCE_KEY: PreferenceEnum = "courseclass_default_fundingSource";
export const DEFAULT_MAXIMUM_PLACES_KEY: PreferenceEnum = "courseclass_default_maximumPlaces";
export const DEFAULT_MINIMUM_PLACES_KEY: PreferenceEnum = "courseclass_default_minimumPlaces";

export const LISTVIEW_MAIN_CONTENT_WIDTH: PreferenceEnum = "listview.main.content.width";

export const ADMIN_EMAIL_KEY = 'email.admin';

// extending String with capitalize method
const stringProto = String.prototype as any;

stringProto.capitalize = function () {
  return this.charAt(0).toUpperCase() + this.slice(1);
};
