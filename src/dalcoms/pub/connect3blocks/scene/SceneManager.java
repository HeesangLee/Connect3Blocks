package dalcoms.pub.connect3blocks.scene;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.andengine.ui.IGameInterface.OnCreateSceneCallback;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import dalcoms.pub.connect3blocks.R;
import dalcoms.pub.connect3blocks.ResourcesManager;
import dalcoms.pub.connect3blocks.level.JsonDataLevelData;

public class SceneManager {
	private final String TAG = this.getClass().getSimpleName();
	private static final SceneManager instance = new SceneManager();

	private boolean flagResultInterstitialAdOn = false;
	private boolean forTstore = false;

	private BaseScene currentScene;

	private BaseScene sceneHome;
	private BaseScene sceneSplash;
	private BaseScene sceneGame;
	private BaseScene sceneIntermission;

	private SceneType currentSceneType;

	private int countReplay = 0;
	final int POP_AD_REPLAY = 15;

	private final float GAME_GRAVITY = 35f;
	private final float GAME_TIMER_TIME_SECOND = 0.1f; // 100msec

	//	private GameLevel mGameLevel = new GameLevel();

	private JsonDataLevelData mLevelData;
	private ArrayList<String> mDefaultRoundFiles;
	final String mGameRoundAssetSubFolderName = "game_round";
	private Map<Integer, RoundInfo> roundInfoMap = new HashMap<Integer, SceneManager.RoundInfo>();

	private int mSelectedRoundNum = 0;

	public static SceneManager getInstance( ) {
		return instance;
	}

	public void initGameLevelData( ) {// Load data from local repository
										// database.

		//		new LoadJsonLevelDataTask().execute();
		new LoadJsonDefaultRoundFilesTask().execute();
	}

	public JsonDataLevelData getLevelData( ) {
		return this.mLevelData;
	}

	public ArrayList<String> getDefaultRounFileNames( ) {
		return this.mDefaultRoundFiles;
	}

	public Map<Integer, RoundInfo> getRoundInfoMap( ) {
		return roundInfoMap;
	}

	public void setRoundInfoMap( Integer k, RoundInfo pRoundInfo ) {
		this.roundInfoMap.put( k, pRoundInfo );
	}

	public String getLastRoundInfoKey( ) {
		String result = "";

		result = getDefaultRounFileNames().get( getLastRoundInfoIndex() );

		return result;
	}

	public int getLastRoundInfoIndex( ) {
		int result = 0;

		if ( getRoundInfoMap().isEmpty() ) {
			setRoundInfoMap( 0, new RoundInfo( true, 0 ) );
			result = 0;
		} else {
			for ( Integer k : getRoundInfoMap().keySet() ) {
				if ( k > result ) {
					result = k;
				}
			}
		}

		return result;
	}

	private class LoadJsonDefaultRoundFilesTask extends AsyncTask<Void, Void, ArrayList<String>> {

		@Override
		protected void onPreExecute( ) {
			mDefaultRoundFiles = new ArrayList<String>();//Always clear arrayList before loading.
		}

		@Override
		protected ArrayList<String> doInBackground( Void... params ) {
			ArrayList<String> result = new ArrayList<String>();

			try {
				result.addAll( Arrays.asList( ResourcesManager.getInstance().getActivity().getAssets()
						.list( mGameRoundAssetSubFolderName ) ) );

			} catch ( IOException e ) {
				e.printStackTrace();
			}
			return result;
		}

		@Override
		protected void onPostExecute( ArrayList<String> result ) {
			mDefaultRoundFiles = result;
		}
	}

	private class LoadJsonLevelDataTask extends AsyncTask<Void, Void, JsonDataLevelData> {

		@Override
		protected JsonDataLevelData doInBackground( Void... params ) {
			String pJsonLevelString = loadJsonDataFile( "gameLevelData.json" );

			Type pLevelDataType = new TypeToken<JsonDataLevelData>() {
			}.getType();

			JsonDataLevelData result = new GsonBuilder().create().fromJson( pJsonLevelString, pLevelDataType );

			return result;
		}

		@Override
		protected void onPostExecute( JsonDataLevelData result ) {
			mLevelData = result;
		}

		private String loadJsonDataFile( String pJsonFileName ) {
			String strJson = null;
			try {

				InputStream is = ResourcesManager.getInstance().getActivity().getAssets()
						.open( pJsonFileName );
				int size = is.available();
				byte[] buffer = new byte[size];
				is.read( buffer );
				is.close();
				strJson = new String( buffer, "UTF-8" );
			} catch ( IOException e ) {
				e.printStackTrace();
				return null;
			}

			return strJson;
		}

	}

	public void setScene( BaseScene pScene ) {
		ResourcesManager.getInstance().getEngine().setScene( pScene );
		this.currentScene = pScene;
		this.currentSceneType = pScene.getSceneType();
	}

