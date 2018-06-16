package org.daly.letters;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.AsyncTask;

import java.util.HashMap;

public class TileSound extends AsyncTask<Integer, Void, Void> {
  private SoundPool sp;
  private HashMap<Integer,Integer> spmap;
  private AudioManager am;
  private Context cxt;
  int sound;

  // called before doInBackground
  @Override
  protected void onPreExecute() {
    System.out.println("onPreExecute");
  }

  // do the long running work here
  @Override
  protected Void doInBackground(Integer... rawid) {
    int sound = rawid[0].intValue();
    if (am == null) { System.out.println("doInBackground am was null"); }
    if (cxt == null) { System.out.println("doInBackground cxt was null"); }
    try {
    am = (AudioManager)cxt.getSystemService(Context.AUDIO_SERVICE);
    float maxvol = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    float volume = am.getStreamVolume(AudioManager.STREAM_MUSIC) / maxvol;
    System.out.println("doInBackground sound="+sound);
    sp.play(spmap.get(sound),volume,volume,1,8,1f);
    } catch (Exception e) {
       e.printStackTrace();
    }
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
    System.out.println("cxt="+cxt);
    spmap = new HashMap();
    System.out.println("spmap="+spmap);
    am = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
    System.out.println("am="+am);
    sp = new SoundPool(1, AudioManager.STREAM_MUSIC,0);
    System.out.println("sp="+sp);
  }   

  public void initSounds(Context context) {
    cxt = context;
    sp = new SoundPool(1, AudioManager.STREAM_MUSIC,0);
    spmap = new HashMap();
    am = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
  }   

  public void addSound(int id, int rawid) {
    System.out.println("addsound "+rawid);
    spmap.put(id,sp.load(cxt,rawid,1));
  }

}
