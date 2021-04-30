/*****************************************************************
 *   Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 ****************************************************************/

package ish.unit

import org.apache.cayenne.*
import org.apache.cayenne.event.EventManager
import org.apache.cayenne.graph.GraphDiff
import org.apache.cayenne.graph.GraphManager
import org.apache.cayenne.map.EntityResolver
import org.apache.cayenne.query.Query
import org.apache.cayenne.query.Select

/**
 * A noop ObjectContext used for unit testing.
 */
class MockObjectContext implements ObjectContext {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L
    protected GraphManager graphManager

    MockObjectContext() {
		super()
    }

    MockObjectContext(GraphManager graphManager) {
		this.graphManager = graphManager
    }

    EntityResolver getEntityResolver() {
		return null
    }

    DataChannel getChannel() {
		return null
    }

    GraphManager getGraphManager() {
		return this.graphManager
    }

    Persistent localObject(ObjectId id, Object prototype) {
		return null
    }

    void commitChangesToParent() {}

    void rollbackChangesLocally() {}

    void rollbackChanges() {}

    Collection newObjects() {
		return null
    }

    Collection deletedObjects() {
		return Collections.emptyList()
    }

    Collection modifiedObjects() {
		return Collections.emptyList()
    }

    List performQuery(Query query) {
		return Collections.emptyList()
    }

	@Override
    <T> List<T> select(Select<T> query) {
		return null
    }

	@Override
    <T> void iterate(Select<T> query, ResultIteratorCallback<T> callback) {
		
	}

	@Override
    <T> ResultIterator<T> iterator(Select<T> query) {
		return null
    }
	
	@Override
    <T> ResultBatchIterator<T> batchIterator(Select<T> query, int size) {
		return null
    }

    int[] performNonSelectingQuery(Query query) {
		return null
    }

    void commitChanges() {

	}

    void deleteObject(Object object) {}

	/**
	 * @see org.apache.cayenne.ObjectContext#newObject(java.lang.Class)
	 */
	def <T> T newObject(Class<T> arg0) {
		return null
    }

    void registerNewObject(Object object) {}

    void prepareForAccess(Persistent object, String property, boolean lazyFaulting) {}

    void propertyChanged(Persistent persistent, String property, Object oldValue, Object newValue) {}

    void addedToCollectionProperty(Persistent object, String property, Persistent added) {}

    void removedFromCollectionProperty(Persistent object, String property, Persistent removed) {}

    Collection uncommittedObjects() {
		return Collections.emptyList()
    }

    QueryResponse performGenericQuery(Query queryPlan) {
		return null
    }

	/**
	 * @see org.apache.cayenne.ObjectContext#getUserProperty(java.lang.String)
	 */
	Object getUserProperty(String arg0) {
		return null
    }

	/**
	 * @see org.apache.cayenne.ObjectContext#hasChanges()
	 */
	boolean hasChanges() {
		return false
    }

	/**
	 * @see org.apache.cayenne.ObjectContext#invalidateObjects(java.util.Collection)
	 */
	void invalidateObjects(Collection arg0) {}

	/**
	 * @see org.apache.cayenne.ObjectContext#setUserProperty(java.lang.String, java.lang.Object)
	 */
	void setUserProperty(String arg0, Object arg1) {}

	/**
	 * @see org.apache.cayenne.ObjectContext#deleteObjects(java.util.Collection)
	 */
	void deleteObjects(Collection<?> arg0) throws DeleteDenyException {}

	/**
	 * @see org.apache.cayenne.ObjectContext#localObject(org.apache.cayenne.Persistent)
	 */
	def <T extends Persistent> T localObject(T objectFromAnotherContext) {
		return null
    }

	/**
	 * @see org.apache.cayenne.ObjectContext#deleteObjects(T[])
	 */
	def <T> void deleteObjects(T... objects) throws DeleteDenyException {}

	/**
	 * @see org.apache.cayenne.ObjectContext#invalidateObjects(T[])
	 */
	def <T> void invalidateObjects(T... objects) {

	}

	@Override
    <T> T selectOne(Select<T> query) {
		return null
    }
	
	@Override
    <T> T selectFirst(Select<T> query) {
		return null
    }

	@Override
    GraphDiff onSync(ObjectContext originatingContext, GraphDiff changes, int syncType) {
		return null
    }

	@Override
    EventManager getEventManager() {
		return null
    }

	@Override
    QueryResponse onQuery(ObjectContext originatingContext, Query query) {
		return null
    }
}