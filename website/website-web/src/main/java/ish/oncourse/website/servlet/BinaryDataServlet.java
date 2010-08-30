package ish.oncourse.website.servlet;

import ish.oncourse.model.BinaryInfo;
import ish.oncourse.services.binary.IBinaryDataService;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tapestry5.TapestryFilter;
import org.apache.tapestry5.internal.services.DefaultSessionPersistedObjectAnalyzer;
import org.apache.tapestry5.internal.services.RequestImpl;
import org.apache.tapestry5.internal.services.ResponseImpl;
import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.services.RequestGlobals;

public class BinaryDataServlet extends HttpServlet {

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		IBinaryDataService service = getService(IBinaryDataService.class);

		BinaryInfo binaryInfo = service.getBinaryInfo(BinaryInfo.ID_PK_COLUMN,
				req.getParameter("id"));

		resp.setContentType(binaryInfo.getMimeType());
		OutputStream out = resp.getOutputStream();
		

		byte[] content = service.getBinaryData(binaryInfo).getContent();
		out.write(content);

		out.close();
	}

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		RequestGlobals requestGlobals = getService(RequestGlobals.class);

		requestGlobals.storeServletRequestResponse(req, resp);

		requestGlobals.storeRequestResponse(new RequestImpl(req, req
				.getCharacterEncoding(),
				new DefaultSessionPersistedObjectAnalyzer()), new ResponseImpl(
				resp));

		super.service(req, resp);
	}

	private <T> T getService(Class<T> clazz) {
		Registry registry = (Registry) getServletContext().getAttribute(
				TapestryFilter.REGISTRY_CONTEXT_NAME);
		return registry.getService(clazz);
	}
}
