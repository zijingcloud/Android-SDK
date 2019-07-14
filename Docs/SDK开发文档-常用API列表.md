## SDK开发文档-常用API列表

### API文档

- [VCRTCPreferences方法列表](#vcrtcpreferences方法列表)   
- [Call类方法列表](#call类方法列表)            
- [VCRTC类方法列表](#vcrtc类方法列表)            
- [VCRTCListener回调方法列表](#vcrtclistener回调方法列表)
- [VCRegistrationUtil方法列表](#vcregistrationutil方法列表)

#### VCRTCPreferences方法列表

| 方法                                                         | 描述                                                        |
| :----------------------------------------------------------- | :---------------------------------------------------------- |
| <a href="#setServerAddress">setServerAddress</a>             | 设置服务器地址                                              |
| <a href="#setBandwidth">setBandwidth</a>                     | 设置呼叫带宽                                                |
| <a href="#setBandwidthSmall">setBandwidthSmall</a>           | 设置小流视频的带宽                                          |
| <a href="#setBandwidthPresentation">setBandwidthPresentation</a>| 设置双流辅流的带宽                                      |
| <a href="#setCaptureVideoSize">setCaptureVideoSize</a>       | 设置摄像头采集分辨率                                   |
| <a href="#setVideoSize">setVideoSize</a>                     | 设置发送/接收视频分辨率                                     |
| <a href="#setSmallVideoSize">setSmallVideoSize</a>           | 设置发送小视频的分辨率。（设置发送多流后生效）              |
| <a href="#setCapturePresentationVideoSize">setCapturePresentationVideoSize</a> | 设置发送双流时辅流（屏幕共享、图片h264）视频的采集分辨率 |
| <a href="#setPresentationVideoSize">setPresentationVideoSize</a> | 设置发送双流时辅流（屏幕共享、图片h264）视频的上行分辨率               |
| <a href="#setCaptureVideoFps">setCaptureVideoFps</a>         | 设置摄像头采集帧率                                   |
| <a href="#setVideoFps">setVideoFps</a>                       | 设置发送/接收视频的帧率                                   |
| <a href="#setSmallVideFps">setSmallVideFps</a>               | 设置发送小视频的帧率                                        |
| <a href="#setMaxVideoFps">setMaxVideoFps</a>                 | 设置视频最大帧率                                          |
| <a href="#setCapturePresentationVideoFps">setCapturePresentationVideoFps</a> | 设置发送双流辅流视频采集帧率。              |
| <a href="#setPresentationVideoFps">setPresentationVideoFps</a>           | 设置屏幕共享视频上行帧率。                                  |
| <a href="#setPresentationMaxVideoFps">setPresentationMaxVideoFps</a> | 设置发送双流辅流视频最大上行帧率。                          |
| <a href="#setSimulcast">setSimulcast</a>                     | 设置是否接收多流（转发模式）                                            |
| <a href="#setMultistream">setMultistream</a>                 | 设置是否发送多流                                            |
| <a href="#setEnableH264HardwareEncoder">setEnableH264HardwareEncoder</a> | 设置是否使用H264硬编                                        |
| <a href="#setDisableH264hHardwareDecoder">setDisableH264hHardwareDecoder</a> | 设置是否禁用H264硬解。                                      |
| <a href="#setSpeakerphone">setSpeakerphone</a>               | 设置扬声器                                                  |
| <a href="#setImageFilePath">setImageFilePath</a>             | 设置关闭摄像头时发送图片的路径                              |
| <a href="#setPrintLogs">setPrintLogs</a>                     | 设置是否打印日志。                                          |



#### Call类方法列表

| 方法                                   | 描述                                     |
| :------------------------------------- | :--------------------------------------|
| <a href="#setChanel">setChanel</a>     | 设置会议室号码或者联系人短号                |
| <a href="#setPassword">setPassword</a> | 设置会议室密码                           |
| <a href="#setNickname">setNickname</a> | 设置显示名称                             |
| <a href="#setAccount">setAccount</a>   | 设置自己的账号                           |
| <a href="#setCheckDup">setCheckDup</a> | 用于检查重复参会者                        |
| <a href="#setClayout">setClayout</a>   | 转发模式下，控制接收大小视频流的数量         |
| <a href="#setHideMe">setHideMe</a>     | 设置隐身入会                             |
| <a href="#setHost">setHost</a>         | 设置是否是主持人                         |
| <a href="#setP2P">setP2P</a>           | 设置是否是点对点                         |
| <a href="#setCallOut">setCallOut</a>   | 点对点呼叫时设置是否是外呼               |
| <a href="#setCallName">setCallName</a> | 点对点外呼，设置呼叫人的名称             |
| <a href="#setMsgJson">setMsgJson</a>   | 点对点被呼时。设置推送的消息的json字符串  |



#### VCRTC类方法列表

| 方法                                                         | 描述                                                          |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| <a href="#setVCRTCListener">setVCRTCListener</a>             | 设置会中监听                                                   |
| <a href="#setApiServer">setApiServer</a>                     | 设置api服务器（仅对当次入会生效，不设置则使用VCRTCPreferences保存的服务器地址）|
| <a href="#setLivingRecorderServer">setLivingRecorderServer</a> | 设置录制直播服务器，优先级高于偏好设置，如果不设置则为偏好设置值。    |
| <a href="#setAccount">setAccount</a>                         | 设置自己的账号                                                 |
| <a href="#setOemID">setOemID</a>                             | 设置OemID                                                     |
| <a href="#setCheckdup">setCheckdup</a>                       | 检查重复参会者                                                 |
| <a href="#setHideMea">setHideMe</a>                          | 设置隐身入会                                                  |
| <a href="#setCallType">setCallType</a>                       | 设置呼叫类型                                                  |
| <a href="#setClayout">setClayout</a>                         | 转发模式下，控制接收大小视频流的数量                              |
| <a href="#setTime">setTime</a>                               | 被呼时设置接收到的消息中的呼叫时间。                              |
| <a href="#setBsskey">setBsskey</a>                           | 点对点被呼时设置接收到的消息中的bsskey                           |
| <a href="#setOneTimeToken">setOneTimeToken</a>               | 被呼时设置接收到的消息中的token                                 |
| <a href="#connect">connect</a>                               | 建立连接                                                     |
| <a href="#disconnect">disconnect</a>                         | 断开链接                                                     |
| <a href="#getParticipants">getParticipants</a>               | 获取参会人列表                                                |
| <a href="#isVMR">isVMR</a>                                   | 是否是讲堂                                                    |
| <a href="#canRecord">canRecord</a>                           | 是否允许录制                                                  |
| <a href="#canLive">canLive</a>                               | 是否允许直播                                                  |
| <a href="#reconnectNewSessionCall">reconnectNewSessionCall</a>| 重新入会                                                     |
| <a href="#reconnectOnlyMediaCall">reconnectOnlyMediaCall</a> | 重新建立媒体连接（多用于断网重连）                                |
| <a href="#updateClayout">updateClayout</a>                   | 更新clayout                                                  |
| <a href="#getCameraDevices">getCameraDevices</a>             | 获取摄像头设备列表                                             |
| <a href="#switchCamera">switchCamera</a>                     | 切换摄像头                                                    |
| <a href="#updateVideoImage">updateVideoImage</a>             | 更新关闭摄像头时发送的图片                                       |
| <a href="#setVideoEnable">setVideoEnable</a>                 | 关闭/打开摄像头                                                |
| <a href="#setAudioEnable">setAudioEnable</a>                 | 关闭/打开麦克风                                                |
| <a href="#setAudioModelEnable">setAudioModelEnable</a>        | 关闭/打开语音模式                                             |
| <a href="#sendPresentationImage">sendPresentationImage</a>    | 发送双流图片（上传文件方式）                                     |
| <a href="#sendPresentationBitmap">sendPresentationBitmap</a>  | 发送双流图片（视频流方式）                                        |
| <a href="#sendPresentationScreen">sendPresentationScreen</a>  | 屏幕共享                                                      |
| <a href="#sendPresentationVideo">sendPresentationVideo</a>    | 发送视频文件（仅支持Y4M(YUV4MPEG2)格式视频文件）                   |
| <a href="#sendPresentationRawH264">sendPresentationRawH264</a>| 发送H264视频流                                                 |
| <a href="#putRawH264Data">putRawH264Data</a>                  | 添加H264数据                                                  |
| <a href="#stopPresentation">stopPresentation</a>              | 停止发双流                                                     |
| <a href="#getMediaStatistics">getMediaStatistics</a>          | 获取通讯媒体流参数                                             |



#### VCRTCListener回调方法列表

| 方法                                                         | 描述                                   |
| ------------------------------------------------------------ | -------------------------------------- |
| <a href="#onConnected">onConnected</a>                       | 入会连接成功回调                       |
| <a href="#onCallConnected">onCallConnected</a>               | 视频连接成功回调                       |
| <a href="#onLocalVideo">onLocalVideo</a>                     | 本地视频回调                           |
| <a href="#onRemoteVideo">onRemoteVideo</a>                   | 全编全解远端视频回调                   |
| <a href="#onAddView">onAddView</a>                           | 转发远端视频回调                       |
| <a href="#onRemoveView">onRemoveView</a>                     | 转发远端视频退出                       |
| <a href="#onLocalStream">onLocalStream</a>                   | 本地视频流回调                         |
| <a href="#onRemoteStream">onRemoteStream</a>                 | 远端视频流回调                         |
| <a href="#onAddParticipant">onAddParticipant</a>             | 新增与会者                             |
| <a href="#onRemoveParticipant">onRemoveParticipant</a>       | 与会者退出                             |
| <a href="#onUpdateParticipant">onUpdateParticipant</a>       | 与会者更新                             |
| <a href="#onStageVoice">onStageVoice</a>                     | 与会者声音列表回调                     |
| <a href="#onChatMessage">onChatMessage</a>                   | 接收到消息                             |
| <a href="#onDisconnect">onDisconnect</a>                     | 被服务器断开                           |
| <a href="#onCallDisconnect">onCallDisconnect</a>             | call（媒体呼叫、双流、屏幕共享）被断开    |
| <a href="#onPresentation">onPresentation</a>                 | 双流状态回调                           |
| <a href="#onPresentationReload">onPresentationReload</a>     | 双流图片地址回调                       |
| <a href="#onScreenShareState">onScreenShareState</a>         | 屏幕共享状态回调                       |
| <a href="#onLayoutUpdate">onLayoutUpdate</a>                 | 布局状态回调                           |
| <a href="#onLayoutUpdateParticipants">onLayoutUpdateParticipants</a> | 布局更新与会者顺序列表回调       |
| <a href="#onRecordState">onRecordState</a>                   | 录制状态回调                           |
| <a href="#onLiveState">onLiveState</a>                       | 直播状态回调                           |
| <a href="#onRoleUpdate">onRoleUpdate</a>                     | 自身身份改变回调                       |
| <a href="#onConferenceUpdate">onConferenceUpdate</a>         | 会议室状态更新                         |
| <a href="#onError">onError</a>                               | 错误回调                               |

#### VCRegistrationUtil方法列表

| 方法                                | 描述                      |
| ---------------------------------- | ------------------------- |
| <a href="#login">login</a>         | 登录                       |
| <a href="#logout">logout</a>       | 登出                       |
| <a href="#hangup">hangup</a>       | 拒接                       |

<h5>VCRTCPreferences详细信息</h5>
<h6 name="setServerAddress">1. setServerAddress(String serverAddress, CallBack callBack) 必须</h6>

​	设置服务器地址，必须设置项，没有设置的话会导致呼叫失败，callBack回调返回是否设置成功。

<h6 name="setBandwidth">2. setBandwidth(int bandwidth)</h6>

​	设置呼叫带宽，上行/下行一致

<h6 >3. setBandwidth(int upBw, int downBw)</h6>

​	分别设置上行/下行带宽（其中下行带宽仅全编全解模式下生效）

<h6 name="setBandwidthSmall">4. setBandwidthSmall(int bandwidth)</h6>

​	设置小流视频的带宽（设置发送多流后生效）

<h6 name="setBandwidthPresentation">5. setBandwidthPresentation(int bandwidth)</h6>

​	设置双流辅流的带宽

<h6 name="setCaptureVideoSize">6. setCaptureVideoSize(int videoWidth, int videoHeight)</h6>

​	设置摄像头采集分辨率

<h6 name="setVideoSize">7. setVideoSize(int videoWidth, int videoHeight)</h6>

​	设置发送/接收视频分辨率，发送和接收一致

<h6 id="setVideoSize">8. setVideoSize(int upVideoWidth, int upVideoHeight, int downVideoWidth, int downVideoHeight)</h6>

​	分别设置发送/接收视频分辨率（其中接收视频分辨率仅在全编全解模式下生效）

<h6 name="setSmallVideoSize">9. setSmallVideoSize(int videoWidth, int videoHeight)</h6>

​	设置发送小视频的分辨率。（设置发送多流后生效）

<h6 name="setCapturePresentationVideoSize">10. setCapturePresentationVideoSize(int videoWidth, int videoHeight)</h6>

​	设置发送双流时辅流（屏幕共享、图片h264）视频的采集分辨率

<h6 name="#setPresentationVideoSize">11. setPresentationVideoSize(int fps)</h6>

​	设置发送双流时辅流（屏幕共享、图片h264）视频上行分辨率

<h6 name="#setCaptureVideoFps">12. setCaptureVideoFps(int fps)</h6>

​	设置摄像头采集帧率。

<h6 name="setVideoFps">13. setVideoFps(int fps)</h6>

​	设置发送/接收视频的帧率。

<h6 name="">14. setVideoFps(int upFps, int downFps)</h6>

​	分别设置发送/接收视频的帧率。（其中接收视频帧率在全编全解模式下生效）

<h6 name="setSmallVideFps">15. setSmallVideFps(int fps)</h6>

​	设置发送小视频的帧率。（设置发送多流后生效）

<h6 name="setMaxVideoFps">16. setMaxVideoFps(int fps)</h6>

​	设置视频最大帧率。

<h6 name="setCapturePresentationVideoFps">17. setCapturePresentationVideoFps(int fps)</h6>

​	设置发送双流辅流视频采集帧率。

<h6 name="setPresentationVideoFps">18. setPresentationVideoFps(int fps)</h6>

​	设置发送双流辅流视频上行帧率。

<h6 name="setPresentationMaxVideoFps">19. setPresentationMaxVideoFps(int fps)</h6>

​	设置发送双流辅流视频最大上行帧率。

<h6 name="setSimulcast">20. setSimulcast(boolean simulcast)</h6>

​	设置是否使用转发（即接收多流）true为转发，false为全编全解。

<h6 name="setMultistream">21. setMultistream(boolean multistream)</h6>

​	设置是否发送多流。true发送一大一小两个视频流，false只发送一个大视频流。

<h6 name="setEnableH264HardwareEncoder">22. setEnableH264HardwareEncoder(boolean enableH264HardwareEncoder)</h6>

​	设置是否使用H264硬编

<h6 name="setDisableH264hHardwareDecoder">23. setDisableH264hHardwareDecoder(boolean disableH264hHardwareDecoder)</h6>

​	设置是否禁用H264硬解。

<h6 name="setSpeakerphone">24. setSpeakerphone(String speakerphone)</h6>

​	设置扬声器，true打开、false关闭、autu自动。

<h6 name="setImageFilePath">25. setImageFilePath(String imageFilePath)</h6>

​	设置关闭摄像头时发送图片的路径

<h6 name="setPrintLogs">26. setPrintLogs(boolean printLogs)</h6>

​	设置是否打印日志


<h5>Calll类方法详细</h5> 主要用于跳转到的视频通话Activity传递呼叫信息。
<h6 name="setChanel">1. setChanel(String chanel)</h6>

​	设置会议室号码或者联系人短号

<h6 name="setPassword">2. setPassword(String password)</h6>

​	设置会议室密码

<h6 name="setNickname">3. setNickname(String nickname)</h6>

​	设置显示名称

<h6 name="setAccount">4. setAccount(String account)</h6>

​	设置自己的账号（如果登录了就得设置）

<h6 name="setCheckDup">5. setCheckDup(String checkDup)</h6>

​	用于检查重复参会者。 入会时会检查同一会议室中是否已存在同名且checkDup值一样的参会者，如果存在则入会，并将同名参会者踢出会议。checkDup是一个30位以上长度的字符串，一般用MD5 Hash生成（32位）。

<h6 name="setClayout">6. setClayout(String clayout)</h6>

​	转发模式下，控制接收大小视频流的数量。如"1:4"表示接收一个大流和四个小流。

<h6 name="setHideMe">7. setHideMe(boolean hideMe)</h6>

​	设置隐身入会

<h6 name="setHost">8. setHost(boolean host)</h6>

​	设置是否是主持人

<h6 name="setP2P">9. setP2P(boolean P2P)</h6>

​	设置是否是点对点

<h6 name="setCallOut">10. setCallOut(boolean callOut)</h6>

​	点对点呼叫时设置是否是外呼

<h6 name="setCallName">11. setCallName(String callName)</h6>

​	点对点外呼，设置呼叫人的名称

<h6 name="setMsgJson">12. setMsgJson(String msgJson)</h6>

​	点对点被呼时。设置推送的消息的json字符串


<h5>VCRTC类方法详细</h5>
<h6 name="setVCRTCListener">1. setVCRTCListener(VCRTCListener listener)</h6>

​	设置会中监听，回调会议室、与会者、视频view等信息

```java
VCRTCListener listener = new VCRTCListenerImpl() {
	//Override部分方法进行监听
}

vcrtc.setVCRTCListener(listener);
```

<h6 name="setApiServer">2. setApiServer(String apiServer)</h6>

​	设置api服务器，优先级高于偏好设置，如果不设置则为偏好设置值。

<h6 name="setLivingRecorderServer">3. setLivingRecorderServer(String livingRecorderServer)</h6>

​	设置录制直播服务器，优先级高于偏好设置，如果不设置则为偏好设置值。

<h6 name="setAccount">4. setAccount(String account)</h6>

​	设置登录的账号。一般用户登录平台后必须设置。

<h6 name="setOemID">5. setOemID(String oemID)</h6>

​	设置OemID。如果单纯用sdk开发，这个可以不设置。

<h6 name="setCheckdup">6. setCheckdup(String checkdup)</h6>

​	用于检查重复参会者。 入会时会检查同一会议室中是否已存在同名且checkDup值一样的参会者，如果存在则入会，并将同名参会者踢出会议。checkDup是一个30位以上长度的字符串，一般用MD5 Hash生成（32位）。

<h6 name="setHideMe">7. setHideMe(boolean hideMe)</h6>

​	设置隐身入会,一般情况下，如果需要主持会议功能才会使用到，设置为true的时候，进入会议其他人看不到，自身不接收音视频信息，可以使用会控接口，可以用在会议管理的功能上。

```java
// 主持会议
private void makeCall() {
        zjrtc.setVCRTCListener(listener);
        zjrtc.setHideMe(true);
        zjrtc.setCallType(VCRTC.CallType.none);
        zjrtc.connect(“会议室号”, “主持人密码”, “名称”, new CallBack() {
            @Override
            public void success(String response) {

            }

            @Override
            public void failure(String reason) {

            }
        });
    }
```

<h6 name="setCallType">8. setCallType(CallType callType)</h6>

​	设置呼叫类型，`CallType`为`VCRTC.CallType`枚举类型。

```Java
public enum CallType {
        none,                   //不建立音视频连接，用于管理会议场景
        video,                  //建立音视频连接
        audio,                  //只建立音频连接
        recvOnly,               //只接收音视频
        recvOnlyVideo,          //只接收视频
        recvAndSendVideo,       //接收音视频并发送视频
        recvAndSendAudioBitmap, //接收音视频并发送音频和图片h264视频
        recvAndSendBitmap,      //接收音视频并发送图片h264视频
        videoFile,              //双流时发送视频文件
        picture,                //双流时发送图片（上传图片文件）
        bitmap,                 //双流时发送图片（转h264视频流）
        screen,                 //双流时共享
        rawH264                 //双流时发送H264视频流
    }
```

<h6 name="setClayout">9. setClayout(String clayout)</h6>

​	转发模式下，控制接收大小视频流的数量。如"1:4"表示接收一个大流和四个小流。发送双流时一般会设置成"0:1"，比如分享图片，屏幕共享功能等，一旦结束双流发送，需要重新设置一个"1:4"，恢复到原始状态。

```java
    /**
     * 开启屏幕共享
     */
    private void startScreenShare() {
        zjrtc.updateClayout("0:1");
        ivShare.setSelected(true);
        //切到桌面
        Intent home = new Intent(Intent.ACTION_MAIN);
        home.addCategory(Intent.CATEGORY_HOME);
        startActivity(home);
    }

    /**
     * 停止双流
     */
    private void stopPresentation() {
        zjrtc.stopPresentation();
        zjrtc.updateClayout("1:4");
      	......
    }
```

<h6 name="setTime">10. setTime(String time)</h6>

​	被呼时设置接收到的消息中的呼叫时间。如果time的时间和当前接收到被呼消息的时间超过30s，证明这个呼叫失效了，被呼时，这个time要回传给服务器。

<h6 name="setBsskey">11. setBsskey(String bsskey)</h6>

​	被呼时设置接收到的消息中的bsskey，用于入会验证，由于被呼叫不知道会议室密码等入会信息，所以，服务器推送过来的msgJson中存在bsskey、oneTimeToken用于入会验证。

<h6 name="setOneTimeToken">12. setOneTimeToken(String oneTimeToken)</h6>

​	被呼时设置接收到的消息中的token。同上

<h6 name="connect">13. connect(String chanel, String password, String nickname, CallBack callBack)</h6>

​	建立连接,示例代码如下：

```java
vcrtc.setVCRTCListener(listener);
    vcrtc.setClayout("1:4");
    vcrtc.connect("1234", "123456", "王富贵", new CallBack() {

        @Override
        public void success(String message) {

        }

        @Override
        public void failure(String reason) {

        }
        
    });
```

<h6 name="disconnect">14. disconnect()</h6>

​	断开链接

<h6 name="getParticipants">15. getParticipants()</h6>

​	获取当前会议中的参会人列表

<h6 name="isVMR">16. isVMR()</h6>

​	当前会议是否是讲堂

<h6 name="canRecord">17. canRecord()</h6>

​	当前会议室是否允许录制

<h6 name="canLive">18. canLive()</h6>

​	当前会议室是否允许直播

<h6 name="reconnectNewSessionCall">19. reconnectNewSessionCall()</h6>

​	重新入会

<h6 name="reconnectOnlyMediaCall">20. reconnectOnlyMediaCall()</h6>

​	重新建立媒体呼叫（多用于断网重连）

<h6 name="updateClayout">21. updateClayout(String clayout)</h6>

​	已经加入会议后，在会中动态改变clayout，clayout的作用详见setClayout方法说明

<h6 name="getCameraDevices">22. getCameraDevices()</h6>

​	获取摄像头设备列表

<h6 name="switchCamera">23. switchCamera()</h6>

​	切换摄像头

<h6>24. switchCamera(String deviceName)</h6>

​	切换到指定摄像头设备

<h6 name="updateVideoImage">25. updateVideoImage(Bitmap bitmap)</h6>

​	更新关闭摄像头时发送的图片

<h6 name="setVideoEnable">26. setVideoEnable(boolean enable)</h6>

​	关闭/打开摄像头

<h6>27. setVideoEnable(boolean enable, boolean sendImage)</h6>

​	关闭/打开摄像头，第二个参数表示关闭摄像头时是否发送图片视频流

<h6 name="setAudioEnable">28. setAudioEnable(boolean enable)</h6>

​	关闭/打开麦克风

<h6 name="setAudioModelEnable">29. setAudioModelEnable(boolean enable)</h6>

​	关闭/打开语音模式

<h6 name="sendPresentationImage">30. sendPresentationImage(File file)</h6>

​	发送双流图片，上传文件方式

<h6>31. sendPresentationImage(Bitmap bitmap)</h6>

​	发送双流图片，上传bitmap方式

<h6 name="sendPresentationBitmap">32. sendPresentationBitmap(Bitmap bitmap)</h6>

​	发送双流图片，图片转视频流方式

<h6 name="sendPresentationScreen">33. sendPresentationScreen()</h6>

​	屏幕共享

<h6 name="sendPresentationVideo">34. sendPresentationVideo(String videoFilePath)</h6>

​	发送视频文件（仅支持Y4M(YUV4MPEG2)格式视频文件）

<h6 name="sendPresentationRawH264">35. sendPresentationRawH264()</h6>

​	开始发送H264视频流

<h6 name="putRawH264Data">36. putRawH264Data(byte[] data)</h6>

​	添加H264数据

<h6 name="stopPresentation">37. stopPresentation()</h6>

​	停止发双流，包括停止图片共享、屏幕共享等发送双流状态。

<h6 name="getMediaStatistics">38. getMediaStatistics()</h6>

​	获取会议中实时的媒体通讯参数数据


<h5>VCRTCListener回调方法详细</h5>
<h6 name="onConnected">1. onConnected()</h6>

​	加入会议成功回调

<h6 name="onCallConnected">2. onCallConnected()</h6>

​	视频呼叫成功回调

<h6 name="onLocalVideo">3. onLocalVideo(String uuid, VCRTCView view)</h6>

​	本地视频view回调

<h6 name="onRemoteVideo">4. onRemoteVideo(String uuid, VCRTCView view)</h6>

​	全编全解模式远端视频view回调

<h6 name="onAddView">5. onAddView(String uuid, VCRTCView vie, String viewType)</h6>

​	转发模式远端视频view回调

<h6 name="onRemoveView">6. onRemoveView(String uuid, VCRTCView view)</h6>

​	转发模式远端视频退出

<h6 name="onLocalStream">7. onLocalStream(String uuid, String streamURL, String streamType)</h6>

​	本地视频流回调

<h6 name="onRemoteStream">8. onRemoteStream(String uuid, String streamURL, String streamType)</h6>

​	远端视频流回调

<h6 name="onAddParticipant">9. onAddParticipant(Participant participant)</h6>

​	新增与会者

<h6 name="onRemoveParticipant">10. onRemoveParticipant(String uuid)</h6>

​   与会者退出

<h6 name="onUpdateParticipant">11. onUpdateParticipant(Participant participant)</h6>

​	与会者更新

<h6 name="onStageVoice">12. onStageVoice(List<Stage> stages)</h6>

​	与会者声音列表回调 Stage的vad字段有两种取值：0为没有讲话 100为正在讲话

<h6 name="onChatMessage">13. onChatMessage(String uuid, String message)</h6>

​	接收到消息

<h6 name="onDisconnect">14. onDisconnect(String reason)</h6>

​	被服务器断开

<h6 name="onCallDisconnect">15. onCallDisconnect(String reason)</h6>

​	call（媒体呼叫、双流、屏幕共享）被断开

<h6 name="onPresentation">16. onPresentation(boolean isActive, String uuid)</h6>

​	双流状态回调

<h6 name="onPresentationReload">17. onPresentationReload(String url)</h6>

​   双流图片地址回调

<h6 name="onScreenShareState">18. onScreenShareState(boolean isActive)</h6>

​	屏幕共享是否成功状态回调

<h6 name="onLayoutUpdate">19. onLayoutUpdate(String layout, String hostLayout, String guestLayout)</h6>

​	布局更新回调，多用于全编全解模式

<h6 name="onLayoutUpdateParticipants">20. onLayoutUpdateParticipants(List<String> participants)</h6>

​	布局更新与会者顺序列表回调

<h6 name="onRecordState">21. onRecordState(boolean isActive)</h6>

​	录制状态回调 

<h6 name="onLiveState">22. onLiveState(boolean isActive)</h6>

​	直播状态回调

<h6 name="onRoleUpdate">23. onRoleUpdate(String role)</h6>

​   自己角色更新

<h6 name="onConferenceUpdate">24. onConferenceUpdate(ConferenceStatus status)</h6>

   会议室状态更新

<h6 name="onError">25. onError(ErrorCode error,String description)</h6>

​	错误回调

<h5>VCRegistrationUtil详细信息</h5>
<h6 name="login">1. login(Context context, String account, String password)</h6>

​	登录

<h6 name="logout">2. logout(ontext context)</h6>

​	登出

<h6 name="hangup">3. hangup(ontext context)</h6>

​	拒接来电