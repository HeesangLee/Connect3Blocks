package dalcoms.pub.connect3blocks.scene;

import java.util.ArrayList;
import java.util.Random;

import lib.dalcoms.andengineheesanglib.buttons.TiledSpriteOnRectangleToggleButton;
import lib.dalcoms.andengineheesanglib.utils.HsMath;
import lib.dalcoms.andengineheesanglib.utils.HsUtils;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.RotationModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.color.Color;
import org.andengine.util.modifier.ease.EaseBackOut;
import org.andengine.util.modifier.ease.EaseCircularIn;
import org.andengine.util.modifier.ease.IEaseFunction;

import com.google.android.gms.playlog.internal.LogEvent;

import android.app.Activity;
import android.content.Entity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import dalcoms.pub.connect3blocks.GoMarketSharStarAnimatedSprite;
import dalcoms.pub.connect3blocks.Gotype;
import dalcoms.pub.connect3blocks.R;
import dalcoms.pub.connect3blocks.RectangleButton;
import dalcoms.pub.connect3blocks.RectangleLevelButton;
import dalcoms.pub.connect3blocks.RectangleLevelButton.EnumRectLevelBtnProfile;
import dalcoms.pub.connect3blocks.RectangleLevelButton.EnumSelPage;
import dalcoms.pub.connect3blocks.ResourcesManager;

public class SceneHome extends BaseScene {
	final String TAG = this.getClass().getSimpleName();
	final static boolean LOG_EN = true;

	HsMath hsMath = new HsMath();

	boolean flag_interstitialAdOn = false;

	Rectangle mRoundImgBgRect;
	Text mTextRoundKind;
	Text mTextRoundPoint;
	Text mTextRoundNum;
	Sprite mSpriteArrowRight;
	Sprite mSpriteArrowLeft;
	Sprite mSpriteLockSymbol;
	TiledSpriteOnRectangleToggleButton mSpriteSoundOnOff;

	private int mFocusedRoundNum;
	private int mDefaultRoundCount;

	final int ROUND_DP_SIZE_X = 6;
	final int ROUND_DP_SIZE_Y = 4;

	final float BUTTON_HEIGHT = resourcesManager.applyResizeFactor( 174f );
	final float ROUNDBTN_POS_Y = resourcesManager.applyResizeFactor( 368.316f );
	final float BUTTON_GAP = resourcesManager.applyResizeFactor( 5f );

	ArrayList<RectangleLevelButton> mRoundBtnArray;

	private final float mCameraWidth = camera.getWidth();

	private RoundPageInfo mRoundPageInfo;

	@Override
	public void createScene( ) {
		this.setBackground( new Background( this.appColor.APP_BACKGROUND ) );
		// prepareNotificationTest();

		initScene();

		this.engine.runOnUpdateThread( new Runnable() {
			@Override
			public void run( ) {
				attachSprites();
			}
		} );
	}

	private void initScene( ) {
		setFocusedRoundNum( getLastRoundNum() );
		mDefaultRoundCount = sceneManager.getDefaultRounFileNames().size();

		mRoundPageInfo = new RoundPageInfo( ROUND_DP_SIZE_X, ROUND_DP_SIZE_Y, getDefaultRoundCount() );
	}

	private int getDefaultRoundCount( ) {
		return this.mDefaultRoundCount;
	}

	@Override
	public void attachSprites( ) {
		attachTitileText();
		attachSoundOnOffButton();
		attachBackgoundOfButtons();
		attachRoundButtons();
		attachCompanyText();
		attachLevelText();
		attachPlayButton();
		attachMarketShareStarButtons();
	}

	private int getLastRoundNum( ) {
		HsUtils.logV( LOG_EN, "lastRoundNum",
				"lastRoundNum=" + String.valueOf( sceneManager.getLastRoundInfoIndex() ) );
		return sceneManager.getLastRoundInfoIndex();
	}

	private int getFocusedRoundNum( ) {
		HsUtils.logV( LOG_EN, "focusedRoundNum",
				"focusedRoundNum=" + String.valueOf( this.mFocusedRoundNum ) );
		return this.mFocusedRoundNum;
	}

