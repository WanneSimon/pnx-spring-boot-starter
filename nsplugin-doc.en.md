[[中文]](nsplugin-doc.md)  [[english]](nsplugin-doc.en.md)

## Multilingual

The language file type - `.yml`, the files are locate in sub-folder `lang` below plugin's data folder, 
language type is `en`.  
The final location is `lang/en.yml`.  
You can add other language files on `prod` environment.

```
super(plugin, ConfigFileType.YML, "lang/", `en`);
```

[e.g：ns-world](https://github.com/WanneSimon/ns-world/tree/main/src/main/java/cc/wanforme/nukkit/nsworld/nsworld/LangHolder.java)  

## NSPluginBase

`NSPluginBase` is  a plugin's super class provided by `nsplugin` .  
It extends `PluginBase`,  and handles command's args by NSCommand 。当然，不是一定要继承此类。

`NSPluginBase` 内部维持多个主命令 `NSCommand` ，每个主命令又有多个参数处理器 `ArgumentsHandler`。
以如下命令进行解释：

```
/wl {load|unload} {name}
```

定义继承自 `NSCommand` 的子类 `WorldLoadCommand`，用于响应所有的 `/wl` 命令。  
为 `WorldLoadCommand` 定义处理器 `WorldLoadHandler`，处理 `/wl {load|unload} {name}` 命令。
[样例: 多世界主类](https://github.com/WanneSimon/ns-world/tree/main/src/main/java/cc/wanforme/nukkit/nsworld/NSWorld.java)
