package dalcoms.pub.connect3blocks.scene;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lib.dalcoms.andengineheesanglib.utils.HsMath;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.modifier.MoveXModifier;
import org.andengine.entity.modifier.ParallelEntityModifier;
import org.andengine.entity.modifier.RotationModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.shape.IAreaShape;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.color.Color;
import org.andengine.util.modifier.ease.EaseBackInOut;

import android.app.Activity;
import android.support.v4.view.VelocityTrackerCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

import dalcoms.pub.connect3blocks.BallSprite;
import dalcoms.pub.connect3blocks.HaloOfBallSprite;
import dalcoms.pub.connect3blocks.PrickleSprite;
import dalcoms.pub.connect3blocks.RectangleBrick;
import dalcoms.pub.connect3blocks.RectangleGround;
import dalcoms.pub.connect3blocks.ResourcesManager;
import dalcoms.pub.connect3blocks.ThisAppCommon;
import dalcoms.pub.connect3blocks.level.JsonDataBrick;
import dalcoms.pub.connect3blocks.level.JsonDataEntity;
import dalcoms.pub.connect3blocks.level.JsonDataLevel;

public class SceneGame extends BaseScene implements IOnSceneTouchListener {
	private final String TAG = this.getClass().getSimpleName();

	private PhysicsWorld physicsWorld;

	ThisAppCommon appComm;
	HsMath hsMath;

	ArrayList<IAreaShape> mIAreaShapeDetachOnPlay = new ArrayList<IAreaShape>();

	HaloOfBallSprite mHaloOfBallSprite;
	BallSprite mMainBall;
	JsonDataEntity mMainBallJsonDataEntity;
	JsonDataEntity mHaloJsonDataEntity;
	private boolean isBallDieFlag = true;

	private int mGameTimePerGameTimer = 0;

	private int mGameLevel = 0;
	private int mGameHeartCount = 2;
	ArrayList<Sprite> mGameHearts = new ArrayList<Sprite>();
	Text mGamePointText;
	private int mGamePoint = 0;
	Queue<Integer> mPointQueue;

	private JsonDataLevel mLevelData;

	private VelocityTracker mVelocityTracker = null;
	private final int VELOCITY_TRACKER_CAL_UNIT = 25;

	private float BALL_TOUCH_AREA;

	private Map<String, RectangleBrick> mMapRectBrick = new HashMap<String, RectangleBrick>();

	@Override
	public void createScene( ) {
		appComm = new ThisAppCommon();
		hsMath = new HsMath();

		this.mLevelData = loadGameLevel();

		this.setBackground( new Background( this.appColor.APP_BACKGROUND ) );

		initialPhysicsWorld();

		registerUpdateHandlerForGame( sceneManager.getGameTimerTimeSecond() );

		initPools();

		this.engine.runOnUpdateThread( new Runnable() {
			@Override
			public void run( ) {
				attachSprites();
			}
		} );

		this.engine.registerUpdateHandler( new TimerHandler( 0.1f, new ITimerCallback() {

			@Override
			public void onTimePassed( TimerHandler pTimerHandler ) {
				engine.unregisterUpdateHandler( pTimerHandler );
				setOnSceneTouchListenerDelay();
			}
		} ) );

	}

	private void setOnSceneTouchListenerDelay( ) {
		setOnSceneTouchListener( this );
	}

	private void initPools( ) {

	}

	synchronized private void registerUpdateHandlerForGame( float timeSecond ) {
		if ( mPointQueue == null ) {
			mPointQueue = new LinkedList<Integer>();
		}

		this.engine.registerUpdateHandler( new TimerHandler( timeSecond, true, new ITimerCallback() {

			@Override
			public void onTimePassed( final TimerHandler pTimerHandler ) {
				mGameTimePerGameTimer++;

				checkHaloAlpha();
				upDateGamePointText();
			}
		} ) );

	}

	private void checkHaloAlpha( ) {
		//		if ( mMainBall != null ) {
		//			this.mHaloOfBallSprite.setCenterPosition( mMainBall.getCenterX(), mMainBall.getCenterY() );
		//			
		//		}

		this.mHaloOfBallSprite.setSelectedAlpha( mMainBall.isSelected() );

	}

	private JsonDataLevel loadGameLevel( ) {
		//		this.mGameLevel = sceneManager.getGameLevelData().getSelectedLevel();
		return sceneManager.getLevelData().getLevels().get( this.mGameLevel );
	}

