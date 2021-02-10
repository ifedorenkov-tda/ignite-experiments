package org.example.service;

import org.apache.ignite.lifecycle.LifecycleEventType;
import org.apache.ignite.resources.SpringResource;
import org.apache.ignite.services.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.BiConsumer;

public abstract class AbstractService implements Service {
    @SpringResource(resourceClass = NodeStateWaiter.class)
    private transient NodeStateWaiter nodeStateWaiter;

    /**
     * Invokes the given consumer after the node has started.
     *
     * @param executorService to be used for asynchronous wait
     * @return promise that may be used for additional operations that must be
     * executed after the node has started
     */
    protected CompletableFuture<Void> afterNodeStart(BiConsumer<Void, Throwable> consumer, ExecutorService executorService) {
        CompletableFuture<Void> promise = CompletableFuture.runAsync(() ->
                        nodeStateWaiter.waitForState(LifecycleEventType.AFTER_NODE_START), executorService);
        promise.whenComplete(consumer);
        return promise;
    }
}
