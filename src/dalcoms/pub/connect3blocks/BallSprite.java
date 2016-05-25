package dalcoms.pub.connect3blocks;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.ParallelEntityModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.util.color.Color;

import android.util.Log;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import dalcoms.pub.connect3blocks.scene.SceneGame;

public abstract class BallSprite extends Sprite {
	private Body body;
	private PhysicsWorld mPhysicsWorld;
	private SceneGame mSceneGame;
	PhysicsConnector mPhysicsConnector;
	private Sprite mInnerBall;
	private Vector2 mFingerThrowVelocity = new Vector2();
	private float mDeadLineUpper;
	private float mDeadLineLower;
	private boolean mFlagIsSelected = false;

	public BallSprite( float pX, float pY, ITextureRegion pTextureRegion, Color pBallColor,
			ITextureRegion pInnerBallRegion, Color pInnerColor, SceneGame pSceneGame ) {
		super( pX, pY, pTextureRegion, pSceneGame.getVbom() );

		mSceneGame = pSceneGame;
		mPhysicsWorld = pSceneGame.getPhysicsWorld();

		setDeadLineY( 0f, mSceneGame.getCamera().getHeight() );//Set default dead line

		final Color fBallColor = pBallColor;
		final ITextureRegion fInnerBallRegion = pInnerBallRegion;
		final Color fInnerColor = pInnerColor;

		mSceneGame.getEngine().runOnUpdateThread( new Runnable() {

			@Override
			public void run( ) {
				setColor( fBallColor );
				attachInnerBall( fInnerBallRegion, fInnerColor );
			}
		} );
	}

	public void setDeadLineY( float pDeadLineUpper, float pDeadLineLower ) {
		this.mDeadLineUpper = pDeadLineUpper;
		this.mDeadLineLower = pDeadLineLower;
	}

	public float getDeadLineUpper( ) {
		return this.mDeadLineUpper;
	}

	public float getDeadLineLower( ) {
		return this.mDeadLineLower;
	}

	private void attachInnerBall( ITextureRegion pInnerBallRegion, Color pInnerColor ) {
		final float pX = ( this.getWidth() - pInnerBallRegion.getWidth() ) * 0.5f;
		final float pY = ( this.getHeight() - pInnerBallRegion.getHeight() ) * 0.5f;

		mInnerBall = new Sprite( pX, pY, pInnerBallRegion, mSceneGame.getVbom() );
		mInnerBall.setColor( pInnerColor );
		attachChild( mInnerBall );
	}

	public boolean setInnerColor( Color pColor ) {
		boolean result = false;
		if ( mInnerBall != null ) {
			result = true;
			mInnerBall.setColor( pColor );
		}
		return result;
	}

	public float getCenterX( ) {
		return this.getX() + this.getWidth() * 0.5f;
	}

	public float getCenterY( ) {
		return this.getY() + this.getHeight() * 0.5f;
	}

	public void setCenterPosition( float pCenterX, float pCenterY ) {
		this.setPosition( pCenterX - this.getWidth() * 0.5f, pCenterY - this.getHeight() * 0.5f );
	}

