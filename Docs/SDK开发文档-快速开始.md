## SDK开发文档-快速开始

#### 目录
 - [1. 复制 `vcrtc-v*.aar`文件到app的libs下](#1-复制-vcrtc-vaar文件到app的libs下)   
 - [2. 在项目的根目录的`build.gradle`加入以下代码](#2-在项目的根目录的buildgradle加入以下代码)        
 - [3. 修改app下的`build.gradle`文件](#3-修改app下的buildgradle文件)        
 - [4. 初始化](#4-初始化)        
 - [6. 初始化参数设置](#6-初始化参数设置)        
 - [7. 加入会议（发起呼叫）](#7-加入会议发起呼叫)        
 - [8. 接收回调信息](#8-接收回调信息)        
 - [9. 退出会议](#9-退出会议)

#### 前提条件：

* Android SDK API Level >= 16
* Android Studio 3.0以上版本
* APP要求Android 4.1或以上设备

#### 1. 复制 `vcrtc-v*.aar`文件到app的libs下

#### 2. 在项目的根目录的`build.gradle`加入以下代码

```gradle
allprojects {
   repositories {
      jcenter()
      maven { url 'https://jitpack.io' }
   }
}
```

#### 3. 修改app下的`build.gradle`文件

```java
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
    implementation 'org.java-websocket:Java-WebSocket:1.4.0'
    implementation 'com.google.code.gson:gson:2.8.5'
    implementation 'com.github.LuckSiege.PictureSelector:picture_library:v2.2.3'
    implementation 'com.github.barteksc:pdfium-android:1.7.0'
}

```

#### 4. 初始化

集成完成后需要自定义一个Application，在新建的Application中的onCreate()方法进行SDK初始化，并可以做一些其他操作，然后在manifest文件中指定Application，代码如下：

```java
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化SDK
        RTCManager.init(this);

        //以下可不设置，设置是为了出现问题方便服务端定位
        //设备类型
        RTCManager.DEVICE_TYPE = "Android";
        //应用id
        RTCManager.APPLICATION_ID = BuildConfig.APPLICATION_ID;  
        //应用版本号
        RTCManager.VERSION_NAME = BuildConfig.VERSION_NAME;
        //合作伙伴
        RTCManager.OEM = "oem";
    }
}
```

#### 5. 初始化参数设置

通话之前，优先进行偏好设置，包括服务器地址、视频参数及编码、是否打印日志等设置，服务器地址必须设置，其他选项如果不设置，将使用默认值，偏好设置一次即可一直生效，建议在app的设置页面做配置，具体可设置选项详见api列表。

```java
VCRTCPreferences preferences = new VCRTCPreferences(this);
preferences.setServerAddress("服务器地址", new CallBack() {
            @Override
            public void success(String message) {
                
            }

            @Override
            public void failure(String reason) {
                
            }
        });
preferences.setSimulcast(true);//设置为转发模式
```

#### 6. 加入会议（发起呼叫）

呼叫前首先要设置回调监听`VCRTCListener`

```java
private void makeCall(){
    vcrtc = new VCRTC(this);
    // 呼叫前必须建立回调链接
    vcrtc.setVCRTCListener(listener);
    // 发起呼叫
    vcrtc.connect("会议室号", "会议室密码", "显示名", new CallBack() {
      @Override
      public void success(String s) {

      }

      @Override
      public void failure(String s) {

      }
    });
}
```

#### 7. 接收回调信息

连接成功后，视频view或者视频流以及会议室相关状态信息会在回调中返回，开发者可在相关回调中处理业务逻辑以及对视频画面进行展示。**注：有些回调在子线程，做UI刷新操作记得切到主线程**。

```java
VCRTCListener listener = new VCRTCListener() {

   /**
    * 本地视频回调
    * @param uuid
    * @param view
    */
   @Override
   public void onLocalVideo(String uuid, VCRTCView view) {
       vcrtcView.setMirror(true);
       flLocal.addView(vcrtcView, FrameLayout.LayoutParams.MATCH_PARENT, 	FrameLayout.LayoutParams.MATCH_PARENT);
   }

   /**
    * 转发模式下远端视频回调
    * @param uuid
    * @param view
    * @param viewType
    */
   @Override
   public void onAddView(String uuid, VCRTCView view, String viewType) {
       llRemote.addView(vcrtcView, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
   }

   /**
    * 转发远端视频退出
    * @param s
    * @param vcrtcView
    */
   @Override
   public void onRemoveView(String uuid, VCRTCView vcrtcView) {
    	 llRemote.removeView(vcrtcView);
   }

   /**
    * 被服务器断开
    * @param reason
    */
   @Override
   public void onDisconnect(String reason) {
       flLocal.removeAllViews();
       flRemote.removeAllViews();
       disconnect();
   }
};
```

#### 8. 退出会议

```java
private void disconnect() {
    vcrtc.disconnect();
}
```