	private int getGameLevel( ) {
		return this.mGameLevel;
	}

	private void initialPhysicsWorld( ) {
		//		this.physicsWorld = new PhysicsWorld( new Vector2( 0, sceneManager.getGameGravity() ), false );
		this.physicsWorld = new PhysicsWorld( new Vector2( 0, 0 ), false );
		registerUpdateHandler( physicsWorld );
		this.physicsWorld.setContactListener( createContactListener() );

	}

	private ContactListener createContactListener( ) {
		ContactListener contactListener = new ContactListener() {

			@Override
			public void preSolve( Contact contact, Manifold oldManifold ) {
				// TODO Auto-generated method stub

			}

			@Override
			public void postSolve( Contact contact, ContactImpulse impulse ) {
				// TODO Auto-generated method stub

			}

			@Override
			public void endContact( Contact contact ) {
				final Fixture x1 = contact.getFixtureA();
				final Fixture x2 = contact.getFixtureB();
			}

			@Override
			public void beginContact( Contact contact ) {
				final Fixture x1 = contact.getFixtureA();
				final Fixture x2 = contact.getFixtureB();

				if ( isBrickContactWithBall( x1, x2 ) ) {
					mMainBall.setSelect( false );
					Log.d( "contact", getBrickUserName( x1, x2 ) );
					breakBrick( getBrickUserName( x1, x2 ) );

				} else if ( isBrickContactWithPrickle( x1, x2 ) ) {
					mMainBall.onDie();
				}
			}
		};

		return contactListener;
	}

	private void breakBrick( String pUserName ) {
		if ( this.mMapRectBrick.containsKey( pUserName ) ) {
			if ( mMapRectBrick.get( pUserName ).getBreakLevel() > 0 ) {
				brickContactPointUpWithEffect( mMapRectBrick.get( pUserName ).getCenterX(),
						mMapRectBrick.get( pUserName ).getCenterY() );

				if ( mMapRectBrick.get( pUserName ).checkBreakMySelf() == true ) {
					mMapRectBrick.get( pUserName ).scaleAlphaByeBye( 0.3f );
				}
			}
		}
	}

	private synchronized void brickContactPointUpWithEffect( float pFromX, float pFromY ) {
		final float pToX = camera.getWidth() * 0.5f;
		final float pToY = resourcesManager.applyResizeFactor( 106f );

		final float pDuration = ( 2f / camera.getHeight() ) * pFromY + 0.1f;

		final Sprite pSprite = new Sprite( pFromX, pFromY, resourcesManager.regionCoin50, vbom );
		pSprite.setPosition( pFromX - pSprite.getWidth() * 0.5f, pFromY - pSprite.getHeight() * 0.5f );
		attachChild( pSprite );

		pSprite.registerEntityModifier( new ParallelEntityModifier(
				new MoveModifier( pDuration, pFromX, pToX, pFromY, pToY ),
				new RotationModifier( pDuration, 0, 360 ),
				new AlphaModifier( pDuration, 1f, 0.8f ),
				new ScaleModifier( pDuration, 1f, 2.0f ) ) {
			@Override
			protected void onModifierFinished( IEntity pItem ) {
				// TODO Auto-generated method stub
				super.onModifierFinished( pItem );
				offerPoint( 1 );
				engine.runOnUpdateThread( new Runnable() {

					@Override
					public void run( ) {
						pSprite.setVisible( false );
						pSprite.detachSelf();

					}
				} );
			}
		} );

	}

	private String getBrickUserName( Fixture x1, Fixture x2 ) {
		String result = "";
		String ptnBrick = "brick\\d+";
		final String strX1 = ( String ) x1.getBody().getUserData();
		final String strX2 = ( String ) x2.getBody().getUserData();
		if ( findString( strX1, ptnBrick ) ) {
			result = strX1;
		} else if ( findString( strX2, ptnBrick ) ) {
			result = strX2;
		}
		return result;
	}

	private boolean isBrickContactWithBall( Fixture x1, Fixture x2 ) {
		boolean result = false;

		String ptnX1 = "brick\\d+";
		String ptnX2 = "mainBall";

		result = isContactWith( x1, x2, ptnX1, ptnX2 );

		return result;
	}

