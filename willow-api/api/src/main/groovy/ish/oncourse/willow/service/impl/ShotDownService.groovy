package ish.oncourse.willow.service.impl

import com.google.inject.Inject
import io.bootique.shutdown.ShutdownManager

import java.util.concurrent.atomic.AtomicBoolean

class ShotDownService {
    
    AtomicBoolean signalReceived = new AtomicBoolean(false) 
    
    @Inject
    ShotDownService(ShutdownManager manager) {
        manager.addShutdownHook(new AutoCloseable() {
            @Override
            void close() throws Exception {
                signalReceived.getAndSet(true)
            }
        })
    }
    
    
    boolean isKillSignalReceived() {
        signalReceived.get()
    }
    
    
}
