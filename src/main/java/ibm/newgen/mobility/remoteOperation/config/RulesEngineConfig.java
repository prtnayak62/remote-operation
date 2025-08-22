package ibm.newgen.mobility.remoteOperation.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ibm.autoconnect.rule.service.RulesEngineProcessor;

@Configuration
public class RulesEngineConfig {

    @Bean
    public RulesEngineProcessor rulesEngineProcessor() {
        return new RulesEngineProcessor(null); // adjust constructor args if required
    }
}