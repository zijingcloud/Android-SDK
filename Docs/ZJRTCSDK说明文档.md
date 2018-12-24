# ZJRTCSDK说明文档

## 目录

- [一、集成SDK](#一集成sdk) 

    - [1. 复制`zjrtc.aar`包到`app`的`libs`下](#1-复制zjrtcaar包到app的libs下)  

    - [2. 修改`app`下的`build.gradle`文件](#2-修改app下的buildgradle文件)  

    - [3. 在项目跟目录的`build.gradle`加入](#3-在项目跟目录的build.gradle加入)        
     
    - [4. SDK初始化](#4sdk初始化)   

- [二、参数设置](#二参数设置)            
     
    - [构造方法ZJRTCPreferences(Context context)](#构造方法说明)      
      
    - [其他方法说明](#其他方法说明)            
     
- [三、建立通话](#三建立通话)        
    - [Call类方法说明](#call类方法说明)          
           
    - [1.加入会议](#1加入会议)       
    
    - [2.点对点呼叫](#2点对点呼叫)    
    
- [四、被呼功能](#四被呼功能)       

    - [极光推送](#极光推送)        

    - [登录](#登录)            
       
        - [接口描述](#接口描述)  

        - [数据定义](#数据定义)        
    
    - [接收消息](#接收消息)     

        - [获取消息](#获取消息)        

        - [消息说明](#消息说明)      
        
        - [接听](#接听)        
     
        - [拒接](#拒接)            
        
        - [接口描述](#接口描述-1)            
        
        - [数据定义](#数据定义-1)    
    
- [五、自定义通话界面](#五自定义通话界面)       

     - [`ZJRTC`类接口说明](#zjrtc类接口说明)       
        
    - [1.构造方法](#1构造方法)       

    - [2.参数设置方法（建立呼叫连接前调用）](#2参数设置方法建立呼叫连接前调用)     
            
    - [3.建立连接方法](#3建立连接方法)   
    
    - [3.断开连接方法](#3断开连接方法)         
    
    - [4.获取信息接口](#4获取信息接口)      
    
    - [5.控制接口](#5控制接口)          

## 一、集成SDK

#### 1. 复制`zjrtc.aar`包到`app`的`libs`下

#### 2. 修改`app`下的`build.gradle`文件

   代码如下：

    ```
    android {

        defaultConfig {

            ...

            ndk {
                abiFilters "armeabi-v7a", "x86"
            }
        }

   	    compileOptions {
	        sourceCompatibility JavaVersion.VERSION_1_8
	        targetCompatibility JavaVersion.VERSION_1_8
	    }
	    repositories {
	        flatDir {
	            dirs 'libs'
	        }
	    }
    }
    dependencies {
        implementation 'com.squareup.okhttp3:okhttp:3.11.0'
	    implementation 'org.java-websocket:Java-WebSocket:1.3.9'
	    implementation 'com.google.code.gson:gson:2.8.5'
	    implementation 'com.github.LuckSiege.PictureSelector:picture_library:v2.2.3'
        implementation 'com.github.barteksc:pdfium-android:1.7.0'
    }

    ```
#### 3.在项目跟目录的`build.gradle`加入

    ```
    allprojects {
       repositories {
          jcenter()
          maven { url 'https://jitpack.io' }
       }
    }
    ```

#### 4.SDK初始化

	```
    public class MyApplication extends Application {

	    @Override
	    public void onCreate() {
            super.onCreate();
            //初始化SDK
            RTCManager.init(this);
	    }
	}
	```

## 二、参数设置
	
在进行通话前，需要先进行偏好设置。

偏好设置包括：api服务器地址设置、录制直播服务器地址设置、视频参数设置，不设置将使用默认值。

偏好设置一次即可一直生效，建议在app的设置页面做配置。

通过ZJRTCPreferences类进行设置

#### 构造方法ZJRTCPreferences(Context context)

创建ZJRTCPreferences的对象，
如在Activity中可按以下代码进行创建：

```
ZJRTCPreferences prefs = new ZJRTCPreferences(this);
```

### 其他方法说明

#### setApiServer(String apiServer)（必需）

设置服务器地址，必需设置项，没有设置将导致呼叫失败。

#### setLivingRecorderServer(String livingRecorderServer);

设置录制直播服务器地址。

#### setBandwidth(int bandwidth)

设置呼叫带宽，上行/下行一致。

#### setBandwidth(int upBw, int downBw)

分别设置上行/下行带宽。（其中下行带宽仅全编全解模式下生效）

#### setBandwidthSmall(int bandwidth)

设置小流视频的带宽。（设置发送多流后生效）

#### setPreviewVideoSize(int videoWidth, int videoHeight)

设置本地预览视频的分辨率。

#### setVideoSize(int videoWidth, int videoHeight)

设置发送/接收视频分辨率，发送接收一致。

#### setVideoSize(int upVideoWidth, int upVideoHeight, int downVideoWidth, int downVideoHeight)

分别设置发送/接收视频分辨率。（其中接收视频分辨率仅在全编全解模式下生效）

#### setSmallVideoSize(int videoWidth, int videoHeight)

设置发送小视频的分辨率。（设置发送多流后生效）

#### setPreviewVideoFps(int fps)

设置本地预览视频的帧率。

#### setVideoFps(int fps)

设置发送/接收视频的帧率。

#### setVideoFps(int upFps, int downFps)

分别设置发送/接收视频的帧率。（其中接收视频帧率在全编全解模式下生效）

#### setSmallVideFps(int fps)

设置发送小视频的帧率。（设置发送多流后生效）

#### setSimulcast(boolean simulcast)

设置是否使用转发（即接收多流）true为转发，false为全编全解。

#### setMultistream(boolean multistream)

设置是否发送多流。true发送一大一小两个视频流，false只发送一个大视频流。

#### setEnableH264HardwareEncoder(boolean enableH264HardwareEncoder)

设置是否使用H264硬编。

#### setDisableH264hHardwareDecoder(boolean disableH264hHardwareDecoder)

设置是否禁用H264硬解。

## 三、建立通话

建立通话前需要创建一个Call的对象，然后跳转到通话界面，并把call对象传过去，即可建立通话。

### Call类方法说明

#### setChanel(String chanel)

设置会议室号码或者联系人短号

#### setPassword(String password)

设置会议室密码

#### setNickname(String nickname)

设置显示名称

#### setAccount(String account)

设置自己的账号（如果登录了就得设置）

#### setCheckDup(String checkDup)

用于检查重复参会者。 入会时会检查同一会议室中是否已存在同名且checkDup值一样的参会者，如果存在则入会，并将同名参会者踢出会议。checkDup是一个30位以上长度的字符串，一般用MD5 Hash生成（32位）。

#### setClayout(String clayout)

转发模式下，控制接收大小视频流的数量。如"1:4"表示接收一个大流和四个小流。

#### setHideMe(boolean hideMe)

设置隐身入会

#### setHost(boolean host)

设置是否是主持人

#### setCallOut(boolean callOut)

点对点呼叫时设置是否是外呼

#### setCallName(String callName)

点对点外呼，设置呼叫人的名称

#### setMsgJson(String msgJson)

点对点被呼时。设置推送的消息的json字符串

### 1.加入会议

```
//创建call对象、设置呼叫参数
Call call = new Call();
call.setChanel(etAddress.getText().toString());
call.setPassword(etPwd.getText().toString());
call.setNickname(etName.getText().toString());
call.setCheckDup(((MyApplication)getApplication()).checkDup);
call.setClayout("1:4");
call.setHideMe(false);
call.setHost(true);

ZJRTCPreferences prefs = new ZJRTCPreferences(this);

Intent intent;
if (prefs.isSimulcast()) {
    //跳转到转发模式的界面
    intent = new Intent(this, ZJVideoSimulcastActivity.class);
} else {
    //跳转到全编全解的界面
    intent = new Intent(this, ZJVideoActivity.class);
}
intent.putExtra("call",call);
startActivity(intent);
```

### 2.点对点呼叫

```
Call call = new Call();
call.setChanel(etSipkey.getText().toString());
call.setCallName(etCallName.getText().toString());
call.setAccount(etAccount.getText().toString());
call.setNickname(etMyName.getText().toString());
call.setCallOut(true);

//跳转到点对点界面
Intent intent = new Intent(this, ZJVideoP2PActivity.class);
intent.putExtra("call",call);
startActivity(intent);
```

## 四、被呼功能

移动端被呼功能包括`点对点被呼`，和`会议室邀请被呼`两种场景。
被呼功能的实现，首先需要在APP上通过接口进行账号登录，登录成功后该账号被呼叫时，服务器端会通过极光推送发送消息给APP，APP可根据收到的消息来建立通话，或调用接口进行拒接。

### 极光推送

被呼功能需要使用极光推送，来接收服务器发送的消息。

关于极光推送，你需要做：

1.	在极光推送官网中[创建应用](https://www.jiguang.cn/dev/#/app/create)；
2.	创建应用成功后，请将AppKey和Master Secret发送给紫荆云视服务保障人员，他将为你们的APP在平台上进行相关配置；
3.	创建应用成功后，完成推送设置，在Android下填写你们的应用包名；
4.	[集成](https://docs.jiguang.cn/jpush/client/Android/android_guide/)极光推送服务至Android APP中；

极光推送官网：https://www.jiguang.cn/
极光集成文档：https://docs.jiguang.cn/jpush/client/Android/android_guide/
紫荆云视服务保障邮箱：xiaoqiang.yue@zijingcloud.com

以下是被呼功能的具体实现过程：

### 登录

#### 接口描述

账号登录功能，将账号、设备信息提交至服务器。

#### 数据定义

**请求地址：** `https://domain/api/registrations/<:account>/new_session`

account为登录账号，需要对账号进行URL编码。
URL编码可参考以下代码：

```
String accountEncoded = URLEncoder.encode(account, “UTF-8”);
```

**请求方式：** POST
**请求参数：**


| 参数类别 | 参数名称 | 类型 | 注释 | 说明 |
| :-: | :-: | :-: | :-: | :-: |
|	请求头部<br>(Header)	|	X-Cloud-<br>Authorization | String | 认证信息 |	用户名和密码的Base64加密字符串；<br>可参考举例说明。 |
|	请求头部<br>(Header)	|	Authorization | String | 认证信息 | 用户名和密码的Base64加密字符串； |
|	请求参数<br>(Body)	|	device_id | String | 设备id | 格式：极光推送RegistrationID__<br>极光推送APPKEY |
|	请求参数<br>(Body)	|	device_type | String | 设备类型 | 必须填写android	|

Header举例说明：

账号`test@zijingcloud.com`，账号密码`123456`。
用户名为test，密码为123456，编码格式：`用户名:密码`
将`test:123456`进行Base64编码为dGVzdDoxMjM0NTY=
然后在已编码的字符串前添加`“x-cloud-basic ”`

最终认证信息为：

| Header | 消息体 |
| --- | --- |
| X-Cloud-Authorization | x-cloud-basic dGVzdDoxMjM0NTY= |
| Authorization | x-cloud-basic dGVzdDoxMjM0NTY= |

### 接收消息

#### 获取消息

接收消息需使用自定义`广播接收器`，创建过程如下：

1. 创建自定义BroadcastReceiver，重写onReceive()方法；

    ```
    public class ZjJPushReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
        	if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
    	        String msg = bundle.getString(JPushInterface.EXTRA_MESSAGE);
    	        String json = new String(Base64.decode(msg, Base64.CRLF));
        	}
        }
    }
    ```

2. 在AndroidManifext.xml中注册receiver。

    ```
    <receiver
        android:name=".ZjJPushReceiver"
        android:enabled="true">
        <intent-filter>
            <action android:name="cn.jpush.android.intent.MESSAGE_RECEIVED" />
    
            <category android:name="com.zijingdemo" />
        </intent-filter>
    </receiver>
    ```
#### 消息说明

App收到消息后，可根据消息内容来实现接听和拒接功能。
消息字段说明：


| 参数名称 | 类型 | 注释 |
| :-: | :-: | :-: |
|	remote_alias	|	String	| 呼叫发起方的账号地址 |
|	remote_display_name	|	String | 呼叫发起方的显示名称 |
|	conference_alias	|	String | 通话地址 |
|	token	|	String	|	通话鉴权token |
|	time	|	String	|	呼叫发起的时间 |
|	service_type	|	String	| 呼叫类型，点对点或会议室 |
|   bsskey  |    String    |    点对点被呼关键字段


### 接听

被呼后接听，解析json，构建呼叫参数类call，并跳转到相应的通话界面。

### 拒接

#### 接口描述

账号被呼叫后，调用接口拒接此次通话。

#### 数据定义

**请求地址：** `https://domain/api/services/<:address>/end_session?token=<:token>`

address即通话地址，为接收消息的`conference_alias`字段，需要对通话地址进行URL编码；
token为接收消息的token字段。

**请求方式：** POST
**请求参数：** 无请求参数

## 五、自定义通话界面

ZJRTCSDK提供了全编全解、转发、点对点通话三种情况下的通话界面：

`ZJVideoActivity`、`ZJVideoSimulcastActivity`、`ZJVideoP2PActivity`。

用户也可以自己开发通话界面。使用`ZJRTC`类完成通话中的所有功能。

### `ZJRTC`类接口说明

### 1.构造方法

#### ZJRTC(Context context)

```
 ZJRTC zjrtc = new ZJRTC(this);
```

### 2.参数设置方法（建立呼叫连接前调用）

#### setZJRTCListener(ZJRTCListener listener)（必需）

设置会中监听，回调会议室、与会者、视频view等信息。

#### listener回调方法说明：

    ```
    public interface ZJRTCListener {
	    //本地视频回调
        void onLocalVideo(String uuid,ZJRTCView view);
	    //全编全解远端视频回调
        void onRemoteVideo(String uuid,ZJRTCView view);
	    //转发远端视频回调
        void onAddView(String uuid, ZJRTCView view);
        //转发远端视频退出
        void onRemoveView(String uuid, ZJRTCView view);
	    //本地视频流回调
        void onLocalStream(String uuid, String streamURL);
        //远端视频流回调
        void onRemoteStream(String uuid, String streamURL);
        //新增与会者
        void onAddParticipant(Participant participant);
        //与会者退出
        void onRemoveParticipant(String uuid);
        //与会者更新
        void onUpdateParticipant(Participant participant);
        //与会者声音列表回调 Stage的vad字段有两种取值：0为没有讲话 100为正在讲话
        void onStageVoice(List<Stage> stages);
	    //接收到消息
        void onChatMessage(String uuid, String message);
        //被服务器断开
        void onDisconnect(String reason);
        //双流状态回调
        void onPresentation(boolean isActive, String uuid);
	    //双流图片地址回调
        void onPresentationReload(String url);
        //布局更新回调
        void onLayoutUpdate(String layout, String hostLayout, String guestLayout);
        //布局更新与会者顺序回调
        void onLayoutUpdateParticipants(List<String> participants);
        //录制状态回调       
        void onRecordState(boolean isActive);
        //直播状态回调
        void onLiveState(boolean isActive);
        //自己角色更新
        void onRoleUpdate(String role);
        //会议室状态更新
        void onConferenceUpdate(ConferenceStatus status);
    }
    ```
可创建`ZJRTCListenerImpl`监听部分回调

    ```
	ZJRTCListener listener = new ZJRTCListenerImpl() {
		//Override部分方法进行监听
	}

	zjrtc.setZJRTCListener(listener);
    ```
#### setApiServer(String apiServer)

设置api服务器，优先级高于偏好设置，如果不设置则为偏好设置值。

#### setLivingRecorderServer(String livingRecorderServer)

设置录制直播服务器，优先级高于偏好设置，如果不设置则为偏好设置值。

#### setAccount(String account)

设置登录的账号。

#### setOemID(String oemID)

设置OemID。

#### setCheckdup(String checkdup)

用于检查重复参会者。 入会时会检查同一会议室中是否已存在同名且checkDup值一样的参会者，如果存在则入会，并将同名参会者踢出会议。checkDup是一个30位以上长度的字符串，一般用MD5 Hash生成（32位）。

#### setHideMe(boolean hideMe)

设置隐身入会

#### setCallType(CallType callType)

设置呼叫类型，`CallType`为`ZJRTC.CallType`枚举类型。

```
    public enum CallType {
        none,           //不建立音视频连接，用于管理会议场景
        video,          //建立音视频连接
        audio,          //只建立音频连接
        picture,        //发送图片
        screen,         //屏幕
        recvOnly,       //只接受音视频
        recvOnlyVideo   //只接收视频
    }
```
#### setClayout(String clayout)

转发模式下，控制接收大小视频流的数量。如"1:4"表示接收一个大流和四个小流。

#### setTime(String time)

被呼时设置接收到的消息中的呼叫时间。

#### setBsskey(String bsskey)

点对点被呼时设置接收到的消息中的bsskey。

#### setOneTimeToken(String oneTimeToken)

被呼时设置接收到的消息中的token。

### 3.建立连接方法

#### connect(String chanel, String password, String nickname, CallBack callBack)

建立连接,示例代码如下：

    ```
        zjrtc.setZJRTCListener(listener);
        zjrtc.setClayout("1:4");
        zjrtc.connect("1234", "123456", "王富贵", new CallBack() {

            @Override
            public void success(String response) {

            }

            @Override
            public void failure(ResponseCode responseCode) {

            }
            
        });
    ```

### 3.断开连接方法

#### disconnect()

### 4.获取信息接口

#### getParticipantList()

获取与会者列表。

#### isVMR()

是否是讲堂。

#### canRecord()

是否能录制。

#### canLive()

是否能直播。

#### List< MediaStats > getMediaStatistics()

获取音视频参数。

### 5.控制接口

#### disconnectAll()

结束会议。

#### setConferenceLock(boolean setting)

锁定/解锁会议。

#### disconnectParticipant(String uuid)

踢人。

#### setParticipantMute(String uuid, boolean setting)

静音/取消静音某人。

#### setParticipantVideoMute(String uuid, boolean setting)

静画/取消静画某人。

#### setParticipantStick(String uuid, boolean setting)

订阅/取消订阅某人。

#### unlockParticipant(String uuid)

允许某人入会。

#### setMuteAllGuests(boolean setting)

静音/取消静音所有访客。

#### setParticipantName(String uuid, String newName)

修改某人显示名称。

#### setParticipantRole(String uuid, String role)

修改某人角色，role值："chair"为主持人 "guest"为访客。

#### setLayout(String layout, String glayout)

设置布局。 layout为主持人布局，glayout为访客布局

取值："1:0","4:0","1:7","1:21","2:21"

#### getCameraDevices()

获取摄像头列表。

#### switchCamera()

切换摄像头。

#### muteVideo(boolean enable)

静音/取消静音。（关掉麦克风，自己说话别人听不到）

#### muteAudio(boolean enable)

静画/取消静画。（别人看不到自己）

#### sendChatMessage(String message)

发送消息。

#### dialOut(String destination, String protocol, String displayName, String role)

外呼。 destination：呼叫地址；protocol：协议；displayName：设置被呼人显示名字；role：角色（"chair","guest"）；

#### switchLiving(boolean enable)

打开/关闭直播

#### switchRecorder(boolean enable)

打开/关闭录制

#### sendPresentationImage(File file)

发送双流图片

#### sendPresentationScreen()

发送双流屏幕共享

#### stopPresentation()

停止发双流