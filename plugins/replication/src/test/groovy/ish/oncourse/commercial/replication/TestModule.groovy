/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.commercial.replication

import com.google.inject.Binder
import com.google.inject.Module
import groovy.transform.CompileStatic
import ish.oncourse.commercial.replication.builders.AngelStubBuilderImpl
import ish.oncourse.commercial.replication.builders.IAngelStubBuilder
import ish.oncourse.commercial.replication.modules.ISoapPortLocator
import ish.oncourse.commercial.replication.modules.SoapPortLocatorImpl
import ish.oncourse.commercial.replication.reference.updater.IReferenceUpdaterFactory
import ish.oncourse.commercial.replication.reference.updater.ReferenceUpdaterFactory
import ish.oncourse.commercial.replication.services.AngelQueueService
import ish.oncourse.commercial.replication.services.IAngelQueueService
import ish.oncourse.commercial.replication.services.TransactionGroupProcessorImpl
import ish.oncourse.commercial.replication.updaters.AngelUpdaterImpl
import ish.oncourse.commercial.replication.updaters.IAngelUpdater
import ish.oncourse.webservices.ITransactionGroupProcessor

@CompileStatic
class TestModule implements Module {
    
    @Override
    void configure(Binder binder) {
        binder.bind(ISoapPortLocator.class).to(SoapPortLocatorImpl.class)

        binder.bind(IReferenceUpdaterFactory.class).to(ReferenceUpdaterFactory.class)

        binder.bind(ITransactionGroupProcessor.class).to(TransactionGroupProcessorImpl.class)

        binder.bind(IAngelStubBuilder.class).to(AngelStubBuilderImpl.class)
        binder.bind(IAngelUpdater.class).to(AngelUpdaterImpl.class)
        binder.bind(IAngelQueueService.class).to(AngelQueueService.class)
    }
}
