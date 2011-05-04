package org.daly.letters;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.AsyncTask;

import java.util.Arrays;
import java.util.HashMap;

public class TileSound extends AsyncTask<Integer, Void, Void> {
  private SoundPool sp;
  private HashMap<Integer,Integer> spmap;
  private AudioManager am;
  private Context cxt;
  private int sound;
  private boolean toggle = false; // default to quiet

  // called before doInBackground
  @Override
  protected void onPreExecute() {
    System.out.println("onPreExecute");
  }

  // do the long running work here
  @Override
  protected Void doInBackground(Integer... rawid) {
//    playSound(rawid[0].intValue);
    return null;
  }

  // called after doInBackground with the result
  @Override
  protected void onPostExecute(Void v) {
    System.out.println("onPostExecute");
  }

/* called by invoking publishProgress any time from doInBackground
  @Override
  protected void onProgressUpdate(Void values) {
    System.out.println("onProgressUpdate");
  }
*/

  public void setSound(Context context, int rawid) {
    cxt = context;
    spmap = new HashMap();
    am = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
    sp = new SoundPool(1, AudioManager.STREAM_MUSIC,0);
  }   

  public boolean toggleSound() {
    toggle = !toggle;
    return toggle;
  }

  public boolean soundIsOn() {
    return toggle;
  }

  public void playSound(int sound) {
    if (toggle == false) return;
    try {
      float maxvol = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
      System.out.println("doInBackground sound="+sound);
      sp.play(spmap.get(sound),maxvol,maxvol,1,0,1f);
      } catch (Exception e) {
         e.printStackTrace();
      }
  }

  public void initSounds(Context context) {
    if (cxt != null) return;
    cxt = context;
    sp = new SoundPool(1, AudioManager.STREAM_MUSIC,0);
    spmap = new HashMap();
    am = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
  }   

  public void addSound(int id, int rawid) {
    if (toggle == false) return;
    if (!spmap.containsKey(id)) {
      spmap.put(id,sp.load(cxt,rawid,1));
    }
  }

}
