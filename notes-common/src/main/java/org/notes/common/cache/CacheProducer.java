package org.notes.common.cache;


import org.infinispan.configuration.cache.*;
import org.infinispan.eviction.EvictionStrategy;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.transaction.lookup.GenericTransactionManagerLookup;
import org.notes.common.configuration.Configuration;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Daniel Scheidle, daniel.scheidle@ucs.at
 *         17:42, 10.07.12
 */
public class CacheProducer {

    @Produces
    @ApplicationScoped
    public EmbeddedCacheManager defaultEmbeddedCacheManager() {
        DefaultCacheManager returnCacheManager;

        ConfigurationBuilder builder = new ConfigurationBuilder();

        EvictionConfigurationBuilder evictionConfigurationBuilder = builder.eviction();
        evictionConfigurationBuilder.strategy(EvictionStrategy.LRU);
        evictionConfigurationBuilder.maxEntries(org.notes.common.configuration.Configuration.getIntValue("migor.cache.max_entries", 1000));

        TransactionConfigurationBuilder transactionConfigurationBuilder = evictionConfigurationBuilder.transaction();
        transactionConfigurationBuilder.transactionManagerLookup(new GenericTransactionManagerLookup());

        ExpirationConfigurationBuilder expirationConfigurationBuilder = transactionConfigurationBuilder.expiration();
        expirationConfigurationBuilder.lifespan(Configuration.getIntValue("migor.cache.lifespan", 600000));
        expirationConfigurationBuilder.maxIdle(Configuration.getIntValue("migor.cache.max_idle", 600000));

        returnCacheManager = new DefaultCacheManager(builder.build());

        Set<String> cacheNames = new HashSet<String>();
        for (CacheName cacheName : CacheName.values()) {
            cacheNames.add(cacheName.toString());
        }
        returnCacheManager.startCaches(cacheNames.toArray(new String[cacheNames.size()]));
        return returnCacheManager;
    }
}
