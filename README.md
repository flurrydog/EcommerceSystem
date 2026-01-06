# 电子商务系统

华南理工大学《网络应用开发》课程实验项目

## 项目信息
- **课程名称**：网络应用开发
- **实验题目**：设计并实现一个电子商务网站的开发和在线部署
- **提交日期**：2025年12月31日
- **代码仓库**：https://github.com/flurrydog/EcommerceSystem
- **在线地址**：http://119.29.183.99:8080
- **测试账号**：admin / 123456 (您也可以自行注册账号体验完整功能)

## 项目简介
基于Spring Boot的B2C电商网站，实现完整的电商业务流程，包括用户管理、商品展示、购物车、订单处理等功能。

## 核心功能
- ✅ 用户注册、登录、注销
- ✅ 商品浏览与搜索
- ✅ 购物车管理
- ✅ 订单创建与查询
- ✅ 基础后台管理

## 技术栈
- **后端**：Spring Boot 3.x, Spring Data JPA
- **前端**：Thymeleaf, Bootstrap 5
- **数据库**：MySQL 8.0
- **部署**：Docker, 云服务器
- **工具**：Maven, Git

## 快速开始
```bash
# 克隆项目
git clone https://github.com/flurrydog/EcommerceSystem.git

# 启动服务
cd EcommerceSystem
docker-compose up -d  # 启动数据库
./mvnw spring-boot:run  # 启动应用

# 访问：http://localhost:8080
```

## 部署信息
- **访问地址**：http://119.29.183.99:8080
- **部署环境**：腾讯云服务器 + Docker容器化
- **数据库**：MySQL 8.0 (容器部署)

## 项目结构
```
src/main/java/org/monicandy/ecommerceapp/
├── controller/  # 控制器
├── entity/      # 数据实体
├── repository/  # 数据访问
├── service/     # 业务逻辑
└── config/      # 配置类
```

## 使用说明
1. **测试账号登录**：使用提供的测试账号 admin / 123456 快速体验
2. **自行注册**：您也可以通过注册功能创建个人账号，体验完整用户流程
3. **功能体验**：注册后即可浏览商品、管理购物车、创建订单等

## 实验报告
完整的设计文档、实现细节和部署过程详见课程实验报告。

---
*课程实验项目 - 仅供学习交流使用*
