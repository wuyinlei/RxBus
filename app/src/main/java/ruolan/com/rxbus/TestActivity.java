package ruolan.com.rxbus;

import android.media.tv.TvView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import ruolan.com.rxbus.event.TestEvent;
import ruolan.com.rxbus.helper.RxSubscriptions;
import ruolan.com.rxbus.rxbus.RxBus;
import ruolan.com.rxbus.rxbus.RxBusSubscriber;
import rx.Subscription;
import rx.functions.Func1;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        //进行注册
        subscribeEvent();

        //粘性事件注册
        subscribeStickyEvent();
    }

    private Subscription mRxSub,mRxStickySub;

    /**
     * 接受到事件并做相关处理
     */
    private void subscribeEvent(){
        RxSubscriptions.remove(mRxSub);
        mRxSub = RxBus.getDefault().toObservable(TestEvent.class)
                .map(new Func1<TestEvent, TestEvent>() {
                    @Override
                    public TestEvent call(TestEvent testEvent) {
                        return testEvent;
                    }
                })
                .subscribe(new RxBusSubscriber<TestEvent>() {
                    @Override
                    public void onEvent(TestEvent testEvent) {
                        Toast.makeText(TestActivity.this, testEvent.test, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                    }
                });

        RxSubscriptions.add(mRxSub);
    }

    /**
     * 接受粘性事件并做相关处理
     */
    public void subscribeStickyEvent(){
        if (mRxStickySub != null && !mRxStickySub.isUnsubscribed()){
            RxSubscriptions.remove(mRxStickySub);  //移除
        } else {
            TestEvent testEvent = RxBus.getDefault().getStickyEvent(TestEvent.class);

            mRxStickySub = RxBus.getDefault().toObservableSticky(TestEvent.class)
                    .map(new Func1<TestEvent, TestEvent>() {
                        @Override
                        public TestEvent call(TestEvent testEvent) {
                            try {
                            //在这里主动进行异常拦截
                            } catch (Exception e){

                            }
                            return testEvent;
                        }
                    }).subscribe(new RxBusSubscriber<TestEvent>() {
                        @Override
                        public void onEvent(TestEvent testEvent) {
                            Toast.makeText(TestActivity.this, testEvent.test, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(Throwable e) {
                            super.onError(e);
                        }
                    });
        }

        RxSubscriptions.add(mRxStickySub);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //这里进行移除  也是可以测试正常的发送事件如果在注册之前发送  是接收不到的
        RxSubscriptions.remove(mRxSub);
        //而这个就是可以的，及时事件发送是在注册之前，但是粘性事件会保留，直到有观察者注册事件并且去接收
        RxSubscriptions.remove(mRxStickySub);
    }
}
