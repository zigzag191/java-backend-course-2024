package edu.java.scrapper.configuration;

import edu.java.common.filter.ThrottlingFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<ThrottlingFilter> throttlingFilter() {
        var filterRegistrationBean = new FilterRegistrationBean<ThrottlingFilter>();
        filterRegistrationBean.setFilter(new ThrottlingFilter());
        return filterRegistrationBean;
    }

}