	private boolean isBrickContactWithPrickle( Fixture x1, Fixture x2 ) {
		boolean result = false;

		String ptnX1 = "prickle\\d+";
		String ptnX2 = "mainBall";
		Log.d( "contact", ( String ) x1.getBody().getUserData() + "," + ( String ) x2.getBody().getUserData() );

		result = isContactWith( x1, x2, ptnX1, ptnX2 );

		return result;
	}

	private boolean isContactWith( Fixture x1, Fixture x2, String ptnX1, String ptnX2 ) {
		boolean result = false;
		final String strUserNameX1 = ( String ) x1.getBody().getUserData();
		final String strUserNameX2 = ( String ) x2.getBody().getUserData();

		result = ( findString( strUserNameX1, ptnX1 ) && findString( strUserNameX2, ptnX2 ) )
				|| ( findString( strUserNameX2, ptnX1 ) && findString( strUserNameX1, ptnX2 ) );
		return result;
	}

	private boolean findString( String pInput, String pPattern ) {
		boolean result = false;

		Pattern p = Pattern.compile( pPattern );
		Matcher m = p.matcher( pInput );

		result = m.find();

		return result;
	}

	@Override
	public void attachSprites( ) {
		attachDefaultSprite();
		String pUserNamePrefix = "brick";
		int count = 0;

		for ( JsonDataBrick pBrick : mLevelData.getBricks() ) {
			attachBrick( pUserNamePrefix + String.valueOf( count++ ), pBrick );
		}

		count = 0;

		for ( JsonDataEntity pEntity : mLevelData.getEntities() ) {
			attachEntity( pEntity, count++ );
		}

	}

	private void attachBrick( String pUserName, JsonDataBrick pJsonDataBrick ) {

		if ( pJsonDataBrick.getName().equals( "rect" ) ) {
			attachRectBrick( pUserName, pJsonDataBrick );
		}
	}

	private void attachEntity( JsonDataEntity pJsonDataEntity, int pEntityCount ) {
		if ( pJsonDataEntity.getName().equals( "groundRect" ) ) {
			attachGroundRectEntity( pJsonDataEntity );
		} else if ( pJsonDataEntity.getName().equals( "halo" ) ) {
			attachHaloOfBall( pJsonDataEntity );
		} else if ( pJsonDataEntity.getName().equals( "mainBall" ) ) {
			attachBall( pJsonDataEntity );
		} else if ( pJsonDataEntity.getName().equals( "prickleV108x35" ) ) {
			attachPrickle( pJsonDataEntity, pEntityCount );
		}
	}

	private void attachPrickle( JsonDataEntity pJsonDataEntity, int pEntityCount ) {
		final float pX = resourcesManager.applyResizeFactor( pJsonDataEntity.getX() );
		final float pY = resourcesManager.applyResizeFactor( pJsonDataEntity.getY() );
		//		final float pWidth = resourcesManager.applyResizeFactor( pJsonDataEntity.getWidth() );
		//		final float pHeight = resourcesManager.applyResizeFactor( pJsonDataEntity.getHeight() );
		ITextureRegion pRegion = null;
		final String pUserNamePrefix = "prickle";

		if ( pJsonDataEntity.getName().equals( "prickleV108x35" ) ) {
			pRegion = resourcesManager.regionPrickleV108x35;
		}

		PrickleSprite tPrickle = new PrickleSprite( pX, pY, pRegion, vbom, this );
		tPrickle.setColor( pJsonDataEntity.getColor() );
		tPrickle.createPhysics( pUserNamePrefix + String.valueOf( pEntityCount ),
				pJsonDataEntity.getBodyType(), pJsonDataEntity.getFixtureDef(),
				pJsonDataEntity.getBodyShape() );

		attachChild( tPrickle );
	}

	private void attachGroundRectEntity( JsonDataEntity pJsonDataEntity ) {
		//Strongly don't use position.layout of brick.
		final float pX = resourcesManager.applyResizeFactor( pJsonDataEntity.getX() );
		final float pY = resourcesManager.applyResizeFactor( pJsonDataEntity.getY() );
		final float pWidth = resourcesManager.applyResizeFactor( pJsonDataEntity.getWidth() );
		final float pHeight = resourcesManager.applyResizeFactor( pJsonDataEntity.getHeight() );

		RectangleGround tRect = new RectangleGround( pX, pY, pWidth, pHeight, this );
		tRect.createPhysics( pJsonDataEntity.getName(), pJsonDataEntity.getBodyType(),
				pJsonDataEntity.getFixtureDef() );
		tRect.setColor( pJsonDataEntity.getColor() );
		attachChild( tRect );
	}

