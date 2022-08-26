package cc.wanforme.nukkit.spring.plugins.command;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.plugin.PluginBase;

/**
 * 多主指令插件基础类 <br>
 * 继承自 PluginBase ，已重写 {@link #onCommand(CommandSender, Command, String, String[])} 方法。
 * 该方法会自动根据主命令去调用命令处理器，你只需要做的就是
 * 在 {@link #onLoad()} 中调用 {@link #registerNSCommand(String, NSCommand)} 方法注册各个指令。<br>
 * 
 * @author wanne 2020年7月22日
 */
public abstract class NSPluginBase extends PluginBase {
	
	/** 注册的所有指令，键是主指令*/
	protected Map<String, NSCommand> mainCommands = new HashMap<>();

	@Override
	public void onLoad() {
		super.onLoad();
	}

	@Override
	public void onEnable() {
		super.onEnable();
	}

	@Override
	public void onDisable() {
		super.onDisable();
	}

	/** 根据主命令去执行相应的指令 <br>
	 * 更多说明参阅 {@link cn.nukkit.command.CommandExecutor#onCommand(CommandSender, Command, String, String[])}
	 * @param sender 发送命令的对象
	 * @param command 发送的命令。command.getName() 无论什么时候获取到的都是原始主命令（非别名）
	 * @param label 实际发送的命令。可能是原始主命令，也可能是主命令的别名
	 * @param args 主命令后面的参数，使用空格隔开的字符串。
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		getLogger().debug("\ncommand:" + command.getName() +", label:" + label +", args:" + Arrays.asList(args));
		NSCommand multiCommandHandler = mainCommands.get(command.getName());
		if(multiCommandHandler!=null) {
			return multiCommandHandler.onCommand(sender, command, label, args);
		}
		return false;
	}
	
	/** 添加一个主指令处理器，并调用 initCommand 进行初始化*/
	@SuppressWarnings("unchecked")
	public void registerNSCommand(NSCommand mutilCommandHandler) {
		String main = mutilCommandHandler.getMain();
		mutilCommandHandler.initCommand();
		mainCommands.put(main, mutilCommandHandler);

		// 添加别名
		Map<String, Object> command = (Map<String, Object>) (this.getDescription().getCommands().get(main));
		if(command.get("aliases") != null) {
			List<String> alias = (List<String>) command.get("aliases");
			for (String a : alias) {
				mainCommands.put(a, mutilCommandHandler);
			}
		}
	}
}
