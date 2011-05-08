package org.daly.letters;

import org.daly.letters.Tile;
import org.daly.letters.TileSound;

import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.lang.InterruptedException;
import java.lang.Runnable;
import java.lang.Thread;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Random;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AsyncPlayer;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.os.PowerManager;
import android.os.SystemClock;
import android.text.Layout;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

/*
                     ===LAYER 0===
    11  10   9   8   7   6   5   4   3   2   1   0
            19  18  17  16  15  14  13  12
        29  28  27  26  25  24  23  22  21  20
    43  42  41  40  39  38  37  36  35  34  33  32  
56                                                  31  30
    55  54  53  52  51  50  49  48  47  46  45  44
        66  65  64  63  62  61  60  59  58  57
            74  73  72  71  70  69  68  67
    86  85  84  83  82  81  80  79  78  77  76  75

                     ===LAYER 1===
                92  91  90  89  88  87
                98  97  96  95  94  93
               104 103 102 101 100  99
               110 109 108 107 106 105
               116 115 114 113 112 111
               122 121 120 119 118 117

                     ===LAYER 2===
                    126 125 124 123
                    130 129 128 127
                    134 133 132 131
                    138 137 136 135

                     ===LAYER 3===
                        140 139
                        142 141

                     ===LAYER 4===
                          143

*/


