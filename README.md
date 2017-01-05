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
这个时候就可以实现，当登录成功之后返回，用户名和头像等详情信息就会更新.其实这个逻辑还是很适合的，毕竟如果左侧有用户名和头像，而且主页的title的左侧也是一个头像的话，利用这个逻辑，还是很合适的。并且同一个被观察者可以被多个观察者订阅。逻辑代码是一样的，注册订阅同一个被观察者就行。
###待续(粘性事件)



