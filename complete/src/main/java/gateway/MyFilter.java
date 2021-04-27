package gateway;


import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import lombok.Data;
import reactor.core.publisher.Mono;

@Component
public class MyFilter extends AbstractGatewayFilterFactory<MyFilter.Config> {
    
	Logger logger = LoggerFactory.getLogger(CustomGatewayFilterFactory.class);
	
	public MyFilter() {
        super(Config.class);
    }

    private boolean isAuthorizationValid(String authorizationHeader) {
        boolean isValid = true;

        // Logic for checking the value

        return isValid;
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus)  {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);

        return response.setComplete();
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

  
            logger.info("CustomFilter baseMessage>>>>>>" + config.getBaseMessage());
            
            if (!request.getHeaders().containsKey("Authorization")) {
                return this.onError(exchange, "No Authorization header", HttpStatus.UNAUTHORIZED);
            };

            String authorizationHeader = request.getHeaders().get("Authorization").get(0);

            logger.info("authorizationHeader ==>" + authorizationHeader);
            if (!this.isAuthorizationValid(authorizationHeader)) {
                return this.onError(exchange, "Invalid Authorization header", HttpStatus.UNAUTHORIZED);
            }

            if (!authorizationHeader.equals("admin:fusion@2020")) {
                return this.onError(exchange, "Invalid Authorization header", HttpStatus.FORBIDDEN);
            }

            ServerHttpRequest modifiedRequest = exchange.getRequest().mutate().
                    header("secret", RandomStringUtils.random(10)).
                    build();

            return chain.filter(exchange.mutate().request(modifiedRequest).build());
        };
    }

    @Data
    public static class Config {
        private String baseMessage;
        private boolean preLogger;
        private boolean postLogger;
    }
}