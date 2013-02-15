package ish.oncourse.webservices.soap;

import ish.oncourse.webservices.v4.stubs.reference.ReferenceStub;
import ish.oncourse.webservices.v4.stubs.replication.ReplicationStub;
import ish.oncourse.webservices.v4.stubs.replication.TransactionGroup;
import org.apache.cxf.common.util.StringUtils;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.headers.Header;
import org.apache.cxf.jaxb.JAXBDataBinding;
import org.apache.cxf.transport.common.gzip.GZIPInInterceptor;
import org.apache.cxf.transport.common.gzip.GZIPOutInterceptor;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.SimpleBeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.TypeFilter;

import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

public abstract class AbstractTransportTest {

	public static final GZIPInInterceptor IN = new GZIPInInterceptor();
	public static final GZIPOutInterceptor OUT = new GZIPOutInterceptor();

	public static final long DEFAULT_TIMEOUT = 1000l * 60 * 5;
	public static final long PAYMENT_SERVICE_TIMEOUT = 1000l * 60 * 25;
	public static  TestServer server;

	public static final String PACKAGE_NAME_REPLICATION_STUBS = "ish.oncourse.webservices.v4.stubs.replication";
	public static final String PACKAGE_NAME_REFERENCE_STUBS = "ish.oncourse.webservices.v4.stubs.reference";


	@BeforeClass
	public static void before() throws Exception {
		/* please note, in actuality, for multiple tests you will have
			 to ensure a single version of the server is running only.

			 For each test case, it will invoke start and will give an error.
			 This is simplified for Blog consumption here only. */
		server = new TestServer();
		server.start();
	}

	@AfterClass
	public static void after() throws Exception {
		server.stop();
	}


	public void initPortType(BindingProvider bindingProvider, String url) throws JAXBException {
		bindingProvider.getRequestContext().put(BindingProvider.SESSION_MAINTAIN_PROPERTY, true);
		bindingProvider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
				String.format("%s%s", server.getServerUrl(),url));

		Client client = ClientProxy.getClient(bindingProvider);

		client.getInInterceptors().add(IN);
		client.getOutInterceptors().add(OUT);
		client.getOutFaultInterceptors().add(OUT);

		HTTPConduit conduit = (HTTPConduit) client.getConduit();

		HTTPClientPolicy httpClientPolicy = new HTTPClientPolicy();
		httpClientPolicy.setAllowChunking(false);
		httpClientPolicy.setConnectionTimeout(DEFAULT_TIMEOUT);
		httpClientPolicy.setReceiveTimeout(DEFAULT_TIMEOUT);

		conduit.setClient(httpClientPolicy);

		List<Header> headers = new ArrayList<Header>();
		headers.add(new Header(new QName("SecurityCode"), "securityCode", new JAXBDataBinding(String.class)));
		headers.add(new Header(new QName("AngelVersion"), "angelVersion", new JAXBDataBinding(String.class)));
		headers.add(new Header(new QName("CommunicationKey"), Long.MAX_VALUE, new JAXBDataBinding(String.class)));
		bindingProvider.getRequestContext().put(Header.HEADER_LIST, headers);
	}

	public static <T> boolean containsStubBeanName(String stubBeanName, List<T> stubs)
	{
		for (T stub : stubs) {
			if (stub.getClass().getSimpleName().equals(StringUtils.capitalize(stubBeanName)))
			{
				return true;
			}
		}
		return false;
	}

	private static List<String> getStubClassNamesBy(String packageName, Class filterClass)
	{
		BeanDefinitionRegistry bdr = new SimpleBeanDefinitionRegistry();
		ClassPathBeanDefinitionScanner s = new ClassPathBeanDefinitionScanner(bdr);

		TypeFilter tf = new AssignableTypeFilter(filterClass);
		s.addIncludeFilter(tf);
		s.scan(packageName);

		String[] beanNames = bdr.getBeanDefinitionNames();

		ArrayList<String> result = new ArrayList<String>();

		for (String beanName : beanNames) {

			String className = org.apache.commons.lang.StringUtils.capitalize(beanName);
			if (className.endsWith("Stub") && !className.equals("HollowStub")) {
				result.add(className);
			}
		}
		return result;
	}

	public static List<String> getReplicationStubBeanNames() {
		return getStubClassNamesBy(PACKAGE_NAME_REPLICATION_STUBS, ReplicationStub.class);
	}

	public static List<String> getReferenceStubBeanNames() {
		return getStubClassNamesBy(PACKAGE_NAME_REFERENCE_STUBS, ReferenceStub.class);
	}



	public static TransactionGroup createTransactionGroupWithAllStubs() throws Throwable {
		TransactionGroup transactionGroup = new TransactionGroup();
		List<String> stubClassNames = AbstractTransportTest.getReplicationStubBeanNames();

		ArrayList<ReplicationStub> stubs = createStubsBy(stubClassNames,PACKAGE_NAME_REPLICATION_STUBS, ReplicationStub.class);
		transactionGroup.getAttendanceOrBinaryDataOrBinaryInfo().addAll(stubs);
		return transactionGroup;
	}

	public static <T> ArrayList<T> createStubsBy(List<String> stubClassNames,String packageName,Class<T> parentStubClass) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
		ArrayList<T> stubs = new ArrayList<T>();
		for (String stubClassName : stubClassNames) {
			Class aClass = Class.forName(String.format("%s.%s", packageName, stubClassName));
			Constructor constructor = aClass.getConstructor();
			T replicationStub = (T) constructor.newInstance();
			stubs.add(replicationStub);
		}
		return stubs;
	}

	public static void assertTransactionGroup(TransactionGroup transactionGroup) {
		List<ReplicationStub> replicationStubs = transactionGroup.getAttendanceOrBinaryDataOrBinaryInfo();
		assertListStubs(replicationStubs, PACKAGE_NAME_REPLICATION_STUBS, ReplicationStub.class);
	}

	public static <T> void  assertListStubs(List<T> stubs, String packageName, Class<T> parentStubClass) {
		List<String>  stubBeanNames  = getStubClassNamesBy(packageName,parentStubClass);
		for (String stubBeanName : stubBeanNames) {
			assertTrue("Stub:" + stubBeanName, containsStubBeanName(stubBeanName,stubs));
		}
	}





}
