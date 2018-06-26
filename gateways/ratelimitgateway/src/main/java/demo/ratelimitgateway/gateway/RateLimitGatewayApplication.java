package demo.ratelimitgateway.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RateLimiter;
import org.springframework.context.annotation.Bean;
import org.springframework.http.server.PathContainer;
import reactor.core.publisher.Mono;

import java.util.List;

@SpringBootApplication
public class RateLimitGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(RateLimitGatewayApplication.class, args);
	}

	@Bean
	public KeyResolver articlesKeyResolver() {
		return exchange -> {
			List<PathContainer.Element> pathElements = exchange.getRequest().getPath().elements();
			return Mono.just(pathElements.get(pathElements.size() - 1).toString());
		};
	}
}
