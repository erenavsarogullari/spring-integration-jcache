package org.springframework.integration.jcache.service.activator;

import org.springframework.integration.handler.ServiceActivatingHandler;
import org.springframework.messaging.Message;

public class JCacheEntryProcessorServiceActivatingHandler extends ServiceActivatingHandler {

	public JCacheEntryProcessorServiceActivatingHandler(Object object) {
		super(object);
	}

	@Override
    protected Object handleRequestMessage(Message<?> message) {
		return message;
	}

}
