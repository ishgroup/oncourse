// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package ish.oncourse.ui.services.pageload.template;

import ish.oncourse.ui.services.pageload.PageLoadService;
import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.internal.event.InvalidationEventHubImpl;
import org.apache.tapestry5.internal.parser.ComponentTemplate;
import org.apache.tapestry5.internal.services.ComponentTemplateSource;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.model.ComponentModel;
import org.apache.tapestry5.services.InvalidationEventHub;
import org.apache.tapestry5.services.UpdateListener;
import org.apache.tapestry5.services.pageload.ComponentResourceSelector;

public final class ComponentTemplateSourceImpl extends InvalidationEventHubImpl implements ComponentTemplateSource,
		UpdateListener {

	private PageLoadService pageLoadService;

	public ComponentTemplateSourceImpl(@Inject @Symbol(SymbolConstants.PRODUCTION_MODE)
											   boolean productionMode, @Inject PageLoadService pageLoadService) {
		super(productionMode);
		this.pageLoadService = pageLoadService;
	}
	
	public ComponentTemplate getTemplate(ComponentModel componentModel, ComponentResourceSelector selector) {
		return pageLoadService.getTemplate(componentModel, selector);
	}

	public void checkForUpdates() {
		if (this.pageLoadService.containsChanges()) {
			invalidate();
		}
	}

	private void invalidate() {
		this.pageLoadService.clean();
		fireInvalidationEvent();
	}

	public InvalidationEventHub getInvalidationEventHub() {
		return this;
	}
}
