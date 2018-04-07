/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.tapestry;

import org.apache.tapestry5.services.pageload.ComponentRequestSelectorAnalyzer;
import org.apache.tapestry5.services.pageload.ComponentResourceSelector;

import java.util.Map;

/**
 * This interface is a replacement fro standard tapestry ComponentRequestSelectorAnalyzer and
 * need to have the one way in building ComponentResourceSelector.
 * Tapestry uses ComponentResourceSelector instance as the key for caching tapestry elements
 *
 *  <code>buildSelectorForRequest(Map<String, Object> textileParams);</code>
 *  are used to build correct selector for Textile elements
 */
public interface IWillowComponentRequestSelectorAnalyzer extends ComponentRequestSelectorAnalyzer {
	ComponentResourceSelector buildSelectorForRequest(Map<String, Object> textileParams);
}
