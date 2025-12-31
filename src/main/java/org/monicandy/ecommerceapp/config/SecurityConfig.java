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
                        // 放行所有请求（课设阶段）
                        .anyRequest().permitAll()
                )
                // 禁用默认的表单登录，使用我们自己的登录页
                .formLogin(form -> form
                        .loginPage("/login")           // 指定登录页面
                        .loginProcessingUrl("/login")  // 指定登录处理URL
                        .defaultSuccessUrl("/products/page", true)  // 登录成功后跳转
                        .failureUrl("/login?error=true")  // 登录失败后跳转
                        .permitAll()                    // 允许所有用户访问登录页
                )
                // 退出登录配置
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .permitAll()
                )
                // 记住我功能
                .rememberMe(remember -> remember
                        .key("uniqueAndSecret")
                        .tokenValiditySeconds(86400)
                )
                // 关闭 CSRF（开发阶段）
                .csrf(csrf -> csrf.disable())
                // 禁用默认的HTTP Basic认证
                .httpBasic(basic -> basic.disable());

        return http.build();
    }
}


