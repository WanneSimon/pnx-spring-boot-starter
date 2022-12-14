package cc.wanforme.nukkit.spring.starter;

import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cc.wanforme.nukkit.spring.util.NukkitServerUtil;
import cn.nukkit.Server;

/** listener for nukkit-launch.<br>
 * 0 - not finished. 未完成 
 * 1 - finished. 完成 
 * @author wanne
 * 2020年6月28日
 */
public class PnxLaunchListener implements Callable<Integer>{
	private static final Logger log = LoggerFactory.getLogger(PnxLaunchListener.class);
	
	public static final int NOT_EXECUTE=-1,
			NOT_LAUNCH=0, 
			SERVER_CREATED=1, 
			PLUGIN_MANAGER_CREATED=2;
//			FINISHED = 3;
	/** 进行的状态*/ 
	private int flag = NOT_EXECUTE;
	private String period = "not executed";
	
	private PnxProperties properties;
	
	public PnxLaunchListener(PnxProperties properties) {
		this.properties = properties;
	}
	
	
	/** <br>
	 * 0 - not launch. 未完成  
	 * 1 - server created 完成
	 */
	@Override
	public Integer call() throws Exception {
		long start = System.currentTimeMillis();
		
		long sleepStart = start;
		// 轮询 server 状态
		do {
			if(System.currentTimeMillis() - sleepStart < properties.getListenTick()) {
				continue;
			}
			
			this.queryNukkitStatus();
			if(this.isFinished()) {
				break;
			}
			
			sleepStart = System.currentTimeMillis();
		} while(System.currentTimeMillis() - start < properties.getListenTimeOut());
		
		if(!isFinished()) {
			log.warn("[{}] takes too long! listener-nukkit failed. ", period);
			return 0;
		}
		
		return 1;
	}
	
	/** 当前状态判断*/
	protected void queryNukkitStatus() {
//		int flag = NOT_EXECUTE;
		Server server = NukkitServerUtil.getServer();
		if(flag == NOT_EXECUTE) {
			period = "launching nukkit";
		}
		if(server == null) {
			flag = NOT_LAUNCH;
			period = "launching nukkit-server";
		} else if(server.isRunning()) {
			flag = SERVER_CREATED;
			period = "creating plugin-manager";
		
			if(server.getPluginManager() != null) {
				flag = PLUGIN_MANAGER_CREATED;
				period = "";
			}
		}
//		return flag;
	}
	
	/** 启动完成判断
	 * @return
	 */
	protected boolean isFinished() {
		return flag == PLUGIN_MANAGER_CREATED;
	}
}
