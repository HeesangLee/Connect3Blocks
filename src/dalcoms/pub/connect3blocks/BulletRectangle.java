package dalcoms.pub.connect3blocks;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.ParallelEntityModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.DrawType;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import android.graphics.PointF;
import android.util.Log;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

import dalcoms.pub.connect3blocks.scene.SceneGame;
import dalcoms.pub.connect3blocks.scene.SceneManager;

public class BulletRectangle extends Rectangle {
	private final String TAG = this.getClass().getSimpleName();
	private final boolean LOGVON = true;

	private Body body;
	private SceneGame mSceneGame;
	private PhysicsWorld mPhysicsWorld;
	PhysicsConnector mPhysicsConnector;

	public BulletRectangle( final float pX, final float pY, final float pWidth, final float pHeight,
			final VertexBufferObjectManager pVertexBufferObjectManager, SceneGame pSceneGame ) {

		super( pX, pY, pWidth, pHeight, pVertexBufferObjectManager );

		this.mSceneGame = pSceneGame;
		this.mPhysicsWorld = mSceneGame.getPhysicsWorld();
	}

	public BulletRectangle( final float pWidth, final float pHeight,
			final VertexBufferObjectManager pVertexBufferObjectManager, SceneGame pSceneGame ) {

		this( 0f, 0f, pWidth, pHeight, pVertexBufferObjectManager, pSceneGame );
	}

	public BulletRectangle startPosition( PointF pStartPos ) {
		this.setPosition( pStartPos.x, pStartPos.y );

		return this;
	}

	public BulletRectangle startPosition( float pStartPosX, float pStartPosY ) {
		this.setPosition( pStartPosX, pStartPosY );

		return this;
	}

	public BulletRectangle createPhysics( String pUserData, BodyType pBodyType ) {

		FixtureDef pFixtureDef = PhysicsFactory.createFixtureDef( ResourcesManager.getInstance()
				.applyResizeFactor( 0.5f ), ResourcesManager.getInstance().applyResizeFactor( 0.6f ),
				ResourcesManager.getInstance().applyResizeFactor( 0.25f ) );

		if ( body == null ) {
			body = PhysicsFactory.createBoxBody( mPhysicsWorld, this, pBodyType, pFixtureDef );

			body.setTransform( this.getX() / PhysicsConnector.PIXEL_TO_METER_RATIO_DEFAULT, this.getY()
					/ PhysicsConnector.PIXEL_TO_METER_RATIO_DEFAULT, body.getAngle() );

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

	public BulletRectangle createPhysics( String pUserData ) {
		this.createPhysics( pUserData, BodyType.DynamicBody );

		return this;
	}

	public BulletRectangle createPhysics( ) {
		this.createPhysics( "bullet", BodyType.DynamicBody );

		return this;
	}

	private synchronized void onUpdateCheck( ) {
		boolean flagByeBye = false;

		flagByeBye = isOutOfScreen();

		if ( flagByeBye ) {
			preByeBye();
		}
	}

	private boolean isOutOfScreen( ) {
		boolean ret = false;

		if ( this.getX() < -1 * this.getWidth() ) {
			ret = true;
		} else if ( this.getX() > mSceneGame.getCamera().getWidth() + this.getWidth() ) {
			ret = true;
		}
		if ( this.getY() < -1 * ( this.getHeight() ) ) {
			ret = true;
		}

		return ret;
	}

	public Body getBody( ) {
		return this.body;
	}

	public BulletRectangle fire( ) {
//		getBody().applyForce( new Vector2( 10f, mSceneGame.sceneManager.getGameGravity() * -2f ),
//				getBody().getWorldCenter() );
		
		getBody().applyLinearImpulse( new Vector2( 0f, SceneManager.getInstance().getGameGravity() * -4f ),
				getBody().getWorldCenter() );
		return this;
	}

	public void preByeBye( ) {
		final float timeModifier = 0.15f;

		this.registerEntityModifier( new ParallelEntityModifier( new ScaleModifier( timeModifier, 1f, 2.0f ),
				new AlphaModifier( timeModifier, 0.78f, 0f ) {
					@Override
					protected void onModifierFinished( IEntity pItem ) {
						super.onModifierFinished( pItem );
						mSceneGame.getEngine().runOnUpdateThread( new Runnable() {

							@Override
							public void run( ) {
								byeBye();
							}
						} );

					}
				} ) );
	}

	public void byeBye( ) {
		this.logV( TAG, "byebye:" + body.getUserData().toString() );
		//		mSceneGame.getPooPool().onHandleRecycleItem( this );

	}

	public void clearFlagVar( ) {
		//		TODO : clear Flags when recycleItem on Pool of this.
	}

	private void logV( String pTag, String pMsg ) {
		if ( this.LOGVON == true ) {
			Log.v( pTag, pMsg );
		}
	}
}
