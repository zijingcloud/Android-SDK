## SDK开发文档-常用功能列表

#### 目录 

- [常用功能实现（主要类VCRTC）](#常用功能实现主要类vcrtc)            
 - [1. 屏幕共享](#1-屏幕共享)            
 - [2. 共享图片](#2-共享图片)            
 - [3. 共享PDF](#3-共享pdf)            
 - [4. 查看通话质量](#4-查看通话质量)            
 - [5. 视频通话质量对照表（具体设置方法在API文档中）](#5-视频通话质量对照表具体设置方法在api文档中)            
 - [6. 打开/关闭麦克风](#6-打开关闭麦克风)            
 - [7. 打开/关闭摄像头](#7-打开关闭摄像头)            
 - [8. 切换摄像头](#8-切换摄像头)            
 - [9. 被呼功能](#9-被呼功能)               
    - [1. 简介](#1-简介)                
    - [2. 登录](#2-登录)                
    - [3. 注册静态广播接收器监听全局消息](#3-注册静态广播接收器监听全局消息) 

#### 常用功能实现类VCRTC

所有功能均需要VCRTC类的实例化对象完成。

#### 1. 屏幕共享

功能描述：android屏幕共享需要支持`Android5.0`版本以上(包括5.0)，在视频通话中，将屏幕以视频的方式分享给其他说话人或者观众看，提高沟通效率。屏幕共享在如下场景中应用广泛：

* 会议中分享自己屏幕的内容，展示自己的操作。

实现方法：所有示例代码均假设vcrtc已经初始化完毕（API详情见SDK开发文档-常用API列表）并且已经完成环境准备（详见快速开始文档）。

1. 发起屏幕共享：利用vcrtc发送屏幕共享请求

    ```java
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        vcrtc.sendPresentationScreen();
        .....
    }
    ```

2. 发起屏幕共享回调：

    ```java
    @Override
    public void onScreenShareState(boolean isActive) {
        .....
        //isActive = true 用户允许了权限，发送成功
        //跳转到桌面
        startScreenShare（);
    }
    ```
   
3. 跳转桌面，开始共享：

    ```java
    private void startScreenShare() {
        //切到桌面
        Intent home = new Intent(Intent.ACTION_MAIN);
        home.addCategory(Intent.CATEGORY_HOME);
        startActivity(home);
    }
    ```

4. 停止共享：

    ```java
    private void stopPresentation() {
        vcrtc.stopPresentation();
     	.....
    }
    ```

5. 屏幕共享接收端回调，专属云和公有云的接收屏幕共享视频的回调略有不同

    ```java
    //公有云以一张张图片的形式接收其他端屏幕共享内容
    @Override
    public void onPresentationReload(String picUrl) {
        //picUrl为双流图片的链接
    }
    //专属云一视频的形式接收其他端屏幕共享内容，返回的是远端双流视频view或视频流，onAddView和onRemoteStream根据实际需求监听一个就好。
      	@Override
    public void onAddView(String uuid, VCRTCView vcrtcView, String viewType) {
        //viewType分两种。1. presentation（双流视频）2. video（正常视频）
        if (streamType.equals("presentation")) {
            ......
        }
    }
    @Override
    public void onRemoteStream(String uuid, String streamURL,String streamType) {
        // viewType分两种。1. presentation（双流视频）2. video（正常视频）
        if (streamType.equals("presentation")) {
            ......        
        }
    }
    ```

#### 2. 共享图片

功能描述：在视频通话中将自己的图片文件分享给其他的参会人，以此提高沟通效率。

1. 开始共享图片：专属云和公有云的共享图片略有不同

    ```java
    private void sendSharePicture() {
        // 推荐使用sdk的工具裁剪一下图片，防止图片填充不了视频画面以及图片过大造成的oom的问题
        Bitmap bitmap = bitmapUtil.formatBitmap16_9(imagePathList.get(pictureIndex), 1920, 1080);
        // 专属云是以bitmap的形式发起图片共享
        vcrtc.sendPresentationBitmap(bitmap);
        // 公有云是以file文件的形式发起图片共享
        File file = new File("图片本地路径");
        zjrtc.sendPresentationImage(file);
    }
    ```

2. 关闭共享图片功能：

   ```java
   private void stopPresentation() {
           vcrtc.stopPresentation();
       }
   ```

3. 共享图片接收端的回调: 专属云和公有云的接收共享图片的回调略有不同

    ```java
    // 公有云图片共享回调，公有云是以图片的方式接收的共享图片资源，返回的是url的图片地址
    @Override
    public void onPresentationReload(String url) {
      		.......
    }
      
    // 专属云的图片共享回调，专属云是以视频的方式接受图片共享的资源，返回的是远端双流视频view或视频流，onAddView和onRemoteStream根据实际需求监听一个就好。
    @Override
    public void onAddView(String uuid, VCRTCView vcrtcView, String viewType) {
        //viewType分两种。1. presentation（双流视频）2. video（正常视频）
        if (streamType.equals("presentation")) {
            ......
        }
    }
    @Override
    public void onRemoteStream(String uuid, String streamURL,String streamType) {
        // viewType分两种。1. presentation（双流视频）2. video（正常视频）
        if (streamType.equals("presentation")) {
            ......        
        }
    }
    ```

#### 3. 共享PDF

共享PDF功能和共享图片功能类似，主要都是图片的共享，需要将PDF转化成图片资源文件再发起共享。

#### 4. 查看通话质量

​	功能描述：本功能反应远端音视频质量以及本地音视频质量，以下是信息对照表

|     信息     |          描述          |
| :----------: | :--------------------: |
|     uuid     |    参会人的唯一标示    |
|  mediaType   |  类型（audio/video）   |
|  direcition  |  发送或接收（out/in）  |
|  resolution  |         分辨率         |
|    codec     |        编码格式        |
|  frameRate   |          帧率          |
|   bitrate    |          码率          |
|   packets    | 发送或接收数据包的数量 |
| packagesLost |        丢包数量        |
| fractionLost |         丢包率         |
|    jitter    |          抖动          |

1. 调用方法：vcrtc.getMediaStatistics()，返回为一个List

2. 事例代码：

    ```java
    /**
    * 实体类，所有的信息都在这个类里，通过对类的解析，显示用户想要看到的信息
    **/
    public class MediaStats {
        private String uuid;
        private String mediaType;
        private String direction;
        private String resolution;
        private String codec;
        private int frameRate;
        private int bitrate;
        private int packets;
        private int packetsLost;
        private double fractionLost;
        private int jitter;
    }
    ```


    List<MediaStats> stats = vcrtc.getMediaStatistics();
    
    for (MediaStats mediaStats : stats) {
        if(mediaStats.getMediaType().equals("audio")
            &&mediaStats.getDirection().equals("out")) {
                ......
            }
        }
        if (mediaStats.getMediaType().equals("audio") 
            && mediaStats.getDirection().equals("in")) {
                ......
            }
        }
        if (mediaStats.getMediaType().equals("video") 
            && mediaStats.getDirection().equals("out")) {
            ......
        }
        if (mediaStats.getMediaType().equals("video") 
            && mediaStats.getDirection().equals("in")) {
            .....
        }
    }
    ```

#### 5. 视频通话质量对照表（具体设置方法见SDK开发文档-常用API列表）

| 字段名称                        | 推荐值   | 描述                                                       |  推荐范围  |
| ------------------------------ | ------- | --------------------------------------------------------- | --------- |
| bandwidthUp                    | 800     | 上行带宽（大流）                                             | 800~2048 |
| bandwidthDown                  | 800     | 下行带宽（全编全解模式下起作用）                                | 800~2048 |
| bandwidthSmall                 | 120     | 上行带宽（小流）                                             | 80~120  |
| bandwidthPresentation          | 1200    | 双流辅流上行带宽                                                | 800~1200 |
| video(Width/Height)Capture     | 1280/720| 摄像头采集分辨率                                             | 1280/720~1920/1080 |
| video(Width/Height)Up          | 640/360 | 视频上行分辨率                                               | 640/360~1920/1080 |
| video(Width/Height)Down        | 640/360 | 视频下行分辨率                                               | 640/360~1920/1080 |
| video(Width/Height)Small       | 320/180 | 视屏上行小流分辨率                                            | 320/180 |
| videoPresentation(Width/Height)Capture|1280/720| 双流辅流的采集分辨率                                    | 1280/720~1920/1080 |
| videoPresentation(Width/Height)Up|1280/720| 双流辅流上行分辨率                                           | 1280/720~1920/1080 |
| fpsCapture                     | 20      | 摄像头采集帧率                                               | 20~30  |
| fpsUp                          | 20      | 视频上行帧率                                                 | 20~30  |
| fpsDown                        | 20      | 视频下行帧率                                                 | 20~30  |
| fpsSmall                       | 15      | 小视频帧率                                                   | 15~20  |
| fpsMax                         | 20      | 视频上行最大帧率                                              | 20~30  |
| fpsPresentationCapture         | 10      | 双流辅流视频采集帧率                                           | 5～15 |
| fpsPresentationUp              | 10      | 双流辅流视频上行帧率                                           | 5～15  |
| fpsPresentationMax             | 10      | 发送双流辅流视频最大上行帧率                                    | 15～20 |
| simulcast                      | true    | 就收多流（转发模式）                                           |          |
| multistream                    | true    | 发送多流（一大一小两个上行视频流）                                |          |
| enableH264HardwareEncoder      | true    | 使用H264硬编码                                                |          |
| disableH264hHardwareDecoder    | true    | 使用H264软解码


#### 6. 打开/关闭麦克风

```java
public void muteAudio(View view) {
    vcrtc.setAudioEnable(isMuteAudio);
    isMuteAudio = !isMuteAudio;
}
```

#### 7. 打开/关闭摄像头

```java
public void muteVideo(View view) {
    vcrtc.setVideoEnable(isMuteVideo, true);
    isMuteVideo = !isMuteVideo;
}
```

#### 8. 切换摄像头

```java
public void switchCamera(View view) {
    vcrtc.switchCamera();
}
```

#### 9. 被呼功能

##### 1. 简介

移动端被呼功能包括`点对点被呼`，和`会议室邀请被呼`两种场景。 被呼功能的实现，首先需要在APP上通过接口进行账号登录，登录成功后该账号被呼叫时，SDK会通过广播将被呼的消息推送给客户端，客户端区分是点对点模式还是会议模式，分开进行处理。

##### 2. 登录

主要为VCRegistrationUtil这个类进行操作，调用login方法

```java
VCRegistrationUtil.login(context, “账号”, “密码”);
```

传入上下文信息，用户名，密码，进行登录操作，并注册一个动态广播，监听登录的结果

```java
public class LoginReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String message = intent.getStringExtra(VCSevice.MSG);
        switch (message) {
            case VCSevice.MSG_LOGIN_SUCCESS:
                Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                finish();
                break;
            case VCSevice.MSG_LOGIN_FAILED:
                Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                break;
            default:
        }
    }
}
```
##### 3.退出登录

```java
VCRegistrationUtil.logout(context);
```

##### 4. 注册静态广播接收器监听全局消息

```java
public class MyPushReceiver extends BroadcastReceiver {

    private static final String TAG = "MyPushReceiver";

    public static boolean isShowIncoming = false;

    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        String msg = intent.getStringExtra(VCSevice.MSG);
        switch (msg) {
            case VCSevice.MSG_USER_INFO:
                String userJson = intent.getStringExtra(VCSevice.DATA_BROADCAST);
                Log.i(TAG, "用户信息" + userJson);
                break;
            case VCSevice.MSG_SESSION_ID:
                String sessionID = intent.getStringExtra(VCSevice.DATA_BROADCAST);
                Log.i(TAG, "sessionID:" + sessionID);
                break;
            case VCSevice.MSG_ONLINE_STATUS:
                boolean onlineStatus = intent.getBooleanExtra(VCSevice.DATA_BROADCAST, false);
                Log.i(TAG, "在线状态:" + onlineStatus);
                break;
            case VCSevice.MSG_LOGOUT:
                Log.i(TAG, "账号在别的端登录");
                break;
            case VCSevice.MSG_INCOMING:
                Log.i(TAG, "收到消息");
                IncomingCall incomingCall = (IncomingCall) intent.getSerializableExtra(VCSevice.DATA_BROADCAST);
                if (isInvalidCall(incomingCall)){
                    Log.e(TAG, " isInvalidCall timeout 30 second ");
                    return;
                }
                if (isInConference() || isShowIncoming){ //已经在通话中，直接挂掉；
                    VCRegistrationUtil.hangup(context);
                } else {//没有在通话中，显示来电界面
                    
                }
                break;
            case VCSevice.MSG_INCOMING_CANCELLED:
                Log.i(TAG, "呼叫端撤销了呼叫");
                finishIncomingView(context);
                break;
        }
    }
}
```
