package xp.librarian.config;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.*;

/**
 * @author xp
 */
@Configuration
@Import({
        RepositoryConfig.class,
        ScheduleConfig.class,
        I18NConfig.class
})
@ComponentScan(basePackages = {"xp.librarian.service"})
public class ServiceConfig {

    @Bean
    public ValidatorFactory validatorFactory() {
        return Validation.byDefaultProvider()
                .configure()
                .buildValidatorFactory();
    }

    @Bean
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    public Validator validator(ValidatorFactory factory) {
        return factory.getValidator();
    }

}
