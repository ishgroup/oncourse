package ish.oncourse.asset.servlet;

import ish.oncourse.model.BinaryData;
import ish.oncourse.model.BinaryInfo;
import ish.oncourse.services.binary.IBinaryDataService;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

public class BinaryDataServlet extends ServiceAwareServlet {
	private static final String DIGIT_REGEXP = "\\d+";
	private static final String DOT_CHARACTER = ".";
	private static final String BINARY_INFO_ID_PARAMETER = "id";
	private static final long serialVersionUID = 1376738011903521168L;

	@Override
	public synchronized void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		IBinaryDataService service = getService(IBinaryDataService.class);
		final String idParameter = req.getParameter(BINARY_INFO_ID_PARAMETER);
		if (idParameter != null && idParameter.matches(DIGIT_REGEXP)) {
			BinaryInfo binaryInfo = service.getBinaryInfoById(idParameter);
			if (binaryInfo != null) {
				resp.setContentType(binaryInfo.getMimeType());
				final StringBuilder fileName = new StringBuilder(binaryInfo.getName());
				if (StringUtils.trimToNull(binaryInfo.getType()) != null) {
					fileName.append(DOT_CHARACTER).append(binaryInfo.getType());
				}
				resp.setHeader("Content-disposition", "filename=" + fileName.toString());
				OutputStream out = resp.getOutputStream();
				final BinaryData binaryData = binaryInfo.getBinaryData();
				if (binaryData != null) {
					byte[] content = binaryData.getContent();
					out.write(content);
				}
				out.close();
			}
		}
	}
}
