package ish.oncourse.willow.service.impl

import com.google.inject.Inject
import io.bootique.shutdown.ShutdownManager
import sun.misc.Signal
import sun.misc.SignalHandler

import java.util.concurrent.atomic.AtomicBoolean

class ShutdownService implements SignalHandler {

    private AtomicBoolean signalReceived = new AtomicBoolean(false)

    @Inject
    ShutdownService() {
        Signal drainSignal = new Signal("INT")
        Signal.handle(drainSignal, this)
    }

    @Override
    void handle(Signal signal) {
        signalReceived.set(true)
    }

    boolean isKillSignalReceived() {
        signalReceived.get()
    }
}