	private void attachRectBrick( String pUserName, JsonDataBrick pJsonDataBrick ) {
		//Strongly don't use position.layout of brick.
		final float pX = resourcesManager.applyResizeFactor( pJsonDataBrick.getX() );
		final float pY = resourcesManager.applyResizeFactor( pJsonDataBrick.getY() );

		final float pWidth = resourcesManager.applyResizeFactor( pJsonDataBrick.getWidth() );
		final float pHeight = resourcesManager.applyResizeFactor( pJsonDataBrick.getHeight() );

		final int pBreakLevel = pJsonDataBrick.getBreakLevel();

		RectangleBrick tRect = new RectangleBrick( pX, pY, pWidth, pHeight, this )
				.setBreakLevel( pBreakLevel );

		tRect.createPhysics( pUserName, pJsonDataBrick.getBodyType(),
				pJsonDataBrick.getFixtureDef() );
		tRect.setColor( pJsonDataBrick.getColor() );

		attachChild( tRect );

		this.mMapRectBrick.put( pUserName, tRect );

	}

	private void attachHaloOfBall( JsonDataEntity pJsonDataEntity ) {
		final float pX = resourcesManager.applyResizeFactor( pJsonDataEntity.getX() );
		final float pY = resourcesManager.applyResizeFactor( pJsonDataEntity.getY() );

		if ( mHaloJsonDataEntity == null ) {
			mHaloJsonDataEntity = pJsonDataEntity;
		}

		this.mHaloOfBallSprite = new HaloOfBallSprite( pX, pY, resourcesManager.regionCicle225, vbom )
				.setSceneGame( this );
		//		this.mHaloOfBallSprite.setColor( pJsonDataEntity.getColor() );
		this.mHaloOfBallSprite.setDefaultColor( pJsonDataEntity.getColor() );
		attachChild( mHaloOfBallSprite );
		mHaloOfBallSprite.setVisible( false );
	}

	private void applyHaloCreateEffect( float pCenterX, float pCenterY, float pDuration ) {
		mHaloOfBallSprite.setCenterPosition( pCenterX, pCenterY );
		mHaloOfBallSprite.registerEntityModifier( new AlphaModifier( pDuration, 0.5f, 1f ) );
	}

	public HaloOfBallSprite getHaloOfBall( ) {
		return this.mHaloOfBallSprite;
	}

	public BallSprite getMainBall( ) {
		return this.mMainBall;
	}

	private void createBall( ) {
		final JsonDataEntity ballJson = mMainBallJsonDataEntity;
		final float DURATION = 2f;

		mMainBall.registerEntityModifier( new MoveModifier( DURATION, mMainBallJsonDataEntity.getX(),
				mMainBallJsonDataEntity.getX(), 0, mMainBallJsonDataEntity.getY(), EaseBackInOut
						.getInstance() ) {
			@Override
			protected void onModifierFinished( IEntity pItem ) {
				mMainBall.createPhysics( "mainBall", ballJson.getBodyType(),
						ballJson.getFixtureDef() );
				setBallDie( false );
				mHaloOfBallSprite.setVisible( true );
				super.onModifierFinished( pItem );
			}
		} );
	}

	private void attachBall( JsonDataEntity pJsonDataEntity ) {
		//Strongly don't use position.layout of brick.
		float pX = 0;
		float pY = 0;
		if ( this.mHaloOfBallSprite == null ) {
			pX = resourcesManager.applyResizeFactor( pJsonDataEntity.getX() );
			pY = resourcesManager.applyResizeFactor( pJsonDataEntity.getY() );
		} else {
			pX = this.mHaloOfBallSprite.getCenterX() - resourcesManager.regionCicle80.getWidth() * 0.5f;
			pY = this.mHaloOfBallSprite.getCenterY() - resourcesManager.regionCicle80.getHeight() * 0.5f;
		}

		if ( mMainBallJsonDataEntity == null ) {
			mMainBallJsonDataEntity = pJsonDataEntity;
			mMainBallJsonDataEntity.setPosition( pX, pY );
		}

		final Color pInnerColor = pJsonDataEntity.getColor();

		this.mMainBall = new BallSprite( pX, pY, resourcesManager.regionCicle80, appColor.BALL,
				resourcesManager.regionCicle38, pInnerColor, this ) {

			@Override
			public void onDie( ) {
				setBallDie( true );
				this.safeByeBye();
				mHaloOfBallSprite.explode( 0.3f );
				engine.registerUpdateHandler( new TimerHandler( 0.7f, new ITimerCallback() {

					@Override
					public void onTimePassed( TimerHandler pTimerHandler ) {
						engine.unregisterUpdateHandler( pTimerHandler );
						checkGameHeartReply();
					}
				} ) );
			}
		};

		attachChild( mMainBall );

		createBall();

		this.BALL_TOUCH_AREA = mMainBall.getWidth() * 3f;
	}

