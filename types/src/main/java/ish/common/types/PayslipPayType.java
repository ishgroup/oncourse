package ish.common.types;

import ish.common.util.DisplayableExtendedEnumeration;
import ish.oncourse.API;

/**
 * Payslips can be either
 *
 * 1. payable to an employee as a wage (with tax and super and other calculations). These are typically exported
 * to some payroll system to complete calculations, submit government reports and process to their bank account
 *
 * 2. payable to a contractor. The contractor is usually responsible for issuing an invoice back to the college
 *
 */
@API
public enum PayslipPayType implements DisplayableExtendedEnumeration<Integer> {

    @API
    EMPLOYEE(1, "employee"),

    @API
    CONTRACTOR(2, "contractor");

    private final String displayName;
    private final int value;

    PayslipPayType(int value, String displayName) {
        this.value = value;
        this.displayName = displayName;
    }

    /**
     * @see ish.common.util.DisplayableExtendedEnumeration#getDatabaseValue()
     */
    @Override
    public Integer getDatabaseValue() {
        return this.value;
    }

    /**
     * @see ish.common.util.DisplayableExtendedEnumeration#getDisplayName()
     */
    @Override
    public String getDisplayName() {
        return this.displayName;
    }

    /**
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return getDisplayName();
    }
}
