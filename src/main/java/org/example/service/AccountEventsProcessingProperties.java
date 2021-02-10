package org.example.service;

import org.springframework.beans.factory.annotation.Value;

import java.io.*;

public class AccountEventsProcessingProperties implements Serializable {
    private static final long serialVersionUID = 5710441451712886016L;

    /**
     * The initial delay before the first account events query will be fired (in milliseconds).
     */
    @Value("${com.devexperts.tos.riskmonitor.clusternode.account-events-processing.scheduled-cache-query-initial-delay:30000}")
    private long scheduledCacheQueryInitialDelay;

    /**
     * Fixed delay between account events queries (in milliseconds).
     */
    @Value("${com.devexperts.tos.riskmonitor.clusternode.account-events-processing.scheduled-cache-query-delay:1000}")
    private long scheduledCacheQueryDelay;

    /**
     * How much time can we wait for account version change events to get processed (in milliseconds).
     */
    @Value("${com.devexperts.tos.riskmonitor.clusternode.account-events-processing.version-change-events-processing-timeout:60000}")
    private long versionChangeEventsProcessingTimeout;

    public long getScheduledCacheQueryInitialDelay() {
        return scheduledCacheQueryInitialDelay;
    }

    public void setScheduledCacheQueryInitialDelay(long scheduledCacheQueryInitialDelay) {
        this.scheduledCacheQueryInitialDelay = scheduledCacheQueryInitialDelay;
    }

    public long getScheduledCacheQueryDelay() {
        return scheduledCacheQueryDelay;
    }

    public void setScheduledCacheQueryDelay(long scheduledCacheQueryDelay) {
        this.scheduledCacheQueryDelay = scheduledCacheQueryDelay;
    }

    public long getVersionChangeEventsProcessingTimeout() {
        return versionChangeEventsProcessingTimeout;
    }

    public void setVersionChangeEventsProcessingTimeout(long versionChangeEventsProcessingTimeout) {
        this.versionChangeEventsProcessingTimeout = versionChangeEventsProcessingTimeout;
    }

    @Override
    public String toString() {
        return "AccountEventsProcessingProperties{" +
                "scheduledCacheQueryInitialDelay=" + scheduledCacheQueryInitialDelay +
                ", scheduledCacheQueryDelay=" + scheduledCacheQueryDelay +
                ", versionChangeEventsProcessingTimeout=" + versionChangeEventsProcessingTimeout +
                '}';
    }

    /* @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(scheduledCacheQueryInitialDelay);
        out.writeLong(scheduledCacheQueryDelay);
        out.writeLong(versionChangeEventsProcessingTimeout);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        scheduledCacheQueryInitialDelay = in.readLong();
        scheduledCacheQueryDelay = in.readLong();
        versionChangeEventsProcessingTimeout = in.readLong();
    }*/
}
