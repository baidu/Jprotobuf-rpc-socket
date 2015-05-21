/**
 * 
 */
package com.baidu.bjf.lb.remoting.strategy.mcpack;

import com.baidu.bjf.lb.remoting.strategy.RemoteTransactionStrategyInterceptor;
import com.baidu.bjf.lb.remoting.strategy.rmi.RmiTransactionStrategyInterceptor;

/**
 * Load balance strategy for Mcpack RPC service.<br>
 * support transaction invoking.<br>
 * note:<br>
 *   it directly extends from {@link RmiTransactionStrategyInterceptor}
 * 
 * @author xiemalin
 * @see RmiTransactionStrategyInterceptor
 */
public class McpackTransactionStrategyInterceptor extends
RemoteTransactionStrategyInterceptor {

}
