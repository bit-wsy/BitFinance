BitFinance是一个分布式微服务金融项目，实现了网络借贷信息中介服务平台，为个人投资者、个人融资用户和小微企业提供专业的线上信贷及出借撮合服务。包括账户绑定、借款、投标、提现、还款等功能。
- **技术栈：** SpringMVC、SpringBoot、SpringCloud、MyBatis-Plus、Sentinel、OpenFeign、MySQL、Redis、RabbitMQ

- **项目内容：**

  - **服务治理：** 使用 OpenFeign 实现声明式服务调用，简化微服务间的通信流程，并集成 Sentinel 实现服务熔断与降级，提升用户体验。


  - **权限管理：** 基于 SpringSecurity + JWT 实现登录认证，使用 RBAC 模型进行权限划分，限定投资人/借款人的访问权限。


  - **消息队列：** 使用 RabbitMQ 结合 Redis+Lua 脚本，解决了标的超卖和重复下单问题。

  - **延时任务：** 基于 Redisson 内置的延时队列 RDelayedQueue + 状态机实现超时投资订单取消功能。
  - **缓存优化：** 使用 Redis 缓存热点数据，布隆过滤器解决缓存穿透问题，随机TTL解决缓存雪崩问题，逻辑过期解决缓存击穿问题。

  - **性能调优：** 使用JMeter进行压测，通过慢查询重构+索引优化，使 QPS 提升20%。
