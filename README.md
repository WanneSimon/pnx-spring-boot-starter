# pnx-spring-boot-starter

SpringBoot starter for PowerNukkitX  
[[中文]](README.md) [[english]](README.en.md)  

[开发Demo](https://github.com/WanneSimon/pnx-starter-demo.git) 
[插件Demo](https://github.com/WanneSimon/pnx-starter-plugin-demo.git)

下面所有 `nukkit` 均指代 `PowerNukkitX`

### 要求

`jdk17`

### 功能

1. 启动 `nukkit` ，但是 `spring` 不管理 `nukkit`。
2. 保持 `nukkit` 原生，基于 `nukkit` 的插件依然可以使用。
3. 支持 `web` 环境和 ~~`non-web` 环境（待测试）~~，默认为 `non-web` 环境。
4. 基于 `spring` 注解注入的 `nukkit` 插件开发。
5. ~~内置原nukkit插件 -- [PMPlus](https://github.com/WanneSimon/PMPlus/tree/2.0/build)（注：已支持cloudburst）~~
6. 支持在启动前保存内部资源到外部，例如预置插件。

### 使用
以下只做最简单的说明，请查看 [pnx-starter-demo](https://github.com/WanneSimon/pnx-starter-demo.git)
#### 引入

```xml
  <repositories>
      <repository>
          <id>ossrh</id>
          <name>ossrh</name>
          <url>https://s01.oss.sonatype.org/content/repositories/releases/</url>
          <releases>
              <enabled>true</enabled>
          </releases>
          <snapshots>
              <enabled>false</enabled>
          </snapshots>
      </repository>
  </repositories>

  <dependencies>
      <dependency>
          <groupId>org.springframework.boot</groupId>
          <artifactId>spring-boot-starter</artifactId>
          <exclusions>
              <exclusion>
                  <groupId>org.springframework.boot</groupId>
                  <artifactId>spring-boot-starter-logging</artifactId>
              </exclusion>
          </exclusions>
      </dependency>
    
      <dependency>
          <groupId>cc.wanforme.nukkit</groupId>
          <artifactId>pnx-spring-boot-starter</artifactId>
          <version>0.2.0</version>
      </dependency>
    
      <!-- 引入 web 依赖则表示是 web 环境，同时需要修改 spring.main.web-application-type -->
      <!--
      <dependency>
          <groupId>org.springframework.boot</groupId>
          <artifactId>spring-boot-starter-web</artifactId>
      </dependency>
      -->
    
      <dependency>
          <groupId>cn.powernukkitx</groupId>
          <artifactId>powernukkitx</artifactId>
          <version>${powernukkitx.version}</version>
          <scope>provided</scope>
          <exclusions>
              <exclusion>
                  <groupId>org.apache.logging.log4j</groupId>
                  <artifactId>log4j-api</artifactId>
              </exclusion>
              <exclusion>
                  <groupId>org.apache.logging.log4j</groupId>
                  <artifactId>log4j-core</artifactId>
              </exclusion>
              <exclusion>
                  <groupId>org.apache.logging.log4j</groupId>
                  <artifactId>log4j-slf4j-impl</artifactId>
              </exclusion>
              <exclusion>
                  <groupId>org.apache.logging.log4j</groupId>
                  <artifactId>log4j-slf4j18-impl</artifactId>
              </exclusion>
          </exclusions>
      </dependency>
  </dependencies>
```

#### 运行

需要添加以下 `vm` 参数

```
--add-opens java.base/java.lang=ALL-UNNAMED
```

**无法启动或运行过程中报错（特别是模块），请根据 pnx 官方给出的[参数](https://github.com/PowerNukkitX/PowerNukkitX#%E4%BD%BF%E7%94%A8%E6%AD%A5%E9%AA%A4)进行调整**

### 配置说明

```yml
nukkit:
  spring:
    # 启用 nukkit 相关模块
    # enable nukkit module
    enable: true
    # 是否一同启动 nukkit
    # run nukkit after launch app
    start-nukkit: true
    # 使用 spring 注解注入编写的 nukkit 插件的位置 （支持jar 和 字节码形式的）
    # nsplugins - 默认的存放位置， target - 开发环境中maven项目的输出位置，用于开发测试。
    # plugins' location written with spring annotation ( jar and classes supported )
    # nsplugins - the default location, target - maven output folder (for develop plugins)
    nukkit-spring-plugin-location: nsplugins,target
    # 监听 nukkit 启动的间隔（ms）
    # 内部启动了一个子线程运行 nukkit ，通过检查 Server 实例和 PluginManager 实例来确认是否启动完成。 
    # duration time while retrying to detect if nukkit is started
    listenTick: 500
    # 监听 nukkit 启动的超时时间（ms）
    # timeout detect if nukkit is started
    listenTimeOut: 10000
    # 保存到外部的资源目录（ nukkit 启动前）
    # plugins/ - nukkit 的原生插件目录
    # config/ - springboot 的配置
    # 注：文件夹需要以 '/' 结尾
    # the resources that needs to be saved before starting nukkit 
    savingFilesBeforeNukkit: plugins/
```

### 插件开发

[插件文档](nsplugin-doc.md)

### version
|  ver  | spring-boot |         pnx          |
|:-----:|:-----------:|:--------------------:|
| 0.1.x |    2.7.2    |     1.6.0.0-PNX      |
| 0.2.x |    3.1.5    |      1.20.40-r1      |