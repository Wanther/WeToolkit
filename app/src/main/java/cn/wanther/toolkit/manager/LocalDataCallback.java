package cn.wanther.toolkit.manager;

/**
 * 有些场景需要先加载本地数据，显示出来，再去加载真实数据，来更新本地数据的操作的callback
 * 配合DataLoadAsyncController
 *
 * @author wanghe
 *
 * @param <T>
 */
public interface LocalDataCallback<T> extends Callback<T> {
    void onLocalDataLoaded(T data);
}
