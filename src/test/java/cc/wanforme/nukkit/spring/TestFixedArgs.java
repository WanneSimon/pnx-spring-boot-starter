package cc.wanforme.nukkit.spring;

import java.util.Arrays;
import java.util.List;

import cc.wanforme.nukkit.spring.plugins.command.FixedArgsHandler;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;

public class TestFixedArgs {

	public static void main(String[] args) {
		List<String> list = Arrays.asList( "test", "test 123 flat", "test 873089 nor go", "test 873089 neth go",
				"test 873089 nor go bye");
		String[] as = { "{name}", "{seed}", "{flat|nor}", "go" };
		
		FixedArgsHandler handler = new FixedArgsHandler("wc", as) {
			@Override
			public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
				return false;
			}
		};
		
		for (String str : list) {
			String[] split = str.split(" ");
			int index = handler.similarityWith(split);
			System.out.println(index + ": " + Arrays.toString(split));
		}
		
	}
	
}
