MybatisExt项目扩展自Mybatis，具有以下特点:

一、运行环境支持

通过指定虚拟机参数，自动加载properties文件中的指定代码块（velocity实现）

各种耗性能的操作在生产环境下不会执行

二、内置的拦截器(plugin)

1. SQL语句打印和性能监控功能(SQLPrintPlugin)
2. 批量执行功能(BatchPlugin)
3. 主键自动生成功能(IdentityPlugin)
4. MySQL查询语句性能分析功能(IndexingPlugin)
5. XML文件修改后自动加载功能(XMLMapperLoader)