package org.example.service;

import org.apache.ignite.IgniteException;
import org.apache.ignite.lifecycle.LifecycleBean;
import org.apache.ignite.lifecycle.LifecycleEventType;
import org.springframework.beans.factory.DisposableBean;

public class NodeStateWaiter implements LifecycleBean, DisposableBean {
    /**
     * Current node state
     */
    private volatile LifecycleEventType nodeState;

    /**
     * Determines if this bean should
     */
    private volatile boolean cancelled;

    @Override
    public void onLifecycleEvent(LifecycleEventType evt) throws IgniteException {
        nodeState = evt;
        synchronized (this) {
            notifyAll();
        }
    }

    /**
     * Wait until the grid node is in the expected state
     * @param state we are waiting for
     */
    public void waitForState(LifecycleEventType state) {
        if (cancelled || nodeState == state) {
            return;
        }

        while (!cancelled && nodeState != state) {
            synchronized (this) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    System.out.println("Interrupted!");
                }
            }
        }
    }

    @Override
    public void destroy() throws Exception {
        cancelled = true;
    }
}
