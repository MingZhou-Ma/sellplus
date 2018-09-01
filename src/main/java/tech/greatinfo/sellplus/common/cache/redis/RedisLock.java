/**
 * <html>
 * <body>
 *  <P> Copyright Guangzhou Wanguo info-tech co,ltd.</p>
 *  <p> All rights reserved.</p>
 *  </body>
 * </html>
 */
package tech.greatinfo.sellplus.common.cache.redis;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;
import tech.greatinfo.sellplus.common.exception.SystemException;

/**
* @Package：tech.greatinfo.sellplus.common.cache.redis   
* @ClassName：RedisLock   
* @Description：   <p> redis锁</p>
* @Author： - Jason   
* @CreatTime：2018年8月15日 下午3:41:49   
* @Modify By：   
* @ModifyTime：  2018年8月15日
* @Modify marker：   
* @version    V1.0
 */
public class RedisLock {
	
	public static Logger logger = LoggerFactory.getLogger(RedisLock.class);

	/** 加锁标志 */
	public static final String LOCKED = "TRUE";

	/** 毫秒与毫微秒的换算单位 1毫秒 = 1000000毫微秒 */
	public static final long MILLI_NANO_CONVERSION = 1000 * 1000L;

	/** 默认超时时间（毫秒） */
	public static final long DEFAULT_TIME_OUT = 10000;

	public static final Random RANDOM = new Random();

	/** 锁的超时时间（秒），过期删除 */
	public static final int EXPIRE = 3 * 60;

	/**
	 * 切片
	 */
	private ShardedJedisPool shardedJedisPool;
	private ShardedJedis shardedJedis;

	/** 非切片 **/
	private JedisPool redisPool;
	
	private Jedis singleJedis;

	/** 切片redis锁状态标志  ***/
	private boolean shardedLocked = false;

	/** 单机redis锁状态标志  ***/
	private boolean singleLocked = false;

	/**
	* RedisLock.  This creates a shared RedisLock  -- 切片锁
	* @param shardedJedisPool
	 */
	public RedisLock(final ShardedJedisPool shardedJedisPool) {
		this.shardedJedisPool = shardedJedisPool;
		this.shardedJedis = this.shardedJedisPool.getResource();
	}

	/**
	* RedisLock.  This creates a single RedisLock  -- 单机锁
	* @param redisPool
	 */
	public RedisLock(final JedisPool redisPool) {
		try {
			this.redisPool = redisPool;
			this.singleJedis = this.redisPool.getResource();
		} catch (Exception e) {
			logger.error("redis server connect exception!", e);
			throw new SystemException(e);
		}
	}

	/**
 	 * @Description: shardedJedisPool
	 * @param shardedJedisPool 
	 * @Autor: Jason
	 */
	public void setShardedJedisPool(final ShardedJedisPool shardedJedisPool) {
		this.shardedJedisPool = shardedJedisPool;
		this.shardedJedis = this.shardedJedisPool.getResource();
	}

	public void setRedisPool(final JedisPool redisPool) {
		this.redisPool = redisPool;
		this.singleJedis = this.redisPool.getResource();
	}

	/**
	 * 单机redis加锁
	 * singleLock(); try {  doSomething(); } finally {  singleLock();} 的方式调用 
	 * @param key
	 * @param timeout 超时时间 
	 * @return 成功或失败标志 
	 */
	public boolean singleLock(String key, long timeout) {
		long nano = System.nanoTime();
		key = key.concat("_lock");
		timeout *= MILLI_NANO_CONVERSION;//毫秒与毫微秒的换算单位 1毫秒 = 1000000毫微秒
		try {
			while ( ( System.nanoTime() - nano ) < timeout) { //锁没有超时
				if (this.singleJedis.setnx(key, LOCKED) == 1) { //not exsist 
					this.singleJedis.expire(key, EXPIRE);//EXPIRE 默认3分钟.
					this.singleLocked = true;  //设置锁
					return this.singleLocked;   //锁标志
				}
				// 短暂休眠，避免出现活锁 millis -  nanos
				Thread.sleep(3, RANDOM.nextInt(500));
			}
		} catch (Exception e) {
			throw new RuntimeException("Locking error 单机锁异常!", e);
			//return false;
		}
		return false;
	}

