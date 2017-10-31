package ish.oncourse.test;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class TestInitialContextFactory implements InitialContextFactory {

	/**
	 * The context instance to store jndi properties.
	 */
	private static AtomicReference<TestInitialContext> context = new AtomicReference<>();

	/**
	 * Block which initializes the context.
	 */
	static {
		initialize();
	}

	private static void initialize() {
		try {
			context.compareAndSet(null, new TestInitialContext());
		} catch (NamingException e) { // can't happen.
			throw new RuntimeException(e);
		}
	}

	/**
	 * Returns the context. {@inheritDoc}
	 *
	 * @see InitialContextFactory#getInitialContext(Hashtable)
	 */
	public Context getInitialContext(Hashtable<?, ?> environment) throws NamingException {
		if (context.get() == null) {
			initialize();
		}
		return context.get();
	}

	/**
	 * Binds the object "obj" to context with the name "name".
	 *
	 * @param name
	 * @param obj
	 */
	public static void bind(String name, Object obj) {
		try {
			context.get().bind(name, obj);
		} catch (NamingException e) { // can't happen.
			throw new RuntimeException(e);
		}
	}

	public static void close() {
		try {
			TestInitialContext value = context.get();
			context.compareAndSet(value, null);
			value.close();
		} catch (NamingException e) {
			throw new RuntimeException(e);
		}
	}

	private static class TestInitialContext extends InitialContext {
		private TestInitialContext() throws NamingException {
			super(true);
		}

		Map<String, Object> bindings = new HashMap<>();

		@Override
		public void bind(String name, Object obj) throws NamingException {
			bindings.put(name, obj);
		}

		@Override
		public Object lookup(String name) throws NamingException {
			return bindings.get(name);
		}
	}
}