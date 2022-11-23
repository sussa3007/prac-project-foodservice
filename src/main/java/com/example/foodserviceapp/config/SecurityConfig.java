package com.example.foodserviceapp.config;

import com.example.foodserviceapp.auth.JwtTokenizer;
import com.example.foodserviceapp.auth.filter.JwtAuthenticationFilter;
import com.example.foodserviceapp.auth.filter.JwtVerificationFilter;
import com.example.foodserviceapp.auth.handler.MemberAccessDeniedHandler;
import com.example.foodserviceapp.auth.handler.MemberAuthenticationEntryPoint;
import com.example.foodserviceapp.auth.handler.MemberAuthenticationFailureHandler;
import com.example.foodserviceapp.auth.handler.MemberAuthenticationSuccessHandler;
import com.example.foodserviceapp.auth.utils.JwtAuthorityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenizer jwtTokenizer;

    private final JwtAuthorityUtils authorityUtils;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.headers().frameOptions().sameOrigin()
                .and()
                .csrf().disable()
                .cors(Customizer.withDefaults())
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .formLogin().disable()
                .httpBasic().disable()
                .exceptionHandling()
                .authenticationEntryPoint(new MemberAuthenticationEntryPoint())
                .accessDeniedHandler(new MemberAccessDeniedHandler())
                .and()
                .apply(new CustomFilterConfig())
                .and()
                .authorizeRequests(auth -> auth
                        // test
                        .antMatchers(HttpMethod.GET, "/members/test").permitAll()
                        .antMatchers(HttpMethod.GET, "/foods/test").permitAll()
                        // member authorize
                        .antMatchers(HttpMethod.POST, "/members").permitAll()
                        .antMatchers(HttpMethod.PATCH, "/members/**").hasRole("USER")
                        .antMatchers(HttpMethod.GET, "/members").hasRole("ADMIN")
                        .antMatchers(HttpMethod.GET, "/members/**").hasAnyRole("USER", "ADMIN")
                        .antMatchers(HttpMethod.DELETE, "/members/**").hasRole("USER")
                        // food authorize
                        .antMatchers(HttpMethod.POST, "/foods").hasRole("ADMIN")
                        .antMatchers(HttpMethod.PATCH, "/foods/**").hasRole("ADMIN")
                        .antMatchers(HttpMethod.GET, "/foods").hasAnyRole("USER", "ADMIN")
                        .antMatchers(HttpMethod.GET, "/foods/**").hasAnyRole("USER", "ADMIN")
                        .antMatchers(HttpMethod.DELETE, "/foods/**").hasRole("ADMIN")
                        // order authorize
                        .antMatchers(HttpMethod.POST, "/orders").hasAnyRole("USER", "ADMIN")
                        .antMatchers(HttpMethod.PATCH, "/orders/**").hasAnyRole("USER", "ADMIN")
                        .antMatchers(HttpMethod.GET, "/orders").hasRole("ADMIN")
                        .antMatchers(HttpMethod.GET, "/*/orders").hasAnyRole("USER", "ADMIN")
                        .antMatchers(HttpMethod.GET, "/orders/**").hasAnyRole("USER", "ADMIN")
                        .antMatchers(HttpMethod.DELETE, "/orders/**").hasAnyRole("USER", "ADMIN")
                        .anyRequest().permitAll());
        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOrigins(Arrays.asList("*"));
        corsConfiguration.setAllowedMethods(Arrays.asList("GET","POST", "PATCH", "DELETE"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }

    public class CustomFilterConfig extends AbstractHttpConfigurer<CustomFilterConfig, HttpSecurity> {
        @Override
        public void configure(HttpSecurity builder) throws Exception {
            AuthenticationManager authenticationManager = builder.getSharedObject(AuthenticationManager.class);
            JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(authenticationManager, jwtTokenizer);

            jwtAuthenticationFilter.setFilterProcessesUrl("/auth/login");
            jwtAuthenticationFilter.setAuthenticationSuccessHandler(new MemberAuthenticationSuccessHandler());
            jwtAuthenticationFilter.setAuthenticationFailureHandler(new MemberAuthenticationFailureHandler());

            JwtVerificationFilter jwtVerificationFilter = new JwtVerificationFilter(jwtTokenizer, authorityUtils);

            builder.addFilter(jwtAuthenticationFilter)
                    .addFilterAfter(jwtVerificationFilter, JwtAuthenticationFilter.class);

        }
    }


}
