package org.example.service;

import org.apache.ignite.lifecycle.LifecycleEventType;

public interface NodeStateListener {
    void onNodeStateChanged(LifecycleEventType eventType);
}
