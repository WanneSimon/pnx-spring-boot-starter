[[中文]](nsplugin-doc.md)  [[english]](nsplugin-doc.en.md)

##多语言支持  
声明语言文件为 `.yml`, 位于插件资源数据的 `lang` 文件夹夹下, 使用的语言为 `en`。  
最终读取的语言文件是插件资源目录下的 `lang/en.yml`。  
在运行过程中，你也可以动态增加新的语言文件，达到自定义多语言的效果。
```
super(plugin, ConfigFileType.YML, "lang/", `en`);
```

[样例：多世界语言](https://github.com/WanneSimon/ns-world/tree/main/src/main/java/cc/wanforme/nukkit/nsworld/nsworld/LangHolder.java)  
##插件基类  
`NSPluginBase` 是 `nsplugin` 提供的一个插件基类。  
它继承自 `PluginBase`，额外提供了一种命令处理流程。当然，不是一定要继承此类。

`NSPluginBase` 内部维持多个主命令 `NSCommand` ，每个主命令又有多个参数处理器 `ArgumentsHandler`。
以如下命令进行解释：
```
/wl {load|unload} {name}
```
定义继承自 `NSCommand` 的子类 `WorldLoadCommand`，用于响应所有的 `/wl` 命令。  
为 `WorldLoadCommand` 定义处理器 `WorldLoadHandler`，处理 `/wl {load|unload} {name}` 命令。
[样例: 多世界主类](https://github.com/WanneSimon/ns-world/tree/main/src/main/java/cc/wanforme/nukkit/nsworld/NSWorld.java)
