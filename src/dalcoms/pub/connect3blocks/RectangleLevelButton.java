package dalcoms.pub.connect3blocks;

import java.util.ArrayList;

import lib.dalcoms.andengineheesanglib.utils.HsMath;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.color.Color;

import android.os.AsyncTask;

public class RectangleLevelButton extends Rectangle {
	HsMath hsMath;
	VertexBufferObjectManager vbom;
	Font mTextFont;
	ITextureRegion mRegionIconNext;
	EnumRectLevelBtnProfile mButtonProfile = EnumRectLevelBtnProfile.RECTBTN_BLANK;//blank is default
	EnumSelPage mSelPage = EnumSelPage.NEXT;

	final int LEVEL_GAUGE_COUNT = 5;

	Text mLevelText;
	ArrayList<Rectangle> mLevelGaugeRects;
	Sprite mIconNext;

	private boolean mIsVisibleLevelText, mIsVisibleLevelGauge, mIsVisibleIconSelPage = false;
	private boolean mIsFocused = false;

	Rectangle mTouchEffectRect;

	private int mLevelNum = 0;

	Color mColorBackground, mColorLevelText, mColorGageOff, mColorGageOn;

	public RectangleLevelButton( float pX, float pY, float pWidth, float pHeight,
			VertexBufferObjectManager pVertexBufferObjectManager,
			Font pTextFont,
			ITextureRegion pRegionIconNext ) {
		super( pX, pY, pWidth, pHeight, pVertexBufferObjectManager );

		vbom = pVertexBufferObjectManager;
		mTextFont = pTextFont;
		mRegionIconNext = pRegionIconNext;
		hsMath = new HsMath();

		new AttachInnerComponentsTask().execute();
	}

	/**
	 * 버튼 속성 정의 : default = blank. 정의된 속성의 버튼으로 구성됨
	 * 
	 * @param pButtonProfile
	 *            :
	 * @return self
	 */
	public RectangleLevelButton setButtonProfile( EnumRectLevelBtnProfile pButtonProfile ) {
		if ( this.mButtonProfile != pButtonProfile ) {
			this.mButtonProfile = pButtonProfile;
			upDateAsGivenProfile( mButtonProfile );
		}

		return this;
	}

	public EnumRectLevelBtnProfile getButtonProfile( ) {
		return this.mButtonProfile;
	}

	public int getLevelNum( ) {
		return this.mLevelNum;
	}

	public void setLevelNum( int pLevelNum ) {
		this.mLevelNum = pLevelNum;
		if ( this.mLevelText != null ) {
			this.mLevelText.setText( String.valueOf( getLevelNum() ) );
			allignLevelTextToCenter();
		}
	}

	public boolean isFocused( ) {
		return this.mIsFocused;
	}

	public void setFocuse( boolean pIsFocused ) {
		if ( isFocused() != pIsFocused ) {
			this.mIsFocused = pIsFocused;
			
			if ( getButtonProfile().equals( EnumRectLevelBtnProfile.RECTBTN_LEVELVIEW_SEL ) ) {
				setLevelSelColor( isFocused() );
				updateColorAsProfile();
			}
		}
	}

	private void upDateAsGivenProfile( EnumRectLevelBtnProfile pButtonProfile ) {
		switch ( pButtonProfile ) {
			case RECTBTN_LEVELVIEW_ONLY :
				updateProfileAsLevelViewOnly();
				break;
			case RECTBTN_LEVELVIEW_SEL :
				updateProfileAsLevelViewSel();
				break;
			case RECTBTN_SELPAGE :
				updateProfileAsSelPage();
				break;
			case RECTBTN_BLANK :
				updateProfileAsBlank();
				break;
			default :
				break;
		}
	}

	private void setVisibleLevelText( boolean pVisible ) {
		this.mIsVisibleLevelText = pVisible;
	}

	private void setVisibleLevelGauge( boolean pVisible ) {
		this.mIsVisibleLevelGauge = pVisible;
	}

	private void setVisibleIconSelPage( boolean pVisible ) {
		this.mIsVisibleIconSelPage = pVisible;
	}

	private boolean isVisibleLevelText( ) {
		return this.mIsVisibleLevelText;
	}

	private boolean isVisibleLevelGauge( ) {
		return this.mIsVisibleLevelGauge;
	}

	private boolean isVisibleIconSelPage( ) {
		return this.mIsVisibleIconSelPage;
	}

	private void setBackgroundColor( Color pColor ) {
		this.mColorBackground = pColor;
	}

	private Color getBackgroundColor( ) {
		return this.mColorBackground == null ? new Color( 1f, 1f, 1f ) : this.mColorBackground;
	}

