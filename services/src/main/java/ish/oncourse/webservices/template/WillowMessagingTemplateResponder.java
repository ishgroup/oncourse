//
// WillowMessagingTemplateResponder.java
// WillowMessaging
//
// Created by Lachlan Deck on 10/03/06.
// Copyright (c) 2006 ISH Group Pty Ltd. All rights reserved.
//
package ish.oncourse.webservices.template;

import java.util.Map;
import java.util.Set;

/**
 * An instance of <code>WillowMessagingTemplateResponder</code> ought to have
 * knowledge of
 * <ul>
 * <li>one template, particularly its string representation.</li>
 * <li>the possible keys/keypaths available for the template, and how to
 * discover the values that ought to replace them.
 * </ul>
 * Note: corresponding values for a key or keypath ought to be instances of
 * either <code>WillowMessagingTemplateResponder</code> or
 * <code>java.lang.Object</code>.
 * 
 * @author Lachlan Deck
 */
public interface WillowMessagingTemplateResponder
{
	
	/**
	 * The message portion of the template this responder handles.
	 * 
	 * @return the string representation of the template's message.
	 */
	public String templateMessage();
	
	/**
	 * The subject portion of the template this responder handles.
	 * 
	 * @return the string representation of the template's subject.
	 */
	public String templateSubject();
	
	/**
	 * Responds with values for the given keys. Each value should be either an
	 * <code>Object</code> or <code>WillowMessagingTemplateResponder</code>.
	 * 
	 * @return values mapped for the given keys.
	 */
	public Map< String, Object > templateValuesForKeys( Set< String > keys );
	
}
