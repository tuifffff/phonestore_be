package com.example.demo.config;
import org.springframework.beans.factory.annotation.Value;
import lombok.experimental.NonFinal;
import javax.crypto.spec.SecretKeySpec;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.crypto.spec.SecretKeySpec;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // ⚠️ QUAN TRỌNG: Để các @PreAuthorize("hasRole('ADMIN')") ở Controller có tác dụng
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SecurityConfig {
    @NonFinal
    @Value("${jwt.secret}")
    String jwtSecret;
    JwtAuthFilter jwtAuthFilter;

    // 1. Dùng BCrypt luôn cho nó "thật", đừng dùng NoOp nữa boboi ơi!
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();

        // 1. Chỉ định cho Spring biết: "Hãy nhìn vào ô 'scope' để lấy quyền"
        jwtGrantedAuthoritiesConverter.setAuthoritiesClaimName("scope");

        // 2. Vì Token đã có "ROLE_..." rồi, nên ta để Prefix là rỗng để không bị trùng lặp
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);

        return jwtAuthenticationConverter;
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
    @NonFinal // <--- Thêm dòng này để Spring nạp được link từ Render vào
    @Value("${app.frontend-url:http://localhost:5173}") // Thêm giá trị mặc định sau dấu : cho chắc
    String frontendUrl;
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(cors -> cors.configurationSource(request -> {
                    var corsConfig = new org.springframework.web.cors.CorsConfiguration();
                    corsConfig.setAllowedOrigins(java.util.List.of("http://localhost:3000", "http://localhost:5173",frontendUrl));
                    corsConfig.setAllowedMethods(java.util.List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
                    corsConfig.setAllowedHeaders(java.util.List.of("*"));
                    corsConfig.setAllowCredentials(true);
                    return corsConfig;
                }))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // 0. Cho phép tất cả OPTIONS preflight requests
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // A. Các API mở cửa hoàn toàn
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll() // Khách chỉ được XEM sản phẩm
                        .requestMatchers("/api/user/forgot-password/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/reviews/**").permitAll() // Khách xem đánh giá

                        // B. Các API chỉ dành cho ADMIN (Phòng hờ nếu quên đặt @PreAuthorize)
                        .requestMatchers("/api/roles/**").hasRole("ADMIN")
                        .requestMatchers("/api/permissions/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/orders/*/status").hasAuthority("APPROVE_ORDER") // Cập nhật trạng thái đơn hàng
                        // C. Các API Admin quản lý (POST/PUT/DELETE sản phẩm)
                        .requestMatchers(HttpMethod.POST, "/api/products/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/products/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/products/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/brands/**").permitAll() // Khách xem hãng máy thoải mái
                        .requestMatchers("/api/statistics/**").hasRole("ADMIN") // Chỉ Admin mới được xem doanh thu
                        .requestMatchers(HttpMethod.POST, "/api/reviews/**").hasRole("USER") // User mới được đánh giá
                        .requestMatchers(HttpMethod.PUT, "/api/users/*/role").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/users/*").hasRole("ADMIN")
                        // D. Còn lại (Cart, Order, Update Profile...) cứ đăng nhập là vào được
                        // test payment
                        .requestMatchers("/api/auth/**").permitAll() // Mở cho Đăng nhập/Đăng ký
                        .requestMatchers("/api/payment/**").permitAll() // MỞ CỬA CHO VNPAY GỌI VỀ CHỖ NÀY
                        .anyRequest().authenticated()
                )
        .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
        );
        // Thêm Filter JWT trước khi xác thực username/password
        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
    @Bean
    public JwtDecoder jwtDecoder() {
        byte[] bytes = io.jsonwebtoken.io.Decoders.BASE64.decode(jwtSecret);
        SecretKeySpec secretKeySpec = new SecretKeySpec(bytes, "HmacSHA256");
        return NimbusJwtDecoder.withSecretKey(secretKeySpec).build();
    }

}