	private void setLevelTextColor( Color pColor ) {
		this.mColorLevelText = pColor;
	}

	private Color getLevelTextColor( ) {
		return this.mColorLevelText == null ? new Color( 1f, 1f, 1f ) : this.mColorLevelText;
	}

	private void setGageOffColor( Color pColor ) {
		this.mColorGageOff = pColor;
	}

	private Color getGageOffColor( ) {
		return this.mColorGageOff == null ? new Color( 1f, 1f, 1f ) : this.mColorGageOff;
	}

	private void setGageOnColor( Color pColor ) {
		this.mColorGageOn = pColor;
	}

	private Color getGageOnColor( ) {
		return this.mColorGageOn == null ? new Color( 1f, 1f, 1f ) : this.mColorGageOn;
	}

	private void setLevelSelColor( boolean pFocused ) {
		if ( pFocused ) {
			this.setBackgroundColor( MyColor.LevelSelFocused.getBackgraound() );
			this.setLevelTextColor( MyColor.LevelSelFocused.getLevelText() );
			this.setGageOffColor( MyColor.LevelSelFocused.getGageOff() );
			this.setGageOnColor( MyColor.LevelSelFocused.getGageOn() );
		} else {
			this.setBackgroundColor( MyColor.LevelNormal.getBackgraound() );
			this.setLevelTextColor( MyColor.LevelNormal.getLevelText() );
			this.setGageOffColor( MyColor.LevelNormal.getGageOff() );
			this.setGageOnColor( MyColor.LevelNormal.getGageOn() );
		}
	}

	private void setLevelViewOnlyColor( ) {
		this.setBackgroundColor( MyColor.LevelViewOnly.getBackgraound() );
		this.setLevelTextColor( MyColor.LevelViewOnly.getLevelText() );
	}

	private void setBlankPageSelColor( ) {
		this.setBackgroundColor( MyColor.BlankPageSel.getBackgraound() );
	}

	private void updateColorAsProfile( ) {
		this.setColor( getBackgroundColor() );
		if ( mLevelText != null ) {
			mLevelText.setColor( getLevelTextColor() );
		}
		if ( mLevelGaugeRects != null ) {
			//--> update by level
		}
	}

	private void updateVisibleAsProfile( ) {

		if ( mLevelText != null ) {
			mLevelText.setVisible( isVisibleLevelText() );
		}
		if ( mIconNext != null ) {
			mIconNext.setVisible( isVisibleIconSelPage() );
		}
		if ( mLevelGaugeRects != null ) {
			for ( Rectangle rectGauge : mLevelGaugeRects ) {
				rectGauge.setVisible( isVisibleLevelGauge() );
			}
		}
	}

	private void updateProfileAsLevelViewOnly( ) {
		this.setVisibleLevelText( true );
		this.setVisibleIconSelPage( false );
		this.setVisibleLevelGauge( false );

		setLevelViewOnlyColor();

		updateVisibleAsProfile();
		updateColorAsProfile();
	}

	private void updateProfileAsLevelViewSel( ) {
		this.setVisibleLevelText( true );
		this.setVisibleIconSelPage( false );
		this.setVisibleLevelGauge( true );

		setLevelSelColor( isFocused() );

		updateVisibleAsProfile();
		updateColorAsProfile();
	}

	private void updateProfileAsBlank( ) {
		this.setVisibleLevelText( false );
		this.setVisibleIconSelPage( false );
		this.setVisibleLevelGauge( false );

		setBlankPageSelColor();

		updateVisibleAsProfile();
		updateColorAsProfile();
	}

	private void updateProfileAsSelPage( ) {
		this.setVisibleLevelText( false );
		this.setVisibleIconSelPage( true );
		this.setVisibleLevelGauge( false );

		setBlankPageSelColor();

		updateVisibleAsProfile();
		updateColorAsProfile();
	}

	public RectangleLevelButton setSelPage( EnumSelPage pSelPage ) {
		this.mSelPage = pSelPage;
		if ( this.mIconNext != null ) {
			this.mIconNext.setFlippedHorizontal( pSelPage == EnumSelPage.NEXT ? false : true );
		}

		return this;
	}

	public EnumSelPage getSelPageProperty( ) {
		return this.mSelPage;
	}

	public boolean isForNextPage( ) {
		return ( this.mSelPage == EnumSelPage.NEXT ? true : false );
	}

	private void createLevelText( ) {
		this.mLevelText = new Text( 0, 0, this.mTextFont, "123", this.vbom );
	}

