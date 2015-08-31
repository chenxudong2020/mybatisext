MybatisExt项目扩展自Mybatis，具有以下特点:

一、运行环境支持

通过指定虚拟机参数，自动加载properties文件中的指定代码块（velocity实现）
各种耗性能的操作在生产环境下不会执行

二、内置的拦截器(plugin)

1. SQL语句打印和性能监控功能(SQLPrintPlugin)
2. 批量执行功能(BatchPlugin)
3. 主键自动生成功能(IdentityPlugin)
4. MySQL查询语句性能分析功能(IndexingPlugin)
5. 分页拦截器(PagingPlugin)
6. XML文件修改后自动加载功能(XMLMapperLoader)

三、扩展Mybatis，实现拦截Mapper接口方法功能

原生的MyBatis是不能访问映射接口中的方法的，这使自己在方法中加注解无法访问，通过配置拦截器可以实现对接口中方法的拦截。

四、内置默认生成的增、删、改、查方法

Mapper接口继承CommonMapper或AutoMapper自动添加系统自带方法进行简单的操作，如果不合适可以继续在接口中和xml文件中添加自定义方法。

五、自带了entity、mapper和xml生成工具

自动生成代码工具，不需要配置，可根据需求自行修改AutoEntityUtil或者EntityUtil

六、内置ActiveRecord功能

不用任何配置也可以操作数据库，适合导入导出数据，有如下功能：
1. 支持原生SQL语句
2. 支持MyBatis脚本语句
3. 支持对象操作
4. 支持自定义字段和属性映射
5. 简单的分页方言支持