	public BallSprite createPhysics( String pUserData, BodyType pBodyType, FixtureDef pFixtureDef ) {

		pFixtureDef.density = this.mSceneGame.getResourcesManager().applyResizeFactor( pFixtureDef.density );
		pFixtureDef.restitution = this.mSceneGame.getResourcesManager().applyResizeFactor(
				pFixtureDef.restitution );
		pFixtureDef.friction = this.mSceneGame.getResourcesManager().applyResizeFactor( pFixtureDef.friction );

		if ( body == null ) {
			this.body = PhysicsFactory.createCircleBody( this.mPhysicsWorld, 0f, 0f,
					this.getHeight() * 0.5f, pBodyType, pFixtureDef );

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
//		TODO : must be defined on json file.
		this.body.setLinearDamping( 0.2f );
		
		mPhysicsWorld.registerPhysicsConnector( mPhysicsConnector );

		return this;
	}

	private synchronized void onUpdateCheck( ) {
		mSceneGame.getHaloOfBall().setCenterPosition( this.getCenterX(), this.getCenterY() );
		checkBoundaryY();
		checkBoundaryX( false );
	}

	private void checkBoundaryX( boolean isRefect ) {
		if ( isRefect ) {
			reflectXWall();
		} else {
			throughXWall();
		}
	}

	private void throughXWall( ) {
		final float nBodyX = this.getX();
		final float pBodyX = nBodyX + this.getWidth();
		final Vector2 pLinearVelocity = this.getBody().getLinearVelocity();
		boolean isTransform = false;

		if ( pBodyX < 0 ) {
			this.setPosition( mSceneGame.getCamera().getWidth(), this.getY() );
			isTransform = true;
		}
		else if ( nBodyX > mSceneGame.getCamera().getWidth() ) {
			this.setPosition( -1f * getWidth(), this.getY() );
			isTransform = true;
		}

		if ( isTransform ) {
			body.setTransform(
					this.getCenterX()
							/ PhysicsConnector.PIXEL_TO_METER_RATIO_DEFAULT,
					this.getCenterY()
							/ PhysicsConnector.PIXEL_TO_METER_RATIO_DEFAULT,
					body.getAngle() );
			body.setLinearVelocity( new Vector2( 0.9f * pLinearVelocity.x, 0.9f * pLinearVelocity.y ) );
		}
	}

	private void reflectXWall( ) {
		final float nBodyX = this.getX();
		final float pBodyX = nBodyX + this.getWidth();
		final Vector2 pLinearVelocity = this.getBody().getLinearVelocity();

		if ( nBodyX < 0 ) {
			body.setLinearVelocity( new Vector2( -1 * pLinearVelocity.x, pLinearVelocity.y ) );
		}
		else if ( pBodyX > ResourcesManager.getInstance().getCamera().getWidth() ) {
			body.setLinearVelocity( new Vector2( -1 * pLinearVelocity.x, pLinearVelocity.y ) );
		}
	}

	private void checkBoundaryY( ) {
		if ( isOutOfUpperLimit() || isOutOfLowerLimit() ) {
			this.onDie();
		}
	}

	private boolean isOutOfUpperLimit( ) {
		boolean result = false;
		if ( this.getY() < getDeadLineUpper() ) {
			result = true;
		}

		return result;
	}

	private boolean isOutOfLowerLimit( ) {
		boolean result = false;
		if ( ( this.getY() + this.getHeight() ) > getDeadLineLower() ) {
			result = true;
		}
		return result;
	}

	public Body getBody( ) {
		return this.body;
	}

	public void clearFlagVars( ) {
		//Include flags must to be cleared
	}

	public void byebye( ) {
		Log.v( this.getClass().getSimpleName(), "byebye:" + body.getUserData().toString() );

		clearFlagVars();

		mPhysicsWorld.unregisterPhysicsConnector( this.mPhysicsConnector );
		this.getBody().setActive( false );
		mPhysicsWorld.destroyBody( this.getBody() );
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

	public void setFingerThrowVelocity( float pVelocityX, float pVelocityY ) {
		this.mFingerThrowVelocity.set( pVelocityX, pVelocityY );
	}

	public Vector2 getFingerThrowVelocity( ) {
		return this.mFingerThrowVelocity;
	}

	public void catchBall( ) {
		this.getBody().setLinearVelocity( 0f, 0f );
	}

	public void decelerateBall( float pRatio ) {
		final float ratio = pRatio > 1f ? 1f : pRatio;

		final float pVelocityX = this.getBody().getLinearVelocity().x * ratio;
		final float pVelocityY = this.getBody().getLinearVelocity().x * ratio;

		this.getBody().setLinearVelocity( pVelocityX, pVelocityY );
	}

	public void decelerateBall( ) {
		decelerateBall( 0.5f );
	}

	public void throwBall( ) {
		throwBall( this.getFingerThrowVelocity() );
	}

	public void throwBall( Vector2 pVector2 ) {
		this.getBody().setLinearVelocity( pVector2 );
	}

	public boolean isOnTouchArea( float pSceneTouchX, float pSceneTouchY, float pLen ) {//pLen have to be resized by resize factor.
		boolean result = false;

		final float thisCenterX = this.getCenterX();
		final float thisCenterY = this.getCenterY();

		final float diffX = thisCenterX - pSceneTouchX;
		final float diffY = thisCenterY - pSceneTouchY;

		float len2 = diffX * diffX + diffY * diffY;
		if ( ( pLen * pLen ) > len2 ) {
			result = true;
		}

		return result;
	}

	public boolean isSelected( ) {
		return this.mFlagIsSelected;
	}

	public boolean setSelect( boolean pSelect ) {
		this.mFlagIsSelected = pSelect;
		return this.mFlagIsSelected;
	}

	public abstract void onDie( );

}
