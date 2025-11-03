// 定义组件模块类型
interface ComponentModule {
  default: any;
}

// 创建一个空对象用于存储所有组件
const components: Record<string, any> = {};

// 使用import.meta.glob动态导入所有组件
const componentModules = import.meta.glob('./**/*.vue', { eager: true }) as Record<string, ComponentModule>;

// 遍历所有导入的组件模块
Object.entries(componentModules).forEach(([path, module]) => {
  // 获取组件名称 - 改进正则表达式，使其能匹配各种路径格式
  // 从路径中提取文件名，例如 './TrainIcon/TrainIcon.vue' -> 'TrainIcon'
  const filename = path.split('/').pop();
  if (filename && module.default) {
    // 移除.vue扩展名
    const componentName = filename.replace(/\.vue$/, '');
    components[componentName] = module.default;
  }
});

// 导出所有组件作为对象和单独导出
export default components;
// 同时导出为命名导出，方便直接导入单个组件
export { components };