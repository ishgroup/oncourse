/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.services.content.cache;

/**
 * User: akoiro
 * Date: 8/09/2016
 */
public interface IContentKeyFactory<T, K> {
	K createKey(String tapestryElement, T keyObject);
}
