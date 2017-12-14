/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.replication.updaters.v16;

import ish.oncourse.model.College;
import ish.oncourse.model.Queueable;
import ish.oncourse.model.Tax;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.test.tapestry.ServiceTest;
import ish.oncourse.webservices.replication.updaters.RelationShipCallback;
import ish.oncourse.webservices.replication.v16.updaters.TaxUpdater;
import ish.oncourse.webservices.soap.ReplicationTestModule;
import ish.oncourse.webservices.v16.stubs.replication.TaxStub;
import org.apache.cayenne.ObjectContext;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Date;

import static org.junit.Assert.assertEquals;

public class TaxUpdaterTest extends ServiceTest {

    @Before
    public void setupDataSet() throws Exception {
        initTest("ish.oncourse.webservices.services", StringUtils.EMPTY, ReplicationTestModule.class);
    }
    
    @Test
    public void test() {
        TaxUpdater updater = new TaxUpdater();
        TaxStub stub = new TaxStub();
        stub.setAngelId(1L);
        stub.setWillowId(1L);
        stub.setTaxCode("GST");
        stub.setIsGSTTaxType(true);
        stub.setRate(new BigDecimal("0.1"));
        stub.setModified(new Date());
        stub.setCreated(new Date());
        
        ObjectContext objectContext = getService(ICayenneService.class).newContext();
        
        Tax entity = objectContext.newObject(Tax.class);
        College college = objectContext.newObject(College.class);
        entity.setCollege(college);
        RelationShipCallback relationShipCallback = new RelationShipCallback() {
            @Override
            public <M extends Queueable> M updateRelationShip(Long entityId, Class<M> clazz) {
                return null;
            }
        };
        updater.updateEntity(stub, entity, relationShipCallback);
        assertEquals(entity.getCode(), stub.getTaxCode());
        assertEquals(entity.getDescription(), stub.getDescription());
        assertEquals(entity.getIsGSTTaxType(), stub.isIsGSTTaxType());
        assertEquals(entity.getRate(), stub.getRate());
    }
}
