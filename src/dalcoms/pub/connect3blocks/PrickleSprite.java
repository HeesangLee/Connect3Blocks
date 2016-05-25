package dalcoms.pub.connect3blocks;

import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import dalcoms.pub.connect3blocks.scene.SceneGame;

public class PrickleSprite extends PhysicsSprite{

	public PrickleSprite( float pX, float pY, ITextureRegion pTextureRegion,
			VertexBufferObjectManager pVertexBufferObjectManager, SceneGame pSceneGame ) {
		super( pX, pY, pTextureRegion, pVertexBufferObjectManager, pSceneGame );
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onUpdateCheck( ) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void clearFlagVars( ) {
		// TODO Auto-generated method stub
		
	}

}
