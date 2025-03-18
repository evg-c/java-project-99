package hexlet.code.config;

import hexlet.code.service.CustomUserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtDecoder jwtDecoder;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CustomUserDetailService userService;

    /**
     * Метод определяет правила доступа к различным URL-адресам, настройки сессий и JWT-аутентификацию.
     * @param http - параметр для конфигурации безопасности.
     * @param introspector - параметр для разрешения управляющих механизмов.
     * @return http - возвращает настроенный объект HttpSecurity
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, HandlerMappingIntrospector introspector)
        throws Exception {
        // По умолчанию все запрещено
        return http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // разрешаем доступ к /api/login, чтобы аутентифицироваться и получить токен
                        .requestMatchers("/api/login").permitAll()
                        .requestMatchers("/welcome").permitAll()
                        .requestMatchers("/").permitAll()
                        .requestMatchers("/index.html").permitAll()
                        .requestMatchers("/assets/**").permitAll()
                        .requestMatchers("/h2-console").permitAll()
                        .requestMatchers("/swagger-ui*.*").permitAll()
                        .requestMatchers("/swagger-ui/*.*").permitAll()
                        .requestMatchers("/api-docs*.*").permitAll()
                        .requestMatchers("/api-docs").permitAll()
                        .requestMatchers("/api-docs/*.*").permitAll()
                        //.requestMatchers("/api/**").permitAll()
                        //.requestMatchers(HttpMethod.POST, "/api/users").permitAll()
                        .anyRequest().authenticated())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2ResourceServer((rs) -> rs
                        .jwt((jwt) -> jwt.decoder(jwtDecoder)))
                .httpBasic(Customizer.withDefaults())
                .build();
    }

    /**
     * Метод предоставляет менеджер аутентификации для использования в приложении.
     * @param http - параметр для конфигурации безопасности.
     * @return http - возвращает объект AuthenticationManager
     */
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .build();
    }

    /**
     * Метод определяет провайдер аутентификации, который использует сервис пользователей и кодировщик паролей.
     * @param auth - менеджер аутентификации.
     * @return - возвращает настроенный объект AuthenticationProvider
     */
    @Bean
    public AuthenticationProvider daoAuthProvider(AuthenticationManagerBuilder auth) {
        var provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }
}
