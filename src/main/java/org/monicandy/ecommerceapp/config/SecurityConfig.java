package org.monicandy.ecommerceapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // 公开访问的路径
                        .requestMatchers(
                                "/",
                                "/home",
                                "/login",
                                "/register",
                                "/products/**",           // 产品列表和详情页
                                "/product/**",            // 产品相关页面
                                "/css/**",                // CSS静态资源
                                "/js/**",                 // JS静态资源
                                "/images/**",             // 图片资源
                                "/favicon.ico",           // 网站图标
                                "/error"                  // 错误页面
                        ).permitAll()

                        // 需要登录的路径
                        .requestMatchers(
                                "/cart/**",               // 购物车所有页面
                                "/orders/**",             // 订单所有页面
                                "/profile/**",            // 个人资料
                                "/checkout/**",           // 结算页面
                                "/payment/**"             // 支付页面
                        ).authenticated()

                        // 其他所有请求都需要登录
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/products/page", true)
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .permitAll()
                )
                .rememberMe(remember -> remember
                        .key("uniqueAndSecret")
                        .tokenValiditySeconds(86400)
                )
                .csrf(csrf -> csrf.disable())
                .httpBasic(basic -> basic.disable());

        return http.build();
    }
}


