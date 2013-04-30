package ish.oncourse.utils;

import ish.oncourse.model.*;
import org.apache.cayenne.ObjectContext;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


/**
 * @author akoyro
 */
public class MembershipDiscountHelperTest {
    private ObjectContext context;

    @Before
    public void setUp() {

        context = mock(ObjectContext.class);
    }

    private Contact createContact()
    {
        return createContact(null);
    }

    private Contact createContact(MembershipProduct product)
    {
        Contact contact = mock(Contact.class);
        when(contact.getObjectContext()).thenReturn(context);

        if (product != null)
        {
            Membership membership = mock(Membership.class);
            when(membership.getProduct()).thenReturn(product);
            when(membership.getContact()).thenReturn(contact);
            when(contact.getMemberships()).thenReturn(Collections.singletonList(membership));
        }
        return contact;
    }

    private Discount createDiscountWithMembership()
    {
       return createDiscountWithMembership(null,true);
    }

    private Discount createDiscountWithMembership(List<ContactRelationType> relationTypes, boolean applyToMemberOnly)
    {
        Discount discount = mock(Discount.class);
        MembershipProduct product = mock(MembershipProduct.class);
        DiscountMembership discountMembership1 = mock(DiscountMembership.class);
        when(discountMembership1.getApplyToMemberOnly()).thenReturn(applyToMemberOnly);
        when(discountMembership1.getDiscount()).thenReturn(discount);
        when(discountMembership1.getMembershipProduct()).thenReturn(product);
        when(discount.getDiscountMembershipProducts()).thenReturn(Collections.singletonList(discountMembership1));
        if (relationTypes != null)
        {
            ArrayList<DiscountMembershipRelationType> list = new ArrayList<>();
            for (ContactRelationType relationType : relationTypes) {
                DiscountMembershipRelationType dmrt = mock(DiscountMembershipRelationType.class);
                when(dmrt.getContactRelationType()).thenReturn(relationType);
                when(dmrt.getDiscountMembership()).thenReturn(discountMembership1);
                list.add(dmrt);
            }
            when(discountMembership1.getDiscountMembershipRelationTypes()).thenReturn(list);
        }
        return discount;
    }

    @Test
    public void test1()
    {
        //discount without memebership and contact without memebership.
        Discount discount = mock(Discount.class);
        Contact contact = mock(Contact.class);
        MembershipDiscountHelper membershipDiscountHelper = new MembershipDiscountHelper();
        membershipDiscountHelper.setDiscount(discount);
        membershipDiscountHelper.setContact(contact);
        assertNotNull(membershipDiscountHelper.getContact());
        assertNotNull(membershipDiscountHelper.getDiscount());
        assertTrue(membershipDiscountHelper.isEligibile());
    }

    @Test
    public void test2()
    {
        //discount with memebership and contact without memebership.
        Discount discount =createDiscountWithMembership();
        Contact contact = mock(Contact.class);
        MembershipDiscountHelper membershipDiscountHelper = new MembershipDiscountHelper();
        membershipDiscountHelper.setDiscount(discount);
        membershipDiscountHelper.setContact(contact);
        assertFalse(membershipDiscountHelper.isEligibile());
    }


    @Test
    public void test3()
    {
        //discount with memebership and contact with memebership.
        Discount discount =createDiscountWithMembership();
        Contact contact = createContact(discount.getDiscountMembershipProducts().get(0).getMembershipProduct());

        MembershipDiscountHelper membershipDiscountHelper = new MembershipDiscountHelper();
        membershipDiscountHelper.setDiscount(discount);
        membershipDiscountHelper.setContact(contact);
        assertTrue(membershipDiscountHelper.isEligibile());

        //discount with memebership and contact with other memebership.
        Discount discount1 =createDiscountWithMembership();
        membershipDiscountHelper = new MembershipDiscountHelper();
        membershipDiscountHelper.setDiscount(discount1);
        membershipDiscountHelper.setContact(contact);
        assertFalse(membershipDiscountHelper.isEligibile());
    }

    @Test
    public void test4()
    {
        //discount with memebership and contact related to contactTo with memebership.
        Contact contact = createContact();

        ContactRelationType contactRelationType = mock(ContactRelationType.class);
        Discount discount = createDiscountWithMembership(Collections.singletonList(contactRelationType), false);

        Contact contactTo = createContact(discount.getDiscountMembershipProducts().get(0).getMembershipProduct());
        ContactRelation contactRelation = mock(ContactRelation.class);
        when(contactRelation.getRelationType()).thenReturn(contactRelationType);
        when(contactRelation.getToContact()).thenReturn(contactTo);
        when(contact.getToContacts()).thenReturn(Collections.singletonList(contactRelation));

        MembershipDiscountHelper membershipDiscountHelper = new MembershipDiscountHelper();
        membershipDiscountHelper.setDiscount(discount);
        membershipDiscountHelper.setContact(contact);
        assertTrue(membershipDiscountHelper.isEligibile());
    }

    @Test
    public void test5()
    {
        //discount with memebership and contact related to fromContact with memebership.
        Contact contact = createContact();

        ContactRelationType contactRelationType = mock(ContactRelationType.class);
        Discount discount = createDiscountWithMembership(Collections.singletonList(contactRelationType), false);

        Contact fromContact = createContact(discount.getDiscountMembershipProducts().get(0).getMembershipProduct());
        ContactRelation contactRelation = mock(ContactRelation.class);
        when(contactRelation.getRelationType()).thenReturn(contactRelationType);
        when(contactRelation.getFromContact()).thenReturn(fromContact);
        when(contact.getFromContacts()).thenReturn(Collections.singletonList(contactRelation));

        MembershipDiscountHelper membershipDiscountHelper = new MembershipDiscountHelper();
        membershipDiscountHelper.setDiscount(discount);
        membershipDiscountHelper.setContact(contact);
        assertTrue(membershipDiscountHelper.isEligibile());

        //contact without relations
        contact = createContact();
        membershipDiscountHelper = new MembershipDiscountHelper();
        membershipDiscountHelper.setDiscount(discount);
        membershipDiscountHelper.setContact(contact);
        assertFalse(membershipDiscountHelper.isEligibile());
    }

}
