package dalcoms.pub.connect3blocks;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.ParallelEntityModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;

import android.graphics.PointF;
import android.util.Log;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import dalcoms.pub.connect3blocks.scene.SceneGame;

public abstract class RectanglePhysics extends Rectangle {
	private final String TAG = this.getClass().getSimpleName();

	private Body body;
	private SceneGame mSceneGame;
	private PhysicsWorld mPhysicsWorld;
	PhysicsConnector mPhysicsConnector;

	String mUserData;
	BodyType mBodyType;
	FixtureDef mFixtureDef;

	public RectanglePhysics( float pX, float pY,
			float pWidth, float pHeight,
			SceneGame pSceneGame ) {
		super( pX, pY, pWidth, pHeight, pSceneGame.getVbom() );

		this.mSceneGame = pSceneGame;
		this.mPhysicsWorld = mSceneGame.getPhysicsWorld();
	}

	public RectanglePhysics( float pWidth, float pHeight,
			SceneGame pSceneGame ) {
		this( 0, 0, pWidth, pHeight, pSceneGame );
	}

	public RectanglePhysics startPosition( PointF pStartPos ) {
		this.setPosition( pStartPos.x, pStartPos.y );

		return this;
	}

	public RectanglePhysics startPosition( float pStartPosX, float pStartPosY ) {
		this.setPosition( pStartPosX, pStartPosY );

		return this;
	}

	public RectanglePhysics createPhysics( String pUserData, BodyType pBodyType, FixtureDef pFixtureDef ) {
		this.mUserData = pUserData;
		this.mBodyType = pBodyType;
		this.mFixtureDef = pFixtureDef;

		pFixtureDef.density = this.mSceneGame.getResourcesManager().applyResizeFactor( pFixtureDef.density );
		pFixtureDef.restitution = this.mSceneGame.getResourcesManager().applyResizeFactor(
				pFixtureDef.restitution );
		pFixtureDef.friction = this.mSceneGame.getResourcesManager().applyResizeFactor( pFixtureDef.friction );

		if ( body == null ) {
			body = PhysicsFactory.createBoxBody( mPhysicsWorld, this, pBodyType, pFixtureDef );

			body.setTransform(
					( this.getX() + this.getWidth() * 0.5f ) / PhysicsConnector.PIXEL_TO_METER_RATIO_DEFAULT,
					( this.getY() + this.getHeight() * 0.5f ) / PhysicsConnector.PIXEL_TO_METER_RATIO_DEFAULT,
					body.getAngle() );

			body.setUserData( pUserData );
			body.setFixedRotation( false );

			mPhysicsConnector = new PhysicsConnector( this, body, true, true ) {
				@Override
				public void onUpdate( float pSecondsElapsed ) {
					super.onUpdate( pSecondsElapsed );
					onUpdateCheck();
				}
			};
		}
		mPhysicsWorld.registerPhysicsConnector( mPhysicsConnector );

		return this;
	}

	public abstract void onUpdateCheck( );

	public Body getBody( ) {
		return this.body;
	}

	public void byebye( ) {
		Log.v( TAG, "byebye:" + body.getUserData().toString() );

		clearFlagVars();

		mPhysicsWorld.unregisterPhysicsConnector( this.mPhysicsConnector );
		this.getBody().setActive( false );
		mPhysicsWorld.destroyBody( this.getBody() );
		this.setIgnoreUpdate( true );
		this.setVisible( false );
		this.detachSelf();
	}

	public void reCreateBody( ) {
		mPhysicsWorld.unregisterPhysicsConnector( this.mPhysicsConnector );
		this.getBody().setActive( false );
		mPhysicsWorld.destroyBody( this.getBody() );
		this.body = null;

		mSceneGame.getEngine().runOnUpdateThread( new Runnable() {

			@Override
			public void run( ) {
				createPhysics( mUserData, mBodyType, mFixtureDef );

			}
		} );
	}

	public void clearFlagVars( ) {
		//Include flags must to be cleared
	}

	public void safeByeBye( ) {
		mSceneGame.getEngine().runOnUpdateThread( new Runnable() {

			@Override
			public void run( ) {
				byebye();
			}
		} );
	}

	public void scaleAlphaByeBye( float pDisapearTime ) {
		this.registerEntityModifier( new ParallelEntityModifier(
				new ScaleModifier( pDisapearTime, 1f, 2.0f ),
				new AlphaModifier( pDisapearTime, 0.78f, 0f ) {
					@Override
					protected void onModifierFinished( IEntity pItem ) {
						super.onModifierFinished( pItem );
						safeByeBye();
					}
				} ) );
	}

	public SceneGame getGameScene( ) {
		return this.mSceneGame;
	}

}
