package org.springframework.integration.jcache.outbound;

import javax.cache.Cache;

import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.integration.handler.AbstractMessageHandler;
import org.springframework.messaging.Message;
import org.springframework.util.Assert;

import reactor.util.StringUtils;

/**
 * MessageHandler implementation that writes {@link Message} or payload to defined
 * JCache object.
 *
 * @author Eren Avsarogullari
 * @since 1.0.0
 */
public class JCacheCacheWritingMessageHandler<K,V> extends AbstractMessageHandler {

	private Cache<K,V> cache;

	private String cacheExpression;

	private String keyExpression;

	private boolean extractPayload = true;

	public void setCache(Cache<K,V> cache) {
		Assert.notNull(cache, "'cache' must not be null");
		this.cache = cache;
	}

	public void setCacheExpression(String cacheExpression) {
		Assert.notNull(cacheExpression, "'cacheExpression' must not be null");
		this.cacheExpression = cacheExpression;
	}

	public void setKeyExpression(String keyExpression) {
		Assert.notNull(keyExpression, "'keyExpression' must not be null");
		this.keyExpression = keyExpression;
	}

	public void setExtractPayload(boolean extractPayload) {
		this.extractPayload = extractPayload;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected void handleMessageInternal(final Message<?> message) throws Exception {
		Cache cache = getCache(message);
		cache.put(parseKeyExpression(message), getPayloadOrMessage(message));
	}

	private Cache<?,?> getCache(final Message<?> message) {
		if (this.cache != null) {
			return this.cache;
		}
		else if (StringUtils.hasText(this.cacheExpression)) {
			return parseCacheExpression(message);
		}
		else {
			throw new IllegalStateException("One of 'cache' and 'cache-expression' must be set for cache object definition.");
		}
	}

	private Cache<?,?> parseCacheExpression(final Message<?> message) {
		Object cacheName = parseExpression(this.cacheExpression, message);
		return this.getBeanFactory().getBean(cacheName.toString(), Cache.class);
	}

	private Object parseKeyExpression(final Message<?> message) {
		if (StringUtils.hasText(this.keyExpression)) {
			return parseExpression(this.keyExpression, message);
		}
		else {
			throw new IllegalStateException("'key-expression' must be set.");
		}
	}

	private Object parseExpression(final String expressionString, final Message<?> message) {
		Expression expression = new SpelExpressionParser().parseExpression(expressionString);
		return expression.getValue(new StandardEvaluationContext(message));
	}

	private Object getPayloadOrMessage(final Message<?> message) {
		if (this.extractPayload) {
			return message.getPayload();
		}
		else {
			return message;
		}
	}

}
