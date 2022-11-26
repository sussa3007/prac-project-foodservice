package com.example.foodserviceapp.config;

import com.example.foodserviceapp.auth.JwtTokenizer;
import com.example.foodserviceapp.auth.filter.JwtAuthenticationFilter;
import com.example.foodserviceapp.auth.filter.JwtVerificationFilter;
import com.example.foodserviceapp.auth.handler.MemberAccessDeniedHandler;
import com.example.foodserviceapp.auth.handler.MemberAuthenticationEntryPoint;
import com.example.foodserviceapp.auth.handler.MemberAuthenticationFailureHandler;
import com.example.foodserviceapp.auth.handler.MemberAuthenticationSuccessHandler;
import com.example.foodserviceapp.auth.oauth2.OAuth2MemberSuccessHandler;
import com.example.foodserviceapp.auth.oauth2.provider.CustomOAuth2Provider;
import com.example.foodserviceapp.auth.oauth2.service.CustomOAuth2UserService;
import com.example.foodserviceapp.auth.utils.JwtAuthorityUtils;
import com.example.foodserviceapp.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Value("${GOOGLE_AOUTH2_SAMPLEPROJECT_ID}")
    private String clientId;
    @Value("${GOOGLE_AOUTH2_SAMPLEPROJECT_SECRETKEY}")
    private String clientSecret;

    @Value("${KAKAO_AOUTH2_ID}")
    private String kakaoClientId;

    @Value("${KAKAO_AOUTH2_SECRETKEY}")
    private String kakaoClientSecret;



    private final JwtTokenizer jwtTokenizer;

    private final JwtAuthorityUtils authorityUtils;

    private final MemberService memberService;

    private final CustomOAuth2UserService oAuth2UserService;

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
                        .antMatchers(HttpMethod.DELETE, "/members/**").hasAnyRole("USER", "ADMIN")
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
                        .anyRequest().permitAll())
                .oauth2Login(oauth2 -> oauth2
                        .successHandler(new OAuth2MemberSuccessHandler(jwtTokenizer, memberService))
                        .userInfoEndpoint().userService(oAuth2UserService)
                );
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

    private ClientRegistration clientRegistration(OAuth2ClientProperties clientProperties, String client) {
        if("google".equals(client)) {
            OAuth2ClientProperties.Registration google = clientProperties.getRegistration().get("google");
            return CustomOAuth2Provider.GOOGLE
                    .getBuilder(client)
                    .clientId(google.getClientId())
                    .clientSecret(clientSecret)
                    .build();
        }
        if("kakao".equals(client)) {
            OAuth2ClientProperties.Registration kakao = clientProperties.getRegistration().get("kakao");
            return CustomOAuth2Provider.KAKAO
                    .getBuilder(client)
                    .clientId(kakao.getClientId())
                    .clientSecret(kakaoClientSecret)
                    .jwkSetUri("temp")
                    .build();
        }
        return null;
    }

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository(
            OAuth2ClientProperties oAuth2ClientProperties
    ) {
        List<ClientRegistration> registrations = oAuth2ClientProperties
                .getRegistration().keySet().stream()
                .map(client -> clientRegistration(oAuth2ClientProperties, client))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        return new InMemoryClientRegistrationRepository(registrations);
    }
}
