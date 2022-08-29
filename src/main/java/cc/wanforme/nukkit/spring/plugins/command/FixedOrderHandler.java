package cc.wanforme.nukkit.spring.plugins.command;

import java.util.regex.Pattern;

/** 动态长度的指令处理器， 动态参数使用 {}<br> 注：在这里，动态参数都是可选的！ <br>
 * 多候选值参数使用 []， 候选值使用 | 隔开。 可选参数为 '[]' 形式时， 视为任意值 <br>
 * 例如：<br>
 * 部分是动态的指令： /cm send {player} [msg|warn] <br>
 * @author wanne
 * 2020年7月22日
 */
public abstract class FixedOrderHandler extends ArgumentsHandler {
	// 其它正数表示，相似
	
	// 动态占位符
	private Pattern dynPat= null;
	// 可选占位符
	private Pattern optPat = null;
	
	public FixedOrderHandler(String main, String[] args) {
		super(main, args);
		dynPat =  Pattern.compile("^\\{.*\\}$");
		optPat =  Pattern.compile("^\\[.*\\]$");
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
		
		if(args.length < arguments.length) {
			return DIFFERRENT;
		}
		
		for(int i=0; i<args.length && i<arguments.length; i++) {
			String placeholder = args[i];
			String input = arguments[i];
			
			// 检查是否是动态参数
			if(dynPat.matcher(placeholder).matches()) {
				continue;
			}
			
			// 检查是否是可选参数
			boolean isOpt = false;
			if(optPat.matcher(placeholder).matches()) {
				String _placeholder = placeholder.substring(1, placeholder.length()-1);
				// 没有候选参数，视为 任意值均可
				if("".equals(_placeholder)) {
					continue;
				}
				
				String[] opts = _placeholder.split("\\|");
				for(int k=0; k<opts.length; k++) {
					if(opts[k].equals(input)) {
						isOpt = true;
						break;
					}
				}
			}
			
			if(isOpt) {
				continue;
			}
			
			// 检查固定值参数
			if(placeholder.equals(input)) {
				continue;
			}
			
			// 不相同，返回当前下标
			return i+1;
		}
		
		return EQUAL;
	}
	
}
