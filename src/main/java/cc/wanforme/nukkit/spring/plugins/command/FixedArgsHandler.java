package cc.wanforme.nukkit.spring.plugins.command;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** 固定长度的指令处理器，动态参数使用 {} 进行包裹，动态参数的候选值使用 | 分隔<br>
 * 完全固定的指令：/cm test <br>
 * 部分是动态的指令： /cm send {player} <br><br>
 * 完整示例：/go {name} {flat|nor} panda  <br>
 * go - 主指令，{name} - 任意值的动态参数，{flat|nor} - 值为 flat 或 nor 的参数，panda - 值只允许为 panda 的参数 
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
			String placeholder = args[i];
			String input = arguments[i];
			
			Matcher matcher = p.matcher(placeholder);
			// 占位符跳过，固定值必须相等
			if(matcher.matches()) {
				// 占位符中定义了候选参数
				String _placeholder = placeholder.substring(1, placeholder.length()-1);
				// 没有候选参数，视为 任意值均可
				if("".equals(_placeholder) || !_placeholder.contains("|")) {
					continue;
				}
				
				boolean isOpt = false;
				String[] opts = _placeholder.split("\\|");
				for(int k=0; k<opts.length; k++) {
					if(opts[k].equals(input)) {
						isOpt = true;
						break;
					}
				}
				
				if(!isOpt) {
					return DIFFERRENT;
				}
			} else if(!placeholder.equals(input)) {
				return i == 0 ? DIFFERRENT : i+1;
			}
		}
		
		return EQUAL;
	}
	
}
