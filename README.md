# TOMCAT 启动数据源自加密实施文档
 
## 1、先决条件

    1、JDK版本大于等于1.7
    2、TOMCAT大版本大于等于8
    3、不适用于在server.xml中配置的数据源（官方也不推荐）

## 2、实施步骤

### 2.1、修改原context.xml

自动加密由于涉及到对原配置文件的重写，所以需要将数据源配置从原配置文件中分离，保证重写后的配置文件不会影响到本身的其他配置。

> 将原context.xml


```<?xml version="1.0" encoding="UTF-8"?>  ```  
.
.
.

```<Resource name="jdbc/TestDB" auth="Container" type="javax.sql.DataSource" maxTotal="100" maxIdle="30" maxWaitMillis="10000" username="root" password="1qaz2wsx" driverClassName="com.mysql.jdbc.Driver" url="jdbc:mysql://212.64.24.151:9033/test"/>```

.
.
.

```</Context>```

> 拆分为2个文件context.xml & datasource_test.xml 下划线后可自定义名称，推荐使用jndi name

context.xml ：

```<?xml version="1.0" encoding="UTF-8"?>  ``` 

```
<!DOCTYPE Context [<!ENTITY datasource_test SYSTEM "datasource_test.xml">]>
```

.
.
.
&datasource_test;
.
.
.

```</Context>```

datasource_test.xml:

```<?xml version="1.0" encoding="UTF-8"?>```

```<Resource name="jdbc/TestDB" auth="Container" type="javax.sql.DataSource" maxTotal="100" maxIdle="30" maxWaitMillis="10000" username="root" password="1qaz2wsx" driverClassName="com.mysql.jdbc.Driver" url="jdbc:mysql://212.64.24.151:9033/test"/>```



<i>其中:</i>

"datasource_test" 和 "datasource_test.xml" 分别代表了原xml中引用的别名以及在原xml的同级目录中引用的外部xml文件名

    
### 2.2 添加jar包

将eproe-tomcat-datasource-encryption.jar、eproe-tomcat-datasource-decryption.jar和commons-codec-1.9.jar

放入$TOMCAT_HOME/lib/中

### 2.3 修改启动脚本

打开start.sh文件

在第二行开始添加如下

```for xml in $(ls ../conf/datasource_*.xml)```

```do```
        
        java -jar ../lib/eproe-tomcat-datasource-encryption.jar $xml
        
```done```

## 3、启动tomcat即可
