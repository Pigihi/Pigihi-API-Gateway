/**
 * 
 */
package com.pigihi;

import java.util.ArrayList;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.Data;
import reactor.core.publisher.Mono;


/**
 * @author Ashish Sam T George
 *
 */
@Component
public class LoggingGatewayFilterFactory extends AbstractGatewayFilterFactory<LoggingGatewayFilterFactory.Config> {
	
	final Logger logger = LoggerFactory.getLogger(LoggingGatewayFilterFactory.class);
	
	public LoggingGatewayFilterFactory() {
		super(Config.class);
	}
	
	@Override
	public GatewayFilter apply(Config config) {
//		return (exchange, chain) -> {
//			// Pre-filter
//			if(config.isPreLogger()) {
//				logger.info("Granular Pre Gateway Filter Logging" + config.getBaseMessage());
//			}
//			// Post-filter
//			return chain.filter(exchange)
//					.then(Mono.fromRunnable(() -> {
//						if(config.isPostLogger()) {
//							logger.info("Granular Post Gateway Filter Logging" + config.getBaseMessage());
//						}
//					}));
//		};
		
		
		return (exchange, chain) -> {
			ServerHttpRequest request = exchange.getRequest();
			
			String authHeader = request.getHeaders().get("Authorization").get(0);
			String authJwt = authHeader.substring(7);
			System.out.println("JWT: " + authJwt);
			
//			if(!request.getHeaders().containsKey("Authorization")) {
				return WebClient.create()
						.get()
//						Request is being made to the service along with the initial path (ex: /user/customer?email=)
						.uri(config.getAuthenticationUri().concat("?jwt=").concat(authJwt))
						.exchange()
						.flatMap(response -> {
							return (response.statusCode()
									.is2xxSuccessful()) ? response.bodyToMono(String.class) : Mono.just(config.getAuthenticationUri());
						})
						.map(String::toString)
						.map(range -> {
							exchange.getRequest()
								.mutate()
								.headers(h -> h.set("X-USER-ROLES", range));
							
							String outgoingRoles = exchange.getRequest()
										.getHeaders()
										.get("X-USER-ROLES")
										.stream()
										.collect(Collectors.joining(","));
							
							logger.info("Chain Request Output: " + outgoingRoles);
							
							return exchange;
						
						})
						.flatMap(chain::filter);
//			}
		};
		
	}
	
	@Data
	public static class Config {
//		private String baseMessage;
		private String authenticationUri;
//		private boolean preLogger;
//		private boolean postLogger;
	}

}
