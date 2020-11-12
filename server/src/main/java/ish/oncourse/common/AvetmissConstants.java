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
package ish.oncourse.common;

import ish.util.Maps;

import java.util.Map;

public class AvetmissConstants {

	// training organisation type identifier p117
	// Secondary school
	// 21 School - Government
	// 23 School - Australian Technical College
	// 25 School - Catholic
	// 27 School - Independent
	// TAFE
	// 31 Technical and Further Education institute
	// University
	// 41 University - Government
	// 43 University - Non-Government Catholic
	// 45 University - Non-Government Independent
	// Enterprise
	// 51 Enterprise - Government
	// 53 Enterprise - Non-government
	// Community-based adult education
	// 61 Community-based adult education provider
	// Other training provider
	// 91 Education/training business or centre: Privately operated registered training organisation
	// 93 Professional association
	// 95 Industry association
	// 97 Equipment and/or product manufacturer or supplier
	// 99 Other - not elsewhere classified
	public static final Map<String, String> TrainingOrg_Types = Maps.asLinkedMap(new String[]{
			"School - Government",
			"School - Australian Technical College",
			"School - Catholic",
			"School - Independent",
			"TAFE",
			"University - Government",
			"University - Non-Government Catholic",
			"University - Non-Government Independent",
			"Enterprise - Government",
			"Enterprise - Non-government",
			"Community-based adult education provider",
			"Education/training business or centre",
			"Professional association",
			"Industry association",
			"Equipment and/or product manufacturer or supplier",
			"Other - not elsewhere classified"},
			new String[]{"21", "23", "25", "27", "31", "41", "43", "45", "51", "53", "61", "91", "93", "95", "97", "99"});
}
