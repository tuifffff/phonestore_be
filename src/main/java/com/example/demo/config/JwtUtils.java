package com.example.demo.config; // Đổi lại package nếu ông để thư mục khác

import com.example.demo.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private int jwtExpirationMs;

    // Lấy cái khóa bí mật từ file properties ra để dùng
    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    // 1. Hàm TẠO Token khi user đăng nhập thành công
    public String generateJwtToken(User user) {
        // 1. Tạo chuỗi Scope: Gom Role và tất cả Permission lại
        StringBuilder scopeBuilder = new StringBuilder();
        if (user.getRole() != null) {
            // Thêm Role (Ví dụ: ROLE_ADMIN)
            scopeBuilder.append("ROLE_").append(user.getRole().getName());

            // Thêm các Permission đi kèm (Ví dụ: CREATE_PRODUCT, DELETE_PRODUCT...)
            if (user.getRole().getPermissions() != null) {
                user.getRole().getPermissions().forEach(p ->
                        scopeBuilder.append(" ").append(p.getName())
                );
            }
        }
        return Jwts.builder()
                .setSubject(user.getUsername()) // Username sẽ được lưu trong phần Subject của Token
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .claim("scope", scopeBuilder.toString().trim())
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    // 2. Hàm LẤY Username từ Token gửi lên
    public String getUserNameFromJwtToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key()).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    // 3. Hàm KIỂM TRA Token xem có hợp lệ không (có bị hết hạn hay giả mạo không)
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key()).build().parse(authToken);
            return true;
        } catch (MalformedJwtException e) {
            System.err.println("Token không đúng định dạng: " + e.getMessage());
        } catch (ExpiredJwtException e) {
            System.err.println("Token đã hết hạn: " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            System.err.println("Token không được hỗ trợ: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("Chuỗi Token trống: " + e.getMessage());
        }
        return false;
    }
}