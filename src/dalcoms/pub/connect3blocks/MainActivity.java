package dalcoms.pub.connect3blocks;

import org.andengine.engine.Engine;
import org.andengine.engine.LimitedFPSEngine;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.WakeLockOptions;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.opengl.view.RenderSurfaceView;
import org.andengine.ui.activity.LayoutGameActivity;

import android.graphics.PixelFormat;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.KeyEvent;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import dalcoms.pub.connect3blocks.scene.SceneManager;


public class MainActivity extends LayoutGameActivity {
public Camera mCamera;
	
	private final int MYFPS = 60;
	private final float TIME_SPLASH2START = 2.8f;
	private boolean hasBeenDestroyedPaused = false;
	private boolean sceneMenuCreated = false;

	private AdView adMobAdView;
	
	private Point getResizedCameraSize(){
		Point retSize = new Point(480,789);
		final int[] cameraRefHeight = {1750,1170,780};
		final Point[] useCameraSize = {
				new Point(1080,1776),
				new Point(720,1184),
				new Point(480,789)
		};
		DisplayMetrics displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		
		for(int i=0;i<cameraRefHeight.length;i++){
			if(displayMetrics.heightPixels>cameraRefHeight[i]){
				retSize = useCameraSize[i];
				break;
			}
		}
		
//		retSize = new Point(480,789);
		
		return retSize;
	}
	
	
	@Override
	protected void onSetContentView(){
		super.onSetContentView();
		mRenderSurfaceView = new RenderSurfaceView(this);
		mRenderSurfaceView.setEGLConfigChooser(8,8,8,8,24,0);
		mRenderSurfaceView.setRenderer(mEngine, this);
		mRenderSurfaceView.getHolder().setFormat(PixelFormat.RGBA_8888);
		
		initAdmobAdView();

	}

	@Override
	public EngineOptions onCreateEngineOptions() {
		Point cameraSize = new Point();
		
		cameraSize = getResizedCameraSize();

		mCamera = new Camera(0,0,cameraSize.x,cameraSize.y);
		
		EngineOptions engineOptions = new EngineOptions(
				true,
				ScreenOrientation.PORTRAIT_FIXED, 
				new RatioResolutionPolicy(cameraSize.x,cameraSize.y),
				mCamera);
		engineOptions.getRenderOptions().setDithering(true);
		engineOptions.getAudioOptions().setNeedsMusic(true).setNeedsSound(true);
		engineOptions.setWakeLockOptions(WakeLockOptions.SCREEN_DIM);
		
		return engineOptions;
	}
	
	@Override
	public Engine onCreateEngine(EngineOptions pEngineOptions){
		
		return new LimitedFPSEngine(pEngineOptions, MYFPS);
	}
	
	@Override
	public void onCreateResources(
			OnCreateResourcesCallback pOnCreateResourcesCallback)
			throws Exception {
		ResourcesManager.prepare(mEngine, this, mCamera, getVertexBufferObjectManager());
		ResourcesManager.getInstance().loadResources();
		SceneManager.getInstance().initGameLevelData();
		
		pOnCreateResourcesCallback.onCreateResourcesFinished();
	}

	@Override
	public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback)
			throws Exception {
		SceneManager.getInstance().createSceneSplash(pOnCreateSceneCallback);
		
	}

	@Override
	public void onPopulateScene(Scene pScene,
			OnPopulateSceneCallback pOnPopulateSceneCallback) throws Exception {
		mEngine.registerUpdateHandler(new TimerHandler(TIME_SPLASH2START, new ITimerCallback() {
			
			@Override
			public void onTimePassed(TimerHandler pTimerHandler) {
				mEngine.unregisterUpdateHandler(pTimerHandler);
				sceneMenuCreated = true;
				SceneManager.getInstance().createSceneHome();
				
			}
		}));
		pOnPopulateSceneCallback.onPopulateSceneFinished();
		
	}
	

	@Override
	protected int getLayoutID() {
		return R.layout.activity_main;
	}

	@Override
	protected int getRenderSurfaceViewID() {
		return R.id.gameSurfaceView;
	}
	
	@Override
	public void onDestroy(){
//		ResourcesManager.getInstance().mVibrator.cancel();
		ResourcesManager.getInstance().getVibrator().cancel();
		hasBeenDestroyedPaused = true;
		adMobAdView.destroy();
		super.onDestroy();
		System.exit(0);
	}
	
	@Override
	public void onPause(){
//		ResourcesManager.getInstance().mVibrator.cancel();
		ResourcesManager.getInstance().getVibrator().cancel();
		hasBeenDestroyedPaused = true;
		adMobAdView.pause();
		super.onPause();
	}
	
	@Override
	public synchronized void onResume(){
		adMobAdView.resume();
		
		super.onResume();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event){
		if(keyCode == KeyEvent.KEYCODE_BACK){
			SceneManager.getInstance().getCurrentScene().onBackKeyPressed();
		}
		return false;
	}
	
	private void initAdmobAdView(){
		adMobAdView = (AdView) this.findViewById(R.id.adView);
		
		AdRequest request = new AdRequest.Builder().
				addTestDevice(AdRequest.DEVICE_ID_EMULATOR).
				build();
		
		adMobAdView.loadAd(request);
		adMobAdView.setBackgroundColor(android.graphics.Color.TRANSPARENT);
		
	}
}