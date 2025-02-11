package cn.legaltech.lb.im.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT 工具类
 *
 * @author Harrison
 * @version 1.0.0
 * @since 2024/11/28
 */
public class JwtUtil {

    private static final String SECRET_KEY = "your-secret-key"; // 替换为你的安全密钥
    private static final long EXPIRATION_TIME = 86400000L; // 1 天过期时间（毫秒）

    /**
     * 生成 JWT Token
     *
     * @param userId   用户ID
     * @param username 用户名
     * @return 生成的 JWT Token
     */
    public static String generateToken(String userId, String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        return Jwts.builder()
                .claims(claims)  // 设置自定义的 Claims
                .issuedAt(new Date())  // 设置签发时间
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))  // 设置过期时间
                .signWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()))  // 使用 HMAC SHA-256 算法和密钥进行签名
                .compact();  // 构建 JWT Token
    }

    /**
     * 验证 Token 并返回其 Claims
     *
     * @param token token值
     * @return Claims
     */
    public static Claims validateToken(String token) {
        try {
            return Jwts.parser()  // 使用新的 parserBuilder 方法（与 0.12.6 版本兼容）
                    .setSigningKey(SECRET_KEY)  // 设置签名密钥
                    .build()
                    .parseClaimsJws(token)  // 解析 JWT
                    .getBody();  // 获取 Payload 部分的 Claims
        } catch (JwtException e) {
            throw new IllegalArgumentException("Invalid token: " + e.getMessage());
        }
    }

    /**
     * 检查 Token 是否过期
     *
     * @param token token值
     * @return true 已过期
     */
    public static boolean isTokenExpired(String token) {
        Claims claims = validateToken(token);  // 获取 Claims
        return claims.getExpiration().before(new Date());  // 检查过期时间
    }

    /**
     * 从 Token 中获取用户信息
     *
     * @param token token值
     * @return 用户名
     */
    public static String getUsernameFromToken(String token) {
        Claims claims = validateToken(token);
        return claims.get("username", String.class);  // 获取用户名
    }
}