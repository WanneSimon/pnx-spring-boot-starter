package cc.wanforme.nukkit.spring.plugins.command;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;

/**主指令处理器<br> 如果是处理多参数的情况，需要用到另一个类
 * @author wanne
 * 2020年7月22日
 */
public abstract class NSCommand {

	/** 主指令*/
	private String main;
	/** 固定长度参数的指令处理器*/ 
	private Set<ArgumentsHandler> fixedArgsHandlers = new HashSet<>(); // 升序
	
	public NSCommand(String main) {
		this.main = main;
	}
	
	/** 指令初始化，当被添加到 NSPluginBase 的指令集中时调用*/
	protected abstract void initCommand();
	
	/** 只处理完全相等的指令，有任何差异都返回处理失败<br>
	 * @param sender
	 * @param command
	 * @param label
	 * @param args
	 * @return
	 */
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		ArgumentsHandler handler = this.getArgumentsHandler(command, args);
		if(handler!=null) {
			boolean re = handler.onCommand(sender, command, label, args);
			if(!re) {
				sender.sendMessage(handler.getUsage());
			}
			return true;
		}
		return false;
	}
		
	public String getMain() {
		return main;
	}
	
	public void addHandler(ArgumentsHandler handler) {
		fixedArgsHandlers.add(handler);
	}
	
	public void addHandlers(List<? extends ArgumentsHandler> handlers) {
		fixedArgsHandlers.addAll(handlers);
	}
	
	/** 精准匹配指令*/ 
	protected ArgumentsHandler getArgumentsHandler(Command command, String[] args) {
		for (ArgumentsHandler handler : fixedArgsHandlers) {
			if(handler.similarityWith(args) == FixedArgsHandler.EQUAL) {
				return handler;
			}
		}
		return null;
	}
	
	/** 匹配相似的指令*/
	protected List<ArgumentsHandler> getSimilarArgumentsHandlers(Command command, String[] args) {
		List<ArgumentsHandler> similar = new ArrayList<>();
		for (ArgumentsHandler handler : fixedArgsHandlers) {
			if(handler.similarityWith(args) >= FixedArgsHandler.EQUAL) {
				similar.add(handler);
			}
		}
		return similar;
	}
	
}
