FROM nginx:alpine

# 安装必要的工具
RUN apk add --no-cache \
    curl \
    bash \
    && rm -rf /var/cache/apk/*

# 复制nginx配置
COPY nginx.conf /etc/nginx/nginx.conf

# 创建日志目录
RUN mkdir -p /var/log/nginx /var/cache/nginx

# 设置工作目录
WORKDIR /usr/share/nginx/html

# 暴露端口
EXPOSE 80

# 健康检查
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
    CMD curl -f http://localhost/health || exit 1

# 启动nginx
CMD ["nginx", "-g", "daemon off;"]