	private int setFocusedRoundNum( int pRoundNum ) {
		this.mFocusedRoundNum = pRoundNum > 0 ? pRoundNum : 1;
		return this.mFocusedRoundNum;
	}

	private void attachBackgoundOfButtons( ) {
		final float pHeight = ( this.ROUND_DP_SIZE_Y + 2 ) * this.BUTTON_HEIGHT
				+ ( this.ROUND_DP_SIZE_Y + 2 + 1 ) * this.BUTTON_GAP;
		Rectangle bg = new Rectangle( 0f, this.ROUNDBTN_POS_Y - BUTTON_GAP, this.mCameraWidth, pHeight, vbom );
		bg.setColor( appColor.BTN_BG );
		attachChild( bg );
	}

	private void attachRoundButtons( ) {
		this.mRoundBtnArray = new ArrayList<RectangleLevelButton>();
		float pX = 0;
		float pY = 0;

		for ( int y = 0 ; y < this.ROUND_DP_SIZE_Y ; y++ ) {
			for ( int x = 0 ; x < this.ROUND_DP_SIZE_X ; x++ ) {
				pX = ( x + 1 ) * this.BUTTON_GAP + x * this.BUTTON_HEIGHT;
				pY = this.ROUNDBTN_POS_Y + y * ( this.BUTTON_GAP + this.BUTTON_HEIGHT );
				RectangleLevelButton temp = new RectangleLevelButton( pX, pY, this.BUTTON_HEIGHT,
						this.BUTTON_HEIGHT, vbom,
						resourcesManager.getFontButton(), resourcesManager.regionIconRight ) {
					@Override
					public void onLevelViewOnlyButtonClick( ) {
					}

					@Override
					public void onLevelViewSelButtonClick( ) {
						//just select and focus
						//setFocuseRoundNum --> UpdateFocus of all round buttons.
						focusSelectedButton( this );
					}

					@Override
					public void onSelPageNextButtonClick( ) {
						setFocusedRoundNum( mRoundPageInfo.getRoundPage( getFocusedRoundNum() )
								.getLastRound() + 1 );
						setRoundButtons( getFocusedRoundNum() );
						effectRoundButtons( -1, 45f );
					}

					@Override
					public void onSelPagePreButtonClick( ) {
						setFocusedRoundNum( mRoundPageInfo.getRoundPage( getFocusedRoundNum() )
								.getFirstRound() - 1 );
						setRoundButtons( getFocusedRoundNum() );
						effectRoundButtons( -1, -45f );
					}

				};
				attachChild( temp );
				registerTouchArea( temp );
				this.mRoundBtnArray.add( temp );
			}
		}

		setRoundButtons( getFocusedRoundNum() );

	}

	private void focusSelectedButton( RectangleLevelButton pRoundBtn ) {
		if ( !pRoundBtn.isFocused() ) {
			setFocusedRoundNum( pRoundBtn.getLevelNum() );
			getFocusedRoundNum();
			for ( RectangleLevelButton roundBtn : this.mRoundBtnArray ) {
				roundBtn.setFocuse( roundBtn.equals( pRoundBtn ) );
			}
		}
	}

	private void setRoundButtons( int pFocusedRoundNum ) {
		RoundPage roundPage = this.mRoundPageInfo.getRoundPage( pFocusedRoundNum );

		int roundNum = roundPage.getFirstRound();
		int startIndex = 0;
		int endIndex = this.ROUND_DP_SIZE_X * this.ROUND_DP_SIZE_Y;

		if ( roundPage.hasPrePage ) {
			this.mRoundBtnArray.get( 0 ).setButtonProfile( EnumRectLevelBtnProfile.RECTBTN_SELPAGE )
					.setSelPage( EnumSelPage.PREVIEW );

			startIndex = 1;
		}
		if ( roundPage.hasNextPage ) {
			this.mRoundBtnArray.get( --endIndex ).setButtonProfile( EnumRectLevelBtnProfile.RECTBTN_SELPAGE )
					.setSelPage( EnumSelPage.NEXT );
		}

		for ( int i = startIndex ; i < endIndex ; i++ ) {
			if ( roundNum <= roundPage.getLastRound() ) {
				this.mRoundBtnArray.get( i ).setButtonProfile( EnumRectLevelBtnProfile.RECTBTN_LEVELVIEW_SEL );
				this.mRoundBtnArray.get( i ).setLevelNum( roundNum );

				this.mRoundBtnArray.get( i ).setFocuse( roundNum == getFocusedRoundNum() );
				roundNum++;

			} else {
				this.mRoundBtnArray.get( i ).setButtonProfile( EnumRectLevelBtnProfile.RECTBTN_BLANK );
			}

		}
	}

