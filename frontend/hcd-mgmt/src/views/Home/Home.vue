<template>
  <div class="home">
    <div class="home-header">
      <div class="home-header-title">
        欢迎使用火车订票后台管理系统
      </div>
    </div>
  </div>
</template>

<script setup>
// 初始化 Vue 组件逻辑可在此处编写
</script>

<style scoped lang="scss">
// 使用@use替代弃用的@import规则
@use '@/styles/global.scss';

.home {
  @extend %center-layout;
}
.home-header {
  padding: 20px;
}
.home-header-title {
  font-size: 24px;
  font-weight: bold;
  text-align: center;
}
</style>
