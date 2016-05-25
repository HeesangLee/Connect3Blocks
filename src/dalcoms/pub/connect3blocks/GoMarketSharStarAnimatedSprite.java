package dalcoms.pub.connect3blocks;

import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

public class GoMarketSharStarAnimatedSprite extends AnimatedSprite{
	
	private Gotype goButtonType;
	Activity activity;
	
	private String shareSubject;
	private String shareText;
	private String appId;
	
	public GoMarketSharStarAnimatedSprite(float pX, float pY,
			ITiledTextureRegion pTiledTextureRegion,
			VertexBufferObjectManager pVertexBufferObjectManager) {
		super(pX, pY, pTiledTextureRegion, pVertexBufferObjectManager);
		// TODO Auto-generated constructor stub
	}
	
	public GoMarketSharStarAnimatedSprite activityOn(Activity pActivity){
		activity = pActivity;
		return this;
	}
	
	public GoMarketSharStarAnimatedSprite goType(Gotype pGoButtonType){
		this.goButtonType = pGoButtonType;
		
		return this;
	}
	
	public GoMarketSharStarAnimatedSprite shareInformation(String pSubject, String pText){
		this.shareSubject = pSubject;
		this.shareText = pText;
		
		return this;
	}
	
	public GoMarketSharStarAnimatedSprite shareInformation(String pSubject, String pText, String pAppId){
		this.shareSubject = pSubject;
		this.shareText = pText;
		this.appId = pAppId;
		
		return this;
	}
	
	public GoMarketSharStarAnimatedSprite appId(String pAppId){
		this.appId = pAppId;
		
		return this;
	}
	
	@Override
	public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
			float pTouchAreaLocalX, float pTouchAreaLocalY){
		if(pSceneTouchEvent.isActionDown()){
			this.setScale(1.8f);
		}else{
			this.setScale(1f);
			if(pSceneTouchEvent.isActionUp()){
				doActionUp(getGoType());
			}
		}
		return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX,
				pTouchAreaLocalY);
		
	}
	
	public void setShareInformation(String pSubject, String pText){
		this.shareSubject = pSubject;
		this.shareText = pText;
	}
	
	public void setShareInformation(String pSubject, String pText, String pAppId){
		this.shareSubject = pSubject;
		this.shareText = pText;
		this.appId = pAppId;
	}
	
	public void setAppId(String pAppId){
		this.appId = pAppId;
	}
	
	private void doActionUp(Gotype pGoType){
		switch(pGoType){
		case GO_MARKET: 
			goMarket();
			break;
		case GO_SHARE:
			goShare();
			break;
		case GO_STAR:
			goStar();
			break;
		default:
			goStar();
			break;
		}
	}
	
	private void goStar() {
		try{
			activity.startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("market://details?id="+this.appId)));
			
		}catch(android.content.ActivityNotFoundException e){
			activity.startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("http://play.google.com/store/apps/details?id="+this.appId)));
		}
	}

	private void goShare() {
		try{
			Intent sendIntent = new Intent();
			sendIntent.setAction(Intent.ACTION_SEND);
			sendIntent.putExtra(Intent.EXTRA_SUBJECT, this.shareSubject);
			sendIntent.putExtra(Intent.EXTRA_TEXT, this.shareText);
			sendIntent.setType("text/plain");
			activity.startActivity(Intent.createChooser(sendIntent, "Sharing"));
		}catch(android.content.ActivityNotFoundException e){
			activity.startActivity(
					new Intent(Intent.ACTION_VIEW,Uri.parse("http://play.google.com/store/apps/details?id"+this.appId)));
		}
		
	}

	private void goMarket() {
		try{
			activity.startActivity(
					new Intent(Intent.ACTION_VIEW,
							Uri.parse("market://search/?q=pub:Dalcoms")));
		}catch(android.content.ActivityNotFoundException e){
			activity.startActivity(
					new Intent(Intent.ACTION_VIEW,
							Uri.parse("https://play.google.com/store/search?q=dalcoms")));
		}
		
	}

	private void setGoType(int pGoType) {
		switch(pGoType){
		case 0 : 
			goButtonType = Gotype.GO_MARKET;
			break;
		case 1:
			goButtonType = Gotype.GO_SHARE;
			break;
		case 2:
			goButtonType = Gotype.GO_STAR;
			break;
		default:
			goButtonType = Gotype.GO_STAR;
			break;
		}
	}
	
	public Gotype getGoType(){
		return goButtonType;
	}

}
