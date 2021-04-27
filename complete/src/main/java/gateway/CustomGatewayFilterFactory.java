package gateway;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;

import groovy.util.logging.Slf4j;
import lombok.Getter;
import lombok.Setter;

@Slf4j
@Component
public class CustomGatewayFilterFactory
        extends AbstractGatewayFilterFactory<CustomGatewayFilterFactory.Config> {

    public static final String FOO_KEY = "foo";
    public static final String BAR_KEY = "bar";

    Logger logger = LoggerFactory.getLogger(CustomGatewayFilterFactory.class);
    
    public CustomGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return Arrays.asList(FOO_KEY, BAR_KEY);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
        	logger.debug("NOT ORDERED Foo : {} / Bar: {}", config.getFoo(), config.getBar());
            return chain.filter(exchange);
        };
    }

    @Getter
    @Setter
    public static class Config {
        private String foo;
        private String bar;
    }
}
