SPRING INTEGRATION JCACHE SUPPORT
====================================

## JCACHE EVENT-DRIVEN INBOUND CHANNEL ADAPTER 
```
<int:channel id="cacheChannel"/>
	
<int-jcache:inbound-channel-adapter channel="cacheChannel" 
										cache="testCache" 
										cache-events="CREATED,REMOVED,UPDATED,EXPIRED" 
										old-value-required="false"/>

<bean id="testCache" class="javax.cache.Cache" factory-bean="jCacheManager" factory-method="getCache">
    <constructor-arg value="testCache"/>
</bean>
    
<bean id="jCacheManager" class="org.springframework.cache.jcache.JCacheManagerFactoryBean" p:cache-manager-uri="infinispan.xml"/>
```	
## JCACHE OUTBOUND CHANNEL ADAPTER 
```	
<int-jcache:outbound-channel-adapter channel="cacheChannel" 
										 cache="testCache" 
										 key-expression="payload.key" 
										 extract-payload="false"/>
```	
