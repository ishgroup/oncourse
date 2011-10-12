package ish.oncourse.asset.servlet;

import ish.oncourse.model.BinaryData;
import ish.oncourse.model.BinaryInfo;
import ish.oncourse.services.binary.IBinaryDataService;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BinaryDataServlet extends ServiceAwareServlet {

	@Override
	public synchronized void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

		IBinaryDataService service = getService(IBinaryDataService.class);

		BinaryInfo binaryInfo = service.getBinaryInfoById(req.getParameter("id"));

		if (binaryInfo != null) {
			String mimeType = binaryInfo.getMimeType();
			resp.setContentType(mimeType);
			String filename = binaryInfo.getName();
			if (!binaryInfo.getType().isEmpty()) {
				filename += "." + binaryInfo.getType();
			}
			resp.setHeader("Content-disposition", "filename=" + filename);

			OutputStream out = resp.getOutputStream();

			BinaryData binaryData = binaryInfo.getBinaryData();
			if (binaryData != null) {
				byte[] content = binaryData.getContent();
				out.write(content);
			}
			out.close();
		}
	}
}
