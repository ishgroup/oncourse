/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.security.api.permission;

import io.bootique.jetty.servlet.DefaultServletEnvironment;
import ish.common.types.KeyCode;
import ish.oncourse.server.cayenne.Module;
import ish.oncourse.server.cayenne.*;
import ish.oncourse.server.security.api.IPermissionService;
import ish.util.StringUtil;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static ish.oncourse.server.security.api.PermissionServiceFactory.ROOT_API;

/**
 * Lazy implementation of ApiPermission which calculates his KeyCode based on query parameters.
 * Lazy KeyCode has next structure: LAZY/{QUERY_PARAM}
 */
public class LazyApiPermission extends ApiPermission {

    private static final String LAZY_KEY_CODE_DELIMITER = "/";
    private static final String UTF_8 = "UTF-8";
    private static final String QUERY_SPLITTER = "&";
    private static final String PAIR_CONCATENATOR = "=";

    private static final String TRAINEESHIP = "traineeship";

    private String lazyKeyCodeParam;
    private String reserveQueryString;

    private static final Map<String,KeyCode> KEY_CODE_MAP = new HashMap<>();
    static {
        KEY_CODE_MAP.put(Account.class.getSimpleName().toLowerCase(), KeyCode.ACCOUNT);
        KEY_CODE_MAP.put(ProductItem.class.getSimpleName().toLowerCase(), KeyCode.SALE);
        KEY_CODE_MAP.put(AccountTransaction.class.getSimpleName().toLowerCase(), KeyCode.TRANSACTION);
        KEY_CODE_MAP.put(Application.class.getSimpleName().toLowerCase(), KeyCode.APPLICATION);
        KEY_CODE_MAP.put(ArticleProduct.class.getSimpleName().toLowerCase(), KeyCode.PRODUCT);
        KEY_CODE_MAP.put(Audit.class.getSimpleName().toLowerCase(), KeyCode.AUDIT_LOGGING);
        KEY_CODE_MAP.put(Banking.class.getSimpleName().toLowerCase(), KeyCode.BANKING);
        KEY_CODE_MAP.put(Contact.class.getSimpleName().toLowerCase(), KeyCode.CONTACT);
        KEY_CODE_MAP.put(ContactRelationType.class.getSimpleName().toLowerCase(), KeyCode.CONTACT_RELATION_TYPE);
        KEY_CODE_MAP.put(CorporatePass.class.getSimpleName().toLowerCase(), KeyCode.CORPORATE_PASS);
        KEY_CODE_MAP.put(CourseClass.class.getSimpleName().toLowerCase(), KeyCode.CLASS);
        KEY_CODE_MAP.put(Discount.class.getSimpleName().toLowerCase(), KeyCode.DISCOUNT);
        KEY_CODE_MAP.put(Document.class.getSimpleName().toLowerCase(), KeyCode.ATTACHMENT_INFO);
        KEY_CODE_MAP.put(EmailTemplate.class.getSimpleName().toLowerCase(), KeyCode.EMAIL_TEMPLATE);
        KEY_CODE_MAP.put(FundingSource.class.getSimpleName().toLowerCase(), KeyCode.SPECIAL_AVETMISS_EXPORT);
        KEY_CODE_MAP.put(Invoice.class.getSimpleName().toLowerCase(), KeyCode.INVOICE);
        KEY_CODE_MAP.put(InvoiceLine.class.getSimpleName().toLowerCase(), KeyCode.INVOICE);
        KEY_CODE_MAP.put(MembershipProduct.class.getSimpleName().toLowerCase(), KeyCode.MEMBERSHIP);
        KEY_CODE_MAP.put(Module.class.getSimpleName().toLowerCase(), KeyCode.NTIS_DATA);
        KEY_CODE_MAP.put(PaymentIn.class.getSimpleName().toLowerCase(), KeyCode.PAYMENT_IN);
        KEY_CODE_MAP.put(PaymentOut.class.getSimpleName().toLowerCase(), KeyCode.PAYMENT_OUT);
        KEY_CODE_MAP.put(Payslip.class.getSimpleName().toLowerCase(), KeyCode.PAYSLIP);
        KEY_CODE_MAP.put(Qualification.class.getSimpleName().toLowerCase(), KeyCode.NTIS_DATA);
        KEY_CODE_MAP.put(Room.class.getSimpleName().toLowerCase(), KeyCode.ROOM);
        KEY_CODE_MAP.put(Script.class.getSimpleName().toLowerCase(), KeyCode.SCRIPT_TEMPLATE);
        KEY_CODE_MAP.put(Site.class.getSimpleName().toLowerCase(), KeyCode.SITE);
        KEY_CODE_MAP.put(Voucher.class.getSimpleName().toLowerCase(), KeyCode.VOUCHER);
        KEY_CODE_MAP.put(VoucherPaymentIn.class.getSimpleName().toLowerCase(), KeyCode.VOUCHER);
        KEY_CODE_MAP.put(VoucherProduct.class.getSimpleName().toLowerCase(), KeyCode.VOUCHER);
        KEY_CODE_MAP.put(VoucherProductCourse.class.getSimpleName().toLowerCase(), KeyCode.VOUCHER);
        KEY_CODE_MAP.put(WaitingList.class.getSimpleName().toLowerCase(), KeyCode.WAITING_LIST);
        KEY_CODE_MAP.put(Lead.class.getSimpleName().toLowerCase(), KeyCode.WAITING_LIST);
        KEY_CODE_MAP.put(Application.class.getSimpleName().toLowerCase(), KeyCode.APPLICATION);
        KEY_CODE_MAP.put(ArticleProduct.class.getSimpleName().toLowerCase(), KeyCode.PRODUCT);
        KEY_CODE_MAP.put(Course.class.getSimpleName().toLowerCase(), KeyCode.COURSE);
        KEY_CODE_MAP.put(Certificate.class.getSimpleName().toLowerCase(), KeyCode.CERTIFICATE);
        KEY_CODE_MAP.put(Survey.class.getSimpleName().toLowerCase(), KeyCode.SURVEYS);
        KEY_CODE_MAP.put(Outcome.class.getSimpleName().toLowerCase(), KeyCode.OUTCOMES);
        KEY_CODE_MAP.put(ConcessionType.class.getSimpleName().toLowerCase(), KeyCode.DISCOUNT);
        KEY_CODE_MAP.put(Enrolment.class.getSimpleName().toLowerCase(), KeyCode.ENROLMENT);
        KEY_CODE_MAP.put(Tax.class.getSimpleName().toLowerCase(), KeyCode.SALE);
        KEY_CODE_MAP.put(Message.class.getSimpleName().toLowerCase(), KeyCode.EMAIL_TEMPLATE);
        KEY_CODE_MAP.put(Session.class.getSimpleName().toLowerCase(), KeyCode.SESSION);
        KEY_CODE_MAP.put(TRAINEESHIP, KeyCode.CLASS);
        KEY_CODE_MAP.put(PriorLearning.class.getSimpleName().toLowerCase(), KeyCode.OUTCOMES);
        KEY_CODE_MAP.put(FundingSource.class.getSimpleName().toLowerCase(), KeyCode.COURSE);
        KEY_CODE_MAP.put(CustomFieldType.class.getSimpleName().toLowerCase(), KeyCode.CONTACT);
        KEY_CODE_MAP.put(Assessment.class.getSimpleName().toLowerCase(), KeyCode.ASSESSMENT);
        KEY_CODE_MAP.put(AssessmentSubmission.class.getSimpleName().toLowerCase(), KeyCode.ASSESSMENT);

    }


