/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.server.api.checkout

import groovy.transform.CompileStatic
import ish.DatabaseSetup
import ish.TestWithDatabase
import ish.oncourse.server.CayenneService
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.api.dao.FundingSourceDao
import ish.oncourse.server.api.dao.ModuleDao
import ish.oncourse.server.api.service.*
import ish.oncourse.server.api.v1.model.*
import ish.oncourse.server.cayenne.SystemUser
import ish.oncourse.server.users.SystemUserService
import org.apache.cayenne.query.SelectById
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito

/**
 * Course -> Course and Product -> Course relations which make the related course mandatory in quick enrol,
 * see checkoutRequiredRelatedCoursesTest.xml for the data.
 */
@CompileStatic
@DatabaseSetup(value = "ish/oncourse/server/api/checkout/checkoutRequiredRelatedCoursesTest.xml")
class CheckoutApiTest extends TestWithDatabase {

    private static final long DIPLOMA_CLASS = 1L
    private static final long CERT_III_CLASS = 2L
    private static final long OPTIONAL_CLASS = 3L
    private static final long ARTICLE = 100L

    private static final long WITH_HISTORY = 3L
    private static final long NO_HISTORY = 4L
    private static final long QUEUED_HISTORY = 5L
    private static final long NOT_STUDENT = 6L

    @Test
    void testMandatoryCourseMissing() {
        List<String> errors = relationErrors(checkout(NO_HISTORY, [DIPLOMA_CLASS], []))
        Assertions.assertEquals(1, errors.size())
        Assertions.assertTrue(errors[0].contains('CERT3 Certificate III'), errors[0])
        Assertions.assertFalse(errors[0].contains('OPT'), 'add-and-allow-removal relation must not be required')
    }

    @Test
    void testMandatoryCourseInSameCheckout() {
        Assertions.assertEquals([], relationErrors(checkout(NO_HISTORY, [DIPLOMA_CLASS, CERT_III_CLASS], [])))
    }

    @Test
    void testMandatoryCourseInHistory() {
        Assertions.assertEquals([], relationErrors(checkout(WITH_HISTORY, [DIPLOMA_CLASS], [])))
    }

    @Test
    void testQueuedEnrolmentIsNotHistory() {
        Assertions.assertEquals(1, relationErrors(checkout(QUEUED_HISTORY, [DIPLOMA_CLASS], [])).size())
    }

    @Test
    void testContactWithoutStudent() {
        Assertions.assertEquals(1, relationErrors(checkout(NOT_STUDENT, [DIPLOMA_CLASS], [])).size())
    }

    @Test
    void testRelationDirection() {
        // Diploma -> Cert III: enrolling in Cert III alone does not require Diploma
        Assertions.assertEquals([], relationErrors(checkout(NO_HISTORY, [CERT_III_CLASS], [])))
        Assertions.assertEquals([], relationErrors(checkout(NO_HISTORY, [OPTIONAL_CLASS], [])))
    }

    @Test
    void testProductRelation() {
        // relation type does not consider history: the course must be in the same checkout
        List<String> errors = relationErrors(checkout(WITH_HISTORY, [], [ARTICLE]))
        Assertions.assertEquals(1, errors.size())
        Assertions.assertTrue(errors[0].contains('Kit'), errors[0])

        Assertions.assertEquals([], relationErrors(checkout(WITH_HISTORY, [CERT_III_CLASS], [ARTICLE])))
    }

    @Test
    void testProductRelationForContactWithoutStudent() {
        // willow adds Product -> Course relations for students only
        Assertions.assertEquals([], relationErrors(checkout(NOT_STUDENT, [], [ARTICLE])))
    }

    private Checkout checkout(Long contactId, List<Long> classIds, List<Long> articleIds) {
        CheckoutModelDTO model = new CheckoutModelDTO()
        model.payerId = contactId
        model.sendInvoice = false
        model.contactNodes = [new ContactNodeDTO().with { node ->
            node.contactId = contactId
            node.sendConfirmation = false
            node.enrolments = classIds.collect { Long id -> new CheckoutEnrolmentDTO(classId: id) }
            node.products = articleIds.collect { Long id -> new CheckoutArticleDTO(productId: id, quantity: BigDecimal.ONE) }
            node
        }]
        return controller().createCheckout(model)
    }

    private CheckoutController controller() {
        SystemUser user = SelectById.query(SystemUser, 1L).selectOne(cayenneContext)
        SystemUserService systemUserService = Mockito.mock(SystemUserService)
        Mockito.when(systemUserService.currentUser).thenReturn(user)

        new CheckoutController(injector.getInstance(ICayenneService) as CayenneService,
                systemUserService,
                injector.getInstance(ContactApiService),
                injector.getInstance(InvoiceApiService),
                injector.getInstance(CourseClassApiService),
                injector.getInstance(MembershipProductApiService),
                injector.getInstance(VoucherProductApiService),
                injector.getInstance(ArticleProductApiService),
                injector.getInstance(FundingSourceDao),
                injector.getInstance(ModuleDao))
    }

    private static List<String> relationErrors(Checkout checkout) {
        checkout.errors*.error.findAll { String error -> error?.contains('without an enrolment in') }
    }
}
