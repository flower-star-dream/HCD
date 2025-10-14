#!/bin/bash

# 火车订票系统前端部署脚本
# 支持环境: staging, production

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 配置变量
ENV=${1:-staging}
BACKEND_URL=${BACKEND_URL:-http://localhost:8080}
REGISTRY=${REGISTRY:-your-registry.com}
PROJECT_NAME="hcd-frontend"
TIMESTAMP=$(date +%Y%m%d_%H%M%S)

# 显示帮助信息
show_help() {
    echo "Usage: $0 [staging|production]"
    echo ""
    echo "Environment variables:"
    echo "  BACKEND_URL    Backend API URL"
    echo "  REGISTRY       Docker registry URL"
    echo ""
    echo "Examples:"
    echo "  $0 staging"
    echo "  $0 production"
}

# 检查依赖
check_dependencies() {
    echo -e "${YELLOW}检查依赖...${NC}"

    if ! command -v docker &> /dev/null; then
        echo -e "${RED}错误: Docker 未安装${NC}"
        exit 1
    fi

    if ! command -v docker-compose &> /dev/null; then
        echo -e "${RED}错误: Docker Compose 未安装${NC}"
        exit 1
    fi

    echo -e "${GREEN}依赖检查完成${NC}"
}

# 构建项目
build_projects() {
    echo -e "${YELLOW}开始构建项目...${NC}"

    # 构建 hcd-mgmt
    echo "构建 hcd-mgmt..."
    cd frontend/hcd-mgmt
    npm ci
    npm run build
    cd ../..

    # 构建 hcd-applet
    echo "构建 hcd-applet..."
    cd frontend/hcd-applet
    npm ci
    npm run build:h5
    cd ../..

    echo -e "${GREEN}项目构建完成${NC}"
}

# 构建Docker镜像
build_docker_images() {
    echo -e "${YELLOW}构建Docker镜像...${NC}"

    # 构建Nginx镜像
    docker build -t ${REGISTRY}/${PROJECT_NAME}-nginx:${TIMESTAMP} \
                -t ${REGISTRY}/${PROJECT_NAME}-nginx:latest \
                -f frontend/nginx/Dockerfile frontend/nginx

    echo -e "${GREEN}Docker镜像构建完成${NC}"
}

# 推送镜像到仓库
push_images() {
    echo -e "${YELLOW}推送镜像到仓库...${NC}"

    docker push ${REGISTRY}/${PROJECT_NAME}-nginx:${TIMESTAMP}
    docker push ${REGISTRY}/${PROJECT_NAME}-nginx:latest

    echo -e "${GREEN}镜像推送完成${NC}"
}

# 部署到服务器
deploy_to_server() {
    echo -e "${YELLOW}部署到 ${ENV} 环境...${NC}"

    local deploy_dir="/opt/${PROJECT_NAME}-${ENV}"

    # 创建部署目录
    ssh ${DEPLOY_USER}@${DEPLOY_HOST} "mkdir -p ${deploy_dir}"

    # 复制配置文件
    scp frontend/docker-compose.yml ${DEPLOY_USER}@${DEPLOY_HOST}:${deploy_dir}/
    scp frontend/.env.${ENV} ${DEPLOY_USER}@${DEPLOY_HOST}:${deploy_dir}/.env

    # 远程部署
    ssh ${DEPLOY_USER}@${DEPLOY_HOST} "
        cd ${deploy_dir} &&
        export BACKEND_URL=${BACKEND_URL} &&
        docker-compose pull &&
        docker-compose up -d --force-recreate &&
        docker image prune -f
    "

    echo -e "${GREEN}部署完成${NC}"
}

# 健康检查
health_check() {
    echo -e "${YELLOW}健康检查...${NC}"

    local max_attempts=30
    local attempt=1

    while [ $attempt -le $max_attempts ]; do
        if curl -f -s http://${DEPLOY_HOST}/health > /dev/null; then
            echo -e "${GREEN}服务健康检查通过${NC}"
            return 0
        fi

        echo "等待服务启动... (${attempt}/${max_attempts})"
        sleep 10
        ((attempt++))
    done

    echo -e "${RED}健康检查失败${NC}"
    exit 1
}

# 回滚功能
rollback() {
    echo -e "${YELLOW}回滚到上一个版本...${NC}"

    local deploy_dir="/opt/${PROJECT_NAME}-${ENV}"

    ssh ${DEPLOY_USER}@${DEPLOY_HOST} "
        cd ${deploy_dir} &&
        docker-compose down &&
        docker-compose up -d --force-recreate
    "

    echo -e "${GREEN}回滚完成${NC}"
}

# 清理旧镜像
cleanup() {
    echo -e "${YELLOW}清理旧镜像...${NC}"

    # 本地清理
    docker image prune -f

    # 远程清理
    ssh ${DEPLOY_USER}@${DEPLOY_HOST} "docker image prune -f"

    echo -e "${GREEN}清理完成${NC}"
}

# 主流程
main() {
    case "$ENV" in
        staging|production)
            echo -e "${GREEN}开始部署到 ${ENV} 环境...${NC}"
            ;;
        *)
            show_help
            exit 1
            ;;
    esac

    check_dependencies
    build_projects
    build_docker_images

    if [ "$ENV" = "production" ]; then
        push_images
    fi

    deploy_to_server
    health_check
    cleanup

    echo -e "${GREEN}部署成功！${NC}"
}

# 处理命令行参数
case "${1:-}" in
    --help|-h)
        show_help
        exit 0
        ;;
    rollback)
        rollback
        exit 0
        ;;
    *)
        main "$@"
        ;;
esac