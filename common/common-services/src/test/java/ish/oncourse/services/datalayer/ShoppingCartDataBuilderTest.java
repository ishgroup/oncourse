package ish.oncourse.services.datalayer;

import ish.math.Money;
import ish.oncourse.model.*;
import ish.oncourse.services.paymentexpress.TestPaymentGatewayService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.tag.ITagService;
import ish.oncourse.util.HTMLUtils;
import org.apache.cayenne.ObjectContext;
import org.apache.tapestry5.services.Request;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * User: artem
 * Date: 9/18/13
 * Time: 2:42 PM
 */
public class ShoppingCartDataBuilderTest {


    private Request request;
    private ITagService tagService;
    private List<Enrolment> enrolments;


    /**
     * Instance to test.
     */
    private ShoppingCartDataBuilder shoppingCartDataBuilder = new ShoppingCartDataBuilder();




    @Before
    public void initMethod() {

        request =mock(Request.class);
        tagService=mock(ITagService.class);

        Tag tag=mock(Tag.class);
        ArrayList<Tag> tags=new  ArrayList<Tag>();
        tags.add(tag);
        when(tag.getName()).thenReturn("17506");

        Enrolment enrolment1=mock(Enrolment.class);
        Enrolment enrolment2=mock(Enrolment.class);

        enrolments=new ArrayList<Enrolment>();
        enrolments.add(enrolment1);
        enrolments.add(enrolment2);

        InvoiceLine invoiceLine=mock(InvoiceLine.class);

        ArrayList<InvoiceLine> invoiceLines=new ArrayList<InvoiceLine>();
        invoiceLines.add(invoiceLine);

        when(enrolment1.getInvoiceLines()).thenReturn(invoiceLines);
        when(enrolment2.getInvoiceLines()).thenReturn(invoiceLines);

        when(invoiceLine.getFinalPriceToPayExTax()).thenReturn(new Money(80, 00));
        when(invoiceLine.getFinalPriceToPayIncTax()).thenReturn(new Money(88,00));
        when(invoiceLine.getTotalTax()).thenReturn(new Money(8, 00));
        when(invoiceLine.getDiscountTotalIncTax()).thenReturn(new Money(20, 00));

        CourseClass courseClass=mock(CourseClass.class);
        when(enrolment1.getCourseClass()).thenReturn(courseClass);
        when(enrolment2.getCourseClass()).thenReturn(courseClass);

        Course course=mock(Course.class);
        when(courseClass.getCourse()).thenReturn(course);
        when(courseClass.getCourse().getId()).thenReturn(125l);

        when(tagService.getTagsForEntity(Course.class.getSimpleName(),
                courseClass.getCourse().getId())).thenReturn(tags);

        when(courseClass.getCourse().getCode()).thenReturn("17506-C1");
        when(courseClass.getCourse().getName()).thenReturn("17506-Course1");
        when(HTMLUtils.getCanonicalLinkPathFor(courseClass.getCourse(), request)).thenReturn("/course/17506-Course1");
        when(courseClass.getUniqueIdentifier()).thenReturn("IJC978dxJ97");

    }



    @Test
    public void test(){
        shoppingCartDataBuilder.setRequest(request);
        shoppingCartDataBuilder.setTagService(tagService);
        shoppingCartDataBuilder.setEnrolments(enrolments);

        shoppingCartDataBuilder.build();

        assertNotNull(shoppingCartDataBuilder.getCart());

    }


}
