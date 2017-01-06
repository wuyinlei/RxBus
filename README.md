# RxBus  demo解析

标签（空格分隔）： RxJava

---
###前言
这里使用的是Rx实现的事件总线，类似于EventBus和Otto，使用rx实现，有效的减少jar大小，并且使用和EventBus差不多，也是发送事件，然后被观察者订阅事件并在接收到事件之后做响应的处理。
###例子(用在这里不是很好(毕竟就两个activity，更好的可以通过回调))
这里使用的是登录，如果登录成功，就在LoginActivity里面发送一个UserEvent事件，然后在MAinActivity里面进行注册事件，并对注册之后接收到的事件进行处理。
LoginActivity.class里面逻辑
```
                                mDataBean = user.getData();
                                if (mDataBean != null){
                                    String username = mDataBean.getUsername();
                                    String imageUrl = mDataBean.getLogo_url();
                                    String useremail = mDataBean.getEmail();
                                    UserEvent event = new UserEvent(username,imageUrl,useremail);
                                    RxBus.getDefault().post(event);
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            mBuilder.build().dismiss();
                                            LoginActivity.this.finish();
                                        }
                                    },500);
                                }
                       
```
MainActivity.class里面逻辑
```
 /**
     * 接受到事件并做相关处理
     */
    private void subscribeEvent(){
        RxSubscriptions.remove(mRxSub);
        mRxSub = RxBus.getDefault().toObservable(UserEvent.class)
                .map(new Func1<UserEvent, UserEvent>() {
                    @Override
                    public UserEvent call(UserEvent userEvent) {
                        return userEvent;
                    }
                })
                .subscribe(new RxBusSubscriber<UserEvent>() {
                    @Override
                    public void onEvent(UserEvent userEvent) {
                        if (userEvent != null){
                            mTvEmail.setText(userEvent.mUserEmail);
                            mTvName.setText(userEvent.mUserName);
                            Glide.with(MainActivity.this)
                                    .load(userEvent.mImageUrl).asBitmap()
                            .into(mIvAvator);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                    }
                });
    }

```
当然了，不要忘记注册事件监听
```
  /*注册订阅RxBus事件*/
        subscribeEvent();
```
![](http://ww4.sinaimg.cn/mw690/006jcGvzgw1fbgqxwndu8g30ay0jaqk4.gif)
这个时候就可以实现，当登录成功之后返回，用户名和头像等详情信息就会更新.其实这个逻辑还是很适合的，毕竟如果左侧有用户名和头像，而且主页的title的左侧也是一个头像的话，利用这个逻辑，还是很合适的。并且同一个被观察者可以被多个观察者订阅。逻辑代码是一样的，注册订阅同一个被观察者就行。
###待续(粘性事件)
有的时候我们可能会在注册观察者之前就去发送了事件，但是如果我们直接发送了一般的事件，那么由于我们没有注册接收事件，这个时候我们即使后来注册了接收事件，那么也是接收不到的，这个时候我们就想到了粘性事件。
>粘性事件就好比我们在任何时候发送，发送之后如果没有注册者去接收，那么就会缓存到一个cache中，等待观察者接收，等到观察者注册之后接收并且去消费。当然了RxBus的粘性事件只会被消费最后一个被保存的事件。

#####我们先来看下如下的一个例子。
这里我们先发送一个粘性事件，当然例子当中为了对比，我们的一般事件和粘性事件都在点击的时候触发
```
 mTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RxBus.getDefault().post(new TestEvent("Normal Ceshi"));
                RxBus.getDefault().postSticky(new TestEvent("Sticky Ceshi"));
                startActivity(new Intent(MainActivity.this,TestActivity.class));
            }
        });
```
当然了，虽然都触发，但是我们传递的消息是不通的(便于对比),这个时候我们在跳转之后的TestActivity里面进行观察者注册和接收。
我们看下逻辑写法。(当然一般的注册和以上的代码是一样的，这里就不写了，直接上粘性事件注册)
```
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
```
我们来看下效果图吧。
![](http://ww1.sinaimg.cn/mw690/006jcGvzgw1fbgqz683lbg30ay0jagpd.gif)
####注意
一定要在activity或者fragment销毁的时候调用
```
 @Override
    protected void onDestroy() {
        super.onDestroy();
        //这里进行移除  也是可以测试正常的发送事件如果在注册之前发送  是接收不到的
        RxSubscriptions.remove(mRxSub);
        //而这个就是可以的，及时事件发送是在注册之前，但是粘性事件会保留，直到有观察者注册事件并且去接收
        RxSubscriptions.remove(mRxStickySub);
    }
```
因为在之前我没有去调用这个销毁(防止内存泄漏),当我在此点击跳转的时候，一般事件也出现了弹框，而且粘性事件出现了3次(~~~~(>_<)~~~~)。
基本上以上就可以满足一般的项目用法。
####代码传送门
https://github.com/wuyinlei/RxBus
####感谢作者
感谢@YoKey,有兴趣的可以看下http://www.jianshu.com/p/ca090f6e2fe2
再次谢谢作者，这里我只是学习记录。方便于自己以后查找。

