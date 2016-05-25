package dalcoms.pub.connect3blocks;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.extension.physics.box2d.PhysicsWorld;

import android.util.Log;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

import dalcoms.pub.connect3blocks.scene.SceneGame;

public class RectangleBrick extends RectanglePhysics {

	private int mBreakLevel = 1;

	public RectangleBrick( float pX, float pY, float pWidth, float pHeight, SceneGame pSceneGame ) {
		super( pX, pY, pWidth, pHeight, pSceneGame );
	}

	public RectangleBrick( float pWidth, float pHeight, SceneGame pSceneGame ) {
		super( 0, 0, pWidth, pHeight, pSceneGame );
	}

	public RectangleBrick setBreakLevel( int pBreakLevel ) {
		this.mBreakLevel = pBreakLevel;
		return this;
	}

	public int getBreakLevel( ) {
		return this.mBreakLevel;
	}

	@Override
	public void onUpdateCheck( ) {

	}

	public float getCenterX( ) {
		return getX() + getWidth() * 0.5f;
	}

	public float getCenterY( ) {
		return getY() + getHeight() * 0.5f;
	}

	private boolean checkCollisionWithBall( ) {
		boolean result = false;
		if ( this.collidesWith( getGameScene().getMainBall() ) ) {
			result = true;
		}
		return result;
	}

	private boolean checkBreakMySelf( boolean pBreak ) {
		//		if break level reach to zero, byebye myself
		boolean result = false;
		if ( pBreak ) {
			final int pBreakLevel = getBreakLevel() - 1;
			if ( pBreakLevel > 0 ) {
				setBreakLevel( pBreakLevel );
				this.breakMySelf();
			} else {
				result = true;
			}
		}
		return result;
	}

	public boolean checkBreakMySelf( ) {
		//		if break level reach to zero, byebye myself
		boolean result = false;

		final int pBreakLevel = getBreakLevel() - 1;
		if ( pBreakLevel > 0 ) {
			setBreakLevel( pBreakLevel );
			this.breakMySelf();
		} else {
			result = true;
		}
		return result;
	}

	private void breakMySelf( ) {
		if ( this.getWidth() > this.getHeight() ) {
			this.setWidth( this.getWidth() * 0.5f );
		} else {
			this.setHeight( this.getHeight() * 0.5f );
		}

		this.getGameScene().getEngine().registerUpdateHandler( new TimerHandler( 0.05f, new ITimerCallback() {

			@Override
			public void onTimePassed( TimerHandler pTimerHandler ) {
				getGameScene().getEngine().unregisterUpdateHandler( pTimerHandler );
				reCreateBody();
			}
		} ) );
	}

}
