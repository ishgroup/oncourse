package ish.oncourse.webservices.soap.v6;

import ish.oncourse.webservices.soap.ATransportTest;
import ish.oncourse.webservices.soap.v15.AbstractTransportTest;
import ish.oncourse.webservices.v6.stubs.reference.ReferenceResult;
import ish.oncourse.webservices.v6.stubs.reference.ReferenceStub;
import org.apache.cxf.annotations.EndpointProperty;

import javax.jws.WebParam;
import javax.jws.WebService;

@WebService(endpointInterface = "ish.oncourse.webservices.soap.v6.ReferencePortType",
		serviceName = "ReferenceService",
		portName = "ReferencePort", targetNamespace = "http://ref.v6.soap.webservices.oncourse.ish/")
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
			referenceResult.getCountryOrLanguageOrModule().addAll(ATransportTest.createStubsBy(AbstractTransportTest.getReferenceStubBeanNames(), AbstractTransportTest.PACKAGE_NAME_REFERENCE_STUBS, ReferenceStub.class));
			return referenceResult;
		} catch (Throwable e) {
			throw new IllegalArgumentException(e);
		}
	}
}
