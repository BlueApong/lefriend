## 乐寻伙伴

> 一个专注于伙伴交友的移动端网站！
>
> Vue3 + SpringBoot + Redis + MySQL

## 项目背景

在大学的各项比赛中经常需要寻觅队友，但是只能局限于从身边认识的人或者广发朋友圈去”招募“队友。

而大部分人的社交圈子和人脉并没有这么广，并且从中选出的队友也往往不尽人意，缺少优质伙伴。

因此，我做了【乐寻伙伴】，提供一个「智能推荐伙伴」，并且支持「在线组队」的平台。

希望它能帮助大家找到默契的伙伴。

## 功能介绍

> :star: 核心功能  :rocket: 未来计划

**个人**

- 登录 / 注册

- 编写个人信息

- 定义个人标签 :star:

**组队**

- 搜索队伍

- 加入队伍 :star:
  - 队长审核入队 :rocket:

- 创建队伍 :star:

  - 密码加密

  - 设置截止时间

  - 设置最大人数

- 管理队伍 :rocket:
  - 转让队长

**交友**

- 标签搜索伙伴

- 热度推荐伙伴

- 算法推荐伙伴 :star:
- 邀请加入队伍 :rocket:
- 实时聊天 :rocket:

**好友** :rocket:

- 添加好友
- 搜索好友
- 推荐共同好友



## 业务流程图

![image-20241105160322616](https://gcore.jsdelivr.net/gh/BlueApong/images@main/img/202411051603397.png)



## 技术栈

**前端**

- 开发框架：Vue3
- 插件：Vue Router 4、Pinia
- 组件库：Vant 4
- 语法扩展：TypeScript
- 打包工具：Vite



**后端**

- 编程语言：Java
- 开发框架：Spring Boot
- 数据库：MySQL
- 中间件：Redis



## 项目架构

![img](https://gcore.jsdelivr.net/gh/BlueApong/images@main/img/202411051610265.png)



## 快速启动

### 前端

- 安装依赖

``` bash
npm install
```

- 启动

``` bash
npm run dev
```

- 部署

``` bash
npm run build
```

### 后端

- 数据库

  1. 进入当前 project 目录（xx/lefriend-backend）下 cmd 控制台

  2. 执行 sql/create_table.sql 文件

``` bash
# 直接执行
mysql -u[your_name] -p[your_pass] < sql/create_table.sql
```

``` bash
# 在数据库环境下执行
mysql -u[your_name] -p[your_pass]
source sql/create_table.sql
```

## 页面展示

### 首页（交友）

![image-20241105162548242](https://gcore.jsdelivr.net/gh/BlueApong/images@main/img/202411051625304.png)

#### 用户搜索

![image-20241105163155841](https://gcore.jsdelivr.net/gh/BlueApong/images@main/img/202411051631885.png)

### 队伍

![image-20241105162706882](https://gcore.jsdelivr.net/gh/BlueApong/images@main/img/202411051627939.png)

#### 创建队伍

![image-20241105163051818](https://gcore.jsdelivr.net/gh/BlueApong/images@main/img/202411051631925.png)

### 个人

![image-20241105162732226](https://gcore.jsdelivr.net/gh/BlueApong/images@main/img/202411051627268.png)

#### 个人信息

![image-20241105162827410](https://gcore.jsdelivr.net/gh/BlueApong/images@main/img/202411051628455.png)

#### 定义个人标签

![image-20241105162847925](https://gcore.jsdelivr.net/gh/BlueApong/images@main/img/202411051628970.png)
