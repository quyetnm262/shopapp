package com.ecommerce.shopapp.configs;

import com.ecommerce.shopapp.filters.JwtTokenFilter;
import com.ecommerce.shopapp.models.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

import static org.springframework.http.HttpMethod.*;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final JwtTokenFilter jwtTokenFilter;

    @Value("${api.prefix}")
    private String prefix;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
         http
            .csrf(AbstractHttpConfigurer::disable)

            .addFilterBefore(jwtTokenFilter,
                    UsernamePasswordAuthenticationFilter.class)
            .authorizeHttpRequests(request ->{
                request
                        .requestMatchers(
                        prefix+"/users/register",
//                        prefix+"/products/generateFakerProducts",
                        prefix+"/users/login",
                        prefix+"/products/images/**",
                        prefix+"/products/upload/**")

                        .permitAll()

                        //Role
                        .requestMatchers(
                                GET,
                                prefix+"/roles**")
                        .permitAll()


                        //Category
                        .requestMatchers(
                                GET,
                                prefix+"/categories**")
                        .hasAnyRole(Role.ADMIN,Role.USER)

                        .requestMatchers(
                                POST,
                                prefix+"/categories**")
                        .hasAnyRole(Role.ADMIN)

                        .requestMatchers(
                                PUT,
                                prefix+"/categories**")
                        .hasRole(Role.ADMIN)

                        .requestMatchers(
                                DELETE,
                                prefix+"/categories**")
                        .hasRole(Role.ADMIN)

                        //Product
                        .requestMatchers(
                                GET,
                                prefix+"/products**")
                        .permitAll()

                        .requestMatchers(
                                POST,
                                prefix+"/products**")
                        .hasAnyRole(Role.ADMIN)

                        .requestMatchers(
                                PUT,
                                prefix+"/products**")
                        .hasRole(Role.ADMIN)

                        .requestMatchers(
                                DELETE,
                                prefix+"/products**")
                        .hasRole(Role.ADMIN)


                        //Order_details
                        .requestMatchers(
                                GET,
                                prefix+"/order_details**")
                        .hasAnyRole(Role.ADMIN,Role.USER)

                        .requestMatchers(
                                POST,
                                prefix+"/order_details**")
                        .hasAnyRole(Role.ADMIN)

                        .requestMatchers(
                                PUT,
                                prefix+"/order_details**")
                        .hasRole(Role.ADMIN)

                        .requestMatchers(
                                DELETE,
                                prefix+"/order_details**")
                        .hasRole(Role.ADMIN)

                        //Orders
                        .requestMatchers(
                                GET,
                                prefix+"/orders/**")
                        .hasAnyRole(Role.ADMIN,Role.USER)

                        .requestMatchers(
                                POST,
                                prefix+"/orders/**")
                        .hasAnyRole(Role.USER)

                        .requestMatchers(
                                PUT,
                                prefix+"/orders/**")
                        .hasRole(Role.ADMIN)

                        .requestMatchers(
                                DELETE,
                                prefix+"/orders/**")
                        .hasRole(Role.ADMIN)


                        .anyRequest().authenticated();
            });
         http.cors(new Customizer<CorsConfigurer<HttpSecurity>>() {
            @Override
            public void customize(CorsConfigurer<HttpSecurity> httpSecurityCorsConfigurer) {
                CorsConfiguration configuration = new CorsConfiguration();
                configuration.setAllowedOrigins(List.of("*"));
                configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT",
                        "PATCH", "DELETE", "OPTIONS"));
                configuration.setAllowedHeaders(Arrays.asList("authorization", "content-type",
                        "x-auth-token"));
                configuration.setExposedHeaders(List.of("x-auth-token"));
                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);
                httpSecurityCorsConfigurer.configurationSource(source);
            }
        });
        return http.build();
    }

}