	public void setScene( SceneType pSceneType ) {
		switch ( pSceneType ) {

			case SCENE_HOME :
				setScene( sceneHome );
				break;
			case SCENE_SPLASH :
				setScene( sceneSplash );
				break;
			case SCENE_GAME :
				setScene( sceneGame );
			case SCENE_INTERMISSION :
				setScene( sceneIntermission );
			default :
				setScene( sceneHome );
				Log.e( TAG, "Scene creation is done with unexpected routine" );
				break;
		}
	}

	public BaseScene getCurrentScene( ) {
		return this.currentScene;
	}

	public SceneType getCurrentSceneType( ) {
		return this.currentSceneType;
	}

	public void createSceneSplash( OnCreateSceneCallback pOnCreateSceneCallback ) {
		this.sceneSplash = new SceneSplash();
		this.currentScene = this.sceneSplash;
		this.currentSceneType = this.sceneSplash.getSceneType();

		pOnCreateSceneCallback.onCreateSceneFinished( this.currentScene );
	}

	public void createSceneSplash( ) {
		this.sceneSplash = new SceneSplash();
		this.clearScene( this.currentSceneType );
		this.setScene( this.sceneSplash );
	}

	public void createSceneGame( ) {
		this.sceneGame = new SceneGame();
		this.clearScene( this.currentSceneType );
		this.setScene( this.sceneGame );
	}

	public void createSceneHome( ) {
		this.sceneHome = new SceneHome();
		this.clearScene( this.currentSceneType );
		this.setScene( this.sceneHome );
	}

	public void createSceneIntermission( int pDefaultRoundNum ) {
		if ( ++countReplay > POP_AD_REPLAY ) {
			countReplay = 0;
			this.popAdmobInterstitialAd();
		}
		this.sceneIntermission = new SceneIntermission();
		this.clearScene( this.currentSceneType );
		this.setScene( this.sceneIntermission );
	}

	private void clearScene( SceneType pSceneType ) {
		switch ( pSceneType ) {
			case SCENE_HOME :
				this.disposeSceneHome();
				break;
			case SCENE_SPLASH :
				this.disposeSceneSplash();
				break;
			case SCENE_GAME :
				this.disposeSceneGame();
				break;
			case SCENE_INTERMISSION :
				this.disposeSceneIntermission();
			default :
				Log.v( "Dispose Scene Error", "Some Scene selection is not correct" );
				break;
		}
	}

	private void disposeSceneSplash( ) {
		this.sceneSplash.disposeScene();
		this.sceneSplash = null;
	}

	private void disposeSceneHome( ) {
		this.sceneHome.disposeScene();
		this.sceneHome = null;
	}

	private void disposeSceneGame( ) {
		this.sceneGame.disposeScene();
		this.sceneGame = null;
	}

	private void disposeSceneIntermission( ) {
		this.sceneIntermission.disposeScene();
		this.sceneIntermission = null;
	}

	private void displayAdmobInterstitialAd( InterstitialAd pAd ) {
		if ( pAd.isLoaded() ) {
			pAd.show();
		}
	}

	public void popAdmobInterstitialAd( ) {
		final InterstitialAd adMobInterstitialAd = new InterstitialAd( ResourcesManager.getInstance()
				.getActivity() );
		adMobInterstitialAd.setAdUnitId( ResourcesManager.getInstance().getActivity()
				.getString( R.string.admob_interstitial_id ) );
		final AdRequest adRequest = new AdRequest.Builder().build();
		ResourcesManager.getInstance().getActivity().runOnUiThread( new Runnable() {

			@Override
			public void run( ) {
				adMobInterstitialAd.loadAd( adRequest );
			}
		} );

		adMobInterstitialAd.setAdListener( new AdListener() {
			public void onAdLoaded( ) {
				displayAdmobInterstitialAd( adMobInterstitialAd );
			}
		} );
	}

	public boolean isResultInterstitialAdOn( ) {
		return flagResultInterstitialAdOn;
	}

	public boolean isForTStore( ) {
		return forTstore;
	}

	public float getGameGravity( ) {
		return ResourcesManager.getInstance().applyResizeFactor( GAME_GRAVITY );
	}

	public float getGameTimerTimeSecond( ) {
		return GAME_TIMER_TIME_SECOND;
	}

	private void setSelectedRoundNum( int pRoundNum ) {
		this.mSelectedRoundNum = pRoundNum;
	}

	public int getSelectedRoundNum( ) {
		return this.mSelectedRoundNum;
	}

	public class RoundInfo {
		private boolean mUnLock;
		private int mPoint;

		public RoundInfo( boolean pUnLock, int pPoint ) {
			mUnLock = pUnLock;
			mPoint = pPoint;
		}

		public void setClear( boolean pUnLock ) {
			this.mUnLock = pUnLock;
		}

		public void setPoint( int pPOint ) {
			this.mPoint = pPOint;
		}

		public boolean isUnLock( ) {
			return mUnLock;
		}

		public int getPoint( ) {
			return mPoint;
		}
	}
}
