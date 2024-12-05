package tr.edu.ogu.ceng.payment.config;

import org.springframework.web.client.RestClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient getRestClientConfig() {
        return RestClient.create();
    }
}
