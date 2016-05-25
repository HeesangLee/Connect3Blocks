package dalcoms.pub.connect3blocks;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import dalcoms.pub.connect3blocks.scene.SceneManager;

public class ThisAppCommon {
	ResourcesManager resourcesManager = ResourcesManager.getInstance();
	Activity activity = resourcesManager.getActivity();
	
	public void backKeyPressed(float exitRatio){
		if(SceneManager.getInstance().isForTStore()==false){
			if(Math.random()<exitRatio){
				this.popUpExtiMessageDlg();
			}else{
				this.popUpAdMsgDlg();
			}
		}else{
			this.popUpExtiMessageDlg();
		}
	}
	
	private void popUpExtiMessageDlg(){
		AlertDialog.Builder dlgBackPressed = new AlertDialog.Builder(activity);
		dlgBackPressed.setMessage(R.string.say_good_bye)
		.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				resourcesManager.getActivity().onDestroy();
			}
		})
		.setNegativeButton("No", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		})
		.setTitle(activity.getResources().getString(R.string.dlg_title_exit))
		.setIcon(R.drawable.ic_launcher)
		.show();
	}
	
	private void popUpAdMsgDlg(){
		AlertDialog.Builder dlgBackPressed = new AlertDialog.Builder(activity);
		dlgBackPressed.setMessage(R.string.advertize_myself)
		.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
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
		})
		.setNegativeButton("No", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
				popUpExtiMessageDlg();
			}
		})
		.setTitle(activity.getResources().getString(R.string.dlg_title_free_app))
		.setIcon(R.drawable.ic_launcher)
		.show();
	}
	
	public int getAlignCenterInt(int in_,int base_){
		return (base_-in_)>>1;
	}
	
	public float getAlignCenterFloat(float in_,float base_){
		return (base_-in_)/2;
	}
	
	public float getAlignBottomYFloat(float in_h,float base_y,float base_h){
		return base_y+base_h-in_h;
	}

	public float[] getDistributedCenterOrgPosition(
			float objectLength,
			int objectNum,
			float relatedObjectLength,
			float offset){
		float[] retOrgPosition = new float[objectNum];
		
		for(int i=0;i<objectNum;i++){
			retOrgPosition[i] = offset +((relatedObjectLength-objectLength)/(objectNum-1))*(float)i;
		}
		
		return retOrgPosition;
	}
}