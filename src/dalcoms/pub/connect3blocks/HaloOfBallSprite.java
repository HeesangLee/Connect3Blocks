package dalcoms.pub.connect3blocks;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.ParallelEntityModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.color.Color;

import android.util.Log;

import dalcoms.pub.connect3blocks.scene.SceneGame;

public class HaloOfBallSprite extends Sprite {

	private SceneGame mSceneGame;
	private boolean flagSelected = false;
	private Color mDefaultColor;

	public HaloOfBallSprite( float pX, float pY, ITextureRegion pTextureRegion,
			VertexBufferObjectManager pVertexBufferObjectManager ) {
		super( pX, pY, pTextureRegion, pVertexBufferObjectManager );
	}

	public HaloOfBallSprite setSceneGame( SceneGame pSceneGame ) {
		this.mSceneGame = pSceneGame;
		return this;
	}

	private void catchBall( ) {
		mSceneGame.getMainBall().getBody().setLinearVelocity( 0f, 0f );
	}

	public boolean isSelected( ) {
		return flagSelected;
	}

	public void setFlagSelected( boolean pFlagSelected ) {
		this.flagSelected = pFlagSelected;
		float alpha = pFlagSelected ? 0.8f : 0.2f;
		this.setAlpha( alpha );
	}

	public void setSelectedAlpha( boolean isSelected ) {
		if ( isSelected ) {
			this.setAlpha( 0.85f );
		} else {
			this.setAlpha( 0.2f );
		}
	}

	public void setDefaultColor( Color pColor ) {
		mDefaultColor = pColor;
		this.setColor( pColor );
	}

	public Color getDefaultColor( ) {
		return mDefaultColor;
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

	public void explode( float pDuration ) {
		this.setColor( AppColor.getInstance().BALL );

		this.registerEntityModifier( new ParallelEntityModifier(
				new ScaleModifier( pDuration, 0.6f, 3f ),
				new AlphaModifier( pDuration, 0.8f, 0f ) ) {
			@Override
			protected void onModifierFinished( IEntity pItem ) {
				// TODO Auto-generated method stub
				super.onModifierFinished( pItem );
				resetMe();
			}
		} );
	}

	private void resetMe( ) {
		mSceneGame.getEngine().runOnUpdateThread( new Runnable() {

			@Override
			public void run( ) {
				setVisible( false );
				setColor( getDefaultColor() );
				setScale( 1f );
			}
		} );
	}
}
