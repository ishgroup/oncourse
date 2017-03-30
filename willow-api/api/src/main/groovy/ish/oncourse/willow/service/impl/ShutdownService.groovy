package ish.oncourse.willow.service.impl

import com.google.inject.Inject
import io.bootique.shutdown.ShutdownManager

import java.util.concurrent.atomic.AtomicBoolean

class ShutdownService {
    
    private AtomicBoolean signalReceived = new AtomicBoolean(false) 
    
    @Inject
    ShotDownService(ShutdownManager manager) {
        manager.addShutdownHook([close: { 
            signalReceived.getAndSet(true) 
        }] as AutoCloseable)
    }
    
    boolean isKillSignalReceived() {
        signalReceived.get()
    }
}
