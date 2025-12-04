#!/bin/bash

# HCD项目部署脚本
# 支持开发、测试、生产环境的部署

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 默认配置
ENVIRONMENT="dev"
COMPOSE_FILE="docker-compose.yml"
BACKUP_DIR="./backups"
LOG_DIR="./logs"

# 帮助信息
show_help() {
    echo -e "${BLUE}HCD项目部署脚本${NC}"
    echo -e "用法: $0 [选项]"
    echo -e "选项:"
    echo -e "  -e, --environment   部署环境 (dev|test|prod) 默认: dev"
    echo -e "  -b, --backup        部署前创建备份"
    echo -e "  -r, --rollback      回滚到上一个版本"
    echo -e "  -c, --cleanup       清理无用资源"
    echo -e "  -h, --help          显示帮助信息"
    echo -e "  -l, --logs          查看日志"
    echo -e "  -s, --status        查看服务状态"
    echo -e "  --build             重新构建镜像"
    echo -e "  --pull              拉取最新镜像"
    echo -e ""
    echo -e "示例:"
    echo -e "  $0 -e prod -b       # 生产环境部署并备份"
    echo -e "  $0 -e test --build  # 测试环境重新构建"
    echo -e "  $0 -r               # 回滚操作"
}

# 日志函数
log_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

log_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

log_debug() {
    echo -e "${BLUE}[DEBUG]${NC} $1"
}

# 检查依赖
check_dependencies() {
    local deps=("docker" "docker-compose" "git")
    for dep in "${deps[@]}"; do
        if ! command -v $dep > /dev/null; then
            log_error "缺少依赖: $dep"
            exit 1
        fi
    done
}

# 检查Docker服务
 check_docker() {
    if ! docker info > /dev/null; then
        log_error "Docker服务未运行"
        exit 1
    fi
}

# 创建备份
create_backup() {
    log_info "创建备份..."

    local backup_name="hcd-backup-$(date +%Y%m%d-%H%M%S)"
    local backup_path="${BACKUP_DIR}/${backup_name}"

    mkdir -p "${backup_path}"

    # 备份数据库
    if docker-compose -f ${COMPOSE_FILE} ps | grep -q hcd-mysql; then
        log_info "备份MySQL数据库..."
        docker-compose -f ${COMPOSE_FILE} exec -T hcd-mysql mysqldump -uroot -proot123 --all-databases > "${backup_path}/all-databases.sql"
    fi

    # 备份配置文件
    log_info "备份配置文件..."
    cp -r backend/conf "${backup_path}/"
    cp -r frontend/nginx/conf "${backup_path}/nginx-conf"

    # 备份日志
    if [ -d "backend/logs" ]; then
        cp -r backend/logs "${backup_path}/"
    fi

    # 创建压缩包
    cd "${BACKUP_DIR}"
    tar -czf "${backup_name}.tar.gz" "${backup_name}"
    rm -rf "${backup_name}"
    cd - > /dev/null

    log_info "备份完成: ${backup_path}.tar.gz"
}

