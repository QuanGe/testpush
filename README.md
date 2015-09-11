＃testpush
##开发工具

下载[Eclipse](http://www.eclipse.org/downloads/download.php?file=/technology/epp/downloads/release/mars/R/eclipse-jee-mars-R-macosx-cocoa-x86_64.tar.gz) ，[maven](http://maven.apache.org/download.cgi)

##maven配置

下载了lastest version，文件名：apache-maven-3.2.1-bin.tar.gz

将解压在当前目录下。

将解压后的apache-maven-3.2.1文件夹移到/usr/local/maven目录（不存在则新建）下，并重命名为maven3.2.1
即：

/usr/local/maven/maven3.2.1

参考maven官网的安装指导
http://maven.apache.org/download.cgi#Installation，
并结合其他网友贡献的资料，做以下工作：

终端中执行 `vi ~/.bash_profile`
然后输入
```
M3_HOME=/usr/local/maven/maven3.2.1

PATH=$M3_HOME/bin:$PATH

```
按下`esc`键输入:wq

然后在终端输入

```
mvn -version
```
如果能输出类似

```
Apache Maven 3.2.3 (33f8c3e1027c3ddde99d3cdebad2656a31e8fdf4; 2014-08-12T04:58:10+08:00)
Maven home: /Users/zhangruquan/need/apache-maven-3.2.3
Java version: 1.6.0_65, vendor: Apple Inc.
Java home: /System/Library/Java/JavaVirtualMachines/1.6.0.jdk/Contents/Home
Default locale: zh_CN, platform encoding: EUC_CN
OS name: "mac os x", version: "10.10.5", arch: "x86_64", family: "mac"
```
就说明安装成功了

##jersey的RESTful服务器的搭建
Eclipse－>New->Project->Maven Project->Next ->Add Archetype->Archetype Group Id:org.glassfish.jersey.archetypes ,Archetype Artifact Id:jersey-quickstart-webapp ,Archetype Version:2.21 ->ok->选择刚才创建的Archetype－>next->Group Id:csdn ,Artifact Id:testPush,Version 0.0.1->Finish

在pom.xml文件中插入
```
<dependency>
<groupId>com.github.fernandospr</groupId>
<artifactId>javapns-jdk16</artifactId>
<version>2.3</version>
</dependency>
```
另外将MyResource.java 替换为以下内容
```
package net.csdn.testpush;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.json.JSONException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javapns.Push;
import javapns.communication.exceptions.CommunicationException;
import javapns.communication.exceptions.KeystoreException;
import javapns.devices.Device;
import javapns.devices.exceptions.InvalidDeviceTokenFormatException;
import javapns.devices.implementations.basic.BasicDevice;
import javapns.notification.AppleNotificationServerBasicImpl;
import javapns.notification.PushNotificationManager;
import javapns.notification.PushNotificationPayload;
import javapns.notification.PushedNotification;

/**
* Root resource (exposed at "myresource" path)
*/
@Path("myresource")
public class MyResource {

/**
* Method handling HTTP GET requests. The returned object will be sent
* to the client as "text/plain" media type.
*
* @return String that will be returned as a text/plain response.
*/
@GET
@Produces(MediaType.TEXT_PLAIN)
public String getIt() {
return "Got it!";
}
@GET @Path("/push")
@Produces({"application/json", "application/xml"})
public String getListOfPrinters(@QueryParam("message") String message,
@QueryParam("address") String address,
@QueryParam("badgeNum") String badgeNum) throws CommunicationException, KeystoreException, InvalidDeviceTokenFormatException, JSONException { 

String deviceToken = "381883f2f7192337bbee8abf8b8f7f12eee284b94fa389470049997670831ef1";
String alert = message;//push的内容
int badge = 100;//图标小红圈的数值
String sound = "default";//铃音

//List<String> tokens = new ArrayList<String>();
//tokens.add(address);
String[] tokens= address.split("\\.");
String certificatePath = "/Users/zhangruquan/Desktop/aps_push.p12";

String certificatePassword = "123456";//此处注意导出的证书密码不能为空因为空密码会报错
boolean sendCount = true;
String am = "推送成功";

List<Device> device = new ArrayList<Device>();
for (String token : tokens)
{
device.add(new BasicDevice(token));
}
PushNotificationPayload payLoad = new PushNotificationPayload();
payLoad.addAlert(alert); // 消息内容
payLoad.addBadge(Integer.parseInt(badgeNum)); // iphone应用图标上小红圈上的数值
payLoad.addSound(sound);//铃音
payLoad.addCustomDictionary("askid", "3721");

List<PushedNotification> ps = Push.payload(payLoad, certificatePath, certificatePassword, false, device);
//List<PushedNotification> ps = Push.alert(message, certificatePath, certificatePassword, false, device);
for (PushedNotification pn : ps)
{
am = am+pn.toString();
}

return am+address+"成功:"+message;
}
}

```

然后Run As ->Maven install

然后将testpush.war部署到服务器


##测试
测试时候postman 测试
http://localhost:8080/testpush/webapi/myresource/push?address=381883f2f7192337bbee8abf8b8f7f12eee284b94fa389470049997670831ef1&badgeNum=10&message=你好 


这里一定要注意有webapi