package dalcoms.pub.connect3blocks;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.ParallelEntityModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import android.graphics.PointF;
import android.util.Log;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import dalcoms.pub.connect3blocks.scene.SceneGame;

public abstract class PhysicsSprite extends Sprite {
	private Body body;
	private SceneGame mSceneGame;
	PhysicsConnector mPhysicsConnector;

	String mUserData;
	BodyType mBodyType;
	FixtureDef mFixtureDef;
	String mBodyShape;

	public PhysicsSprite( float pX, float pY, ITextureRegion pTextureRegion,
			VertexBufferObjectManager pVertexBufferObjectManager, SceneGame pSceneGame ) {
		super( pX, pY, pTextureRegion, pVertexBufferObjectManager );
		this.mSceneGame = pSceneGame;
	}

	public PhysicsSprite startPosition( PointF pStartPos ) {
		this.setPosition( pStartPos.x, pStartPos.y );

		return this;
	}

	private void savePhysicsInstances( String pUserData, BodyType pBodyType,
			FixtureDef pFixtureDef, String pBodyShape ) {
		this.mUserData = pUserData;
		this.mBodyType = pBodyType;
		this.mFixtureDef = pFixtureDef;
		this.mBodyShape = pBodyShape;
	}

	public PhysicsSprite createPhysics( String pUserData, BodyType pBodyType, FixtureDef pFixtureDef,
			String pBodyShape ) {
		savePhysicsInstances( pUserData, pBodyType, pFixtureDef, pBodyShape );

		pFixtureDef.density = this.mSceneGame.getResourcesManager().applyResizeFactor( pFixtureDef.density );
		pFixtureDef.restitution = this.mSceneGame.getResourcesManager().applyResizeFactor(
				pFixtureDef.restitution );
		pFixtureDef.friction = this.mSceneGame.getResourcesManager().applyResizeFactor( pFixtureDef.friction );

		if ( body == null ) {
			if ( pBodyShape.equalsIgnoreCase( "box" ) ) {
				body = PhysicsFactory.createBoxBody( mSceneGame.getPhysicsWorld(), this, pBodyType,
						pFixtureDef );
			} else if ( pBodyShape.equalsIgnoreCase( "circle" ) ) {
				this.body = PhysicsFactory.createCircleBody( mSceneGame.getPhysicsWorld(), 0f, 0f,
						this.getHeight() * 0.5f, pBodyType, pFixtureDef );
			}

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
		mSceneGame.getPhysicsWorld()
				.registerPhysicsConnector( mPhysicsConnector );

		return this;
	}

	public abstract void onUpdateCheck( );

	public abstract void clearFlagVars( );

	public Body getBody( ) {
		return this.body;
	}

	public void byebye( ) {
		Log.v( this.getClass().getSimpleName(), "byebye:" + body.getUserData().toString() );

		clearFlagVars();

		mSceneGame.getPhysicsWorld()
				.unregisterPhysicsConnector( this.mPhysicsConnector );
		this.getBody().setActive( false );
		mSceneGame.getPhysicsWorld()
				.destroyBody( this.getBody() );
		this.setIgnoreUpdate( true );
		this.setVisible( false );
		this.detachSelf();
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
