package dalcoms.pub.connect3blocks;

import org.andengine.util.adt.pool.GenericPool;
import org.andengine.util.color.Color;

import dalcoms.pub.connect3blocks.scene.SceneGame;

public class BulletPool extends GenericPool<BulletRectangle> {
	private SceneGame mSceneGame;
	private float mBulletWidth = 0f;
	private float mBulletHeight = 0f;
	private Color mBulletColor = AppColor.getInstance().BULLET;

	public BulletPool( SceneGame pSceneGame, float pBulletWidth, float pBulletHeight, Color pBulletColor ) {
		this.mSceneGame = pSceneGame;
		this.mBulletWidth = pBulletWidth;
		this.mBulletHeight = pBulletHeight;
		this.mBulletColor = pBulletColor;
	}

	public BulletPool( SceneGame pSceneGame, float pBulletWidth, float pBulletHeight ) {
		this( pSceneGame, pBulletWidth, pBulletHeight, AppColor.getInstance().BULLET );
	}

	@Override
	protected BulletRectangle onAllocatePoolItem( ) {
		final BulletRectangle pBulletRectangle = new BulletRectangle( mBulletWidth, mBulletHeight,
				mSceneGame.getVbom(), mSceneGame );
		pBulletRectangle.setColor( mBulletColor );

		return pBulletRectangle;
	}

	@Override
	protected void onHandleRecycleItem( BulletRectangle pItem ) {
		pItem.clearFlagVar();
		pItem.setIgnoreUpdate( true );
		pItem.setVisible( false );
		//		pItem.detachSelf();
	}

	@Override
	protected void onHandleObtainItem( BulletRectangle pItem ) {
		pItem.reset();
	}

}
