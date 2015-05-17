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

package org.springframework.integration.jcache.config.xml;

import org.w3c.dom.Element;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.integration.config.xml.AbstractOutboundChannelAdapterParser;
import org.springframework.integration.config.xml.IntegrationNamespaceUtils;
import org.springframework.integration.jcache.outbound.JCacheCacheWritingMessageHandler;

import reactor.util.StringUtils;

/**
 * JCache Outbound Channel Adapter Parser for {@code <int-jcache:outbound-channel-adapter />}.
 *
 * @author Eren Avsarogullari
 * @since 1.0.0
 */
public class JCacheOutboundChannelAdapterParser extends AbstractOutboundChannelAdapterParser {

	private static final String CACHE_ATTRIBUTE = "cache";

	private static final String CACHE_EXPRESSION_ATTRIBUTE = "cache-expression";

	private static final String KEY_EXPRESSION_ATTRIBUTE = "key-expression";

	private static final String EXTRACT_PAYLOAD_ATTRIBUTE = "extract-payload";

	@Override
	protected AbstractBeanDefinition parseConsumer(Element element, ParserContext parserContext) {
		BeanDefinitionBuilder builder = BeanDefinitionBuilder
				.genericBeanDefinition(JCacheCacheWritingMessageHandler.class);

		if (!StringUtils.hasText(element.getAttribute(KEY_EXPRESSION_ATTRIBUTE))) {
			parserContext.getReaderContext().error("'" + KEY_EXPRESSION_ATTRIBUTE + "' attribute is required.", element);
		}

		IntegrationNamespaceUtils.setReferenceIfAttributeDefined(builder, element,
				CACHE_ATTRIBUTE);
		IntegrationNamespaceUtils.setValueIfAttributeDefined(builder, element,
				CACHE_EXPRESSION_ATTRIBUTE);
		IntegrationNamespaceUtils.setValueIfAttributeDefined(builder, element,
				KEY_EXPRESSION_ATTRIBUTE);
		IntegrationNamespaceUtils.setValueIfAttributeDefined(builder, element,
				EXTRACT_PAYLOAD_ATTRIBUTE);

		return builder.getBeanDefinition();
	}

}