	/** 
	 * 单机redis加锁 
	 * 应该以：singleLock lock(); try { doSomething(); } finally {   singleUnlock(); } 的方式调用 
	 * @param timeout 超时时间 
	 * @param expire 锁的超时时间（秒），过期删除 
	 * @return 成功或失败标志 
	 */
	public boolean singleLock(String key, long timeout, int expire) {
		key = key.concat("_lock");
		long nano = System.nanoTime();
		timeout *= MILLI_NANO_CONVERSION;
		try {
			while ( ( System.nanoTime() - nano ) < timeout) {  //超时时间 - 位于这个范围
				if (this.singleJedis.setnx(key, LOCKED) == 1) { //加锁标志
					this.singleJedis.expire(key, expire);//设置锁的过期时间和key
					this.singleLocked = true; //true锁住
					return this.singleLocked;
				}
				// 短暂休眠，避免出现活锁  
				Thread.sleep(3, RANDOM.nextInt(500));
			}
		} catch (Exception e) {
			throw new RuntimeException("Locking error", e);
			//return false;
		}
		return false;
	}

	/** 
	 * 单机redis加锁 
	 * 应该以：singleLock lock(); try { doSomething(); } finally { singleUnlock(); } 的方式调用 
	 * @return 成功或失败标志 
	 */
	public boolean singleLock(String key) {
		return singleLock(key, DEFAULT_TIME_OUT);
	}

	/** 
	 * 单机redis加锁 
	 * 无论是否加锁成功，都需要调用unlock 
	 */
	@SuppressWarnings("deprecation")
	public void singleUnlock(String key) {
		key = key.concat("_lock");
		try {
			if (this.singleLocked) {
				this.singleJedis.del(key);
			}
		} finally {
			this.redisPool.returnResource(this.singleJedis);
		}
	}

	/******************************* sharded redis ******************************************/
	/** 
	 * 对切片redis群加锁 
	 * 应该以：shardedLock lock(); try { doSomething(); } finally {    shardedUnlock(); } 的方式调用 
	 * @param timeout 超时时间 
	 * @return 成功或失败标志 
	 */
	public boolean shardedLock(String key, long timeout) {
		long nano = System.nanoTime();
		key = key.concat("_lock");
		timeout *= MILLI_NANO_CONVERSION;
		try {
			while ( ( System.nanoTime() - nano ) < timeout) {
				if (this.shardedJedis.setnx(key, LOCKED) == 1) {
					this.shardedJedis.expire(key, EXPIRE);
					this.shardedLocked = true;
					return this.shardedLocked;
				}
				// 短暂休眠，避免出现活锁  
				Thread.sleep(3, RANDOM.nextInt(500));
			}
		} catch (Exception e) {
			//throw new RuntimeException("Locking error", e);
			return false;
		}
		return false;
	}

	/** 
	 * 对切片redis群加锁  应该以： 
	 * 应该以：shardedLock lock(); try { doSomething(); } finally {    shardedUnlock(); } 的方式调用 
	 * @param timeout 超时时间 
	 * @param expire 锁的超时时间（秒），过期删除 
	 * @return 成功或失败标志 
	 */
	public boolean shardedLock(String key, long timeout, int expire) {
		key = key.concat("_lock");
		long nano = System.nanoTime();
		timeout *= MILLI_NANO_CONVERSION;
		try {
			while ( ( System.nanoTime() - nano ) < timeout) {
				if (this.shardedJedis.setnx(key, LOCKED) == 1) {
					this.shardedJedis.expire(key, expire);
					this.shardedLocked = true;
					return this.shardedLocked;
				}
				// 短暂休眠，避免出现活锁  
				Thread.sleep(3, RANDOM.nextInt(500));
			}
		} catch (Exception e) {
			//throw new RuntimeException("Locking error", e);
			return false;
		}
		return false;
	}

	/** 
	 * 对切片redis群加锁
	 * 应该以：shardedLock lock(); try { doSomething(); } finally {    shardedUnlock(); } 的方式调用 
	 * @return 成功或失败标志 
	 */
	public boolean shardedLock(String key) {
		return shardedLock(key, DEFAULT_TIME_OUT);
	}

	/** 
	 * 对切片redis群解锁 
	 * 无论是否加锁成功，都需要调用unlock 
	 */
	@SuppressWarnings("deprecation")
	public void shardedUnlock(String key) {
		key = key.concat("_lock");
		try {
			if (this.shardedLocked) {
				this.shardedJedis.del(key);
			}
		} finally {
			this.shardedJedisPool.returnResource(this.shardedJedis);
		}
	}

}