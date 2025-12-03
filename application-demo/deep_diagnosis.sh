#!/bin/bash

echo "=== 深入诊断 Redis 连接问题 ==="

# 1. 检查 Spring Boot 版本和依赖
echo "1. Spring Boot 版本和依赖检查:"
if [ -f "pom.xml" ]; then
    echo "Spring Boot 版本:"
    grep -A1 -B1 "spring-boot-starter-parent" pom.xml | grep "version" | head -1
    
    echo ""
    echo "Redis 相关依赖:"
    grep -A1 -B1 "redis" pom.xml
else
    echo "❌ 未找到 pom.xml"
fi
echo ""

# 2. 检查应用程序启动参数
echo "2. 应用程序启动参数检查:"
echo "检查是否有环境变量覆盖配置:"
ENV_VARS=(
    "SPRING_REDIS_SENTINEL_MASTER"
    "SPRING_REDIS_SENTINEL_NODES"
    "SPRING_REDIS_PASSWORD"
    "SPRING_REDIS_HOST"
    "SPRING_REDIS_PORT"
)

for var in "${ENV_VARS[@]}"; do
    if [ -n "${!var}" ]; then
        echo "⚠️  环境变量 $var 已设置: ${!var}"
    fi
done
echo ""

# 3. 检查 Spring Boot 自动配置
echo "3. Spring Boot 自动配置检查:"
echo "检查自动配置类:"
if [ -f "src/main/resources/application.properties" ]; then
    echo "application.properties 中的 Redis 配置:"
    grep -i "redis" src/main/resources/application.properties
else
    echo "❌ 未找到 application.properties"
fi
echo ""

# 4. 检查网络连接细节
echo "4. 网络连接细节检查:"
echo "测试从 Java 应用程序的角度连接:"
cat > /tmp/JavaConnectionTest.java << 'JAVA'
import java.net.Socket;
import java.net.InetSocketAddress;

public class JavaConnectionTest {
    public static void main(String[] args) {
        String[] hosts = {"localhost", "127.0.0.1"};
        int[] ports = {26379, 26380, 26381};
        
        for (String host : hosts) {
            for (int port : ports) {
                try (Socket socket = new Socket()) {
                    socket.connect(new InetSocketAddress(host, port), 5000);
                    System.out.println("✅ Java 可以连接到 " + host + ":" + port);
                } catch (Exception e) {
                    System.out.println("❌ Java 无法连接到 " + host + ":" + port + " - " + e.getMessage());
                }
            }
        }
    }
}
JAVA

javac /tmp/JavaConnectionTest.java
if [ $? -eq 0 ]; then
    java JavaConnectionTest
else
    echo "❌ 无法编译 Java 测试程序"
fi
echo ""

# 5. 检查 Docker 容器日志
echo "5. Docker 容器日志检查:"
for i in {1..3}; do
    echo "--- redis-sentinel-$i 最近日志 ---"
    docker logs redis-sentinel-$i --tail=5
    echo ""
done
echo ""

# 6. 检查 Spring Boot 详细错误
echo "6. Spring Boot 详细错误分析:"
echo "错误信息显示: 'All sentinels down, cannot determine where is mymaster master is running...'"
echo ""
echo "可能的原因:"
echo "1. Spring Boot 无法解析 localhost 或 127.0.0.1"
echo "2. 防火墙或安全策略阻止连接"
echo "3. Docker 网络配置问题"
echo "4. Spring Boot 版本与 Redis 客户端版本不兼容"
echo "5. 自定义配置类与自动配置冲突"
echo ""

# 7. 检查 Spring Boot 启动时的详细日志
echo "7. 启用详细日志的建议:"
cat > /tmp/debug-logging.properties << 'PROPERTIES'
# 启用最详细的日志
logging.level.org.springframework.data.redis=TRACE
logging.level.redis.clients.jedis=TRACE
logging.level.org.springframework.boot=DEBUG
logging.level.com.example.demo=DEBUG

# 启用 Spring Boot 自动配置报告
debug=true
PROPERTIES

echo "将以下配置添加到 application.properties 以启用详细日志:"
cat /tmp/debug-logging.properties
echo ""

echo "=== 诊断完成 ==="
