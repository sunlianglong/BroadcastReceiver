----------


**Broadcast Receiver**


----------
·广播（Broadcas）是一种广泛运用的在应用程序之间传输信息的机制 。而 广播接收者（BroadcastReceiver）是对发送出来的广播进行过滤接收并响应的一类组件。

·BroadcastReceiver 自身并不实现图形用户界面，但是当它收到某个通知后， BroadcastReceiver 可以启动Activity 作为响应，或者通过 NotificationMananger 提醒用户，或者启动 Service 等等。

·标准广播：完全异步执行的广播 无法被截断 所有的广播接收器同步接收 

·有序广播：同步执行的广播 优先级高的广播首先接收 可以截断

---


> 注册广播的两种方式


 ···动态注册示例：在你更改网络状态（数据连接）时会收到Toast提醒。
 ```java
    public class MainActivity extends AppCompatActivity {
    private IntentFilter intentFilter;
    private NetworkChangeReceiver networkChangeReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        networkChangeReceiver = new NetworkChangeReceiver();
        registerReceiver(networkChangeReceiver,intentFilter);

    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        unregisterReceiver(networkChangeReceiver);
    }
    class NetworkChangeReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context,"network changes",Toast.LENGTH_LONG).show();
        }
    }
    }

```
    

创建了一个IntentFilter实例，并为它添加了一个值为`android.net.conn.CONNECTIVITY_CHANGE`的action：当网络状态发生变化时，系统发出一条值为此的广播。   另外，动态注册的广播接收器一定都要取消注册。这里是在`onDestory()`方法中调用`unregisterReceiver()`方法来实现的。
更加人性化的代码：告诉你网络发生了什么样的变化     在`onCreate()`方法中，首先通过`getSystemService()`方法得到了`ConnectivityManager()`实例，这是一个系统服务类，专门用于管理网络连接的。
```java
    class NetworkChangeReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectivityManager =(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if(networkInfo!=null&&networkInfo.isAvailable()){
                Toast.makeText(context,"network is available",Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(context,"network is unavailable",Toast.LENGTH_LONG).show();
            }
        }
    }
```
另外，有一点很重要的说明：要在AndroidManifest.xml文件中加入权限 
```java
`<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"></uses-permission>`
```

···静态注册示例：实现开机启动时的Toast提醒。

一·AndroidManifest.xml文件中：

1···访问权限：
```java
`<uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED"></uses-permission>`
```
2···标签`<receiver>`注册：
```java
    <receiver android:name=".MyBroadcastReceiver">
            <intent-filter android:priority="100">
                <action android:name="android.intent.action.BOOT_COMPLETED">
                </action>
            </intent-filter>
        </receiver>
 ```
二.直接新建一个BootBroadcastReceiver继承自BroadcastReceiver，代码如示：
```java
    public class BootBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context,"Boot Complete",Toast.LENGTH_LONG).show();
    }
```
不要在`onReceive()`方法中添加过多内容，因为在广播接收器中是不允许开线程的，当`onReceive()`方法运行了较长时间还没有结束时，程序就会报错。另外注意，BroadcastReceiver 会堵塞主线程。唯有 `onReceive()` 结束，主线程才得以继续进行。



----------

> 发送自定义广播


自定义广播也要先定义一个广播接收器   在AndroidManifest.xml文件对广播接收器进行注册  
在按钮的点击事件中：
```java
    public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button  = (Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent("com.sunlianglong.broadcast.MY_BROADCAST");
                sendBroadcast(intent);
              //sendOrderedBroadcast(intent,null);
            }
        });
    }
    }
```
···这样，所有监听的广播接收器就会收到Toast消息，此外，由于广播是使用Intent进行传递的，因此你可以在Intent中携带一些数据。。。

···注：(1)发送有序广播时，将`sendBroadcast()`方法改为`sendOrderedBroadcast()`方法并传入参数。

(2)进行注册时， `<intent-filter android:priority="100">`是设置优先级 

(3)当A收到广播后，可以向广播中添加一些数据给下一个接收者(`intent.putExtra()`)，或者终止广播（在当前BroadcastReceiver内调用方法 `abortBroadcast()` ）。

---

>        使用本地广播

(1)安全性提升，广播只能在程序内部进行传递，广播接收器也只能接受本应用程序发出的广播。

(2)只是使用了一个LocalBroadcastManager来对广播进行管理，并提供了发送广播和注册广播的方法：和前面所说的动态注册广播是一样的。

(3)本地广播只能通过动态注册来实现。因为静态注册主要是为了让程序在未启动的情况下也能收到广播，发送本地广播时程序完全已经启动，也就不需要使用静态注册的功能。

(4)如果你的广播信息是用于应用的自我交流（不需要与其它应用协作），那么建议使用LocalBroadcastManager.

---

> 需要收听权限的广播

改变Activity中发送广播的方法：

```java
sendOrderedBroadcast(new Intent("com.sunlianglong.test.hahaha"), "com.sunlianglong.test");  

```
在发起广播的应用中，需要在AndroidManifest文件中配置自定义的权限：

```java
<permission android:protectionLevel="normal" android:name="com.sunlianglong.test"></permission>  

