import java.util.List;
import java.util.ArrayList;
import com.t_systems.ctv.cdd.*;
import com.t_systems.ctv.cdd.core.*;
import com.t_systems.ctv.cdd.core.data.*;
import com.t_systems.ctv.cdd.core.actions.*;
import com.t_systems.ctv.cdd.core.values.State;
import com.t_systems.ctv.cdd.core.controller.DiscoveryController;

class MediaReceiverRemote {

  /* Howto:
  1. root your Android-device
  2. install Entertain RC on Android-device and connect the app with the receiver
  3. get apk-file /data/app/com.t_systems.ctv.android-1.apk from Android-device
  4. unpack the apk and extract the classes.dex with dex2jar from https://github.com/pxb1988/dex2jar
  4. read companionId and key from /data/data/com.t_systems.ctv.android/shared_prefs/ctv_prefs.xml from Android-device and put them here
  5. compile this file with "javac MediaReceiverRemote.java -cp classes-dex2jar.jar:."
  6. run it with "java -cp classes-dex2jar.jar:./ MediaReceiverRemote command poweron" and your receiver should start
  */

  /* TODO:
  - Pairing
  - Play stationId
  */

  final static String companionId = "xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx";
  final static String key = "xxxxxxxxxxxxxxxx";

  final static String ip = "192.168.xxx.xxx";
  final static int port = 53208;

  public MediaReceiverRemote(String type, String paramString) {
    System.out.println("Type: "+type+"\n");
    System.out.println("paramString: "+paramString+"\n");

    DiscoveryController dc = DiscoveryController.getInstance();
    List<STBDevice> list = null;
    boolean discover = false;
    try {
      STBDevice stb = new STBDevice();
      if(discover) {
        //Discover Devices for 5sec (MediaReceiver sends an UPNP-Beacon every 5sec)
        list = dc.discoverDevices(5000);

        //Pick the first Box
        stb = list.get(0);

        //Print the name of the first Box
        System.out.println(stb.getFriendlyName());
      }

      //if you don't want to wait you can hardcode your connection-settings
      else {
        stb.setAddress(ip);
        stb.setPort(port);
      }

      CompanionDeviceItem localCompanionDeviceItem = new CompanionDeviceItem();
      localCompanionDeviceItem.setCompanionID(companionId);
      localCompanionDeviceItem.setKey(key);

      final EntertainCompanionClient instance = EntertainCompanionClient.getInstance();
      //instance.enableDebug();

      CompanionDeviceConfiguration deviceConfiguration = new CompanionDeviceConfiguration(stb, localCompanionDeviceItem);

      CompanionDevice companionDevice = new CompanionDevice();
      companionDevice.setDeviceConfiguration(deviceConfiguration);
      instance.setupWithSTBDeviceCompanionDevice(stb, localCompanionDeviceItem);

      if(instance.isSuccessfullyPaired()) {
        System.out.println("paired!");

        if(type.equals("get")) {
          if(paramString.equals("channel")) {
            //CompanionActionState state = new CompanionActionState(State.DATASOURCE_SYSTEM_INFO, State.PATH_SYSTEM_AUDIO_MUTE);
            //CompanionActionState state = new CompanionActionState(State.DATASOURCE_TV, State.PATH_TV_DISPLAYED_CHANNEL); // = PATH_TV_CHANNEL
            //CompanionActionState state = new CompanionActionState(State.DATASOURCE_TV, State.PATH_TV_STATION_ID); //does not work
            //CompanionActionState state = new CompanionActionState(State.DATASOURCE_SYSTEM_INFO ,State.PATH_SYSTEM_SYSTEM); //PAL / NTSC

            CompanionActionState state = new CompanionActionState(State.DATASOURCE_TV, State.PATH_TV_CHANNEL);
            instance.getCurrentConnection().performAction(state);
          }

          if(paramString.equals("channels")) {
            //get the first 100 channels //only 30 per Page
            ArrayList<CompanionChannelItem> channelItems = this.getChannels(instance, 0,100,0);
          }

          if(paramString.equals("recordings")) {
            //get all recordings //only 30 per Page
            ArrayList<CompanionRecordingItem> recordings = this.getRecordings(instance, "");
          }
        }

        if(type.equals("playChannel")) {
          //TODO: Not working yet. Can't get the possible stationIds
          instance.tuneChannelBySAndType(paramString, "id");
        }

        if(type.equals("playRecording")) {
          //tune to a recording
          //Example: instance.tuneChannelBySAndType("0288a44d-e73f-47a8-b4a1-f9f3f6802755", "md");
          instance.tuneChannelBySAndType(paramString, "md");
        }

        if(type.equals("command")) {
          CompanionActionRemoteKey localCompanionActionRemoteKey = new CompanionActionRemoteKey();
          localCompanionActionRemoteKey.setKeyEvent(paramString);
          //companionConnectionWithDevice.performAction(localCompanionActionRemoteKey);
          instance.getCurrentConnection().performAction(localCompanionActionRemoteKey);
        }
      }
      else {
        System.out.println("not paired");
      }

    }
    catch (Exception e) {
      System.out.println(e);
    }
  }

  private ArrayList<CompanionChannelItem> getChannels(EntertainCompanionClient instance, final Integer startIndex, final Integer count, final Integer programs) throws CompanionException {
      ArrayList<CompanionChannelItem> channelItems;
      final ArrayList<CompanionChannelItem> list = channelItems = null;
      if (instance.getCurrentConnection() != null) {
          channelItems = list;
          if (instance.isConnected()) {
              final CompanionActionChannels companionActionChannels = new CompanionActionChannels();
              companionActionChannels.setStartIndex(startIndex);
              companionActionChannels.setPrograms(programs);
              companionActionChannels.setCount(count);
              final CompanionActionChannels companionActionChannels2 = (CompanionActionChannels)instance.getCurrentConnection().performAction(companionActionChannels);
              channelItems = list;
              if (companionActionChannels2.isSucceeded()) {
                  channelItems = companionActionChannels2.getChannelItems();
              }
          }
      }
      return channelItems;
  }

  private ArrayList<CompanionRecordingItem> getRecordings(EntertainCompanionClient instance, final String status) throws CompanionException {
      ArrayList<CompanionRecordingItem> recordings;
      final ArrayList<CompanionRecordingItem> list = recordings = null;
      if (instance.getCurrentConnection() != null) {
          recordings = list;
          if (instance.isConnected()) {
              final CompanionActionRecordings companionActionRecordings = new CompanionActionRecordings();
              companionActionRecordings.setStatus(status);
              final CompanionActionRecordings companionActionRecordings2 = (CompanionActionRecordings)instance.getCurrentConnection().performAction(companionActionRecordings);
              recordings = list;
              if (companionActionRecordings2.isSucceeded()) {
                  recordings = companionActionRecordings2.getRecordings();
              }
          }
      }
      return recordings;
  }

  public static void main(String[] args) {
    MediaReceiverRemote mrr = new MediaReceiverRemote(args[0], args[1]);
  }

}
