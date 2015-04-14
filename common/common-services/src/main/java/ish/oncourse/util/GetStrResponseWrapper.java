package ish.oncourse.util;

import org.apache.tapestry5.services.Response;
import org.apache.tapestry5.util.ResponseWrapper;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

public class GetStrResponseWrapper extends ResponseWrapper {

	public GetStrResponseWrapper(Response response) {
		super(response);
	}

	private StringBuffer buffer = new StringBuffer();

	public PrintWriter getPrintWriter(String contentType) throws IOException {

		return new PrintWriter(new StringWriter()) {

			/*
			 * (non-Javadoc)
			 * 
			 * @see java.io.PrintWriter#print(java.lang.String)
			 */
			@Override
			public void print(String s) {
				buffer.append(s);
			}

		};
	}

	public String getResponseString() {
		return buffer.toString();
	}
}
