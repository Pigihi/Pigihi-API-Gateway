/**
 * 
 */
package com.pigihi.config;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
//import org.springframework.web.reactive.function.client.WebClient.Builder;

/**
 * @author Ashish Sam T George
 *
 */
@Component
public class AuthFilter extends AbstractGatewayFilterFactory<AuthFilter.Config> {
	
	private final WebClient.Builder webClientBuilder;
	
	public AuthFilter(WebClient.Builder webClientBuilder) {
//		super();
		this.webClientBuilder = webClientBuilder;
	}

	@Override
	public GatewayFilter apply(Config config) {
		return (exchange, chain) -> {
			if(!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
				throw new RuntimeException("Missing Authorization Header");
			}
			
			String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
			String[] parts = authHeader.split(" ");
			
			if(parts.length != 2 || !"Bearer".equals(parts[0])) {
				throw new RuntimeException("Incorrect Authorization Header structure");
			}
			
			return webClientBuilder.build()
					.post()
					.uri("http://AUTH-SERVICE/auth/authorize/user?validateJWT=" + parts[1])
					.retrieve()
//					Decode the body to the given target type. For an error response (status code of 4xx or 5xx), the Mono emits a WebClientException. Use onStatus(Predicate, Function) to customize error response handling.

//					.bodyToMono(String.class)
//					.map(string -> {
//						exchange.getRequest()
//								.mutate()
//								.header("X-Auth-User-Id", string);
//						return exchange;
//					})
					.bodyToMono(AuthRolesModel.class)
					.map(authRolesModel -> {
						exchange.getRequest()
								.mutate()
								.header("X-Auth-User-Id", String.valueOf(authRolesModel.getAuthRoles()));
						return exchange;
					}).flatMap(chain::filter);
		};
	}
	
	public static class Config{
			
		}

}
