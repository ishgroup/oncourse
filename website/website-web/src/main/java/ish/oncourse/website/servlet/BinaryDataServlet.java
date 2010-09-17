/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.website.servlet;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ish.oncourse.model.BinaryInfo;
import ish.oncourse.services.binary.IBinaryDataService;

public class BinaryDataServlet extends ServiceAwareServlet {

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

		IBinaryDataService service = getService(IBinaryDataService.class);

		BinaryInfo binaryInfo = service.getBinaryInfo(BinaryInfo.REFERENCE_NUMBER_PROPERTY, req.getParameter("id"));

		resp.setContentType(binaryInfo.getMimeType());
		OutputStream out = resp.getOutputStream();

		byte[] content = service.getBinaryData(binaryInfo).getContent();
		out.write(content);

		out.close();
	}
}
