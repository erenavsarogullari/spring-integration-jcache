/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.integration.jcache.inbound;

import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

import javax.cache.Cache;
import javax.cache.configuration.CacheEntryListenerConfiguration;
import javax.cache.configuration.FactoryBuilder;
import javax.cache.configuration.MutableCacheEntryListenerConfiguration;
import javax.cache.event.CacheEntryCreatedListener;
import javax.cache.event.CacheEntryEvent;
import javax.cache.event.CacheEntryExpiredListener;
import javax.cache.event.CacheEntryListenerException;
import javax.cache.event.CacheEntryRemovedListener;
import javax.cache.event.CacheEntryUpdatedListener;
import javax.cache.event.EventType;

import org.springframework.integration.endpoint.MessageProducerSupport;
import org.springframework.integration.jcache.JCacheIntegrationDefinitionValidator;
import org.springframework.messaging.Message;
import org.springframework.util.Assert;

import reactor.util.StringUtils;

/**
 * JCache Event Driven Message Producer is a message producer which enables
 * {@link JCacheEventDrivenMessageProducer.JCacheEntryListener} listener in order
 * to listen related cache events and sends events to related channel.
 *
 * @author Eren Avsarogullari
 * @param <K> the type of key
 * @param <V> the type of value
 * @since 1.0.0
 */
public class JCacheEventDrivenMessageProducer<K,V> extends MessageProducerSupport {

	private final Cache<K,V> cache;

	private Set<String> cacheEvents = Collections.singleton(EventType.CREATED.name());

	private CacheEntryListenerConfiguration<K,V> cacheEntryListenerConfiguration;

	public JCacheEventDrivenMessageProducer(Cache<K,V> cache) {
		Assert.notNull(cache, "'cache' must not be null");
		this.cache = cache;
	}

	public void setCacheEventTypes(String cacheEventTypes) {
		JCacheIntegrationDefinitionValidator.validateEnumType(EventType.class, cacheEventTypes);
		Set<String> cacheEvents = StringUtils.commaDelimitedListToSet(cacheEventTypes);
		Assert.notEmpty(cacheEvents, "'cacheEvents' must have elements");
		this.cacheEvents = cacheEvents;
	}

	@Override
	@SuppressWarnings({"rawtypes", "unchecked"})
	protected void doStart() {
		this.cacheEntryListenerConfiguration = new MutableCacheEntryListenerConfiguration
		   (FactoryBuilder.factoryOf(new JCacheEntryListener()), null, true, false);
		this.cache.registerCacheEntryListener(this.cacheEntryListenerConfiguration);
	}

	@Override
	protected void doStop() {
		this.cache.deregisterCacheEntryListener(this.cacheEntryListenerConfiguration);
	}

	@Override
	public String getComponentType() {
		return "jcache:inbound-channel-adapter";
	}

	private class JCacheEntryListener implements CacheEntryCreatedListener<K,V>,
													CacheEntryUpdatedListener<K,V>,
													CacheEntryRemovedListener<K,V>,
													CacheEntryExpiredListener<K,V>,
													Serializable {

		private static final long serialVersionUID = -5212499264029989444L;

		@Override
		public void onExpired(Iterable<CacheEntryEvent<? extends K, ? extends V>> events)
				throws CacheEntryListenerException {
			printEvents(events);
		}

		@Override
		public void onRemoved(Iterable<CacheEntryEvent<? extends K, ? extends V>> events)
				throws CacheEntryListenerException {
			printEvents(events);
		}

		@Override
		public void onUpdated(Iterable<CacheEntryEvent<? extends K, ? extends V>> events)
				throws CacheEntryListenerException {
			printEvents(events);
		}

		@Override
		public void onCreated(Iterable<CacheEntryEvent<? extends K, ? extends V>> events)
				throws CacheEntryListenerException {
			printEvents(events);
		}

		private void printEvents(Iterable<CacheEntryEvent<? extends K, ? extends V>> cacheEntryEvents) {
			Iterator<CacheEntryEvent<? extends K, ? extends V>> iterator = cacheEntryEvents.iterator();
			while (iterator.hasNext()) {
				CacheEntryEvent<?, ?> event = iterator.next();
				sendMessage(event);
			}
		}

		private void sendMessage(CacheEntryEvent<?,?> event) {
			if (cacheEvents.contains(event.getEventType().toString())) {
				final Message<?> message = getMessageBuilderFactory().withPayload(event).build();
				JCacheEventDrivenMessageProducer.this.sendMessage(message);
			}

			if (logger.isDebugEnabled()) {
				logger.debug("Received Event : " + event);
			}
		}

	}

}
