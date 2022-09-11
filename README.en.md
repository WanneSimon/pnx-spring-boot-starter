# pnx-spring-boot-starter
SpringBoot starter for power-nukkit  
[[中文]](README.md) [[english]](README.en.md)

[dev Demo](https://github.com/WanneSimon/pnx-starter-demo.git)   
[plugin Demo](https://github.com/WanneSimon/pnx-starter-plugin-demo.git)

All `nukkit` below refer to `PowerNukkitX`
### Environment
`jdk17`

### What can it do
1. Launch `nukkit`。
2. Noninvasive, you can still use nukkit's plugin.
3. Support `spring` annotations while developing plugins.
4. Ignore this. ~~内置原nukkit插件 -- [PMPlus](https://github.com/WanneSimon/PMPlus/tree/2.0/build)（注：cloudburst）~~
5. Saving resources before running `nukkit`, such as plugins。

### Usage

~~#### Install pnx-spring-boot-starter~~
~~I'm tring to upload to maven central.~~
~~``` mvn install ```~~
#### Import library
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
      <groupId>cc.wanforme.nukkit</groupId>
      <artifactId>pnx-spring-boot-starter</artifactId>
      <version>0.1.0</version>
    </dependency>
  </dependencies>
```
#### Run
add `vm` option 
```
--add-opens java.base/java.lang=ALL-UNNAMED
```

**See [`pnx` page](https://github.com/PowerNukkitX/PowerNukkitX#%E4%BD%BF%E7%94%A8%E6%AD%A5%E9%AA%A4) If you have a startup problem, Especially about `java module`**

### Spring Boot Config
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
    # plugins' location written with spring annotation ( jar and classes are supported )
    # nsplugins - the default location, target - maven output folder (for developing plugins)
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

### dev nsplugin
[nsplugin-doc](nsplugin-doc.en.md)