public class MahLetters extends Activity implements OnClickListener,
                                                    Handler.Callback {

  private static final short MENU_HINT = 0x2001;
  private static final short MENU_ABOUT = 0x2002;
  private static final short MENU_NEWGAME = 0x2003;
  private static final short MENU_COLOR = 0x2004;
  private static final short MENU_SOUND = 0x2005;
  private float tabletheight;
  private float tabletwidth;
  private RelativeLayout board;
  private Tile lastb = null;
  private int lasttile = 0;
  private Tile tile[] = new Tile[144];
  private Tile hide[] = new Tile[144];
  private int placement[] = new int[144];
  private String appname;

  private String theUserMessage = "";
  private String scoreMessage = "";
  private String countMessage = "";
  private String captionMessage = "";
  private static final int SCORE = 0;
  private static final int COUNT = 1;
  private static final int CAPTION = 2;

  private final int image[] = { 
      R.drawable.tilea80x80, R.drawable.tileb80x80,
      R.drawable.tilec80x80, R.drawable.tiled80x80,
      R.drawable.tilee80x80, R.drawable.tilef80x80,
      R.drawable.tileg80x80, R.drawable.tileh80x80,
      R.drawable.tilei80x80, R.drawable.tilej80x80,
      R.drawable.tilek80x80, R.drawable.tilel80x80,
      R.drawable.tilem80x80, R.drawable.tilen80x80,
      R.drawable.tileo80x80, R.drawable.tilep80x80,
      R.drawable.tileq80x80, R.drawable.tiler80x80,
      R.drawable.tiles80x80, R.drawable.tilet80x80,
      R.drawable.tileu80x80, R.drawable.tilev80x80,
      R.drawable.tilew80x80, R.drawable.tilex80x80,
      R.drawable.tiley80x80, R.drawable.tilez80x80,
      R.drawable.tile080x80, R.drawable.tile180x80,
      R.drawable.tile280x80, R.drawable.tile380x80,
      R.drawable.tile480x80, R.drawable.tile580x80,
      R.drawable.tile680x80, R.drawable.tile780x80,
      R.drawable.tile880x80, R.drawable.tile980x80
    };
  private TextView say = null;
  private TextView scoreboard = null;
  private TextView matchboard = null;
  private final int caption[] = { 
   R.string.tilea80x80,R.string.tilea80x80,
   R.string.tilea80x80,R.string.tilea80x80,
   R.string.tileb80x80,R.string.tileb80x80,
   R.string.tileb80x80,R.string.tileb80x80,
   R.string.tilec80x80,R.string.tilec80x80,
   R.string.tilec80x80,R.string.tilec80x80,
   R.string.tiled80x80,R.string.tiled80x80,
   R.string.tiled80x80,R.string.tiled80x80,
   R.string.tilee80x80,R.string.tilee80x80,
   R.string.tilee80x80,R.string.tilee80x80,
   R.string.tilef80x80,R.string.tilef80x80,
   R.string.tilef80x80,R.string.tilef80x80,
   R.string.tileg80x80,R.string.tileg80x80,
   R.string.tileg80x80,R.string.tileg80x80,
   R.string.tileh80x80,R.string.tileh80x80,
   R.string.tileh80x80,R.string.tileh80x80,  
   R.string.tilei80x80,R.string.tilei80x80,
   R.string.tilei80x80,R.string.tilei80x80,
   R.string.tilej80x80,R.string.tilej80x80,
   R.string.tilej80x80,R.string.tilej80x80,
   R.string.tilek80x80,R.string.tilek80x80,
   R.string.tilek80x80,R.string.tilek80x80,
   R.string.tilel80x80,R.string.tilel80x80,
   R.string.tilel80x80,R.string.tilel80x80,
   R.string.tilem80x80,R.string.tilem80x80,
   R.string.tilem80x80,R.string.tilem80x80,
   R.string.tilen80x80,R.string.tilen80x80,
   R.string.tilen80x80,R.string.tilen80x80,
   R.string.tileo80x80,R.string.tileo80x80,
   R.string.tileo80x80,R.string.tileo80x80,
   R.string.tilep80x80,R.string.tilep80x80,
   R.string.tilep80x80,R.string.tilep80x80,
   R.string.tileq80x80,R.string.tileq80x80,
   R.string.tileq80x80,R.string.tileq80x80,
   R.string.tiler80x80,R.string.tiler80x80,
   R.string.tiler80x80,R.string.tiler80x80,     
   R.string.tiles80x80,R.string.tiles80x80,
   R.string.tiles80x80,R.string.tiles80x80,
   R.string.tilet80x80,R.string.tilet80x80,
   R.string.tilet80x80,R.string.tilet80x80,
   R.string.tileu80x80,R.string.tileu80x80,
   R.string.tileu80x80,R.string.tileu80x80,
   R.string.tilev80x80,R.string.tilev80x80,
   R.string.tilev80x80,R.string.tilev80x80,
   R.string.tilew80x80,R.string.tilew80x80,
   R.string.tilew80x80,R.string.tilew80x80,
   R.string.tilex80x80,R.string.tilex80x80,
   R.string.tilex80x80,R.string.tilex80x80,
   R.string.tiley80x80,R.string.tiley80x80,
   R.string.tiley80x80,R.string.tiley80x80,
   R.string.tilez80x80,R.string.tilez80x80,
   R.string.tilez80x80,R.string.tilez80x80,
   R.string.tile080x80,R.string.tile080x80,
   R.string.tile080x80,R.string.tile080x80,
   R.string.tile180x80,R.string.tile180x80,
   R.string.tile180x80,R.string.tile180x80,
   R.string.tile280x80,R.string.tile280x80,
   R.string.tile280x80,R.string.tile280x80,
   R.string.tile380x80,R.string.tile380x80,
   R.string.tile380x80,R.string.tile380x80,
   R.string.tile480x80,R.string.tile480x80,
   R.string.tile480x80,R.string.tile480x80,
   R.string.tile580x80,R.string.tile580x80,
   R.string.tile580x80,R.string.tile580x80,
   R.string.tile680x80,R.string.tile680x80,
   R.string.tile680x80,R.string.tile680x80,
   R.string.tile780x80,R.string.tile780x80,
   R.string.tile780x80,R.string.tile780x80,
   R.string.tile880x80,R.string.tile880x80,
   R.string.tile880x80,R.string.tile880x80,
   R.string.tile980x80,R.string.tile980x80,
   R.string.tile980x80,R.string.tile980x80
  };
  private final int sound[] = { 
   R.raw.tilea80x80, R.raw.tileb80x80, R.raw.tilec80x80, R.raw.tiled80x80,
   R.raw.tilee80x80, R.raw.tilef80x80, R.raw.tileg80x80, R.raw.tileh80x80,
   R.raw.tilei80x80, R.raw.tilej80x80, R.raw.tilek80x80, R.raw.tilel80x80,
   R.raw.tilem80x80, R.raw.tilen80x80, R.raw.tileo80x80, R.raw.tilep80x80,
   R.raw.tileq80x80, R.raw.tiler80x80, R.raw.tiles80x80, R.raw.tilet80x80,
   R.raw.tileu80x80, R.raw.tilev80x80, R.raw.tilew80x80, R.raw.tilex80x80,
   R.raw.tiley80x80, R.raw.tilez80x80, R.raw.tile080x80, R.raw.tile180x80,
   R.raw.tile280x80, R.raw.tile380x80, R.raw.tile480x80, R.raw.tile580x80,
   R.raw.tile680x80, R.raw.tile780x80, R.raw.tile880x80, R.raw.tile980x80
  };

  private AssetFileDescriptor afd[] = new AssetFileDescriptor[36];

  private static final boolean DEBUG = false;

  int hintTile1;
  int hintTile2;
  boolean hinting;

  volatile int thescore = 60;
  int highscore = 0;

  private Handler handle;
  
  private int clockState;
  private static final int RUNNING = 0;
  private static final int STOPPED = 1;
  private Runnable timescore = new Runnable () {
    public void run() {
      if (clockState == RUNNING) {
        handle.sendMessage(new Message());
      }
    }
  };

  static final long wait = 1000; // 1 second
  public  boolean handleMessage(Message msg) {
    scoreboard.setText(""+thescore);
    scoreboard.postInvalidate();
    thescore = thescore - 1;
    if (thescore <= 0) {
      thescore = 0;
      stopTheClock();
      changeUserMessage(SCORE,"Time up");
    }
    handle.removeCallbacks(timescore);
    if (clockState == RUNNING) {
      handle.postDelayed(timescore,wait);
    }
    return(true);
  }

  PowerManager pm;
  PowerManager.WakeLock keepScreenOn;

  private TileSound player = new TileSound();
  MediaPlayer playr = new MediaPlayer();

  private boolean m[] = new boolean[144];
  private int matchcount = 0;
  private float scale = 1.0f;
  boolean color = true;

  @Override
  protected void onStart() {
    super.onStart();
    System.out.println("TPDHERE0 onStart called");
    try {
     if (keepScreenOn == null) { 
      keepScreenOn=pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK,"tpd");
     }
     if (keepScreenOn.isHeld() == false) { keepScreenOn.acquire(); }
    } catch (SecurityException se) {
    }
//    startTheClock();
  }

  @Override
  protected void onResume() {
    super.onResume();
    System.out.println("TPDHERE0 onResume called");
    try {
     if (keepScreenOn == null) { 
      keepScreenOn=pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK,"tpd");
     }
     if (keepScreenOn.isHeld() == false) { keepScreenOn.acquire(); }
    } catch (SecurityException se) {
    }
    startTheClock();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    System.out.println("TPDHERE0 onDestroy called");
    if ((keepScreenOn != null) && (keepScreenOn.isHeld() == true)) { 
      keepScreenOn.release(); 
    }
    stopTheClock();
  }

  @Override
  protected void onSaveInstanceState(Bundle savestate) {
    super.onSaveInstanceState(savestate);
    System.out.println("TPDHERE0 onSaveInstanceState called");
  }

  @Override
  protected void onRestart() {
    super.onRestart();
    System.out.println("TPDHERE0 onRestart called");
    try {
     if (keepScreenOn == null) { 
      keepScreenOn=pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK,"tpd");
     }
     if (keepScreenOn.isHeld() == false) { keepScreenOn.acquire(); }
    } catch (SecurityException se) {
    }
    startTheClock();
  }

  @Override
  protected void onStop() {
    super.onStop();
    System.out.println("TPDHERE0 onStop called");
    if ((keepScreenOn != null) && (keepScreenOn.isHeld() == true)) { 
      keepScreenOn.release(); 
    }
    stopTheClock();
  }

  @Override
  protected void onPause() { 
    super.onPause();
    System.out.println("TPDHERE0 onStop called");
    if ((keepScreenOn != null) && (keepScreenOn.isHeld() == true)) { 
      keepScreenOn.release(); 
    }
    stopTheClock();
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    System.out.println("TPDHERE0 onCreate called");
    appname = getString(R.string.app_name);
    if (board != null) { 
      ViewGroup vg = (ViewGroup)(board.getParent());
      vg.removeView(board);
    }
    DisplayMetrics metrics = new DisplayMetrics();
    getWindowManager().getDefaultDisplay().getMetrics(metrics);
    tabletheight = metrics.heightPixels;
    tabletwidth = metrics.widthPixels;
    board = new RelativeLayout(this);
    // assumed a board size of height=800 width=1280. scale to best size.
    float scaleh = tabletheight/800.0f;
    float scalew = tabletwidth/1280.0f;
    if (scaleh > scalew) {
      scale = scalew;
    } else {
      scale = scaleh;
    }
//    System.out.println("scaleh="+scaleh+" scalew="+scalew+" scale="+scale);
    makeTiles();
    makeSoundFiles();
    makeBoard();
    createTheClock();
    setContentView(board);
    pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
    keepScreenOn = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK,"tpd");
  }
 
  private void changeUserMessage(int field, String text) {
    switch (field) {
      case SCORE:
        scoreMessage = text;
        break;
      case COUNT:
        countMessage = text;
        break;
      case CAPTION:
        captionMessage = text;
        break;
    }
    theUserMessage = scoreMessage+" "+countMessage+" "+captionMessage;
    say.setText(theUserMessage);
    say.postInvalidate();
  }

  int[] placex = 
   {
        1000,920,840,760,680,600,520,440,360,280,200,120,
                840,760,680,600,520,440,360,280,
            920,840,760,680,600,520,440,360,280,200,
        1160,1080,
        1000,920,840,760,680,600,520,440,360,280,200,120,
        1000,920,840,760,680,600,520,440,360,280,200,120,
    40 , 
            920,840,760,680,600,520,440,360,280,200,
                840,760,680,600,520,440,360,280,
        1000,920,840,760,680,600,520,440,360,280,200,120,
                    770,690,610,530,450,370, // layer 1
                    770,690,610,530,450,370,
                    770,690,610,530,450,370,
                    770,690,610,530,450,370,
                    770,690,610,530,450,370,
                    770,690,610,530,450,370,
                        700,620,540,460, // layer 2
                        700,620,540,460,
                        700,620,540,460,
                        700,620,540,460,
                            630,550, // layer 3
                            630,550,
                              590             // layer 4
   };
  int[] placey = 
   {
         20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20,
                 100,100,100,100,100,100,100,100,
            180,180,180,180,180,180,180,180,180,180,
        300,300,
        260,260,260,260,260,260,260,260,260,260,260, 260, 
        340,340,340,340,340,340,340,340,340,340,340, 340,
    300,        
            420,420,420,420,420,420,420,420,420,420,
                 500,500,500,500,500,500,500,500,
        580,580,580,580,580,580,580,580,580,580,580,580,
                     90, 90, 90, 90, 90, 90, // layer 1
                    170,170,170,170,170,170,
                    250,250,250,250,250,250,
                    330,330,330,330,330,330,
                    410,410,410,410,410,410,
                    490,490,490,490,490,490,
                        160,160,160,160,     // layer 2
                        240,240,240,240,
                        320,320,320,320,
                        400,400,400,400,
                            230,230,         // layer 3
                            310,310,
                              270            // layer 4
    };

  private int shouldShadow(int slot) {
    if (slot < 87) { return Tile.NEITHER; }
    switch (slot) { 
     case 87:  return Tile.BOTH;
     case 88:  if (m[87]) { return Tile.BOTH; } 
                            return Tile.TOP; 
     case 89:  if (m[88]) { return Tile.BOTH; }
                            return Tile.TOP; 
     case 90:  if (m[89]) { return Tile.BOTH; }
                            return Tile.TOP; 
     case 91:  if (m[90]) { return Tile.BOTH; }
                            return Tile.TOP; 
     case 92:  if (m[91]) { return Tile.BOTH; } 
                            return Tile.TOP; 


     case  93: if (m[87])                      { return Tile.BOTH; }
                                                 return Tile.SIDE;
     case  94: if (m[87] && m[88] && m[93])    { return Tile.BOTH; }
               if (m[87] && m[88])             { return Tile.TOP;  }
               if (m[87] && m[93])             { return Tile.SIDE; }
               if (m[88] && m[93])             { return Tile.SHORTCORNER; }
               if (m[88])                      { return Tile.SHORTTOP; }
               if (m[93])                      { return Tile.SHORTSIDE; }
                                                 return Tile.NEITHER;
     case  95: if (m[88] && m[89] && m[94])    { return Tile.BOTH; }
               if (m[88] && m[89])             { return Tile.TOP;  }
               if (m[88] && m[94])             { return Tile.SIDE; }
               if (m[89] && m[94])             { return Tile.SHORTCORNER; }
               if (m[89])                      { return Tile.SHORTTOP; }
               if (m[94])                      { return Tile.SHORTSIDE; }
                                                 return Tile.NEITHER;
     case  96: if (m[89] && m[90] && m[95])    { return Tile.BOTH; }
               if (m[89] && m[90])             { return Tile.TOP; }
               if (m[89] && m[95])             { return Tile.SIDE; }
               if (m[90] && m[95])             { return Tile.SHORTCORNER; }
               if (m[90])                      { return Tile.SHORTTOP; }
               if (m[95])                      { return Tile.SHORTSIDE; }
                                                 return Tile.NEITHER;
     case  97: if (m[90] && m[91] && m[96])    { return Tile.BOTH; }
               if (m[90] && m[91])             { return Tile.TOP; }
               if (m[90] && m[96])             { return Tile.SIDE; }
               if (m[91] && m[96])             { return Tile.SHORTCORNER; }
               if (m[91])                      { return Tile.SHORTTOP; }
               if (m[96])                      { return Tile.SHORTSIDE; }
                                                 return Tile.NEITHER;
     case  98: if (m[91] && m[92] && m[97])    { return Tile.BOTH; }
               if (m[91] && m[92])             { return Tile.TOP; }
               if (m[91] && m[97])             { return Tile.SIDE; }
               if (m[92] && m[97])             { return Tile.SHORTCORNER; }
               if (m[92])                      { return Tile.SHORTTOP; }
               if (m[97])                      { return Tile.SHORTSIDE; }
                                                 return Tile.NEITHER;

     case  99: if (m[93])                      { return Tile.BOTH; }
                                                 return Tile.SIDE;
     case 100: if (m[93] && m[94] && m[99])    { return Tile.BOTH; }
               if (m[93] && m[94])             { return Tile.TOP;  }
               if (m[93] && m[99])             { return Tile.SIDE; }
               if (m[94] && m[99])             { return Tile.SHORTCORNER; }
               if (m[94])                      { return Tile.SHORTTOP; }
               if (m[99])                      { return Tile.SHORTSIDE; }
                                                 return Tile.NEITHER;
     case 101: if (m[94] && m[95] && m[100])   { return Tile.BOTH; }
               if (m[94] && m[95])             { return Tile.TOP;  }
               if (m[94] && m[100])            { return Tile.SIDE; }
               if (m[95] && m[100])            { return Tile.SHORTCORNER; }
               if (m[95])                      { return Tile.SHORTTOP; }
               if (m[100])                     { return Tile.SHORTSIDE; }
                                                 return Tile.NEITHER;
     case 102: if (m[95] && m[96] && m[101])   { return Tile.BOTH; }
               if (m[95] && m[96])             { return Tile.TOP; }
               if (m[95] && m[101])            { return Tile.SIDE; }
               if (m[96] && m[101])            { return Tile.SHORTCORNER; }
               if (m[96])                      { return Tile.SHORTTOP; }
               if (m[101])                     { return Tile.SHORTSIDE; }
                                                 return Tile.NEITHER;
     case 103: if (m[96] && m[97] && m[102])   { return Tile.BOTH; }
               if (m[96] && m[97])             { return Tile.TOP; }
               if (m[96] && m[102])            { return Tile.SIDE; }
               if (m[97] && m[102])            { return Tile.SHORTCORNER; }
               if (m[97])                      { return Tile.SHORTTOP; }
               if (m[102])                     { return Tile.SHORTSIDE; }
                                                 return Tile.NEITHER;
     case 104: if (m[97] && m[98] && m[103])   { return Tile.BOTH; }
               if (m[97] && m[98])             { return Tile.TOP; }
               if (m[97] && m[103])            { return Tile.SIDE; }
               if (m[98] && m[103])            { return Tile.SHORTCORNER; }
               if (m[98])                      { return Tile.SHORTTOP; }
               if (m[103])                     { return Tile.SHORTSIDE; }
                                                 return Tile.NEITHER;

     case 105: if (m[99])                      { return Tile.BOTH; }
                                                 return Tile.SIDE;
     case 106: if (m[99] && m[100] && m[105])  { return Tile.BOTH; }
               if (m[99] && m[100])            { return Tile.TOP;  }
               if (m[99] && m[105])            { return Tile.SIDE; }
               if (m[100] && m[105])           { return Tile.SHORTCORNER; }
               if (m[100])                     { return Tile.SHORTTOP; }
               if (m[105])                     { return Tile.SHORTSIDE; }
                                                 return Tile.NEITHER;
     case 107: if (m[100] && m[101] && m[106]) { return Tile.BOTH; }
               if (m[100] && m[101])           { return Tile.TOP;  }
               if (m[100] && m[106])           { return Tile.SIDE; }
               if (m[101] && m[106])           { return Tile.SHORTCORNER; }
               if (m[101])                     { return Tile.SHORTTOP; }
               if (m[106])                     { return Tile.SHORTSIDE; }
                                                 return Tile.NEITHER;
     case 108: if (m[101] && m[102] && m[107]) { return Tile.BOTH; }
               if (m[101] && m[102])           { return Tile.TOP; }
               if (m[101] && m[107])           { return Tile.SIDE; }
               if (m[102] && m[107])           { return Tile.SHORTCORNER; }
               if (m[102])                     { return Tile.SHORTTOP; }
               if (m[107])                     { return Tile.SHORTSIDE; }
                                                 return Tile.NEITHER;
     case 109: if (m[102] && m[103] && m[108]) { return Tile.BOTH; }
               if (m[102] && m[103])           { return Tile.TOP; }
               if (m[102] && m[108])           { return Tile.SIDE; }
               if (m[103] && m[108])           { return Tile.SHORTCORNER; }
               if (m[103])                     { return Tile.SHORTTOP; }
               if (m[108])                     { return Tile.SHORTSIDE; }
                                                 return Tile.NEITHER;
     case 110: if (m[103] && m[104] && m[109]) { return Tile.BOTH; }
               if (m[103] && m[104])           { return Tile.TOP; }
               if (m[103] && m[109])           { return Tile.SIDE; }
               if (m[104] && m[109])           { return Tile.SHORTCORNER; }
               if (m[104])                     { return Tile.SHORTTOP; }
               if (m[109])                     { return Tile.SHORTSIDE; }
                                                 return Tile.NEITHER;

     case 111: if (m[105])                     { return Tile.BOTH; }
                                                 return Tile.SIDE;
     case 112: if (m[105] && m[106] && m[111]) { return Tile.BOTH; }
               if (m[105] && m[106])           { return Tile.TOP;  }
               if (m[105] && m[111])           { return Tile.SIDE; }
               if (m[106] && m[111])           { return Tile.SHORTCORNER; }
               if (m[106])                     { return Tile.SHORTTOP; }
               if (m[111])                     { return Tile.SHORTSIDE; }
                                                 return Tile.NEITHER;
     case 113: if (m[106] && m[107] && m[112]) { return Tile.BOTH; }
               if (m[106] && m[107])           { return Tile.TOP;  }
               if (m[106] && m[112])           { return Tile.SIDE; }
               if (m[107] && m[112])           { return Tile.SHORTCORNER; }
               if (m[107])                     { return Tile.SHORTTOP; }
               if (m[112])                     { return Tile.SHORTSIDE; }
                                                 return Tile.NEITHER;
     case 114: if (m[107] && m[108] && m[113]) { return Tile.BOTH; }
               if (m[107] && m[108])           { return Tile.TOP; }
               if (m[107] && m[113])           { return Tile.SIDE; }
               if (m[108] && m[113])           { return Tile.SHORTCORNER; }
               if (m[108])                     { return Tile.SHORTTOP; }
               if (m[113])                     { return Tile.SHORTSIDE; }
                                                 return Tile.NEITHER;
     case 115: if (m[108] && m[109] && m[114]) { return Tile.BOTH; }
               if (m[108] && m[109])           { return Tile.TOP; }
               if (m[108] && m[114])           { return Tile.SIDE; }
               if (m[109] && m[114])           { return Tile.SHORTCORNER; }
               if (m[109])                     { return Tile.SHORTTOP; }
               if (m[114])                     { return Tile.SHORTSIDE; }
                                                 return Tile.NEITHER;
     case 116: if (m[109] && m[110] && m[115]) { return Tile.BOTH; }
               if (m[109] && m[110])           { return Tile.TOP; }
               if (m[109] && m[115])           { return Tile.SIDE; }
               if (m[110] && m[115])           { return Tile.SHORTCORNER; }
               if (m[110])                     { return Tile.SHORTTOP; }
               if (m[115])                     { return Tile.SHORTSIDE; }
                                                 return Tile.NEITHER;


     case 117: if (m[111])                     { return Tile.BOTH; }
                                                 return Tile.SIDE;

     case 118: if (m[111] && m[112] && m[117]) { return Tile.BOTH; }
               if (m[111] && m[112])           { return Tile.TOP;  }
               if (m[111] && m[117])           { return Tile.SIDE; }
               if (m[112] && m[117])           { return Tile.SHORTCORNER; }
               if (m[112])                     { return Tile.SHORTTOP; }
               if (m[117])                     { return Tile.SHORTSIDE; }
                                                 return Tile.NEITHER;
     case 119: if (m[112] && m[113] && m[118]) { return Tile.BOTH; }
               if (m[112] && m[113])           { return Tile.TOP;  }
               if (m[112] && m[118])           { return Tile.SIDE; }
               if (m[113] && m[118])           { return Tile.SHORTCORNER; }
               if (m[113])                     { return Tile.SHORTTOP; }
               if (m[118])                     { return Tile.SHORTSIDE; }
                                                 return Tile.NEITHER;
     case 120: if (m[113] && m[114] && m[119]) { return Tile.BOTH; }
               if (m[113] && m[114])           { return Tile.TOP; }
               if (m[113] && m[119])           { return Tile.SIDE; }
               if (m[114] && m[119])           { return Tile.SHORTCORNER; }
               if (m[114])                     { return Tile.SHORTTOP; }
               if (m[119])                     { return Tile.SHORTSIDE; }
                                                 return Tile.NEITHER;
     case 121: if (m[114] && m[115] && m[120]) { return Tile.BOTH; }
               if (m[114] && m[115])           { return Tile.TOP; }
               if (m[114] && m[120])           { return Tile.SIDE; }
               if (m[115] && m[120])           { return Tile.SHORTCORNER; }
               if (m[115])                     { return Tile.SHORTTOP; }
               if (m[120])                     { return Tile.SHORTSIDE; }
                                                 return Tile.NEITHER;
     case 122: if (m[115] && m[116] && m[121]) { return Tile.BOTH; }
               if (m[115] && m[116])           { return Tile.TOP; }
               if (m[115] && m[121])           { return Tile.SIDE; }
               if (m[116] && m[121])           { return Tile.SHORTCORNER; }
               if (m[116])                     { return Tile.SHORTTOP; }
               if (m[121])                     { return Tile.SHORTSIDE; }
                                                 return Tile.NEITHER;


     case 123: return Tile.BOTH;
     case 124: if (m[123]) { return Tile.BOTH; }
                             return Tile.TOP;
     case 125: if (m[124]) { return Tile.BOTH; }
                             return Tile.TOP;
     case 126: if (m[125]) { return Tile.BOTH; }
                             return Tile.TOP;

     case 127: if (m[123])                     { return Tile.BOTH; }
                                                 return Tile.SIDE;
     case 128: if (m[123] && m[124] && m[127]) { return Tile.BOTH; }
               if (m[123] && m[124])           { return Tile.TOP;  }
               if (m[123] && m[127])           { return Tile.SIDE; }
               if (m[124] && m[127])           { return Tile.SHORTCORNER; }
               if (m[124])                     { return Tile.SHORTTOP; }
               if (m[127])                     { return Tile.SHORTSIDE; }
                                                 return Tile.NEITHER;
     case 129: if (m[124] && m[125] && m[128]) { return Tile.BOTH; }
               if (m[124] && m[125])           { return Tile.TOP;  }
               if (m[124] && m[128])           { return Tile.SIDE; }
               if (m[125] && m[128])           { return Tile.SHORTCORNER; }
               if (m[125])                     { return Tile.SHORTTOP; }
               if (m[128])                     { return Tile.SHORTSIDE; }
                                                 return Tile.NEITHER;
     case 130: if (m[125] && m[126] && m[129]) { return Tile.BOTH; }
               if (m[126] && m[125])           { return Tile.TOP; }
               if (m[125] && m[129])           { return Tile.SIDE; }
               if (m[126] && m[129])           { return Tile.SHORTCORNER; }
               if (m[126])                     { return Tile.SHORTTOP; }
               if (m[129])                     { return Tile.SHORTSIDE; }
                                                 return Tile.NEITHER;

     case 131: if (m[127])                     { return Tile.BOTH; }
                                                 return Tile.SIDE;
     case 132: if (m[127] && m[128] && m[131]) { return Tile.BOTH; }
               if (m[127] && m[128])           { return Tile.TOP;  }
               if (m[127] && m[131])           { return Tile.SIDE; }
               if (m[128] && m[131])           { return Tile.SHORTCORNER; }
               if (m[128])                     { return Tile.SHORTTOP; }
               if (m[131])                     { return Tile.SHORTSIDE; }
                                                 return Tile.NEITHER;
     case 133: if (m[128] && m[129] && m[132]) { return Tile.BOTH; }
               if (m[128] && m[129])           { return Tile.TOP;  }
               if (m[128] && m[132])           { return Tile.SIDE; }
               if (m[129] && m[132])           { return Tile.SHORTCORNER; }
               if (m[129])                     { return Tile.SHORTTOP; }
               if (m[132])                     { return Tile.SHORTSIDE; }
                                                 return Tile.NEITHER;
     case 134: if (m[129] && m[130] && m[133]) { return Tile.BOTH; }
               if (m[130] && m[129])           { return Tile.TOP; }
               if (m[129] && m[133])           { return Tile.SIDE; }
               if (m[130] && m[133])           { return Tile.SHORTCORNER; }
               if (m[130])                     { return Tile.SHORTTOP; }
               if (m[133])                     { return Tile.SHORTSIDE; }
                                                 return Tile.NEITHER;

     case 135: if (m[131])                     { return Tile.BOTH; }
                                                 return Tile.SIDE;
     case 136: if (m[131] && m[132] && m[135]) { return Tile.BOTH; }
               if (m[131] && m[132])           { return Tile.TOP;  }
               if (m[131] && m[135])           { return Tile.SIDE; }
               if (m[132] && m[135])           { return Tile.SHORTCORNER; }
               if (m[132])                     { return Tile.SHORTTOP; }
               if (m[135])                     { return Tile.SHORTSIDE; }
                                                 return Tile.NEITHER;
     case 137: if (m[132] && m[133] && m[136]) { return Tile.BOTH; }
               if (m[132] && m[133])           { return Tile.TOP;  }
               if (m[132] && m[136])           { return Tile.SIDE; }
               if (m[133] && m[136])           { return Tile.SHORTCORNER; }
               if (m[133])                     { return Tile.SHORTTOP; }
               if (m[136])                     { return Tile.SHORTSIDE; }
                                                 return Tile.NEITHER;
     case 138: if (m[133] && m[134] && m[137]) { return Tile.BOTH; }
               if (m[134] && m[133])           { return Tile.TOP; }
               if (m[133] && m[137])           { return Tile.SIDE; }
               if (m[134] && m[137])           { return Tile.SHORTCORNER; }
               if (m[134])                     { return Tile.SHORTTOP; }
               if (m[137])                     { return Tile.SHORTSIDE; }
                                                 return Tile.NEITHER;

     case 139: if (!m[140]) { return Tile.BOTH; }
                              return Tile.BOTH; 
     case 140: if (m[139]) { return Tile.BOTH; } 
                             return Tile.TOP; 
     case 141: if (m[139]) { return Tile.BOTH; } 
                             return Tile.SIDETILT;
     case 142: if (m[139] && m[140] && m[141]) { return Tile.BOTH; } 
               if (m[140] && m[141])           { return Tile.SHORTCORNER; }
               if (m[139] && m[141])           { return Tile.SIDETILT; }
               if (m[139] && m[140])           { return Tile.TOP; }
               if (m[141])                     { return Tile.SHORTSIDE; }
               if (m[140])                     { return Tile.SHORTTOP; }
                                                 return Tile.NEITHER;
     case 143: return Tile.BOTH; 
     default:  return Tile.NEITHER;
    }
  }

  private void addTile(RelativeLayout board, int slot) {
    Tile t = tile[placement[slot]];
    t.setVisibility(View.VISIBLE);
    t.setShadow(shouldShadow(slot));
    t.setIndex(slot);
    t.setOnClickListener(this);
    RelativeLayout.LayoutParams boardparams;
    int eighty = Math.round(80*scale);
    boardparams = new RelativeLayout.LayoutParams(eighty,eighty);
    boardparams.leftMargin = Math.round(placex[slot]*scale);
    boardparams.topMargin  = Math.round(placey[slot]*scale);
    try {
      board.addView(t,boardparams);
    } catch (Exception e) {
      ViewGroup vg = (ViewGroup)(t.getParent());
      vg.removeView(t);
      board.addView(t,boardparams);
    }
  }

  private void makeTiles() {
    for(int i=0; i < 36; i++) {
      for(int j=0; j < 4; j++) {
       int index = i*4+j;
       if (tile[index] == null) {
          tile[index] = new Tile(this,image[i],i,scale,color); 
        }
        tile[index].markUnchosen();
        tile[index].postInvalidate();
        m[index]=false;
      }
    }
  }

  private void makeSoundFiles() {
    player.initSounds(this);
    for(int i=0; i<36; i++) { player.addSound(i,sound[i]); }
  }

  private void randomizePlacement() {
    Random rand = new Random();
    int place1;
    int swap = 0;
    for(int i=0; i<144; i++) { placement[i]=i; }
    for(int i=0; i<144; i++) { 
      place1 = rand.nextInt(144);
      swap = placement[i];
      placement[i] = placement[place1];
      placement[place1] = swap;
    }  
  }


  private void makeBoard() {
    randomizePlacement();
    Arrays.fill(m,false); // no tiles missing
    for(int i=0; i<=143; i++) { addTile(board,i); } 
    addSay(board);
    addScoreBoard(board);
    addMatchBoard(board);
    computeMatchCount();
  }

  private void addScoreBoard(RelativeLayout board) {
    RelativeLayout.LayoutParams boardparams;
    boardparams = new RelativeLayout.LayoutParams(Math.round(100*scale),40);
    boardparams.leftMargin = Math.round(1160*scale);
    boardparams.topMargin  = Math.round(100*scale);
    scoreboard = new TextView(this);
    scoreboard.setTextSize(20);
    scoreboard.setSingleLine();
    try {
      board.addView(scoreboard,boardparams);
    } catch (Exception e) {
      ViewGroup vg = (ViewGroup)(scoreboard.getParent());
      vg.removeView(scoreboard);
      board.addView(scoreboard,boardparams);
    }
  }

  private void addMatchBoard(RelativeLayout board) {
    RelativeLayout.LayoutParams boardparams;
    boardparams = new RelativeLayout.LayoutParams(Math.round(100*scale),40);
    boardparams.leftMargin = Math.round(1160*scale);
    boardparams.topMargin  = Math.round(180*scale);
    matchboard = new TextView(this);
    matchboard.setTextSize(20);
    matchboard.setSingleLine();
    try {
      board.addView(matchboard,boardparams);
    } catch (Exception e) {
      ViewGroup vg = (ViewGroup)(matchboard.getParent());
      vg.removeView(matchboard);
      board.addView(matchboard,boardparams);
    }
  }

  private void addSay(RelativeLayout board) {
    RelativeLayout.LayoutParams boardparams;
    boardparams = new RelativeLayout.LayoutParams(Math.round(1000*scale),40);
    boardparams.leftMargin = Math.round(120*scale);
    boardparams.topMargin  = Math.round(700*scale);
    say = new TextView(this);
    say.setTextSize(15);
    say.setSingleLine();
    changeUserMessage(CAPTION,"Welcome to "+appname);
    try {
      board.addView(say,boardparams);
    } catch (Exception e) {
      ViewGroup vg = (ViewGroup)(say.getParent());
      vg.removeView(say);
      board.addView(say,boardparams);
    }
  }

  private void computeMatchCount() {
    boolean unused[] = new boolean[144];
    String debug = "";
    Arrays.fill(unused,true);
    matchcount = 0;
    if (DEBUG) { System.out.println("================="); }
    for(int i=0; i<143; i++) {
      for(int j=0; j<143; j++) {
        if ((i < j) &&
            !m[i] && !m[j] &&           // not missing
            unused[i] && unused[j] &&   // not already used in a match
            (tile[placement[i]].getId() == 
             tile[placement[j]].getId()) && // same tile face
            validMove(i) && validMove(j)) { // can be a valid move
          if (DEBUG) {
            tile[placement[i]].markChosen();
            tile[placement[i]].postInvalidate();
            tile[placement[j]].markChosen();
            tile[placement[j]].postInvalidate();
            debug = debug+"("+i+" "+j+") ";
            System.out.println("match i="+i+" j="+j);
          }
          unused[i] = false;
          unused[j] = false;
          matchcount++;
          hintTile1=i;
          hintTile2=j;
        }
      }
      if (DEBUG) {
        say.setText(debug);
        say.postInvalidate();
      }
    }
    matchboard.setText(""+matchcount);
    matchboard.postInvalidate();
    if (matchcount == 0) {
      int finalscore = thescore;
      stopTheClock();
      newgame();
      if (highscore == 0) {
        say.setText("Game Over. Final score = "+finalscore);
      } else {
        say.setText("Game Over. Final score = "+finalscore+
                               " Previous high score = "+highscore);
      }
      if (finalscore > highscore) { highscore = finalscore; }
      say.postInvalidate();
    }
  }

  @Override
  public void onClick(View view) {
    hideHint();
    Tile tile = (Tile) view;
    int id = tile.getId();
    player.playSound(id);
    matches(tile);
  }

  private void matches(Tile b) {
    int x;
    int y;
    int lastindex;
    int ninety = Math.round(90*scale);
    int bindex = b.getIndex();
    if (!validMove(bindex)) { 
      if (lastb != null) { 
        lastb.markUnchosen();
        lastb.postInvalidate();
        lastb = null;
        lasttile = 0;
      }
      return; 
    }
    if (lastb == null) { // nothing is chosen
      lastb = b;
      lastb.markChosen();
      lastb.postInvalidate();
      lastindex = lastb.getIndex();
      x = (int)(placex[lastindex]*scale);
      y = (int)(placey[lastindex]*scale);
      sayMatches(lastindex);
    } else { // match? same face but different location
      lastindex = lastb.getIndex();
      if ((lastb.getId() == b.getId()) && (lastindex != bindex)) {
        incrementTheScore();
        m[bindex] = true;
        b.setVisibility(View.INVISIBLE);
        b.postInvalidate();
        x = (int)(placex[bindex]*scale);
        y = (int)(placey[bindex]*scale);
        m[lastindex] = true;
        lastb.setVisibility(View.INVISIBLE);
        lastb.postInvalidate();
        x = (int)(placex[lastindex]*scale);
        y = (int)(placey[lastindex]*scale);
        if ((bindex > 87) || (lastindex > 87)) { updateShadows(); }
        lastb = null;
        computeMatchCount();
        sayMatches(-1); // stop saying match tile
      } else { // not a match, make the choice the new choice
        lastb.markUnchosen();
        lastb.postInvalidate();
        lastb = b;
        lasttile = 0;
        b.markChosen();
        b.postInvalidate();
        sayMatches(bindex);
      }
    }
   }

  private void updateShadows() {
    for (int i=87; i<143; i++) { 
     tile[placement[i]].setShadow(shouldShadow(i)); }
  }

  private void sayMatches(int index) {
    if (index >= 0) {
      changeUserMessage(CAPTION,getString(caption[placement[index]]));
      matchboard.setText(""+matchcount);
      matchboard.postInvalidate();
    }
  }

  private void sayDebug(String msg) {
    say.setText(msg);
    say.postInvalidate();
  }

  private boolean validMove(int move) {
    boolean valid = false;
    switch (move) {
     case 0: valid = true;
       break;
     case 1: if (m[0] || m[2]) { valid = true; }
       break;
     case 2: if (m[1] || m[3]) { valid = true; }
       break;
     case 3: if (m[2] || m[4]) { valid = true; }
       break;
     case 4: if (m[3] || m[5]) { valid = true; }
       break;
     case 5: if (m[4] || m[6]) { valid = true; }
       break;
     case 6: if (m[5] || m[7]) { valid = true; }
       break;
     case 7: if (m[6] || m[8]) { valid = true; }
       break;
     case 8: if (m[7] || m[9]) { valid = true; }
       break;
     case 9: if (m[8] || m[10]) { valid = true; }
       break;
     case 10: if (m[9] || m[11]) { valid = true; }
       break;
     case 11: valid = true;
       break;
     case 12: valid = true;
       break;
     case 13: if (m[12] && m[87]) { valid = true; }
       break;
     case 14: if ((m[13] || m[15]) && m[88]) { valid = true; }
       break;
     case 15: if ((m[14] || m[16]) && m[89]) { valid = true; }
       break;
     case 16: if ((m[15] || m[17]) && m[90]) { valid = true; }
       break;
     case 17: if ((m[16] || m[18]) && m[91]) { valid = true; }
       break;
     case 18: if ((m[17] || m[19]) && m[92]) { valid = true; }
       break;
     case 19: valid = true;
       break;
     case 20: valid = true;
       break;
     case 21: if (m[20] || m[22]) { valid = true; }
       break;
     case 22: if ((m[21] || m[23]) && m[93]) { valid = true; }
       break;
     case 23: if ((m[22] || m[24]) && m[94]) { valid = true; }
       break;
     case 24: if ((m[23] || m[25]) && m[95]) { valid = true; }
       break;
     case 25: if ((m[24] || m[26]) && m[96]) { valid = true; }
       break;
     case 26: if ((m[25] || m[27]) && m[97]) { valid = true; }
       break;
     case 27: if ((m[26] || m[28]) && m[98]) { valid = true; }
       break;
     case 28: if (m[27] || m[29]) { valid = true; }
       break;
     case 29: valid = true;
       break;
     case 30: valid = true;
       break;
     case 31: if (m[30] || (m[32] && m[44])) { valid = true; }
       break;
     case 32: if (m[31] || m[33]) { valid = true; }
       break;
     case 33: if (m[32] || m[34]) { valid = true; }
       break;
     case 34: if (m[33] || m[35]) { valid = true; }
       break;
     case 35: if ((m[34] || m[36]) && m[99]) { valid = true; }
       break;
     case 36: if ((m[35] || m[37]) && m[100]) { valid = true; }
       break;
     case 37: if ((m[36] || m[38]) && m[101]) { valid = true; }
       break;
     case 38: if ((m[37] || m[39]) && m[102]) { valid = true; }
       break;
     case 39: if ((m[38] || m[40]) && m[103]) { valid = true; }
       break;
     case 40: if ((m[39] || m[41]) && m[104]) { valid = true; }
       break;
     case 41: if (m[40] || m[42]) { valid = true; }
       break;
     case 42: if (m[41] || m[43]) { valid = true; }
       break;
     case 43: if (m[42] || m[56]) { valid = true; }
       break;
     case 44: if (m[31] || m[45]) { valid = true; }
       break;
     case 45: if (m[44] || m[46]) { valid = true; }
       break;
     case 46: if (m[45] || m[47]) { valid = true; }
       break;
     case 47: if ((m[46] || m[48]) && m[105]) { valid = true; }
       break;
     case 48: if ((m[47] || m[49]) && m[106]) { valid = true; }
       break;
     case 49: if ((m[48] || m[50]) && m[107]) { valid = true; }
       break;
     case 50: if ((m[49] || m[51]) && m[108]) { valid = true; }
       break;
     case 51: if ((m[50] || m[52]) && m[109]) { valid = true; }
       break;
     case 52: if ((m[51] || m[53]) && m[110]) { valid = true; }
       break;
     case 53: if (m[52] || m[54]) { valid = true; }
       break;
     case 54: if (m[53] || m[55]) { valid = true; }
       break;
     case 55: if (m[54] || m[56]) { valid = true; }
       break;
     case 56: valid = true;
       break;
     case 57: valid = true;
       break;
     case 58: if (m[57] || m[59]) { valid = true; }
       break;
     case 59: if ((m[58] || m[60]) && m[111]) { valid = true; }
       break;
     case 60: if ((m[59] || m[61]) && m[112]) { valid = true; }
       break;
     case 61: if ((m[60] || m[62]) && m[113]) { valid = true; }
       break;
     case 62: if ((m[61] || m[63]) && m[114]) { valid = true; }
       break;
     case 63: if ((m[62] || m[64]) && m[115]) { valid = true; }
       break;
     case 64: if ((m[63] || m[65]) && m[116]) { valid = true; }
       break;
     case 65: if (m[64] || m[66]) { valid = true; }
       break;
     case 66: valid = true;
       break;
     case 67: valid = true;
       break;
     case 68: if ((m[67] || m[69]) && m[117]) { valid = true; }
       break;
     case 69: if ((m[68] || m[70]) && m[118]) { valid = true; }
       break;
     case 70: if ((m[69] || m[71]) && m[119]) { valid = true; }
       break;
     case 71: if ((m[70] || m[72]) && m[120]) { valid = true; }
       break;
     case 72: if ((m[71] || m[73]) && m[121]) { valid = true; }
       break;
     case 73: if ((m[72] || m[74]) && m[122]) { valid = true; }
       break;
     case 74: valid = true;
       break;
     case 75: valid = true;
       break;
     case 76: if (m[75] || m[77]) { valid = true; }
       break;
     case 77: if (m[76] || m[78]) { valid = true; }
       break;
     case 78: if (m[77] || m[79]) { valid = true; }
       break;
     case 79: if (m[78] || m[80]) { valid = true; }
       break;
     case 80: if (m[79] || m[81]) { valid = true; }
       break;
     case 81: if (m[80] || m[82]) { valid = true; }
       break;
     case 82: if (m[81] || m[83]) { valid = true; }
       break;
     case 83: if (m[82] || m[84]) { valid = true; }
       break;
     case 84: if (m[83] || m[85]) { valid = true; }
       break;
     case 85: if (m[84] || m[86]) { valid = true; }
       break;
     case 86: valid = true;
       break;
     case 87: valid = true;
       break;
     case 88: if (m[87] || m[89]) { valid = true; }
       break;
     case 89: if (m[88] || m[90]) { valid = true; }
       break;
     case 90: if (m[89] || m[91]) { valid = true; }
       break;
     case 91: if (m[90] || m[92]) { valid = true; }
       break;
     case 92: valid = true;
       break;
     case 93: valid = true;
       break;
     case 94: if ((m[93] || m[95]) && m[123]) { valid = true; }
       break;
     case 95: if ((m[94] || m[96]) && m[124]) { valid = true; }
       break;
     case 96: if ((m[95] || m[97]) && m[125]) { valid = true; }
       break;
     case 97: if ((m[96] || m[98]) && m[126]) { valid = true; }
       break;
     case 98: valid = true;
       break;
     case 99: valid = true;
       break;
     case 100: if ((m[99] || m[101]) && m[127]) { valid = true; }
       break;
     case 101: if ((m[100] || m[102]) && m[128]) { valid = true; }
       break;
     case 102: if ((m[101] || m[103]) && m[129]) { valid = true; }
       break;
     case 103: if ((m[102] || m[104]) && m[130]) { valid = true; }
       break;
     case 104: valid = true;
       break;
     case 105: valid = true;
       break;
     case 106: if ((m[105] || m[107]) && m[131]) { valid = true; }
       break;
     case 107: if ((m[106] || m[108]) && m[132]) { valid = true; }
       break;
     case 108: if ((m[107] || m[109]) && m[133]) { valid = true; }
       break;
     case 109: if ((m[108] || m[110]) && m[134]) { valid = true; }
       break;
     case 110: valid = true;
       break;
     case 111: valid = true;
       break;
     case 112: if ((m[111] || m[113]) && m[135]) { valid = true; }
       break;
     case 113: if ((m[112] || m[114]) && m[136]) { valid = true; }
       break;
     case 114: if ((m[113] || m[115]) && m[137]) { valid = true; }
       break;
     case 115: if ((m[114] || m[116]) && m[138]) { valid = true; }
       break;
     case 116: valid = true;
       break;
     case 117: valid = true;
       break;
     case 118: if (m[117] || m[119]) { valid = true; }
       break;
     case 119: if (m[118] || m[120]) { valid = true; }
       break;
     case 120: if (m[119] || m[121]) { valid = true; }
       break;
     case 121: if (m[120] || m[122]) { valid = true; }
       break;
     case 122: valid = true;
       break;
     case 123: valid = true;
       break;
     case 124: if (m[123] || m[125]) { valid = true; }
       break;
     case 125: if (m[124] || m[126]) { valid = true; }
       break;
     case 126: valid = true;
       break;
     case 127: valid = true;
       break;
     case 128: if ((m[127] || m[129]) && m[139]) { valid = true; }
       break;
     case 129: if ((m[128] || m[130]) && m[140]) { valid = true; }
       break;
     case 130: valid = true;
       break;
     case 131: valid = true;
       break;
     case 132: if ((m[131] || m[133]) && m[141]) { valid = true; }
       break;
     case 133: if ((m[132] || m[134]) && m[142]) { valid = true; }
       break;
     case 134: valid = true;
       break;
     case 135: valid = true;
       break;
     case 136: if (m[135] || m[137]) { valid = true; }
       break;
     case 137: if (m[136] || m[138]) { valid = true; }
       break;
     case 138: valid = true;
       break;
     case 139: if (m[143]) { valid = true; }
       break;
     case 140: if (m[143]) { valid = true; }
       break;
     case 141: if (m[143]) { valid = true; }
       break;
     case 142: if (m[143]) { valid = true; }
       break;
     case 143: valid = true;
       break;
    }
    if (valid) {
      return true;
    }
    return false;
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    super.onCreateOptionsMenu(menu);
    menu.add(0, MENU_ABOUT, 0, R.string.about).setIcon(R.drawable.about);
    menu.add(0, MENU_NEWGAME, 0, R.string.newgame).setIcon(R.drawable.newgame);
    menu.add(0, MENU_HINT, 0, R.string.hint).setIcon(R.drawable.hint);
    menu.add(0, MENU_COLOR, 0, R.string.color).setIcon(R.drawable.color);
    menu.add(0, MENU_SOUND, 0, R.string.sound).setIcon(R.drawable.sound);
    return true;
  }

  private void showHint() {
    Tile hint1 = tile[placement[hintTile1]];
    hint1.markChosen();
    hint1.postInvalidate();
    Tile hint2 = tile[placement[hintTile2]];
    hint2.markChosen();
    hint2.postInvalidate();
    sayMatches(hintTile1);
    hinting = true;
  }

  private void hideHint() {
    if (hinting == true) {
      Tile hint1 = tile[placement[hintTile1]];
      hint1.markUnchosen();
      hint1.postInvalidate();
      Tile hint2 = tile[placement[hintTile2]];
      hint2.markUnchosen();
      hint2.postInvalidate();
      hinting = false;
    }
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case MENU_HINT:
        showHint();
        return true;
      case MENU_ABOUT:
        say.setText("Tim Daly Literate Software April 30, 2011 6");
        say.postInvalidate();
        return true;
      case MENU_NEWGAME:
        newgame();
        return true;
      case MENU_COLOR:
        color = !color;
        for(int i=0; i<144; i++) {
          tile[i].setColor(color);
          tile[i].postInvalidate();
        }
        return true;
      case MENU_SOUND:
        if (player.toggleSound()) {
          makeSoundFiles();
          say.setText("Sound is now on");
        } else {
          say.setText("Sound is now off");
        }
        say.postInvalidate();
        return true;
      default:
        return false;
    }
  }
 
  private void rememberTheScore() {
    if (thescore > highscore) {
      highscore = thescore;
    }
    changeUserMessage(SCORE,"Highest score was "+highscore);
  }

  private void newgame() {
    board = new RelativeLayout(this);
    rememberTheScore();
    resetTheScore();
    hideHint();
    makeTiles();
    makeBoard();
    setContentView(board);
  }

  private synchronized void startTheClock() {
    clockState = RUNNING;
    handle.post(timescore);
  }

  private synchronized void stopTheClock() {
    clockState = STOPPED;
    handle.removeCallbacks(timescore);
    
  }

  private void resetTheScore() {
    stopTheClock();
    thescore = 60;
    startTheClock();
  }

  private void incrementTheScore() {
    if (thescore > 0) { thescore = thescore + 15; }
  }

  private void createTheClock() {
    handle = new Handler(this);
  }

  private boolean isStorageAvailable() {
    String externalStorageState = Environment.getExternalStorageState();
    if (!externalStorageState.equals(Environment.MEDIA_MOUNTED)) {
      Toast.makeText(this, R.string.sd_card_is_not_available,
                     Toast.LENGTH_SHORT).show();
      return false;
    }
    return true;
  }

  private void saveToSD() {
    if (!isStorageAvailable()) { return; }
  }

  private void saveBitmap(String fileName) {
  }

  private String getUniqueFilePath(String path) {
    String filename = "image_";
    String extension = ".png";
    int suffix = 1;
    while (new File(path + filename + suffix + extension).exists()) {
      suffix++;
    }
    return path + filename + suffix + extension;
  }

  private File getLastFile(String path) {
    String filename = "image_";
    String extension = ".png";
    int suffix = 1;
    File newFile = null;
    File file = null;
    boolean exists = false;
    do {
      file = newFile;
      newFile = new File(path + filename + suffix + extension);
      suffix++;
      exists = newFile.exists();
    } while (exists);
    return file;
  }

  private String getSDDir() {
    String path = 
      Environment.getExternalStorageDirectory().getAbsolutePath()+"/MahLetters/";
    File file = new File(path);
    if (!file.exists()) { file.mkdirs(); }
    return path;
  }

  public Bitmap getSavedBitmap() {
    if (!isStorageAvailable()) { return null; }
    File lastFile = getLastFile(getSDDir());
    if (lastFile == null) { return null; }
    Bitmap savedBitmap = null;
    try {
      FileInputStream fis = new FileInputStream(lastFile);
      savedBitmap = BitmapFactory.decodeStream(fis);
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    }
    return savedBitmap;
  }
}
