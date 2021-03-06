
![MybatisExt](http://git.oschina.net/uploads/images/2015/1203/212241_b3cc5b8d_12260.png "雕虫小技")


> 技术讨论 QQ 群

http://www.oschina.net/p/mybatisext

无侵入式增强mybatis

springboot只需要正常引入mybatis 编译本框架jar包加入，同时加入工程https://github.com/chenxudong2020/spring-boot-mybatisext
中的类或者编译jar加入工程，然后修改启动类上注解为 @SpringBootApplication(exclued=MybatisAutoConfiguration.class)
其他配置参照mybatis配置即可。
spring项目集成参照本框架中的test代码

MybatisExt项目扩展自Mybatis，具有以下特点:


**一、运行环境支持**

通过指定虚拟机参数，自动加载properties文件中的指定代码块（velocity实现）
各种耗性能的操作在生产环境下不会执行

**二、内置的拦截器(plugin)**

1. SQL语句打印和性能监控功能(SQLPrintPlugin)
2. 主键自动生成功能(IdentityPlugin)
3. MySQL查询语句性能分析功能(IndexingPlugin)
4. 分页拦截器(PagingPlugin)
5. XML文件修改后自动加载功能(XMLMapperLoader)
6. 批量处理拦截器(BatchPlugin)

**三、扩展Mybatis，实现拦截Mapper接口方法功能**

原生的MyBatis是不能访问映射接口中的方法的，这使自己在方法中加注解无法访问，通过配置拦截器可以实现对接口中方法的拦截。

**四、内置默认生成的增、删、改、查方法**

Mapper接口继承Table或DB自动添加系统自带方法进行简单的操作，如果不合适可以继续在接口中和xml文件中添加自定义方法。

新增通用Mapper处理，通过继承实现大部分的增删改查操作。

**五、自带了entity、mapper和xml生成工具**

自动生成代码工具，不需要配置，可根据需求自行修改AutoEntityUtil或者EntityUtil(在test项目里面)

**六、内置ActiveRecord功能**

不用任何配置也可以操作数据库，适合导入导出数据，有如下功能：

1. 支持原生SQL语句
2. 支持MyBatis脚本语句
3. 支持对象操作
4. 支持自定义字段和属性映射
5. 简单的分页方言支持

**对数据库操作**
```
//创建数据库连接
DB db = MybatisExt.open("org.hsqldb.jdbcDriver", "jdbc:hsqldb:mem:db_name", "sa", "");
//执行创建表操作
db.update("create table person(id bigint primary key,name varchar(30),age int)");
//执行sql插入操作
int count = db.update("insert into person(id,name,age)values(?,?,?)", IdWorker.getId(), "bobo", 28);
//执行MyBatis脚本插入操作
Record rec = new Record();
rec.put("id", IdWorker.getId());
rec.put("name", "hh");
rec.put("age", 30);
count = db.updateScript("insert into person(id,name,age)values(#{id},#{name},#{age})", rec);
//sql脚本查询
List<Record> list = db.list("select id,name,age from person where name=?", "bobo");
List<Person> listPerson = db.list("select * from person where name=?", Person.class, "bobo")
//MyBatis脚本查询
listPerson = db.queryScript("select * from person where name=#{name}", Person.class, "bobo");
```

**对数据库表操作**

```
//获取表操作对象
Table<Person, Long> table = db.active("person", Person.class, "id", Long.class);
//插入操作
InsertSQL insertSql = new InsertSQL("id", "name", "age").values(1, "bobo", 28);
int count = table.excute(insertSql);
count = table.update("insert into person(id,name,age)values(?,?,?)", 2, "hh", 25);
Person person = new Person();
person.setId(3L);
person.setAge(26);
person.setName("wahaha");
count = table.updateScript("insert into person(id,name,age) values(#{id},#{name},#{age})", person);
//查询操作
List<Person> personList = table.list("select * from person");
personList = table.queryScript("select * from person where name=#{name}", "bobo");
Person persion = table.one("select * from person limit 1");
personList = table.queryScript("select * from person where name=#{name}", "bobo");
SelectSQL selectSql = new SelectSQL("id", "name").where("name", "bobo").and("id", 1L).orderBy("id", "asc");
selectSql=table.excute(selectSql);
```
**无sql操作**

```
DB db = MybatisExt.open("org.hsqldb.jdbcDriver", "jdbc:hsqldb:mem:db_name", "sa", "");
Table<Record, Long> table = db.active("person");
//插入
Record rec=new Record();
rec.put("id", IdWorker.getId());
rec.put("name", "hh");
rec.put("age", 30);
table.getInsert().insert(rec);

//查询
rec=table.getSelect().one("name","hh");

//更新
table.getUpdate().update(...);

//删除
table.getDelete().delete(...);

```

具体可参见源码中的测试用例

```    <!--暂时未提交到中央maven仓库可以通过如下编译引入maven依赖 -->
      <dependency>
            <groupId>com.ext</groupId>
            <artifactId>mybatisext</artifactId>
            <version>1.0.0-SNAPSHOT</version>
            <scope>system</scope>
            <systemPath>${project.basedir}/lib/mybatisext-1.0.0-SNAPSHOT.jar</systemPath>
        </dependency>
          <!--springboot需要引入下面maven -->
        <dependency>
            <groupId>org.spring.boot.starter</groupId>
            <artifactId>mybatisext-spring-boot-starter</artifactId>
            <version>1.0-SNAPSHOT</version>
            <scope>system</scope>
            <systemPath>${project.basedir}/lib/mybatisext-spring-boot-starter-1.0-SNAPSHOT.jar</systemPath>
        </dependency>
```

欢迎使用并提交问题.