# 回滚操作
rollback() {
    log_info "执行回滚操作..."

    if [ ! -d "${BACKUP_DIR}" ]; then
        log_error "备份目录不存在"
        exit 1
    fi

    # 获取最新的备份
    local latest_backup=$(ls -t ${BACKUP_DIR}/hcd-backup-*.tar.gz 2>/dev/null | head -1)

    if [ -z "${latest_backup}" ]; then
        log_error "没有找到备份文件"
        exit 1
    fi

    log_info "使用备份: ${latest_backup}"

    # 停止当前服务
    docker-compose -f ${COMPOSE_FILE} down

    # 解压备份
    local backup_name=$(basename "${latest_backup}" .tar.gz)
    cd "${BACKUP_DIR}"
    tar -xzf "${backup_name}.tar.gz"
    cd - > /dev/null

    # 恢复配置文件
    cp -r "${BACKUP_DIR}/${backup_name}/backend/conf"/* backend/conf/
    cp -r "${BACKUP_DIR}/${backup_name}/nginx-conf"/* frontend/nginx/conf/

    # 恢复数据库
    if [ -f "${BACKUP_DIR}/${backup_name}/all-databases.sql" ]; then
        log_info "恢复数据库..."
        # 启动数据库服务
        docker-compose -f ${COMPOSE_FILE} up -d hcd-mysql
        sleep 30
        docker-compose -f ${COMPOSE_FILE} exec -T hcd-mysql mysql -uroot -proot123 < "${BACKUP_DIR}/${backup_name}/all-databases.sql"
    fi

    # 清理临时文件
    rm -rf "${BACKUP_DIR}/${backup_name}"

    log_info "回滚完成"
}

# 清理操作
cleanup() {
    log_info "清理无用资源..."

    # 停止并删除容器
    docker-compose -f ${COMPOSE_FILE} down --remove-orphans

    # 清理无用镜像
    docker image prune -f

    # 清理无用卷
    docker volume prune -f

    # 清理构建缓存
    docker builder prune -f

    log_info "清理完成"
}

# 查看状态
show_status() {
    log_info "服务状态:"
    docker-compose -f ${COMPOSE_FILE} ps

    log_info "容器资源使用情况:"
    docker stats --no-stream --format "table {{.Name}}\t{{.CPUPerc}}\t{{.MemUsage}}\t{{.NetIO}}\t{{.BlockIO}}"
}

# 查看日志
show_logs() {
    local service=${1:-""}
    if [ -n "${service}" ]; then
        docker-compose -f ${COMPOSE_FILE} logs -f ${service}
    else
        docker-compose -f ${COMPOSE_FILE} logs -f
    fi
}

# 服务检查
service_check() {
    log_info "执行服务检查..."

    local services=("hcd-nginx" "hcd-gateway" "hcd-user" "hcd-trainseat" "hcd-ticket" "hcd-order")
    local all_healthy=true

    for service in "${services[@]}"; do
        if docker-compose -f ${COMPOSE_FILE} ps | grep -q "${service}.*Up"; then
            log_info "✓ ${service} 运行正常"
        else
            log_error "✗ ${service} 未运行"
            all_healthy=false
        fi
    done

    if [ "${all_healthy}" = true ]; then
        log_info "所有服务检查通过"
        return 0
    else
        log_error "部分服务检查失败"
        return 1
    fi
}

# 部署函数
deploy() {
    log_info "开始部署到 ${ENVIRONMENT} 环境..."

    # 检查依赖
    check_dependencies
    check_docker

    # 创建必要的目录
    mkdir -p "${BACKUP_DIR}" "${LOG_DIR}"

    # 拉取最新代码
    if [ "${ENVIRONMENT}" != "dev" ]; then
        log_info "拉取最新代码..."
        git pull origin main
    fi

    # 构建镜像
    if [ "${BUILD_IMAGES}" = true ]; then
        log_info "构建Docker镜像..."
        docker-compose -f ${COMPOSE_FILE} build --no-cache
    fi

    # 拉取镜像
    if [ "${PULL_IMAGES}" = true ]; then
        log_info "拉取Docker镜像..."
        docker-compose -f ${COMPOSE_FILE} pull
    fi

    # 启动服务
    log_info "启动服务..."
    docker-compose -f ${COMPOSE_FILE} up -d

    # 等待服务启动
    log_info "等待服务启动..."
    sleep 30

    # 服务检查
    if service_check; then
        log_info "部署成功!"

        # 显示服务状态
        show_status

        # 显示访问信息
        echo -e ""
        echo -e "${GREEN}部署完成! 访问信息:${NC}"
        echo -e "  管理界面: http://localhost/mgmt/"
        echo -e "  API网关:  http://localhost:8080/"
        echo -e "  文档服务: http://localhost:8080/doc.html"
    else
        log_error "部署失败，请检查日志"
        show_logs
        exit 1
    fi
}

# 主函数
main() {
    local backup=false
    local rollback_op=false
    local cleanup_op=false
    local logs=false
    local status=false
    local build_images=false
    local pull_images=false
    local service_name=""

    # 解析参数
    while [[ $# -gt 0 ]]; do
        case $1 in
            -e|--environment)
                ENVIRONMENT="$2"
                shift 2
                ;;
            -b|--backup)
                backup=true
                shift
                ;;
            -r|--rollback)
                rollback_op=true
                shift
                ;;
            -c|--cleanup)
                cleanup_op=true
                shift
                ;;
            -h|--help)
                show_help
                exit 0
                ;;
            -l|--logs)
                logs=true
                if [[ -n "$2" && ! "$2" =~ ^- ]]; then
                    service_name="$2"
                    shift 2
                else
                    shift
                fi
                ;;
            -s|--status)
                status=true
                shift
                ;;
            --build)
                build_images=true
                shift
                ;;
            --pull)
                pull_images=true
                shift
                ;;
            *)
                log_error "未知参数: $1"
                show_help
                exit 1
                ;;
        esac
    done

    # 根据环境选择配置文件
    case ${ENVIRONMENT} in
        dev)
            COMPOSE_FILE="docker-compose.yml"
            ;;
        test)
            COMPOSE_FILE="docker-compose.test.yml"
            ;;
        prod)
            COMPOSE_FILE="docker-compose.prod.yml"
            ;;
        *)
            log_error "无效的环境: ${ENVIRONMENT}"
            exit 1
            ;;
    esac

    # 导出变量
    export ENVIRONMENT COMPOSE_FILE BACKUP_DIR LOG_DIR BUILD_IMAGES PULL_IMAGES

    # 执行操作
    if [ "${rollback_op}" = true ]; then
        rollback
    elif [ "${cleanup_op}" = true ]; then
        cleanup
    elif [ "${logs}" = true ]; then
        show_logs "${service_name}"
    elif [ "${status}" = true ]; then
        show_status
    else
        if [ "${backup}" = true ]; then
            create_backup
        fi
        deploy
    fi
}

# 如果直接运行脚本
if [[ "${BASH_SOURCE[0]}" == "${0}" ]]; then
    main "$@"
fi