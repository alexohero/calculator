package com.mx.raven.calculator.mappers;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapStructConfig {

    @Bean
    public UsersMapper diasInhabilesMapping() { return UsersMapper.INSTANCE; }
    @Bean
    public UserOperationsMapper userOperationsMapping() { return UserOperationsMapper.INSTANCE; }

}