    public LazyApiPermission(String path, String method, String mask, String keyCode, String errorMessage) {
        super(path, method, mask, null, errorMessage);
        this.lazyKeyCodeParam = keyCode.split(LAZY_KEY_CODE_DELIMITER)[1];
    }

    @Override
    public KeyCode getKeyCode(String query) {
        String keyCodeValue;
        try {
            keyCodeValue = splitQuery(query).get(lazyKeyCodeParam);
        } catch (UnsupportedEncodingException e) {
            keyCodeValue = null;
        }
        if (keyCodeValue == null) {
            return null;
        }
        return KEY_CODE_MAP.get(keyCodeValue.toLowerCase());
    }

    @Override
    public PermissionCheckingResult check() {
        var permissionService = injector.getInstance(IPermissionService.class);
        if (permissionService == null) {
            return new PermissionCheckingResult(false, "Permission service isn't initialized!");
        }
        var queryString = getQueryString();
        if (queryString == null) {
            return new PermissionCheckingResult(false, "Query parameters can't be empty for lazy permission checking.");
        }
        var keyCode = getKeyCode(queryString);
        if (keyCode == null) {
            try {
                return permissionService.hasAccess(getLazyEntityPath(), getMethod());
            } catch (Exception e) {
                return new PermissionCheckingResult(false, "Can't find lazy keycode for entity.");
            }
        }
            return new PermissionCheckingResult(permissionService.currentUserCan(keyCode, getMask()), errorMessage);
    }

    private String getLazyEntityPath() throws UnsupportedEncodingException {
        var path = getPath().split("/");
        var entityName = StringUtils.uncapitalize(splitQuery(getQueryString()).get(lazyKeyCodeParam));
        return String.format("/%s/%s/list/entity/%s/", path[1], path[2], entityName);
    }

    private String getQueryString() {
        // this situation can be, if we will get path from /v1/access path. It's in request body
        if (reserveQueryString != null) {
            return reserveQueryString;
        }

        var environment = injector.getInstance(DefaultServletEnvironment.class);
        if (!environment.request().isPresent()) {
            return null;
        }
        var request = environment.request().get();
        return request.getQueryString();
    }

    /**
     * Splits query from request and returns map of query params.
     * @param query that will be split.
     * @return map of query params
     * @throws UnsupportedEncodingException
     */
    private static Map<String, String> splitQuery(String query) throws UnsupportedEncodingException {
        Map<String, String> queryPairs = new LinkedHashMap<>();
        var pairs = query.split(QUERY_SPLITTER);
        for (var pair : pairs) {
            var idx = pair.indexOf(PAIR_CONCATENATOR);
            queryPairs.put(URLDecoder.decode(pair.substring(0, idx), UTF_8), URLDecoder.decode(pair.substring(idx + 1), UTF_8));
        }
        return queryPairs;
    }

    public void setReserveQueryString(String reserveQueryString) {
        this.reserveQueryString = reserveQueryString;
    }
}
