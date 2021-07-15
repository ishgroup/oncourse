/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.entity.mixins

import ish.oncourse.server.cayenne.Site
import ish.oncourse.server.cayenne.WaitingList

class WaitingListMixin {


    /**
     *
     * @return All preferred site names concatenated and seperated with a comma
     */
    @Deprecated
	static getSiteNames(WaitingList self) {
        StringBuilder sitesNameConcatenated = new StringBuilder();
        for (Site site : self.getSites()) {
            if (sitesNameConcatenated.length() > 0) {
                sitesNameConcatenated.append(",");
            }
            sitesNameConcatenated.append(site.getName());
        }
        return sitesNameConcatenated.toString();
	}

}
