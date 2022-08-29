package cc.wanforme.nukkit.spring;

import java.util.Arrays;
import java.util.List;

import cc.wanforme.nukkit.spring.plugins.command.FixedOrderHandler;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;

public class TestFixedOrder {

	public static void main(String[] args) {
		List<String> list = Arrays.asList( "test", "test 123 flat", "test 873089 nor go", "test 873089 neth go");
		String[] as = { "{name}", "{seed}", "[flat|nor]", "go" };
		
		FixedOrderHandler handler = new FixedOrderHandler("wc", as) {
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