	private void attachLevelTextOn( ) {
		this.mLevelText.setText( String.valueOf( getLevelNum() ) );
		this.mLevelText.setColor( AppColor.getInstance().GUIDE_TXT );//temp 
		allignLevelTextToCenter();
		attachChild( this.mLevelText );
	}

	private void allignLevelTextToCenter( ) {
		final float pX = hsMath.getAlignCenterFloat( this.mLevelText.getWidth(), this.getWidth() );
		final float pY = hsMath.getAlignCenterFloat( this.mLevelText.getHeight(), this.getHeight() );
		this.mLevelText.setPosition( pX, pY );
	}

	private void createLevelGaugeRects( ) {

		final float OFFSET = ResourcesManager.getInstance().applyResizeFactor( 17f );
		final float RECT_SIZE = ResourcesManager.getInstance().applyResizeFactor( 20f );

		final float[] pXs = hsMath.getDistributedCenterOrgPosition( RECT_SIZE, LEVEL_GAUGE_COUNT,
				this.getWidth() - OFFSET * 2f, OFFSET );
		final float pY = getHeight() - OFFSET - RECT_SIZE;

		this.mLevelGaugeRects = new ArrayList<Rectangle>();

		for ( int i = 0 ; i < LEVEL_GAUGE_COUNT ; i++ ) {
			mLevelGaugeRects.add( new Rectangle( pXs[i], pY, RECT_SIZE, RECT_SIZE, vbom ) );
		}

	}

	private void attachLevelGaugeRectsOn( ) {
		for ( Rectangle rect : this.mLevelGaugeRects ) {
			rect.setColor( new Color( 0, 0, 0 ) );//temp
			attachChild( rect );
		}
	}

	/**
	 * To display Preview Icon Just Flip this next Icon.
	 */
	private void createIconNext( ) {
		final float pX = hsMath.getAlignCenterFloat( mRegionIconNext.getWidth(), getWidth() );
		final float pY = hsMath.getAlignCenterFloat( mRegionIconNext.getHeight(), getHeight() );
		this.mIconNext = new Sprite( pX, pY, mRegionIconNext, vbom );

	}

	private void attachIconNext( ) {
		attachChild( this.mIconNext );
	}

	private void createTouchEffect( ) {
		final float pWidth = getWidth();
		final float pHeight = getHeight();
		final float pY = 0;
		final float pX = ( getWidth() - pWidth ) / 2f;
		mTouchEffectRect = new Rectangle( pX, pY, pWidth, pHeight, vbom );
		mTouchEffectRect.setColor( 0f, 0f, 0f, 0.35f );
		mTouchEffectRect.setVisible( false );
	}

	private void attachTouchEffect( ) {
		attachChild( mTouchEffectRect );
	}

	private class AttachInnerComponentsTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground( Void... params ) {
			createLevelText();
			createLevelGaugeRects();
			createIconNext();
			createTouchEffect();
			return null;
		}

