package com.example.demo.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtils {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    // 读取配置
    @Value("${jwt.secret-key.access}")
    private String accessTokenSecret;

    @Value("${jwt.secret-key.refresh}")
    private String refreshTokenSecret;

    @Value("${jwt.expiration.access:5000}")
    private long accessTokenExpire;

    @Value("${jwt.expiration.refresh:604800000}")
    private long refreshTokenExpire;

    // 假设有一个Token黑名单服务（如Redis），用于使已注销的令牌失效
    // 您需要实现这个服务接口
    // @Autowired
    // private TokenBlacklistService tokenBlacklistService;

    /**
     * 生成Access Token
     */
    public String generateAccessToken(Map<String, Object> claims) {
        return buildToken(claims, accessTokenExpire, accessTokenSecret);
    }

    /**
     * 生成Refresh Token
     */
    public String generateRefreshToken(Map<String, Object> claims) {
        return buildToken(claims, refreshTokenExpire, refreshTokenSecret);
    }

    private String buildToken(Map<String, Object> claims, long expiration, String secretKey) {
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 增强的Access Token验证方法
     * @param token 待验证的令牌
     * @return 如果验证成功，返回解析出的声明体(Claims)；失败则返回null，并记录日志。
     */
    public Claims validateAccessToken(String token) {
        return validateToken(token, accessTokenSecret, "Access");
    }

    /**
     * 增强的Refresh Token验证方法
     * @param token 待验证的令牌
     * @return 如果验证成功，返回解析出的声明体(Claims)；失败则返回null。
     */
    public Claims validateRefreshToken(String token) {
        return validateToken(token, refreshTokenSecret, "Refresh");
    }

    /**
     * 核心的令牌验证逻辑
     * @param token 待验证的令牌
     * @param secretKey 用于验证签名的密钥
     * @param tokenType 令牌类型（如"Access", "Refresh"），用于日志记录
     * @return 验证成功的Claims，或验证失败时返回null
     */
    private Claims validateToken(String token, String secretKey, String tokenType) {
        // 基础检查
        if (token == null || token.trim().isEmpty()) {
            logger.warn("[{} Token 验证] 失败：Token 为空或为空白字符串", tokenType);
            return null;
        }

        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        String compact = Jwts.builder().addClaims(new HashMap<>()).signWith(key,SignatureAlgorithm.HS256).compact();

        try {
            // 1. 解析并验证JWT签名和基本结构
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            // 2. (可选) 检查令牌是否在黑名单中（例如用户已注销）
            // if (tokenBlacklistService.isBlacklisted(token)) {
            //     logger.warn("[{} Token 验证] 失败：令牌已被加入黑名单 (用户已注销)", tokenType);
            //     return null;
            // }

            // 3. 验证过期时间 (Jwts.parser() 默认会检查，这里可以添加自定义逻辑，如接近过期时告警)
            Date expiration = claims.getExpiration();
            if (expiration.before(new Date())) {
                // 这个异常通常会被上面的Jwts.parser()捕获，此处作为额外保障
                logger.warn("[{} Token 验证] 失败：令牌已过期。过期时间: {}", tokenType, expiration);
                return null;
            }

            // 4. 可以在这里添加其他自定义声明验证，例如签发者(iss)、受众(aud)等
            // if (!"your-expected-issuer".equals(claims.getIssuer())) {
            //     logger.warn("[{} Token 验证] 失败：令牌签发者不匹配", tokenType);
            //     return null;
            // }

            logger.debug("[{} Token 验证] 成功", tokenType);
            return claims;

        } catch (ExpiredJwtException e) {
            // 明确捕获过期异常
            logger.warn("[{} Token 验证] 失败：令牌已过期。过期时间: {}", tokenType, e.getClaims().getExpiration());
        } catch (UnsupportedJwtException e) {
            logger.warn("[{} Token 验证] 失败：不支持的JWT格式", tokenType);
        } catch (MalformedJwtException e) {
            logger.warn("[{} Token 验证] 失败：JWT字符串结构不正确或已被篡改", tokenType);
        } catch (SignatureException e) {
            logger.warn("[{} Token 验证] 失败：签名验证失败 (密钥错误或令牌被篡改)", tokenType);
        } catch (IllegalArgumentException e) {
            logger.warn("[{} Token 验证] 失败：令牌字符串为null或空，或解析时发生其他非法参数错误", tokenType);
        } catch (JwtException e) {
            // 捕获其他所有与JWT相关的异常
            logger.error("[{} Token 验证] 失败：处理JWT时发生意外异常", tokenType, e);
        } catch (Exception e) {
            // 捕获其他任何意想不到的异常
            logger.error("[{} Token 验证] 失败：系统异常", tokenType, e);
        }
        return null;
    }

    /**
     * 解析Access Token（不进行验证，仅解析。适用于已知令牌有效但需获取内容的场景，慎用！）
     */
    public Claims parseAccessTokenWithoutValidation(String token) {
        return parseTokenWithoutValidation(token, accessTokenSecret);
    }

    /**
     * 解析Refresh Token（不进行验证，仅解析。慎用！）
     */
    public Claims parseRefreshTokenWithoutValidation(String token) {
        return parseTokenWithoutValidation(token, refreshTokenSecret);
    }

    /**
     * 核心的令牌解析逻辑（不验证签名和过期时间，仅Base64解码）
     * 注意：此方法不验证令牌的真实性，仅用于特殊场景（如日志记录、分析已过期的令牌内容）
     */
    private Claims parseTokenWithoutValidation(String token, String secretKey) {
        try {
            // 注意：此解析器设置了setSigningKey，但禁用了签名验证，因此不能信任解析出的内容。
            SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            logger.error("解析令牌（不验证）时发生异常", e);
            return null;
        }
    }

    /**
     * 便捷方法：从Authorization请求头中提取Token（去除"Bearer "前缀）
     */
    public String extractTokenFromHeader(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return null;
    }

    /**
     * 检查令牌是否在指定时间内即将过期（用于自动刷新逻辑）
     * @param claims 令牌的声明体
     * @param milliseconds 过期阈值（毫秒），例如300000表示5分钟内过期则返回true
     * @return 如果令牌在指定时间内过期，返回true
     */
    public boolean isTokenExpiringSoon(Claims claims, long milliseconds) {
        if (claims == null) return false;
        Date expiration = claims.getExpiration();
        long timeUntilExpiration = expiration.getTime() - System.currentTimeMillis();
        return timeUntilExpiration > 0 && timeUntilExpiration <= milliseconds;
    }
}
