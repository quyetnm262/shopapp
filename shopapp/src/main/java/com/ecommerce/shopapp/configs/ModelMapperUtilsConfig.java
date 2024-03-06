package com.ecommerce.shopapp.configs;

import com.ecommerce.shopapp.utils.ModelMapperUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperUtilsConfig {
    @Bean
    public ModelMapperUtils modelMapperUtils(){
        return new ModelMapperUtils();
    }

}
