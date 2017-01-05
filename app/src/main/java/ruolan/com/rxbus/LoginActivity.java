package ruolan.com.rxbus;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.convert.StringConvert;
import com.lzy.okrx.RxAdapter;

import ruolan.com.rxbus.bean.User;
import ruolan.com.rxbus.constant.HttpUrlControl;
import ruolan.com.rxbus.rxbus.RxBus;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;

public class LoginActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private TextInputLayout mTextInputLayoutName;
    private EditText mEditTextName;
    private TextInputLayout mTextInputLayoutPassword;
    private EditText mEditTextPassword;
    private Button mButtonLogin;


    private String mUserName;
    private String mPassWord;
    private User.DataBean mDataBean;
    private MaterialDialog.Builder mBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        initListener();
       // btnLogin();
    }

    private void initListener() {
        mButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnLogin();
            }
        });
    }

    private void btnLogin() {
        mUserName = mEditTextName.getText().toString().trim();
        mPassWord = mEditTextPassword.getText().toString().trim();
        if (!TextUtils.isEmpty(mUserName) && !TextUtils.isEmpty(mPassWord)){
            OkGo.post(HttpUrlControl.LOGIN_URL)
                    .params("phone",mUserName)
                    .params("password",mPassWord)
                    .getCall(StringConvert.create(),RxAdapter.<String>create())
                    .doOnSubscribe(new Action0() {
                        @Override
                        public void call() {
                            mBuilder = new MaterialDialog.Builder(LoginActivity.this);
                            mBuilder
                                 .content("登录中...")
                                 .show();
                        }
                    }).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<String>() {
                        @Override
                        public void call(String s) {
                            mBuilder.content("登录成功，正在返回...");
                            mBuilder.build().show();
                            User user = new Gson().fromJson(s,new TypeToken<User>(){}.getType());
                            if (user != null){
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
                            }

                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            mBuilder.build().dismiss();
                        }
                    });
        }
    }

    private void initView() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mTextInputLayoutName = (TextInputLayout) findViewById(R.id.textInputLayoutName);
        mEditTextName = (EditText) findViewById(R.id.editTextName);
        mTextInputLayoutPassword = (TextInputLayout) findViewById(R.id.textInputLayoutPassword);
        mEditTextPassword = (EditText) findViewById(R.id.editTextPassword);
        mButtonLogin = (Button) findViewById(R.id.buttonLogin);
    }
}
