package ish.oncourse.enrol.checkout.payment;

import ish.math.Money;
import ish.oncourse.model.Contact;
import ish.oncourse.model.PaymentIn;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.services.Request;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static ish.oncourse.enrol.checkout.payment.PaymentEditorParser.Field;
import static ish.oncourse.util.MessagesNamingConvention.MESSAGE_KEY_TEMPLATE;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class PaymentEditorParserTest {

    private Request request;
    private List<Contact> contacts;
    private Messages messages;
    private PaymentIn paymentIn;

    @Before
    public void before() {
        request = mock(Request.class);

        Contact contact = mock(Contact.class);
        when(contact.getId()).thenReturn(1L);
        contacts = new ArrayList<>();
        contacts.add(contact);

        messages = mock(Messages.class);
        initMessages();
        paymentIn = mock(PaymentIn.class);
    }

    private void initMessages() {

        Field[] values = Field.values();
        for (Field value : values) {
            String key = String.format(MESSAGE_KEY_TEMPLATE, value.name());
            when(messages.format(key)).thenReturn(key);
        }
    }

    @Test
    public void testZeroPayment() {
        when(paymentIn.getAmount()).thenReturn(new Money("0.0"));
        when(paymentIn.isZeroPayment()).thenCallRealMethod();

        when(request.getParameter(Field.contact.name())).thenReturn("1");
        PaymentEditorParser paymentEditorParser = getPaymentEditorParser();
        paymentEditorParser.parse();
        assertFalse(paymentEditorParser.getErrors().isEmpty());
        assertEquals(1, paymentEditorParser.getErrors().size());
        String key = String.format(MESSAGE_KEY_TEMPLATE, Field.userAgreed.name());
        assertEquals(key, paymentEditorParser.getErrors().get(Field.userAgreed.name()));

        paymentEditorParser = getPaymentEditorParser();
        when(request.getParameter(Field.userAgreed.name())).thenReturn("on");
        paymentEditorParser.parse();
        assertTrue(paymentEditorParser.getErrors().isEmpty());

		//test when corporate pass web enrolment available only and corporate pass entered
		paymentEditorParser = getPaymentEditorParser();
		paymentEditorParser.setCorporatePass(true);
		paymentEditorParser.parse();
		assertTrue(paymentEditorParser.getErrors().isEmpty());
    }

    @Test
    public void testPayment() {
        when(paymentIn.getAmount()).thenReturn(new Money("100"));
        //test empty form
        PaymentEditorParser paymentEditorParser = getPaymentEditorParser();
        paymentEditorParser.parse();
        assertFalse(paymentEditorParser.getErrors().isEmpty());
        assertEquals(5, paymentEditorParser.getErrors().size());
        Field[] values = Field.noZeroPaymentFields();
        for (Field value : values) {
            if (value == Field.expiryYear)
                value = Field.expiryMonth;
            String key = String.format(MESSAGE_KEY_TEMPLATE, value.name());
            assertEquals(key,paymentEditorParser.getErrors().get(value.name()));
        }

        //test set creditCardName
        when(request.getParameter(Field.creditCardName.name())).thenReturn("creditCardName");
        paymentEditorParser = getPaymentEditorParser();
        paymentEditorParser.parse();
        verify(paymentIn, times(1)).setCreditCardName("creditCardName");
        assertEquals(4, paymentEditorParser.getErrors().size());

        //test set creditCardNumber
        when(request.getParameter(Field.creditCardNumber.name())).thenReturn("99999999999999999999");
        when(paymentIn.validateCCNumber()).thenReturn(null);
        paymentEditorParser = getPaymentEditorParser();
        paymentEditorParser.parse();
        verify(paymentIn, times(1)).setCreditCardNumber("99999999999999999999");
        assertEquals(3, paymentEditorParser.getErrors().size());

        //test set creditCardCVV
        when(request.getParameter(Field.creditCardCVV.name())).thenReturn("1111");
        when(paymentIn.validateCVV()).thenReturn(true);
        paymentEditorParser = getPaymentEditorParser();
        paymentEditorParser.parse();
        verify(paymentIn, times(1)).setCreditCardCVV("1111");
        assertEquals(2, paymentEditorParser.getErrors().size());

        //test set expiryMonth/expiryYear
        when(request.getParameter(Field.expiryMonth.name())).thenReturn("01");
        when(request.getParameter(Field.expiryYear.name())).thenReturn("2013");
        when(paymentIn.validateCCExpiry()).thenReturn(true);
        paymentEditorParser = getPaymentEditorParser();
        paymentEditorParser.parse();
        verify(paymentIn, times(1)).setCreditCardExpiry("01/2013");
        assertEquals(1, paymentEditorParser.getErrors().size());

        //test set expiryMonth/expiryYear
        when(request.getParameter(Field.userAgreed.name())).thenReturn("on");
        paymentEditorParser = getPaymentEditorParser();
        paymentEditorParser.parse();
        assertTrue(paymentEditorParser.getErrors().isEmpty());
    }

    private PaymentEditorParser getPaymentEditorParser() {
        PaymentEditorParser paymentEditorParser = new PaymentEditorParser();
        paymentEditorParser.setContacts(contacts);
        paymentEditorParser.setRequest(request);
        paymentEditorParser.setPaymentIn(paymentIn);
        paymentEditorParser.setContacts(contacts);
        paymentEditorParser.setMessages(messages);
		paymentEditorParser.setCorporatePass(false);
        return paymentEditorParser;
    }
}
