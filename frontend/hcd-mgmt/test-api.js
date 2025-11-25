// 简单的API测试脚本
const axios = require('axios');

// 测试配置
const config = {
  baseURL: 'http://localhost:8080/api/v1/mgmt',
  timeout: 5000,
  headers: {
    'X-Biz-Side': 'admin',
    'Authorization': 'Bearer test-token'
  }
};

// 创建axios实例
const testRequest = axios.create(config);

// 测试订单API
async function testOrderAPI() {
  console.log('🧪 测试订单API...');
  
  try {
    // 测试订单列表
    console.log('📋 测试订单列表接口...');
    const orderResponse = await testRequest.get('/order/order/page', {
      params: {
        page: 1,
        pageSize: 10
      }
    });
    console.log('✅ 订单列表接口正常:', orderResponse.data);
    
  } catch (error) {
    console.error('❌ 订单API测试失败:', error.message);
    if (error.response) {
      console.error('响应状态:', error.response.status);
      console.error('响应数据:', error.response.data);
    }
  }
}

// 测试票务API
async function testTicketAPI() {
  console.log('🧪 测试票务API...');
  
  try {
    // 测试车票列表
    console.log('🎫 测试车票列表接口...');
    const ticketResponse = await testRequest.get('/ticket/ticket/page', {
      params: {
        page: 1,
        pageSize: 10
      }
    });
    console.log('✅ 车票列表接口正常:', ticketResponse.data);
    
  } catch (error) {
    console.error('❌ 票务API测试失败:', error.message);
    if (error.response) {
      console.error('响应状态:', error.response.status);
      console.error('响应数据:', error.response.data);
    }
  }
}

// 运行测试
async function runTests() {
  console.log('🚀 开始API测试...');
  console.log('==================');
  
  await testOrderAPI();
  console.log('');
  
  await testTicketAPI();
  console.log('');
  
  console.log('🎉 API测试完成！');
}

// 如果直接运行此脚本
if (require.main === module) {
  runTests().catch(console.error);
}

module.exports = { testOrderAPI, testTicketAPI };