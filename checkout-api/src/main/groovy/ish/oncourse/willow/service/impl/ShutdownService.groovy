package ish.oncourse.willow.service.impl

import com.google.inject.Inject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import sun.misc.Signal
import sun.misc.SignalHandler

import java.util.concurrent.atomic.AtomicBoolean

class ShutdownService implements SignalHandler {
    
    final static  Logger logger = LoggerFactory.getLogger(ShutdownService.class)
    
    private AtomicBoolean signalReceived = new AtomicBoolean(false)

    @Inject
    ShutdownService() {
        Signal drainSignal = new Signal("INT")
        Signal.handle(drainSignal, this)
    }

    @Override
    void handle(Signal signal) {
        logger.warn("$signal signal resived, kill health check")

        signalReceived.set(true)
    }

    boolean isKillSignalReceived() {
        signalReceived.get()
    }
}