package tech.nicesky.balloonpicker;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;

import androidx.appcompat.widget.AppCompatDrawableManager;
import androidx.core.graphics.drawable.DrawableCompat;

import static android.view.animation.AnimationUtils.loadAnimation;

public class sss {
   public void te(){
        ValueAnimator animator = ValueAnimator.ofFloat(0,1);
       animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
           @Override
           public void onAnimationUpdate(ValueAnimator animation) {
             long tiem =   animation.getCurrentPlayTime();
             if (tiem /animation.getDuration() == 100){
                 Log.w("","");
                 Log.w("","");
             }
           }
       });
       animator.addListener(new Animator.AnimatorListener() {
           @Override
           public void onAnimationStart(Animator animation) {

           }

           @Override
           public void onAnimationEnd(Animator animation) {

           }

           @Override
           public void onAnimationCancel(Animator animation) {

           }

           @Override
           public void onAnimationRepeat(Animator animation) {

           }
       });
       Float s = 12F;

        float a =  s.longValue()^2;
        Math.toDegrees(-1.4656447);

//       Build.VERSION


   }

   public void initAnimation(float... values){
//       sss
   }


    public Bitmap getBitmapFromVectorDrawable(Context context, int drawableId)
    {
        Drawable drawable = AppCompatDrawableManager. get ().getDrawable(context, drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }
        Bitmap bitmap = Bitmap . createBitmap (drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}