	/**
	 * 
	 * @param btnIndex
	 *            : -1=all, else = index of mRoundBtnArray
	 */
	private void effectRoundButtons( int btnIndex ) {
		effectRoundButtons( btnIndex, 45f );
	}

	private void effectRoundButtons( int btnIndex, float rotateToAngle ) {
		final float duration = 0.2f;
		if ( btnIndex < 0 ) {//all
			for ( RectangleLevelButton roundbtn : this.mRoundBtnArray ) {
				roundbtn.registerEntityModifier(
						new SequenceEntityModifier(
								new RotationModifier( duration, 0, rotateToAngle, EaseCircularIn
										.getInstance() ),
								new RotationModifier( duration, rotateToAngle, 0, EaseCircularIn
										.getInstance() ) ) );
			}
		} else {//index
			this.mRoundBtnArray.get( btnIndex )
					.registerEntityModifier(
							new SequenceEntityModifier(
									new RotationModifier( duration, 0, rotateToAngle, EaseCircularIn
											.getInstance() ),
									new RotationModifier( duration, rotateToAngle, 0, EaseCircularIn
											.getInstance() ) ) );
		}
	}

	private void attachPlayButton( ) {
		RectangleLevelButton lastRoundBtn = this.mRoundBtnArray.get( this.mRoundBtnArray.size() - 1 );
		final float pY = lastRoundBtn.getY() + lastRoundBtn.getHeight() + this.BUTTON_GAP;
		RectangleButton pPlayButton = new RectangleButton( this.BUTTON_GAP, pY, this.mCameraWidth
				- this.BUTTON_GAP * 2, this.BUTTON_HEIGHT, vbom, resourcesManager.getFontButton(),
				"PLAY" );
		pPlayButton.setColor( appColor.APP_THEME );
		pPlayButton.setTextColor( appColor.WHITE );
		attachChild( pPlayButton );
		registerTouchArea( pPlayButton );
	}

	private void attachSoundOnOffButton( ) {
		final float pWidth = resourcesManager.applyResizeFactor( 174f );
		final float pHeight = pWidth;
		final float pX = camera.getWidth() - pWidth;
		final float pY = 0f;
		//Read sound On/Off state configuration from SharedPreference Before creating Sound OnOff toggle button.
		//Default = Sound ON , false = on, true = off;
		boolean pButtonStatus = false;

		mSpriteSoundOnOff = new TiledSpriteOnRectangleToggleButton( pX, pY,
				pWidth, pHeight, vbom,
				resourcesManager.regionSoundOnOff, pButtonStatus ) {
			@Override
			public void onButtonToggled( ) {
				super.onButtonToggled();
				//				Log.v( "homeSoundOnOff", String.valueOf( getButtonStatus() ) );
			}
		};
		mSpriteSoundOnOff.setEffectColor( new Color( 44f / 255f, 62f / 255f, 80f / 255f, 128f / 255f ) );

		attachChild( mSpriteSoundOnOff );
		registerTouchArea( mSpriteSoundOnOff );
	}

	private void attachTitileText( ) {
		final float pY = resourcesManager.applyResizeFactor( 151.294f );
		Text pTitleText = new Text( 0, 0, resourcesManager.getFontDefault(),
				activity.getString( R.string.app_name ), vbom );
		pTitleText.setPosition( appComm.getAlignCenterFloat( pTitleText.getWidth(), camera.getWidth() ), pY );
		attachChild( pTitleText );
		pTitleText.setColor( appColor.FONT_DEFAULT );

		pTitleText
				.registerEntityModifier( new ScaleModifier( 2.5f, 0.1f, 1f, 1f, 1f, EaseBackOut.getInstance() ) );
	}