	private void setBallDie( boolean pDie ) {
		this.isBallDieFlag = pDie;
	}

	private boolean isBallDie( ) {
		return isBallDieFlag;
	}

	private void checkGameHeartReply( ) {
		if ( setHeart( false ) ) {
			attachBall( mMainBallJsonDataEntity );
		} else {
			//Game over
		}
	}

	private boolean setHeart( boolean pAdd ) {
		boolean result = false;
		final float pLeftOffset = resourcesManager.applyResizeFactor( 25f );
		final float pXOffset = resourcesManager.applyResizeFactor( 10f );
		float pX = 0;
		final float pY = resourcesManager.applyResizeFactor( 73f );

		if ( pAdd == true ) {
			pX = this.mGameHearts.get( this.mGameHearts.size() - 1 ).getX()
					+ resourcesManager.regionGameHeart.getWidth() + pXOffset;
			this.mGameHearts.add( new Sprite( pX, pY, resourcesManager.regionGameHeart, vbom ) );

			attachChild( mGameHearts.get( this.mGameHearts.size() - 1 ) );

			result = true;
		} else {
			if ( this.mGameHearts.isEmpty() ) {
				result = false;
			} else {
				result = true;
				final Sprite pSprite = this.mGameHearts.get( this.mGameHearts.size() - 1 );
				pSprite.setIgnoreUpdate( true );
				pSprite.setVisible( false );
				pSprite.detachSelf();
				this.mGameHearts.remove( this.mGameHearts.size() - 1 );
			}

		}
		return result;
	}

	private void attachDefaultSprite( ) {
		attachGameHeart();
		attachGameLevel();
		attachPointText();
	}

	private void attachPointText( ) {
		float pX = 0f;
		final float pY = resourcesManager.applyResizeFactor( 47.8f );
		mGamePointText = new Text( pX, pY, resourcesManager.getFontPoint(),
				"1234567", vbom );
		mGamePointText.setText( String.valueOf( getGamePoint() ) );
		pX = ( camera.getWidth() - mGamePointText.getWidth() ) / 2.0f;
		mGamePointText.setPosition( pX, pY );

		attachChild( mGamePointText );
	}

	private synchronized boolean offerPoint( int pGamePoint ) {
		return mPointQueue.offer( pGamePoint );
	}

	private synchronized void upDateGamePointText( ) {

		if ( mPointQueue.peek() != null ) {

			final float pY = resourcesManager.applyResizeFactor( 47.8f );

			while ( mPointQueue.peek() != null ) {
				this.mGamePoint += mPointQueue.poll();
			}
			mGamePointText.setText( String.valueOf( mGamePoint ) );
			float pX = ( camera.getWidth() - mGamePointText.getWidth() ) / 2.0f;
			mGamePointText.setPosition( pX, pY );
		}

	}

	private synchronized int getGamePoint( ) {
		return this.mGamePoint;
	}

