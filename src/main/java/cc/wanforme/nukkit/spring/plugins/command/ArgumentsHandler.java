package cc.wanforme.nukkit.spring.plugins.command;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;

/** 指令参数处理器父类<br>
 * 匹配输入的指令时，从左往右依次进行，遇到第一个不匹配时停止检查。
 * @author wanne
 * 2022-08-29
 */
public abstract class ArgumentsHandler implements Comparable<ArgumentsHandler> {
	// 相同
	public static final int EQUAL = 0;
	// 不同
	public static final int DIFFERRENT = -1;
	
	/** 参数部分
	 * 例如："/cm send {name} {msg}", args 就是 "send {name} {msg}" 这部分 , "cm" 就是 main
	 * 注： cm 没有后面的参数时， args 是长度为0的空数组
	 */
	
	protected String main; // main没啥用
	protected String[] args ;
	
	public ArgumentsHandler(String main, String[] args) {
		this.main = main;
		this.args = args==null ? new String[0] : args;
	}
	
	public String[] getArgs() {
		return args;
	}
	
	/** 排序时使用的权重,暂未实现,权重小的先判断*/
	@Override
	public int compareTo(ArgumentsHandler other) {
		return 0;
	}
	
	public String getUsage() {
		return "/" + this.main + " " + String.join(" ", args);
	}
	
	/** 处理指令 */
	public abstract boolean onCommand(CommandSender sender, Command command, String label, String[] args);
	
	/** 0 - 相同<br>
	 *  -1 - 不同<br>
	 * 参数从左往右匹配，遇到第一个不满足规则的时候，视为不同！
     */
	public abstract int similarityWith(String[] arguments) ;
}
