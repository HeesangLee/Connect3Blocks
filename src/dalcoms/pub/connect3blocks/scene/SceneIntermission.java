package dalcoms.pub.connect3blocks.scene;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import android.app.Activity;
import dalcoms.pub.connect3blocks.ResourcesManager;

public class SceneIntermission extends BaseScene {

	private final float ARROW_INTERNAL = 0.12f;
	private final int ARROW_NUM = 8;
	private int displayedArrowIndex = 0;
	private float arrowPositionX[];
	private float arrowPositionY;

	@Override
	public void createScene( ) {
		this.setBackground( new Background( this.appColor.APP_BACKGROUND ) );
		this.engine.runOnUpdateThread( new Runnable() {
			@Override
			public void run( ) {
				attachSprites();
			}
		} );
	}

	@Override
	public void attachSprites( ) {
		final float xOffset = camera.getWidth() / 4f;
		arrowPositionY = appComm.getAlignCenterFloat( resourcesManager.regionArrow.getHeight(),
				camera.getHeight() );

		arrowPositionX = appComm.getDistributedCenterOrgPosition(
				resourcesManager.regionArrow.getWidth(),
				ARROW_NUM,
				camera.getWidth() - 2f * xOffset,
				xOffset );
		attachArrow( arrowPositionY );
		registerUpdateHandlerForThisScene( ARROW_INTERNAL );
	}

	private void registerUpdateHandlerForThisScene( float timeSecond ) {
		this.engine.registerUpdateHandler( new TimerHandler( timeSecond, true, new ITimerCallback() {

			@Override
			public void onTimePassed( final TimerHandler pTimerHandler ) {
				if ( displayedArrowIndex < arrowPositionX.length ) {
					attachArrow( arrowPositionY );
				} else {
					engine.unregisterUpdateHandler( pTimerHandler );

					//Goto gameScene
					sceneManager.createSceneGame();
				}
			}
		} ) );

	}

	private void attachArrow( float pY ) {
		Sprite tSprite = new Sprite( arrowPositionX[displayedArrowIndex++],
				pY, resourcesManager.regionArrow, vbom );
		if ( displayedArrowIndex < arrowPositionX.length ) {
			tSprite.registerEntityModifier( new AlphaModifier( 0.45f, 1f, 0f ) );
		}
		attachChild( tSprite );
	}

	@Override
	public void onBackKeyPressed( ) {
		// TODO Auto-generated method stub

	}

	@Override
	public Engine getEngine( ) {
		return this.engine;
	}

	@Override
	public Activity getActivity( ) {
		return this.activity;
	}

	@Override
	public VertexBufferObjectManager getVbom( ) {
		return this.vbom;
	}

	@Override
	public Camera getCamera( ) {
		return this.camera;
	}

	@Override
	public SceneType getSceneType( ) {
		return SceneType.SCENE_INTERMISSION;
	}

	@Override
	public ResourcesManager getResourcesManager( ) {
		return this.resourcesManager;
	}

	@Override
	public SceneManager getSceneManager( ) {
		return this.sceneManager;
	}

	@Override
	public void disposeScene( ) {
		// TODO Auto-generated method stub

	}

}