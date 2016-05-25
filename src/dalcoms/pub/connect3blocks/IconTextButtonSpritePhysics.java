package dalcoms.pub.connect3blocks;

import lib.dalcoms.andengineheesanglib.buttons.IconTextButtonSprite;
import lib.dalcoms.andengineheesanglib.utils.HsMath;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.color.Color;

import android.util.Log;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import dalcoms.pub.connect3blocks.scene.SceneGame;

public class IconTextButtonSpritePhysics extends IconTextButtonSprite {

	private final String TAG = IconTextButtonSprite.class.getSimpleName();

	private Body body;
	private Camera mCamera;
	private PhysicsWorld mPhysicsWorld;
	private SceneGame mSceneGame;
	PhysicsConnector mPhysicsConnector;

	public IconTextButtonSpritePhysics(float pX, float pY,
			ITextureRegion pTextureRegion, VertexBufferObjectManager vbom,
			ITextureRegion iconRegion, boolean pIsIconRotate, String sPrompt,
			Color pColor, Font pFont, Color pTextColor) {

		super(pX, pY, pTextureRegion, vbom, iconRegion, pIsIconRotate, sPrompt,
				pColor, pFont, pTextColor);
	}

	public IconTextButtonSprite setCamera(Camera pCamera) {
		this.mCamera = pCamera;

		return this;
	}

	public IconTextButtonSpritePhysics setPhysicsWorld(
			PhysicsWorld pPhysicsWorld) {
		this.mPhysicsWorld = pPhysicsWorld;

		return this;
	}

	public IconTextButtonSpritePhysics setSceneGame(SceneGame pSceneGame) {
		this.mSceneGame = pSceneGame;

		return this;
	}

	public IconTextButtonSprite createPhysics() {

		final float pDensity = ResourcesManager.getInstance()
				.applyResizeFactor(1f);
		final float pElasticity = ResourcesManager.getInstance()
				.applyResizeFactor(0.25f);
		final float pFriction = ResourcesManager.getInstance()
				.applyResizeFactor(0.5f);

		FixtureDef pFixtureDef = PhysicsFactory.createFixtureDef(pDensity,
				pElasticity, pFriction);

		body = PhysicsFactory.createBoxBody(mPhysicsWorld, this,
				BodyType.KinematicBody, pFixtureDef);

		body.setTransform((this.getX() + this.getWidth() * 0.5f)
				/ PhysicsConnector.PIXEL_TO_METER_RATIO_DEFAULT, this.getY()
				/ PhysicsConnector.PIXEL_TO_METER_RATIO_DEFAULT,
				body.getAngle());

		this.mPhysicsConnector = new PhysicsConnector(this, this.body, true,
				true) {
			@Override
			public void onUpdate(float pSecondsElapsed) {
				super.onUpdate(pSecondsElapsed);
				onUpdateCheck();
			}
		};

		this.mPhysicsWorld.registerPhysicsConnector(this.mPhysicsConnector);

		return this;
	}

	public IconTextButtonSprite createPhysics(Object userData) {
		createPhysics();
		this.body.setUserData(userData);

		return this;
	}

	private void onUpdateCheck() {

	}

	public Body getBody() {
		if (this.body == null) {
			Log.e(TAG,
					"Physics body is not created yet. create Physics with createPhysics() method");
		}
		return this.body;
	}

	public void destroy() {
		mPhysicsWorld.unregisterPhysicsConnector(mPhysicsConnector);
		getBody().setActive(false);
		mPhysicsWorld.destroyBody(getBody());
		setIgnoreUpdate(true);
		setVisible(false);
		detachSelf();
	}

	@Override
	public void isActionDownEvent() {
		// moveBody();
	}

	@Override
	public void doButtonAction() {

	}

	public void moveBody(boolean pDestroySelf) {
		// Move to random position and rotate with random angular velocity
		// Check stop timing with random stop-time and destroy self.
		// Angular velocity don't need to be re-calculated via
		// applyResizeFactor.... You have to test on real devices..
		final float LINEAR_VELOCITY_MIN = -5f;
		final float LINEAR_VELOCITY_MAX = 5f;
		final float ANGULAR_VELOCITY_MIN = -0.8f;
		final float ANGULAR_VELOCITY_MAX = 0.8f;
		final float DESTROY_TIME_S_MIN = 0.5f;
		final float DESTROY_TIME_S_MAX = 2f;

		float vX, vY, omega;
		float pDestroyTimeSeconds;

		HsMath hsMath = new HsMath();

		vX = hsMath.getRandomRangeFloat(LINEAR_VELOCITY_MIN,
				LINEAR_VELOCITY_MAX);
		vY = hsMath.getRandomRangeFloat(LINEAR_VELOCITY_MIN,
				LINEAR_VELOCITY_MAX);
		omega = hsMath.getRandomRangeFloat(ANGULAR_VELOCITY_MIN,
				ANGULAR_VELOCITY_MAX);
		pDestroyTimeSeconds = hsMath.getRandomRangeFloat(DESTROY_TIME_S_MIN,
				DESTROY_TIME_S_MAX);

		vX = ResourcesManager.getInstance().applyResizeFactor(vX);
		vY = ResourcesManager.getInstance().applyResizeFactor(vY);

		Log.v(TAG, String.format("vX=%f, vY=%f, omega=%f", vX, vY, omega));

		this.body.setAngularVelocity(omega);
		this.body.setLinearVelocity(vX, vY);

		if (pDestroySelf == true) {
			destroyDelayed(pDestroyTimeSeconds);
		}

	}

	private void destroyDelayed(float pDestroyTimeSeconds) {
		mSceneGame.getEngine().registerUpdateHandler(new TimerHandler(
				pDestroyTimeSeconds, new ITimerCallback() {

					@Override
					public void onTimePassed(TimerHandler pTimerHandler) {
						mSceneGame.getEngine()
								.unregisterUpdateHandler(pTimerHandler);
						destroy();
					}
				}));
	}

}
