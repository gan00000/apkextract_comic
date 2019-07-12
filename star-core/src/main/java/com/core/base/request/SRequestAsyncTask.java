package com.core.base.request;

import android.os.AsyncTask;

import com.core.base.utils.PL;
import com.core.base.utils.ThreadUtil;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
* <p>Title: SRequestAsyncTask</p>
* <p>Description: 由于系统提供的标准AsyncTask在targetSdkVersion 4——12（包括12）下是并发执行的
* 		而在targetSdkVersion >= 13是顺序执行的，为了满足任何情况下异步并发执行，故编写此类来工作，此类继承AsyncTask</p>
* @author GanYuanrong
* @date 2014年12月3日
*/
public abstract class SRequestAsyncTask extends AsyncTask<String, Integer, String> {
	
	private static Method executeOnExecutorMethod;

	private static final int DEFAULT_CORE_POOL_SIZE = 5;
	private static final int DEFAULT_MAXIMUM_POOL_SIZE = 128;
	private static final int DEFAULT_KEEP_ALIVE = 1;
	private static final Object LOCK = new Object();

    private static final BlockingQueue<Runnable> DEFAULT_WORK_QUEUE = new LinkedBlockingQueue<Runnable>(10);

    private static volatile Executor executor;
    
    private static final ThreadFactory DEFAULT_THREAD_FACTORY = new ThreadFactory() {
        private final AtomicInteger counter = new AtomicInteger(0);

        public Thread newThread(Runnable runnable) {
            return new Thread(runnable, "SRequestAsyncTask_THREAD #" + counter.incrementAndGet());
        }
    };
    

	static {
		for (Method method : AsyncTask.class.getMethods()) {
			if ("executeOnExecutor".equals(method.getName())) {
				Class<?>[] parameters = method.getParameterTypes();
				if ((parameters.length == 2) && (parameters[0] == Executor.class) && parameters[1].isArray()) {
					executeOnExecutorMethod = method;
					break;
				}
			}
		}
	}

	private boolean asyncExcute = false;
	
	/**
	 * <p>Description: 开始执行异步请求</p>
	 * @return 返回当前实例
	 * @date 2015年1月14日
	 */
	public SRequestAsyncTask asyncExcute() {
		// 必须通过主线程调用
		ThreadUtil.checkUiThread();
		asyncExcute = true;
		try {
			if (executeOnExecutorMethod != null) {
				executeOnExecutorMethod.invoke(this, getExecutor(), null);
				return this;
			}
		} catch (InvocationTargetException e) {
			PL.i("e:" + e.getMessage());
			//e.printStackTrace();
			// fall-through
		} catch (IllegalAccessException e) {
			PL.i("e:" + e.getMessage());
			//e.printStackTrace();
			// fall-through
		}

		this.execute();//2.3或以下无executeOnExecutorMethod方法，故调用系统原来的
		return this;

	}

	/**
	 * Returns the Executor used by the SDK for non-AsyncTask background work.
	 *
	 * By default this uses AsyncTask Executor via reflection if the API level
	 * is high enough. Otherwise this creates a new Executor with defaults
	 * similar to those used in AsyncTask.
	 *
	 * @return an Executor used by the SDK. This will never be null.
	 */
	private static Executor getExecutor() {
		synchronized (LOCK) {
			if (SRequestAsyncTask.executor == null) {
				Executor executor = getAsyncTaskExecutor();
				if (executor == null) {
					executor = new ThreadPoolExecutor(DEFAULT_CORE_POOL_SIZE, DEFAULT_MAXIMUM_POOL_SIZE, DEFAULT_KEEP_ALIVE, TimeUnit.SECONDS,
							DEFAULT_WORK_QUEUE, DEFAULT_THREAD_FACTORY);
				}
				SRequestAsyncTask.executor = executor;
			}
		}
		return SRequestAsyncTask.executor;
	}
	
	public static Executor getSdkExecutor() {
		return getExecutor();
	}

	private static Executor getAsyncTaskExecutor() {
		Field executorField = null;
		try {
			executorField = AsyncTask.class.getField("THREAD_POOL_EXECUTOR");
		} catch (NoSuchFieldException e) {
			return null;
		}

		Object executorObject = null;
		try {
			executorObject = executorField.get(null);
		} catch (IllegalAccessException e) {
			return null;
		}

		if (executorObject == null) {
			return null;
		}

		if (!(executorObject instanceof Executor)) {
			return null;
		}

		return (Executor) executorObject;
	}
	
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		PL.i("onPreExecute");
		if (!asyncExcute) {
			throw new RuntimeException("please use asyncExcute() instead of execute()");
		}
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		PL.i("onPostExecute");
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);
		PL.i("onProgressUpdate");
	}

}
