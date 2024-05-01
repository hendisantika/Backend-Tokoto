package com.apiecommerce.tokoto.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import static com.apiecommerce.tokoto.user.Permission.*;
import static com.apiecommerce.tokoto.user.Role.*;
import static org.springframework.http.HttpMethod.*;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfiguration {

    private static final String[] WHITE_LIST_URL = {"/api/v1/auth/**",
            "/v2/api-docs",
            "/api/v1/genders/list",
            "/api/categories/all",
            "/api/categories/get/**",
            "/api/categories/{imageName}",
            "/api/products/clearAllCache",
	        "/api/v1/auth/findUserByEmail/**",
            "/api/location/indonesia/province/all",
            "/api/images/**",
            "/api/products/list",
            "/api/products/find/byId/**",
            "/api/products/find/byTitleKey/**",
            "/api/products/find/byCategoryList/**",
            "/api/products/find/byPrice/**",
            "/api/products/export",
            "/api/products/import",
            "/api/orders/place",
            "/sendMail",
            "/sendMailWithAttachment",
            "/api/messages",
            "/api/messages/{channelId}",
            "/api/messages/channel/chat/**",
            "/api/integrations/social/twitter/postTweet",
            "/api/integrations/social/whatsapp/sendMessage",
            "/api/products/supply/addSupply/**",
            "/api/products/supply/removeSupply/**",
            "/api/location/indonesia/province/all",
            "/api/location/indonesia/{provinceName}/regencyAll",
            "/api/location/indonesia/{provinceName}/{regencyName}",
            "/api/location/indonesia/{provinceName}/{regencyName}/districtAll",
            "/api/location/indonesia/{provinceName}/{regencyName}/{districtName}",
            "/api/location/indonesia/{provinceName}/{regencyName}/{districtName}/villageAll",
            "/api/location/indonesia/{provinceName}/{regencyName}/{districtName}/{villageName}",
            "/api/products/supply/all",
            "/api/products/supply/{productId}",
            "/api/products/supply/pay/{productId}",
            "/api/products/order/list",
            "/api/products/checkQuantity",
            "/api/products/order/checkout/{productId}",
            "/api/history/product",
            "/api/bot/chat",
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui/**",
            "/webjars/**",
            "/swagger-ui.html"};
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final LogoutHandler logoutHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(req ->
                        req.requestMatchers(WHITE_LIST_URL)
                                .permitAll()
                                .requestMatchers("/api/v1/management/**").hasAnyRole(ADMIN.name(), MANAGER.name())
                                .requestMatchers(GET, "/api/v1/management/**").hasAnyAuthority(ADMIN_READ.name(), MANAGER_READ.name())
                                .requestMatchers(POST, "/api/v1/management/**").hasAnyAuthority(ADMIN_CREATE.name(), MANAGER_CREATE.name())
                                .requestMatchers(PUT, "/api/v1/management/**").hasAnyAuthority(ADMIN_UPDATE.name(), MANAGER_UPDATE.name())
                                .requestMatchers(DELETE, "/api/v1/management/**").hasAnyAuthority(ADMIN_DELETE.name(), MANAGER_DELETE.name())
                                .requestMatchers("/api/categories/**").hasAnyRole(ADMIN.name(), MANAGER.name())
                                .requestMatchers(POST, "/api/categories/create").hasAnyAuthority(ADMIN_CREATE.name(), MANAGER_CREATE.name())
                                .requestMatchers(PATCH, "/api/categories/update/**").hasAnyAuthority(ADMIN_UPDATE.name(), MANAGER_UPDATE.name())
                                .requestMatchers(DELETE, "/api/categories/delete/**").hasAnyAuthority(ADMIN_DELETE.name(), MANAGER_DELETE.name())
                                .requestMatchers("/api/products/**").hasAnyRole(ADMIN.name(), MANAGER.name())
                                .requestMatchers(POST, "/api/products/create").hasAnyAuthority(ADMIN_CREATE.name(), MANAGER_CREATE.name())
                                .requestMatchers(PATCH, "/api/products/update/**").hasAnyAuthority(ADMIN_UPDATE.name(), MANAGER_UPDATE.name())
                                .requestMatchers(DELETE, "/api/products/delete/**").hasAnyAuthority(ADMIN_DELETE.name(), MANAGER_DELETE.name())
                                .requestMatchers(POST, "/api/products/addToCart/**").hasAnyAuthority(ADMIN_CREATE.name(), MANAGER_CREATE.name(), USER_CREATE.name())
                                .requestMatchers(GET, "/api/products/listOrderCart").hasAnyAuthority(ADMIN_READ.name(), MANAGER_READ.name(), USER_READ.name())
                                .requestMatchers("/api/orders/place").hasAnyRole(ADMIN.name(), MANAGER.name(), USER.name())
                                .requestMatchers(POST, "/api/orders/place").hasAnyAuthority(ADMIN_CREATE.name(), MANAGER_CREATE.name(), USER_CREATE.name())
                                .requestMatchers("/api/messages/send/**").hasAnyRole(ADMIN.name(), MANAGER.name(), USER.name())
                                .requestMatchers(POST, "/api/messages/send").hasAnyAuthority(ADMIN_CREATE.name(), MANAGER_CREATE.name(), USER_CREATE.name())
                                .requestMatchers(POST, "/api/messages/sendWithAttachment/**").hasAnyAuthority(ADMIN_CREATE.name(), MANAGER_CREATE.name(), USER_CREATE.name())
                                .requestMatchers(POST, "/api/location/indonesia/province/create").hasAnyAuthority(ADMIN_CREATE.name(), MANAGER_CREATE.name())
                                .requestMatchers(GET, "/api/v1/auth/findUserWithRegency/**").hasAnyAuthority(ADMIN_READ.name())
                                .requestMatchers(POST, "/api/v1/auth/saveUserProfile").hasAnyAuthority(ADMIN_CREATE.name(), MANAGER_CREATE.name(), USER_CREATE.name())
                                .anyRequest()
                                .authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .logout(logout ->
                        logout.logoutUrl("/api/v1/auth/logout")
                                .addLogoutHandler(logoutHandler)
                                .logoutSuccessHandler((request, response, authentication) -> SecurityContextHolder.clearContext())
                )
        ;

        return http.build();
    }
}