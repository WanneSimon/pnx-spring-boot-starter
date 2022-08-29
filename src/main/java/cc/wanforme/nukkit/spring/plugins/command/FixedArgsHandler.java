package cc.wanforme.nukkit.spring.plugins.command;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** 固定长度的指令处理器，动态参数使用 {} 进行包裹<br>
 * 例如：<br>
 * 完全固定的指令：/cm test <br>
 * 部分是动态的指令： /cm send {player} <br>
 * @author wanne
 * 2020年7月22日
 */
public abstract class FixedArgsHandler extends ArgumentsHandler {
	// 其它正数表示，相似
	
	// 替代符
	private Pattern p = null;
	
	public FixedArgsHandler(String main, String[] args) {
		super(main, args);
		p =  Pattern.compile("^\\{.*\\}$");
	}
		
	/** 0 - 相同<br>
	 *  -1 - 不同<br>
	 *  参数从左往右匹配，遇到第一个不满足规则的时候，返回下标+1！<br>
	 *  其它正数 - 第一个不相同的下标+1，（相似）*/
	public int similarityWith(String[] arguments) {
		if(args == null && arguments==null) {
			return EQUAL;
		} else if(args == null || arguments==null) {
			return DIFFERRENT;
		}
		
		if(args.length != arguments.length) {
			return DIFFERRENT;
		}
		
		for(int i=0; i<args.length; i++) {
			Matcher matcher = p.matcher(args[i]);
			// 占位符跳过，非占位符必须相等
			if(!matcher.matches()) {
				if(!args[i].equals(arguments[i])) {
					return i == 0 ? DIFFERRENT : i+1;
				}
			}
		}
		
		return EQUAL;
	}
	
}
