package cn.wanther.toolkit;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;

import cn.wanther.http.HttpExecutor;
import cn.wanther.toolkit.component.DataBaseHelper;
import cn.wanther.toolkit.component.Statistics;
import cn.wanther.toolkit.dao.LocalDataDao;
import cn.wanther.toolkit.dao.SysDao;
import cn.wanther.toolkit.manager.SysManager;

public interface BeanContext {
	/**
	 * 线程工厂
	 * @return
	 */
	ThreadFactory getThreadFactory();
	/**
	 * 短时间内能完成操作的，不会阻塞的线程池
	 * @return
	 */
	Executor getShortTimeExecutor();
	/**
	 * 可能会阻塞的线程池
	 * @return
	 */
	Executor getConcurrentExecutor();
	/**
	 * Http(s)访问
	 * @return
	 */
	HttpExecutor getHttpExecutor();

	Statistics getStatistics();
	/**
	 * SQLite访问
	 * @return
	 */
	DataBaseHelper getDBHelper();

	LocalDataDao getLocalDataDao();
	
	SysDao getSysDao();
	
	SysManager getSysManager();
	
}