		@Override
		protected void onPostExecute( Void result ) {
			attachLevelTextOn();
			attachLevelGaugeRectsOn();
			attachIconNext();
			attachTouchEffect();

			upDateAsGivenProfile( getButtonProfile() );
			super.onPostExecute( result );
		}
	}

	@Override
	public boolean onAreaTouched( TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY ) {
		if ( pSceneTouchEvent.isActionDown() ) {
			mTouchEffectRect.setVisible( true );
			mTouchEffectRect.registerEntityModifier( new ScaleModifier( 0.25f, 0.3f, 1f ) {
				@Override
				protected void onModifierFinished( IEntity pItem ) {
					super.onModifierFinished( pItem );
					mTouchEffectRect.setVisible( false );
				}
			} );
		} else {
			if ( pSceneTouchEvent.isActionUp() ) {
				mTouchEffectRect.setVisible( false );
				onButtonClick();
			}
		}

		return super.onAreaTouched( pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY );
	}

	public void onButtonClick( ) {
		switch ( getButtonProfile() ) {
			case RECTBTN_LEVELVIEW_ONLY :
				onLevelViewOnlyButtonClick();
				break;
			case RECTBTN_LEVELVIEW_SEL :
				onLevelViewSelButtonClick();
				break;
			case RECTBTN_SELPAGE :
				if ( getSelPageProperty().equals( EnumSelPage.NEXT ) ) {
					onSelPageNextButtonClick();
				} else {
					onSelPagePreButtonClick();
				}
				break;
			case RECTBTN_BLANK :
				onBlankButtonClick();
				break;
			default :
				break;
		}
	}

	public void onLevelViewOnlyButtonClick( ) {

	}

	public void onLevelViewSelButtonClick( ) {

	}

	public void onSelPageNextButtonClick( ) {

	}

	public void onSelPagePreButtonClick( ) {
	}

	public void onBlankButtonClick( ) {
	}

	public enum EnumRectLevelBtnProfile {
		RECTBTN_LEVELVIEW_ONLY, //display level number only
		RECTBTN_LEVELVIEW_SEL, //display all and touch and select
		RECTBTN_SELPAGE, //Select page preview or next
		RECTBTN_BLANK//blank
	}

	public enum EnumSelPage {
		NEXT,
		PREVIEW
	}

	//MyColor --> to get btn colors
	public static class MyColor {
		private MyColor( ) {
		}

		public static class LevelSelFocused {
			private LevelSelFocused( ) {
			}

			public static Color getBackgraound( ) {
				final float pRed = 44f / 255f;
				final float pGreen = 62f / 255f;
				final float pBlue = 80f / 255f;

				return new Color( pRed, pGreen, pBlue );
			}

			public static Color getLevelText( ) {
				final float pRed = 255f / 255f;
				final float pGreen = 255f / 255f;
				final float pBlue = 255f / 255f;

				return new Color( pRed, pGreen, pBlue );
			}

			public static Color getGageOff( ) {
				final float pRed = 204f / 255f;
				final float pGreen = 204f / 255f;
				final float pBlue = 204f / 255f;

				return new Color( pRed, pGreen, pBlue );
			}

			public static Color getGageOn( ) {
				final float pRed = 255f / 255f;
				final float pGreen = 255f / 255f;
				final float pBlue = 255f / 255f;

				return new Color( pRed, pGreen, pBlue );
			}
		}

		public static class LevelNormal {
			private LevelNormal( ) {
			}

			public static Color getBackgraound( ) {
				final float pRed = 255f / 255f;
				final float pGreen = 255f / 255f;
				final float pBlue = 255f / 255f;

				return new Color( pRed, pGreen, pBlue );
			}

			public static Color getLevelText( ) {
				final float pRed = 87f / 255f;
				final float pGreen = 87f / 255f;
				final float pBlue = 87f / 255f;

				return new Color( pRed, pGreen, pBlue );
			}

			public static Color getGageOff( ) {
				final float pRed = 204f / 255f;
				final float pGreen = 204f / 255f;
				final float pBlue = 204f / 255f;

				return new Color( pRed, pGreen, pBlue );
			}

			public static Color getGageOn( ) {
				final float pRed = 44f / 255f;
				final float pGreen = 62f / 255f;
				final float pBlue = 80f / 255f;

				return new Color( pRed, pGreen, pBlue );
			}
		}

		public static class LevelViewOnly {
			private LevelViewOnly( ) {
			}

			public static Color getBackgraound( ) {
				final float pRed = 242f / 255f;
				final float pGreen = 244f / 255f;
				final float pBlue = 243f / 255f;

				return new Color( pRed, pGreen, pBlue );
			}

			public static Color getLevelText( ) {
				final float pRed = 171f / 255f;
				final float pGreen = 172f / 255f;
				final float pBlue = 172f / 255f;

				return new Color( pRed, pGreen, pBlue );
			}

			public static Color getGageOff( ) {
				final float pRed = 204f / 255f;
				final float pGreen = 204f / 255f;
				final float pBlue = 204f / 255f;

				return new Color( pRed, pGreen, pBlue );
			}

			public static Color getGageOn( ) {
				final float pRed = 255f / 255f;
				final float pGreen = 255f / 255f;
				final float pBlue = 255f / 255f;

				return new Color( pRed, pGreen, pBlue );
			}
		}

		public static class BlankPageSel {
			private BlankPageSel( ) {
			}

			public static Color getBackgraound( ) {
				final float pRed = 242f / 255f;
				final float pGreen = 244f / 255f;
				final float pBlue = 243f / 255f;

				return new Color( pRed, pGreen, pBlue );
			}

			public static Color getLevelText( ) {
				final float pRed = 255f / 255f;
				final float pGreen = 255f / 255f;
				final float pBlue = 255f / 255f;

				return new Color( pRed, pGreen, pBlue );
			}

			public static Color getGageOff( ) {
				final float pRed = 204f / 255f;
				final float pGreen = 204f / 255f;
				final float pBlue = 204f / 255f;

				return new Color( pRed, pGreen, pBlue );
			}

			public static Color getGageOn( ) {
				final float pRed = 255f / 255f;
				final float pGreen = 255f / 255f;
				final float pBlue = 255f / 255f;

				return new Color( pRed, pGreen, pBlue );
			}
		}
	}

}
