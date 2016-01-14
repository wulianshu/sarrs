package com.chaojishipin.sarrs.async;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.chaojishipin.sarrs.thirdparty.UIs;
import com.chaojishipin.sarrs.utils.LogUtil;
import com.letv.http.bean.LetvBaseBean;
import com.letv.http.bean.LetvDataHull;

/**
 * 网络请求的异步任务
 * */
public abstract class MoviesHttpAsyncTask<T extends LetvBaseBean> extends
		MoviesBaseTaskImpl implements MoviesHttpAsyncTaskInterface<T> {

	protected Context context;

	private Handler handler;

	private boolean isLocalSucceed = false;

	private String message;

	public MoviesHttpAsyncTask(Context context) {
		this.context = context;
		handler = new Handler(Looper.getMainLooper());
	}

	@Override
	public final boolean run() {
		try {
			if (!isCancel) {// 加载本地数据
				final T t = loadLocalData();

				if (t != null) {
					isLocalSucceed = true;
					postUI(new Runnable() {

						@Override
						public void run() {
							loadLocalDataComplete(t);
						}
					});

				}
			}

			boolean hasNet = UIs.hasNet(context);

			if (!hasNet) {// 判断网络
				cancel();
				postUI(new Runnable() {

					@Override
					public void run() {
						if (!isLocalSucceed) {
							netNull();
						}
					}
				});

				return true;
			}

			if (!isCancel) {// 加载网络数据
				final LetvDataHull<T> dataHull = doInBackground();
				if (!isCancel) {
					postUI(new Runnable() {

						@Override
						public void run() {
							try {
								isCancel = true;

								if (dataHull == null) {
									if (!isLocalSucceed)
										netErr(0, null);
								} else {
									message = dataHull.getMessage();
									if (dataHull.getDataType() == LetvDataHull.DataType.DATA_IS_INTEGRITY) {
										//TODO 兼容string类型返回
										onPostExecute(dataHull.getUpdataId(),
												dataHull.getDataEntity());
                                      /*  if(dataHull.getDataEntity() instanceof LetvBaseBean){
											onPostExecute(dataHull.getUpdataId(),
													dataHull.getDataEntity());
											LogUtil.e("xll", "parse bean complete" + dataHull.getDataEntity());
										}else{
											onPostExecute(dataHull.getUpdataId(),
													dataHull.getSourceData());
											LogUtil.e("xll", "parse string complete" + dataHull.getSourceData());
										}*/
										LogUtil.e("xll", "parse complete");


									} else if (dataHull.getDataType() == LetvDataHull.DataType.DATA_CAN_NOT_PARSE) {
										if (!isLocalSucceed)
											dataNull(dataHull.getUpdataId(),
													message);
									} else if (dataHull.getDataType() == LetvDataHull.DataType.DATA_NO_UPDATE) {
										if (!isLocalSucceed)
											noUpdate();
									} else {
										if (!isLocalSucceed)
											netErr(dataHull.getUpdataId(),
													message);
									}
								}
								cancel();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			postUI(new Runnable() {

				@Override
				public void run() {
					try {
						netErr(0, null);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}

		return true;
	}

	@Override
	public boolean onPreExecute() {
		return true;
	}

	private void postUI(Runnable runnable) {
		if (Thread.currentThread() != Looper.getMainLooper().getThread()) {
			handler.post(runnable);
		} else {
			runnable.run();
		}
	}

	public String getMessage() {
		return message;
	}

	public final void start() {
		isCancel = !onPreExecute();
		if (isCancel) {
			postUI(new Runnable() {

				@Override
				public void run() {
					preFail();
				}
			});
		}
		mThreadPool.addNewTask(this);// 加入线程队列，等待执行
	}

	/**
	 * 请求前，准备失败回调
	 * */
	public void preFail() {
	}

	/**
	 * 没有网络，回调
	 * */
	public void netNull() {
	};

	/**
	 * 网络异常和数据错误，回调
	 * */
	public void netErr(int updateId, String errMsg) {
	};

	/**
	 * 数据为空，回调
	 * */
	public void dataNull(int updateId, String errMsg) {
	};

	/**
	 * 数据无更新，回调
	 * */
	public void noUpdate() {
	}

	/**
	 * 加载本地内容
	 * */
	public T loadLocalData() {
		return null;
	}

	/**
	 * 加载本地内容完成后，回调
	 * */
	public boolean loadLocalDataComplete(T t) {
		return false;
	}

	/**
	 * 本地数据是否加载成功
	 * */
	public boolean isLocalSucceed() {
		return isLocalSucceed;
	}
}
