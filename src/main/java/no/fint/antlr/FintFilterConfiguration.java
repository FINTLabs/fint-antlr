package no.fint.antlr;

import no.fint.antlr.odata.ODataFilterService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FintFilterConfiguration {

    @Bean
    public FintFilterService oDataFilterService() {
        return new ODataFilterService();
    }
}
