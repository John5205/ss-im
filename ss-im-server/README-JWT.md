# JWT 配置说明

## 密钥管理

JWT 密钥现在支持多种配置方式，按优先级排序：

### 1. 环境变量（推荐）
```bash
export JWT_SECRET_KEY="your-super-secret-jwt-key-here-must-be-at-least-256-bits-long"
```

### 2. 系统属性
```bash
java -Djwt.secret.key=your-secret-key -jar ss-im-server.jar
```

### 3. 配置文件
在 `application.yml` 或 `application.properties` 中设置：
```yaml
jwt:
  secret-key: your-secret-key
  expiration-time: 86400000
```

### 4. 自动生成（开发环境）
如果以上都没有配置，系统会自动生成一个安全的 256 位密钥，但会输出警告信息。

## 安全建议

1. **生产环境必须设置自定义密钥**，不要使用自动生成的密钥
2. **密钥长度至少 256 位**（32 字节）
3. **定期轮换密钥**，但要注意已签发的 token 会失效
4. **不要在代码中硬编码密钥**
5. **使用环境变量或安全的配置管理系统**

## 生成安全密钥

### 使用 OpenSSL
```bash
openssl rand -base64 32
```

### 使用 Java
```java
import java.security.SecureRandom;
import java.util.Base64;

SecureRandom random = new SecureRandom();
byte[] keyBytes = new byte[32];
random.nextBytes(keyBytes);
String secretKey = Base64.getEncoder().encodeToString(keyBytes);
System.out.println("Secret Key: " + secretKey);
```

### 使用在线工具
- 确保使用可信的在线随机字符串生成器
- 生成至少 32 字节的随机字符串

## 配置示例

### Docker 环境
```dockerfile
ENV JWT_SECRET_KEY=your-secret-key-here
```

### Kubernetes 环境
```yaml
apiVersion: v1
kind: Secret
metadata:
  name: jwt-secret
type: Opaque
data:
  secret-key: <base64-encoded-secret>
```

### 开发环境
```bash
# 在项目根目录创建 .env 文件
echo "JWT_SECRET_KEY=your-development-secret-key" > .env
```
