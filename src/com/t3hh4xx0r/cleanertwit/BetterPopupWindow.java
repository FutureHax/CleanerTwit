package com.t3hh4xx0r.cleanertwit;

import com.t3hh4xx0r.cleanertwit.MainActivity.RowData;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

/**
 * This class does most of the work of wrapping the {@link PopupWindow} so it's simpler to use.
 * 
 * @author qberticus
 * 
 */
public class BetterPopupWindow {
	protected final View anchor;
	private final PopupWindow window;
	private View root;
	private Drawable background = null;
	private final WindowManager windowManager;
	String message;
	int position;

	/**
	 * Create a BetterPopupWindow
	 * 
	 * @param anchor
	 *            the view that the BetterPopupWindow will be displaying 'from'
	 */
	public BetterPopupWindow(View anchor) {
		this.anchor = anchor;
		this.window = new PopupWindow(anchor.getContext());


		// when a touch even happens outside of the window
		// make the window go away
		this.window.setTouchInterceptor(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_OUTSIDE) {
					BetterPopupWindow.this.window.dismiss();
					return true;
				}
				return false;
			}
		});

		this.windowManager = (WindowManager) this.anchor.getContext().getSystemService(Context.WINDOW_SERVICE);
		onCreate();
	}

	/**
	 * Anything you want to have happen when created. Probably should create a view and setup the event listeners on
	 * child views.
	 */
	protected void onCreate() {}

	/**
	 * In case there is stuff to do right before displaying.
	 */
	protected void onShow() {}

	private void preShow() {
		if(this.root == null) {
			throw new IllegalStateException("setContentView was not called with a view to display.");
		}
		onShow();

		if(this.background == null) {
			this.window.setBackgroundDrawable(new BitmapDrawable());
		} else {
			this.window.setBackgroundDrawable(this.background);
		}

		// if using PopupWindow#setBackgroundDrawable this is the only values of the width and hight that make it work
		// otherwise you need to set the background of the root viewgroup
		// and set the popupwindow background to an empty BitmapDrawable
		this.window.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
		this.window.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
		this.window.setTouchable(true);
		this.window.setFocusable(true);
		this.window.setOutsideTouchable(true);

		this.window.setContentView(this.root);
	}

	public void setBackgroundDrawable(Drawable background) {
		this.background = background;
	}

	/**
	 * Sets the content view. Probably should be called from {@link onCreate}
	 * 
	 * @param root
	 *            the view the popup will display
	 */
	public void setContentView(View root) {
		this.root = root;
		this.window.setContentView(root);
	}

	/**
	 * Will inflate and set the view from a resource id
	 * 
	 * @param layoutResID
	 */
	public void setContentView(int layoutResID) {
		LayoutInflater inflator =
				(LayoutInflater) this.anchor.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.setContentView(inflator.inflate(layoutResID, null));
	}

	/**
	 * If you want to do anything when {@link dismiss} is called
	 * 
	 * @param listener
	 */
	public void setOnDismissListener(PopupWindow.OnDismissListener listener) {
		this.window.setOnDismissListener(listener);
	}

	/**
	 * Displays like a popdown menu from the anchor view
	 */
	public void showLikePopDownMenu() {
		this.showLikePopDownMenu(0, 0);
	}

	/**
	 * Displays like a popdown menu from the anchor view.
	 * 
	 * @param xOffset
	 *            offset in X direction
	 * @param yOffset
	 *            offset in Y direction
	 */
	public void showLikePopDownMenu(int xOffset, int yOffset) {
		this.preShow();

		this.window.setAnimationStyle(R.style.Animations_PopDownMenu);

		this.window.showAsDropDown(this.anchor, xOffset, yOffset);
	}

	/**
	 * Displays like a QuickAction from the anchor view.
	 */
	public void showLikeQuickAction() {
		this.showLikeQuickAction(0, 0);
	}

	/**
	 * Displays like a QuickAction from the anchor view.
	 * 
	 * @param xOffset
	 *            offset in the X direction
	 * @param yOffset
	 *            offset in the Y direction
	 */
	public void showLikeQuickAction(int xOffset, int yOffset) {
		this.preShow();

		this.window.setAnimationStyle(R.style.Animations_GrowFromBottom);

		int[] location = new int[2];
		this.anchor.getLocationOnScreen(location);

		Rect anchorRect =
				new Rect(location[0], location[1], location[0] + this.anchor.getWidth(), location[1]
					+ this.anchor.getHeight());

		this.root.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

		int rootWidth = this.root.getMeasuredWidth();
		int rootHeight = this.root.getMeasuredHeight();

		int screenWidth = this.windowManager.getDefaultDisplay().getWidth();

		int xPos = ((screenWidth - rootWidth) / 2) + xOffset;
		int yPos = anchorRect.top - rootHeight + yOffset;

		// display on bottom
		if(rootHeight > anchorRect.top) {
			yPos = anchorRect.bottom + yOffset;
			this.window.setAnimationStyle(R.style.Animations_GrowFromTop);
		}

		this.window.showAtLocation(this.anchor, Gravity.NO_GRAVITY, xPos, yPos);
	}

	public void dismiss() {
		this.window.dismiss();
	}
	
	public static class DemoPopupWindow extends BetterPopupWindow implements OnClickListener {
		public DemoPopupWindow(View anchor, String mes, int pos) {
                  super(anchor);
                  message = mes;
                  position = pos;
                  
        }

        @Override
        protected void onCreate() {

                this.anchor.getContext();
				LayoutInflater inflater =
                                  (LayoutInflater) this.anchor.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                  ViewGroup root = (ViewGroup) inflater.inflate(R.layout.popup, null);
                  
                  for(int i = 0, icount = root.getChildCount() ; i < icount ; i++) {
                          View v = root.getChildAt(i);

                          if(v instanceof LinearLayout) {
                          	
                              LinearLayout lL = (LinearLayout) v;
                              for(int j = 0, jcount = lL.getChildCount() ; j < jcount ; j++) {
                              	View item = lL.getChildAt(j);                              	
                              	item.setOnClickListener(this);
                              }
                          }
                  }
                  this.setContentView(root);
        }
        
  		@Override
  		public void onClick(View v) {
 
  			if (v.getId() == R.id.delete) {
			     DBAdapter db = new DBAdapter(this.anchor.getContext());
		       	 db.open();
		       	 String[] m = new String[] {message};
		       	 db.deleteUser(m);
		       	 db.close();
		       	 
		       	 MainActivity.names.remove(position);

		       	 MainActivity.data.remove(position);
		       	 
		       	 Message msg = new Message();
       			 msg.what = 0;
       			 MainActivity.handy.sendMessage(msg);
       			 
       			 this.dismiss();       			 
  			}
 	    }
	}
}
