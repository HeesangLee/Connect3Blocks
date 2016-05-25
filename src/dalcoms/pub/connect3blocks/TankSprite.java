package dalcoms.pub.connect3blocks;

import java.util.ArrayList;

import lib.dalcoms.andengineheesanglib.utils.HsMath;

import org.andengine.engine.Engine;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import dalcoms.pub.connect3blocks.scene.SceneManager;

public class TankSprite extends Sprite {
	private ArrayList<Rectangle> energyBar;
	private final int ENERGY_BAR_NUM = 5;
	private final int ENERGY_FULL = ENERGY_BAR_NUM;
	private final float ENERGY_CHARGING_TIME_SECOND = 0.35f;
	private int energyLevel = 0;
	private boolean flagTouchEventForFire = true;

	AppColor appColor;
	Engine engine;

	public TankSprite( float pX, float pY, ITextureRegion pTextureRegion,
			VertexBufferObjectManager pVertexBufferObjectManager ) {
		super( pX, pY, pTextureRegion, pVertexBufferObjectManager );

		appColor = AppColor.getInstance();
		this.engine = SceneManager.getInstance().getCurrentScene().getEngine();

		this.engine.runOnUpdateThread( new Runnable() {
			@Override
			public void run( ) {
				attachEnergeBar();
			}
		} );
		registerUpdateHandlerForEnergyCharger( this.ENERGY_CHARGING_TIME_SECOND );
	}

	private void registerUpdateHandlerForEnergyCharger( float energyChargingTimeSec ) {
		this.engine.registerUpdateHandler( new TimerHandler( energyChargingTimeSec, true,
				new ITimerCallback() {

					@Override
					public void onTimePassed( TimerHandler pTimerHandler ) {
						if ( getEnergyLevel() < ENERGY_FULL ) {
							setEnergyLevel( getEnergyLevel() + 1 );
						}
					}
				} ) );

	}

	private void attachEnergeBar( ) {
		createEnergyBar();
		for ( Rectangle pRect : this.energyBar ) {
			attachChild( pRect );
		}
		setEnergyLevel( ENERGY_BAR_NUM );
	}

	public void setEnergyLevel( int pEnergy ) {
		this.energyLevel = pEnergy;
		this.setEnergyBarColor( pEnergy );
	}

	public int getEnergyLevel( ) {
		return this.energyLevel;
	}

	public boolean isEnergyFull( ) {
		boolean result = false;
		if ( getEnergyLevel() >= this.ENERGY_FULL ) {
			result = true;
		}
		return result;
	}

	private void setEnergyBarColor( int pEnergy ) {
		for ( int barIndex = 0 ; barIndex < this.ENERGY_BAR_NUM ; barIndex++ ) {
			if ( barIndex < pEnergy ) {
				this.energyBar.get( barIndex ).setColor( appColor.ENERGY_CHRGE );
			} else {
				this.energyBar.get( barIndex ).setColor( appColor.ENERGY_DISCHARGE );
			}
		}

	}

	private void createEnergyBar( ) {
		final float energyBarWidthRatio = 0.073613f;
		final float energyBarHeightRatio = 0.221694f;
		final float energyBarYposRatio = 0.540822f;
		final float offsetRatio = 0.217462f;

		final float energyBarWidth = this.getWidth() * energyBarWidthRatio;
		final float energyBarHeight = this.getHeight() * energyBarHeightRatio;
		final float xOffset = this.getWidth() * offsetRatio;
		final float posY = this.getHeight() * energyBarYposRatio;
		//		final float[] posXs = SceneManager.getInstance().getCurrentScene().appComm
		//				.getDistributedCenterOrgPosition( energyBarWidth, ENERGY_BAR_NUM, this.getWidth() - 2
		//						* xOffset, xOffset );
		HsMath hsMath = new HsMath();
		final float[] posXs = hsMath.getDistributedCenterOrgPosition( energyBarWidth, ENERGY_BAR_NUM,
				this.getWidth() - 2 * xOffset, xOffset );

		this.energyBar = new ArrayList<Rectangle>();

		for ( float posX : posXs ) {
			this.energyBar.add( new Rectangle( posX, posY, energyBarWidth, energyBarHeight, this
					.getVertexBufferObjectManager() ) );
		}
	}

	@Override
	public boolean onAreaTouched( final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX,
			final float pTouchAreaLocalY ) {

		if ( pSceneTouchEvent.isActionDown() ) {
			onTouchActionDown();
		} else if ( pSceneTouchEvent.isActionUp() ) {
			onTouchActionUp();
		}

		return super.onAreaTouched( pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY );
	}

	public void onTouchActionDown( ) {
		if ( getFlagTouchEventForFire() == true ) {
			this.fire();
		}
	}

	public void onTouchActionUp( ) {
		if ( getFlagTouchEventForFire() == false ) {
			this.fire();
		}
	}

	public void fire( ) {
		//		setEnergyLevel(0); // Test purpose : Determine if doing this action on this class or game scene.
	}

	public TankSprite setTouchEventForFire( boolean pAction ) {//pAction : (1) true : ActionDown, (2) false : ActionUp
		this.flagTouchEventForFire = pAction;

		return this;
	}

	public boolean getFlagTouchEventForFire( ) {
		return this.flagTouchEventForFire;
	}

}