	private void attachCompanyText( ) {
		final float pY = resourcesManager.applyResizeFactor( 1500f );
		Text pText = new Text( 0, 0, resourcesManager.getFontDefault(),
				activity.getString( R.string.company_name ), vbom );
		pText.setPosition( appComm.getAlignCenterFloat( pText.getWidth(), camera.getWidth() ), pY );
		attachChild( pText );
		pText.setColor( appColor.COMPANY_TEXT_HOMESCENE );
		pText
				.registerEntityModifier( new ScaleModifier( 2.5f, 0.1f, 0.8f, 1f, 0.8f, EaseBackOut
						.getInstance() ) );
	}

	private void attachLevelText( ) {
		final float buttonWidth = mRoundBtnArray.get( 0 ).getWidth();
		final float refY = mRoundBtnArray.get( 0 ).getY() - resourcesManager.applyResizeFactor( 15f );
		float pTextX, pTextY;
		int pTextIndex = 1;

		final ArrayList<Text> mTextArray = new ArrayList<Text>() {
			{
				add( new Text( 0, 0, resourcesManager.getFontButtonSmall(), "L", vbom ) );
				add( new Text( 0, 0, resourcesManager.getFontButtonSmall(), "E", vbom ) );
				add( new Text( 0, 0, resourcesManager.getFontButtonSmall(), "V", vbom ) );
				add( new Text( 0, 0, resourcesManager.getFontButtonSmall(), "E", vbom ) );
				add( new Text( 0, 0, resourcesManager.getFontButtonSmall(), "L", vbom ) );
			}
		};

		Sprite spriteIconList = new Sprite( 0, 0, resourcesManager.regionIconList, vbom );
		spriteIconList.setPosition( hsMath.getAlignCenterFloat( spriteIconList.getWidth(), buttonWidth
				+ mRoundBtnArray.get( 0 ).getX() ), refY - spriteIconList.getHeight() );
		attachChild( spriteIconList );

		for ( Text pText : mTextArray ) {
			pTextX = hsMath.getAlignCenterFloat( pText.getWidth(), buttonWidth )
					+ mRoundBtnArray.get( pTextIndex++ ).getX();
			pTextY = refY - pText.getHeight();
			pText.setPosition( pTextX, pTextY );
			pText.setColor( appColor.GRAY_127 );
			attachChild( pText );
		}
	}

