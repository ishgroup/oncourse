package ish.oncourse.webservices.soap.v4;

import ish.oncourse.webservices.soap.AbstractTransportTest;
import ish.oncourse.webservices.v4.stubs.reference.ReferenceResult;
import ish.oncourse.webservices.v4.stubs.reference.ReferenceStub;
import org.apache.cxf.annotations.EndpointProperty;

import javax.jws.WebParam;
import javax.jws.WebService;

@WebService(endpointInterface = "ish.oncourse.webservices.soap.v4.ReferencePortType",
		serviceName = "ReferenceService",
		portName = "ReferencePort", targetNamespace = "http://ref.v4.soap.webservices.oncourse.ish/")
@EndpointProperty(key = "soap.no.validate.parts", value = "true")
public class TestReferencePortTypeImpl implements ReferencePortType {
	@Override
	public long getMaximumVersion() {
		return Long.MAX_VALUE;
	}

	@Override
	public ReferenceResult getRecords(@WebParam(partName = "ishVersion", name = "ishVersion", targetNamespace = "") long l) {
		ReferenceResult referenceResult = new ReferenceResult();

		try {
			referenceResult.getCountryOrLanguageOrModule().addAll(AbstractTransportTest.createStubsBy(AbstractTransportTest.getReferenceStubBeanNames(), AbstractTransportTest.PACKAGE_NAME_REFERENCE_STUBS, ReferenceStub.class));
			return referenceResult;
		} catch (Throwable e) {
			throw new IllegalArgumentException(e);
		}
	}
}
