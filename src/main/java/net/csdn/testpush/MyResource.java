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
        String certificatePath = "/Users/administrator/Desktop/aps_push.p12";
        
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