	private void attachGameLevel( ) {
		final float pRightOffset = resourcesManager.applyResizeFactor( 25f );
		final float pXOffset = resourcesManager.applyResizeFactor( 10f );
		final float pRectWidth = 80f;
		final float pRectHeight = pRectWidth;

		float pX = camera.getWidth() - ( pRightOffset + pRectWidth );
		final float pY = resourcesManager.applyResizeFactor( 47.8f );

		final int pLevel_1 = ( this.mGameLevel + 1 ) % 10;
		final int pLevel_10 = ( int ) ( this.mGameLevel + 1 ) / 10;

		Rectangle tRectBox1 = new Rectangle( pX, pY, pRectWidth, pRectHeight, vbom );
		tRectBox1.setColor( appColor.LEVEL_BOX );
		Text pText1 = new Text( 0f, 0f, resourcesManager.getFontDefault(), String.valueOf( pLevel_1 ), vbom );
		pText1.setPosition( 0.5f * ( pRectWidth - pText1.getWidth() ),
				0.5f * ( pRectHeight - pText1.getHeight() ) );
		tRectBox1.attachChild( pText1 );
		attachChild( tRectBox1 );

		if ( this.mGameLevel > 9 ) {
			Rectangle tRectBox2 = new Rectangle( pX - ( pRectWidth + pXOffset ), pY, pRectWidth, pRectHeight,
					vbom );
			tRectBox2.setColor( appColor.LEVEL_BOX );
			Text pText2 = new Text( 0f, 0f, resourcesManager.getFontDefault(), String.valueOf( pLevel_10 ),
					vbom );
			pText2.setPosition( 0.5f * ( pRectWidth - pText2.getWidth() ),
					0.5f * ( pRectHeight - pText2.getHeight() ) );
			tRectBox2.attachChild( pText2 );
			attachChild( tRectBox2 );
		}
	}

	private void attachGameHeart( ) {
		final float pLeftOffset = resourcesManager.applyResizeFactor( 25f );
		final float pXOffset = resourcesManager.applyResizeFactor( 10f );
		float pX = pLeftOffset;
		final float pY = resourcesManager.applyResizeFactor( 73f );

		for ( int i = 0 ; i < this.mGameHeartCount ; i++ ) {
			this.mGameHearts.add( new Sprite( pX, pY, resourcesManager.regionGameHeart, vbom ) );
			pX = pX + resourcesManager.regionGameHeart.getWidth() + pXOffset;
		}
		for ( Sprite pHeart : this.mGameHearts ) {
			attachChild( pHeart );
			pHeart.registerEntityModifier( new ScaleModifier( 0.5f, 0.1f, 1f ) );
		}
	}

	private void moveOutIAreaShapeEntityAfterPlayButtonTouch( ) {//Just move it to screen out.
		float diffLeftRight = 0;
		float pToX = 0;
		boolean goneDirection = false; // f : left, t:right
		for ( IAreaShape pIAreaShape : mIAreaShapeDetachOnPlay ) {
			diffLeftRight = pIAreaShape.getX()
					- ( camera.getWidth() - pIAreaShape.getX() - pIAreaShape.getWidth() );

			if ( diffLeftRight == 0 ) {//To where ? 
				if ( goneDirection == true ) {//To Left
					goneDirection = false;
					pToX = -1.1f * pIAreaShape.getWidth();
				} else {//To right
					goneDirection = true;
					pToX = 1.1f * camera.getWidth();
				}
			} else if ( diffLeftRight > 0 ) {//To right
				goneDirection = true;
				pToX = 1.1f * camera.getWidth();
			} else {//To Left
				goneDirection = false;
				pToX = -1.1f * pIAreaShape.getWidth();
			}

			pIAreaShape.registerEntityModifier( new MoveXModifier( 0.38f, pIAreaShape.getX(), pToX ) );
		}
	}

	private void destroyIAreaShapeEntityAfterPlayButtonTouch( ) {
		for ( IAreaShape pIAreaShape : mIAreaShapeDetachOnPlay ) {
			pIAreaShape.setIgnoreUpdate( true );
			pIAreaShape.setVisible( false );
			detachChild( pIAreaShape );
		}
		this.engine.runOnUpdateThread( new Runnable() {

			@Override
			public void run( ) {
				mIAreaShapeDetachOnPlay.clear();
			}
		} );

	}

	private void attachText( JsonDataEntity pEntity ) {
		//		Text pText = new Text( 0, 0, resourcesManager.getFontDefault(), pEntity.getText(), vbom );
		//
		//		final PointF entityPosition = getJsonDataEntityPosition( pEntity.getPosition(), pText );
		//		final float pX = entityPosition.x;
		//		final float pY = entityPosition.y;
		//
		//		pText.setPosition( pX, pY );
		//		attachChild( pText );
		//
		//		if ( pEntity.isDetachOnPlay() ) {
		//			this.mIAreaShapeDetachOnPlay.add( pText );
		//		}
		//
		//		if ( pEntity.getName().equals( "fingerText" ) ) {
		//			pText.registerEntityModifier( new LoopEntityModifier( new SequenceEntityModifier(
		//					new RotationModifier( 0.5f, 0, 7 ), new RotationModifier( 0.5f, 7, 0 ) ) ) );
		//		}
	}

