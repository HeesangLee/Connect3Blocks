package dalcoms.pub.connect3blocks;

import lib.dalcoms.andengineheesanglib.utils.HsMath;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.color.Color;

import android.os.AsyncTask;

public class RectangleButton extends Rectangle {

	HsMath hsMath;
	VertexBufferObjectManager vbom;
	Font mTextFont;
	Text mButtonText;
	Rectangle mTouchEffectRect;
	String mStrButtonText;
	Color pButtonTextColor = new Color( 0, 0, 0 );

	public RectangleButton( float pX, float pY, float pWidth, float pHeight,
			VertexBufferObjectManager pVertexBufferObjectManager,
			Font pTextFont, String pButtonText ) {
		super( pX, pY, pWidth, pHeight, pVertexBufferObjectManager );
		// TODO Auto-generated constructor stub

		vbom = pVertexBufferObjectManager;
		mTextFont = pTextFont;
		hsMath = new HsMath();
		mStrButtonText = new String( pButtonText );

		new AttachInnerComponentsTask().execute();
	}

	private void allignLevelTextToCenter( ) {
		final float pX = hsMath.getAlignCenterFloat( this.mButtonText.getWidth(), this.getWidth() );
		final float pY = hsMath.getAlignCenterFloat( this.mButtonText.getHeight(), this.getHeight() );
		this.mButtonText.setPosition( pX, pY );
	}

	private void createButtonText( ) {
		this.mButtonText = new Text( 0, 0, this.mTextFont, this.mStrButtonText, this.vbom );
	}

	private void attachButtonTextOn( ) {
		this.mButtonText.setColor( pButtonTextColor );
		allignLevelTextToCenter();
		attachChild( this.mButtonText );
	}

	public void setTextColor( Color pColor ) {
		pButtonTextColor = pColor;
		if ( this.mButtonText != null ) {
			this.mButtonText.setColor( pColor );
		}
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
	}

	private class AttachInnerComponentsTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground( Void... params ) {
			createButtonText();
			createTouchEffect();
			return null;
		}

		@Override
		protected void onPostExecute( Void result ) {
			attachButtonTextOn();
			attachTouchEffect();
			super.onPostExecute( result );
		}
	}
}
