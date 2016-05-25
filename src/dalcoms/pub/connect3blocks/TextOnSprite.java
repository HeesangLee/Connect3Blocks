package dalcoms.pub.connect3blocks;

import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.color.Color;

public class TextOnSprite extends Sprite{
	Text mText;
	VertexBufferObjectManager mVbom;

	public TextOnSprite(
			float pX,
			float pY,
			ITextureRegion pTextureRegion,
			VertexBufferObjectManager vbom,
			Text pText){
		
		super(pX, pY, pTextureRegion, vbom);
		mVbom = vbom;
		mText = pText;
		
		attachTextOn();
	}

	private void attachTextOn() {
		attachChild(mText);
	}
	public TextOnSprite setTextColor(Color pColor){
		mText.setColor(pColor);
		return this;
	}
	
	public void setCenterText(String pStr){
		final float paddingRatio = 0.05f;
		
		final float textMaxHeight = this.getHeight()-2*this.getHeight()*paddingRatio;
		final float textMaxWidth = this.getWidth()-(2*this.getWidth()*paddingRatio);
		
		float textHeight=1f;
		float textWidth=1f;
		float textDownScale=1f;
		float hRatio=1f;
		float wRatio=1f;
		
		mText.setText(pStr);
		
		textHeight = mText.getHeight();
		textWidth = mText.getWidth();
		
		hRatio = textMaxHeight/textHeight;
		wRatio = textMaxWidth/textWidth;
		
		if((textHeight>textMaxHeight)&&(textWidth>textMaxWidth)){
			if(hRatio>wRatio){
				textDownScale = wRatio;
			}else{
				textDownScale = hRatio;
			}
		}else if((textHeight>textMaxHeight)&&(textWidth<textMaxWidth)){
			textDownScale = hRatio;
		}else if((textHeight<textMaxHeight)&&(textWidth>textMaxWidth)){
			textDownScale = wRatio;
		}
		
		mText.setScale(textDownScale);
	
		mText.setPosition((this.getHeight()-mText.getWidth()/2), 
				(this.getHeight()-mText.getHeight())/2);
		
	}
}