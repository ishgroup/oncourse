package ish.common.types;

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
public enum PayslipPayType {

    @API
    EMPLOYEE,

    @API
    CONTRACTOR
}