	private void attachFingerSprite( JsonDataEntity pEntity ) {
		//		Sprite fingerSprite = new Sprite( 0f, 0f, resourcesManager.regionFinger, vbom );
		//
		//		final PointF entityPosition = getJsonDataEntityPosition( pEntity.getPosition(), fingerSprite );
		//		final float pX = entityPosition.x;
		//		final float pY = entityPosition.y;
		//
		//		fingerSprite.setPosition( pX, pY );
		//		attachChild( fingerSprite );
		//
		//		if ( pEntity.isDetachOnPlay() ) {
		//			this.mIAreaShapeDetachOnPlay.add( fingerSprite );
		//		}
		//
		//		if ( pEntity.getName().equals( "fingerIndicatorSprite" ) ) {
		//			fingerSprite.registerEntityModifier( new LoopEntityModifier( new SequenceEntityModifier(
		//					new MoveYModifier( 0.5f, pY, pY - resourcesManager.applyResizeFactor( 24f ) ),
		//					new MoveYModifier( 0.5f, pY - resourcesManager.applyResizeFactor( 24f ), pY ) ) ) );
		//		}
	}

	@Override
	public void onBackKeyPressed( ) {
		sceneManager.createSceneHome();

	}

	@Override
	public SceneType getSceneType( ) {
		return SceneType.SCENE_GAME;
	}

	@Override
	public void disposeScene( ) {
		// TODO Auto-generated method stub

	}

	public enum GameStatus {
		GAME_PREPLAY, GAME_PLAYING, GAME_END
	}

	public PhysicsWorld getPhysicsWorld( ) {
		return this.physicsWorld;
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
	public ResourcesManager getResourcesManager( ) {
		return this.resourcesManager;
	}

	@Override
	public SceneManager getSceneManager( ) {
		return this.sceneManager;
	}

	@Override
	public boolean onSceneTouchEvent( Scene pScene, TouchEvent pSceneTouchEvent ) {
		MotionEvent pMotionEvent = pSceneTouchEvent.getMotionEvent();
		int pointerId = pSceneTouchEvent.getPointerID();

		switch ( pSceneTouchEvent.getAction() ) {
			case TouchEvent.ACTION_DOWN :
				if ( isBallDie() == false ) {
					if ( this.mMainBall.setSelect( mMainBall.isOnTouchArea( pSceneTouchEvent.getX(),
							pSceneTouchEvent.getY(), this.BALL_TOUCH_AREA ) ) ) { // is selected

						resourcesManager.getVibrator().vibrate( 30 );

						this.mMainBall.decelerateBall();

						if ( mVelocityTracker == null ) {
							mVelocityTracker = VelocityTracker.obtain();
						} else {
							mVelocityTracker.clear();
							mVelocityTracker.addMovement( pMotionEvent );
						}
					}
				}

			case TouchEvent.ACTION_MOVE :
				if ( isBallDie() == false ) {
					if ( this.mMainBall.isSelected() ) {

						mVelocityTracker.addMovement( pMotionEvent );
						mVelocityTracker.computeCurrentVelocity( VELOCITY_TRACKER_CAL_UNIT );

						//					Log.d( "",
						//							String.valueOf( VelocityTrackerCompat.getXVelocity( mVelocityTracker, pointerId ) )
						//									+ ","
						//									+ String.valueOf( VelocityTrackerCompat.getYVelocity( mVelocityTracker,
						//											pointerId ) ) );

						mMainBall.setFingerThrowVelocity(
								VelocityTrackerCompat.getXVelocity( mVelocityTracker, pointerId ),
								VelocityTrackerCompat.getYVelocity( mVelocityTracker, pointerId ) );

						testThowBall();
					} else {
						if ( mVelocityTracker != null ) {
							mVelocityTracker.clear();
						}
					}
				}

				break;
			default :
				if ( isBallDie() == false ) {
					if ( mMainBall.isSelected() == true ) {
						this.mMainBall.setSelect( false );
						testThowBall();
					}
				}

				if ( mVelocityTracker != null ) {
					mVelocityTracker.recycle();
					mVelocityTracker = null;
				}
				break;
		}

		return false;
	}

	public void testThowBall( ) {
		//		this.mMainBall.getBody().setLinearVelocity( this.mMainBall.getFingerThrowVelocity() );
		this.mMainBall.throwBall();

	}

}