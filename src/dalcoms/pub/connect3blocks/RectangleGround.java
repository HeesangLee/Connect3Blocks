package dalcoms.pub.connect3blocks;

import dalcoms.pub.connect3blocks.scene.SceneGame;

public class RectangleGround extends RectanglePhysics {

	public RectangleGround( float pX, float pY, float pWidth, float pHeight, SceneGame pSceneGame ) {
		super( pX, pY, pWidth, pHeight, pSceneGame );
	}

	public RectangleGround( float pWidth, float pHeight, SceneGame pSceneGame ) {
		super( 0f, 0f, pWidth, pHeight, pSceneGame );
	}

	@Override
	public void onUpdateCheck( ) {
		// TODO Auto-generated method stub
		
	}

}