```
相应的，接收器所在的应用中必须设置接收权限：

```java
uses-permission android:name="com.sunlianglong.test"></uses-permission>  
```

---

> **注意**


1.生命周期只有十秒左右，如果在 onReceive() 内做超过十秒内的事情，就会报ANR(Application No Response) 程序无响应的错误信息，如果需要完成一项比较耗时的工作 , 应该通过发送 Intent 给 Service, 由Service 来完成 . 这里不能使用子线程来解决 , 因为 BroadcastReceiver 的生命周期很短 , 子线程可能还没有结束BroadcastReceiver 就先结束了 .BroadcastReceiver 一旦结束 , 此时 BroadcastReceiver 的所在进程很容易在系统需要内存时被优先杀死 , 因为它属于空进程 ( 没有任何活动组件的进程 ). 如果它的宿主进程被杀死 , 那么正在工作的子线程也会被杀死 . 所以采用子线程来解决是不可靠的。

2.动态注册广播接收器还有一个特点，就是当用来注册的Activity关掉后，广播也就失效了。静态注册无需担忧广播接收器是否被关闭,只要设备是开启状态,广播接收器也是打开着的。也就是说哪怕app本身未启动,该app订阅的广播在触发时也会对它起作用。

系统常见广播Intent,如开机启动、电池电量变化、时间改变等广播。



#利用广播实现强制下线功能
### Force Offline
- 关闭所有活动的功能
- 登陆界面以及跳转的实现
- 新建广播接收器，利用AlertDialog实现下线

> 关闭所有活动

·创建一个ActivityCollector类用于管理所有的活动

```java
public class ActivityCollector {
    public static List<Activity> activities =new ArrayList<Activity>();
    public static void addActivity(Activity activity){
        activities.add(activity);
    }
    public static void removeActivity(Activity activity){
        activities.remove(activity);
    }
    public static void finishAll(){
        for (Activity activity:activities){
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }
}
//通过一个List来暂存活动，然后提供一个addActivity()方法用于向List中添加一个活动，
//提供了一个removeActivity方法用于从List中移除活动，
//最后提供了一个finishAll()方法用于将List中储存的活动全部销毁掉。
```
·创建BaseActivity类做为所有活动的父类

```java
public class BaseActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
        //将当前正在创建的活动添加到活动管理器中。
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        ActivityCollector.removeActivity(this);
        //表明将一个马上要摧毁的活动从活动管理器中移除。
    }
}
```
> 登录布局

![](https://github.com/sunlianglong/Img/raw/master/Photos/P.jpg) 

> 简单登录功能
```java
public class LoginActivity extends BaseActivity {
    private EditText accountEdit;
    private EditText passwordEdit;
    private Button login;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        accountEdit = (EditText)findViewById(R.id.account);
        passwordEdit = (EditText)findViewById(R.id.password);
        login = (Button)findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String account = accountEdit.getText().toString();
                String password = passwordEdit.getText().toString();
                if(account.equals("admin")&&password.equals("123456")){
                    Intent intent = new  Intent(LoginActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    Toast.makeText(LoginActivity.this,"account or password is invalid",Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}

```

> 强制下线功能

修改MainActivity中的代码，发送自定义广播：

```java
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button forceOffline  = (Button)findViewById(R.id.force_offline);
        forceOffline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent("com.sunlianglong.broadcastbestpractice.FORCE_OFFLINE");
                sendBroadcast(intent);
            }
        });
    }
}

```
新建ForceOfflineReceiver类

```java
public class ForceOfflineReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);//构建一个对话框
        dialogBuilder.setTitle("Worning");
        dialogBuilder.setMessage("You are to be offline.Please try to login again");
        dialogBuilder.setCancelable(false);//将对话框设置为不可取消
        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override   //给对话框注册按钮
            public void onClick(DialogInterface dialog, int which) {
                ActivityCollector.finishAll();
                //销毁所有活动
                Intent intent = new Intent(context, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //解释：http://www.cnblogs.com/lwbqqyumidi/p/3775479.html
                context.startActivity(intent);
                //重新启动LoginActivity
            }
        });
        AlertDialog alertDialog = dialogBuilder.create();
        //设置AlertDialog的类型，以保证在广播接收器中可以正常弹出。

        alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        alertDialog.show();
        //因为Alert的显示需要依附于一个确定的Activity类。而以上做法就是声明我们要弹出的这个
        // 提示框是一个系统的提示框，即全局性质的提示框，所以只要手机处于开机状态，无论它
        // 现在处于何种界面之下，只要调用alertDialog.show()，就会弹出提示框来。
    }
}
```
针对为什么会需要FLAG_ACTIVITY_NEW_TASK标志，我从[here](http://www.cnblogs.com/lwbqqyumidi/p/3775479.html)得到了学习

在AndroidManifest.xml中设置权限并进行注册，将LoginActivity设置为主活动

```java
<uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW">

 <receiver android:name=".ForceOfflineReceiver">
            <intent-filter>
                <action android:name="com.sunlianglong.broadcastbestpractice.FORCE_OFFLINE">
                </action>
            </intent-filter>
        </receiver>
```
![](https://github.com/sunlianglong/Img/raw/master/Photos/a.png) 
