	private void attachMarketShareStarButtons( ) {
		RectangleLevelButton lastRoundBtn = this.mRoundBtnArray.get( this.mRoundBtnArray.size() - 1 );
		final float pY = lastRoundBtn.getY() + lastRoundBtn.getHeight() + this.BUTTON_GAP * 2
				+ this.BUTTON_HEIGHT;
		final float[] pX = {
				mRoundBtnArray.get( 0 ).getX(),
				mRoundBtnArray.get( 2 ).getX(),
				mRoundBtnArray.get( 4 ).getX() };
		final float pButtonWidth = pX[1] - pX[0] - this.BUTTON_GAP;

		RectangleButton pMarketButton = new RectangleButton( pX[0], pY, pButtonWidth, this.BUTTON_HEIGHT,
				vbom, resourcesManager.getFontButtonSmall(),
				"more" ) {
			@Override
			public void onButtonClick( ) {
				try {
					activity.startActivity(
							new Intent( Intent.ACTION_VIEW,
									Uri.parse( "market://search/?q=pub:Dalcoms" ) ) );
				} catch ( android.content.ActivityNotFoundException e ) {
					activity.startActivity(
							new Intent( Intent.ACTION_VIEW,
									Uri.parse( "https://play.google.com/store/search?q=dalcoms" ) ) );
				}
			}
		};
		pMarketButton.setColor( appColor.WHITE );
		pMarketButton.setTextColor( new Color( 87f / 255f, 87f / 255f, 87f / 255f ) );
		pMarketButton.attachChild( new Sprite( 0, 0, resourcesManager.regionIconMarket, vbom ) );
		attachChild( pMarketButton );
		registerTouchArea( pMarketButton );

		RectangleButton pShareButton = new RectangleButton( pX[1], pY, pButtonWidth, this.BUTTON_HEIGHT,
				vbom, resourcesManager.getFontButtonSmall(),
				"share" ) {
			@Override
			public void onButtonClick( ) {
				try {
					Intent sendIntent = new Intent();
					sendIntent.setAction( Intent.ACTION_SEND );
					sendIntent.putExtra( Intent.EXTRA_SUBJECT, activity.getString( R.string.share_subject ) );
					sendIntent.putExtra( Intent.EXTRA_TEXT, activity.getString( R.string.share_text ) );
					sendIntent.setType( "text/plain" );
					activity.startActivity( Intent.createChooser( sendIntent, "Sharing" ) );
				} catch ( android.content.ActivityNotFoundException e ) {
					activity.startActivity(
							new Intent( Intent.ACTION_VIEW, Uri
									.parse( "http://play.google.com/store/apps/details?id"
											+ activity.getString( R.string.app_id ) ) ) );
				}
			}
		};
		pShareButton.setColor( appColor.WHITE );
		pShareButton.setTextColor( new Color( 87f / 255f, 87f / 255f, 87f / 255f ) );
		pShareButton.attachChild( new Sprite( 0, 0, resourcesManager.regionIconShare, vbom ) );
		attachChild( pShareButton );
		registerTouchArea( pShareButton );

		RectangleButton pStarButton = new RectangleButton( pX[2], pY, pButtonWidth, this.BUTTON_HEIGHT,
				vbom, resourcesManager.getFontButtonSmall(),
				"star" ) {
			@Override
			public void onButtonClick( ) {
				try {
					activity.startActivity( new Intent( Intent.ACTION_VIEW, Uri.parse( "market://details?id="
							+ activity.getString( R.string.app_id ) ) ) );

				} catch ( android.content.ActivityNotFoundException e ) {
					activity.startActivity( new Intent( Intent.ACTION_VIEW, Uri
							.parse( "http://play.google.com/store/apps/details?id="
									+ activity.getString( R.string.app_id ) ) ) );
				}
			}
		};
		pStarButton.setColor( appColor.WHITE );
		pStarButton.setTextColor( new Color( 87f / 255f, 87f / 255f, 87f / 255f ) );
		pStarButton.attachChild( new Sprite( 0, 0, resourcesManager.regionIconStar, vbom ) );
		attachChild( pStarButton );
		registerTouchArea( pStarButton );
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
	public void onBackKeyPressed( ) {
		Random rand = new Random();
		if ( rand.nextInt( 20 ) < 4 ) {
			if ( flag_interstitialAdOn == false ) {
				flag_interstitialAdOn = true;
				sceneManager.popAdmobInterstitialAd();
			}
		}
		appComm.backKeyPressed( 0.85f );
	}

	@Override
	public SceneType getSceneType( ) {
		return SceneType.SCENE_HOME;
	}

	@Override
	public void disposeScene( ) {
		// TODO Auto-generated method stub

	}

	private class RoundPageInfo {
		private int roundDpX;
		private int roundDpY;
		private int totalRoundNum;

		protected ArrayList<RoundPage> mRoundPageArray;

		public RoundPageInfo( ) {
		}

		public RoundPageInfo( int pRoundDpX, int pRoundDpY, int pTotalRoundNum ) {
			setRoundPageInfo( pRoundDpX, pRoundDpY, pTotalRoundNum );
		}

		public RoundPageInfo setRoundPageInfo( int pRoundDpX, int pRoundDpY, int pTotalRoundNum ) {
			this.setRoundDpX( pRoundDpX );
			this.setRoundDpY( pRoundDpY );
			this.setTotalRoundNum( pTotalRoundNum );

			this.calcRoundPage();

			return this;
		}

		public int getPageNum( int pRoundNum ) {
			int ret = -1;
			for ( RoundPage roundPage : mRoundPageArray ) {
				if ( ( pRoundNum >= roundPage.getFirstRound() ) && ( pRoundNum <= roundPage.getLastRound() ) ) {
					ret = roundPage.getPageNum();
					break;
				}
			}
			return ret;
		}

		public RoundPage getRoundPage( int pRoundNum ) {
			return mRoundPageArray.get( getPageNum( pRoundNum ) );
		}

		private void calcRoundPage( ) {
			final int roundDpX = this.getRoundDpX();
			final int roundDpY = this.getRoundDpY();
			final int roundDpSize = roundDpX * roundDpY;
			final int totalRoundNum = this.getTotalRoundNum();
			int pageNum = 0;

			boolean hasPrePage, hasNextPage;
			int firstRoundNum;
			int lastRoundNum = 0;

			mRoundPageArray = new ArrayList<SceneHome.RoundPage>();

			do {
				hasPrePage = pageNum == 0 ? false : true;
				firstRoundNum = lastRoundNum + 1;
				lastRoundNum += ( pageNum == 0 ? roundDpSize - 1 : roundDpSize - 2 );

				if ( lastRoundNum <= totalRoundNum ) {
					hasNextPage = true;

				} else {
					hasNextPage = false;
					lastRoundNum = totalRoundNum;
				}

				Log.v( "roundInfo",
						"pageNum:" + String.valueOf( pageNum ) +
								"\nprepage:" + String.valueOf( hasPrePage ) +
								"\nnextPage:" + String.valueOf( hasNextPage ) +
								"\n1st:" + String.valueOf( firstRoundNum ) +
								"\nlast:" + String.valueOf( lastRoundNum ) );

				this.mRoundPageArray
						.add( new RoundPage( pageNum, hasPrePage, hasNextPage, firstRoundNum, lastRoundNum ) );

				pageNum++;
			} while ( hasNextPage );

		}

		public int getRoundPageCount( ) {
			return mRoundBtnArray == null ? 0 : mRoundBtnArray.size();
		}

		public int getRoundDpX( ) {
			return roundDpX;
		}

		public void setRoundDpX( int roundDpX ) {
			this.roundDpX = roundDpX;
		}

		public int getRoundDpY( ) {
			return roundDpY;
		}

		public void setRoundDpY( int roundDpY ) {
			this.roundDpY = roundDpY;
		}

		public int getTotalRoundNum( ) {
			return totalRoundNum;
		}

		public void setTotalRoundNum( int totalRoundNum ) {
			this.totalRoundNum = totalRoundNum;
		}

	}

	private class RoundPage {
		private int pageNum;
		private boolean hasPrePage;
		private boolean hasNextPage;
		private int firstRound;
		private int lastRound;

		public RoundPage( ) {
			this( -1, false, false, 0, 0 );
		}

		public RoundPage( int pPageNum, boolean pHasPrePage, boolean pHasNextPage, int pFirstRound,
				int pLastRound ) {
			setRoundInfo( pPageNum, pHasPrePage, pHasNextPage, pFirstRound, pLastRound );
		}

		public RoundPage setRoundInfo( int pPageNum, boolean pHasPrePage, boolean pHasNextPage,
				int pFirstRound,
				int pLastRound ) {
			this.setPageNum( pPageNum );
			this.setHasPrePage( pHasPrePage );
			this.setHasNextPage( pHasNextPage );
			this.setFirstRound( pFirstRound );
			this.setLastRound( pLastRound );

			return this;
		}

		public boolean isHasPrePage( ) {
			return hasPrePage;
		}

		public void setHasPrePage( boolean hasPrePage ) {
			this.hasPrePage = hasPrePage;
		}

		public int getLastRound( ) {
			return lastRound;
		}

		public void setLastRound( int lastRound ) {
			this.lastRound = lastRound;
		}

		public int getFirstRound( ) {
			return firstRound;
		}

		public void setFirstRound( int firstRound ) {
			this.firstRound = firstRound;
		}

		public boolean isHasNextPage( ) {
			return hasNextPage;
		}

		public void setHasNextPage( boolean hasNextPage ) {
			this.hasNextPage = hasNextPage;
		}

		public int getPageNum( ) {
			return pageNum;
		}

		public void setPageNum( int pPageNum ) {
			this.pageNum = pPageNum;
		}
	}

}
