package ish.oncourse.website.servlet;

import ish.oncourse.model.BinaryInfo;
import ish.oncourse.services.ServiceModule;
import ish.oncourse.services.binary.IBinaryDataService;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.ioc.RegistryBuilder;
import org.apache.tapestry5.services.RequestGlobals;
import org.apache.tapestry5.services.TapestryModule;

public class ImageServlet extends HttpServlet {
	private Registry registry;

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		// Get the absolute path of the image
		IBinaryDataService service = registry
				.getService(IBinaryDataService.class);
		BinaryInfo binaryInfo = service.getBinaryInfo(BinaryInfo.ID_PROPERTY,
				req.getParameter("id"));

		// Set content type
		resp.setContentType(binaryInfo.getMimeType());
		OutputStream out = resp.getOutputStream();

		// TODO get the content from BinaryData
		byte[] content = new byte[1024];
		out.write(content);

		out.close();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.GenericServlet#init()
	 */
	@Override
	public void init() throws ServletException {
		RegistryBuilder builder = new RegistryBuilder();

		builder.add(ServiceModule.class, TapestryModule.class);
		registry = builder.build();
		registry.performRegistryStartup();
		super.init();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.http.HttpServlet#service(javax.servlet.http.HttpServletRequest
	 * , javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// don't work without adding to builder TapestryModule.class, my be
		// useless
		RequestGlobals requestGlobals = registry
				.getService(RequestGlobals.class);
		requestGlobals.storeServletRequestResponse(req, resp);
		super.service(req, resp);
	}

}
