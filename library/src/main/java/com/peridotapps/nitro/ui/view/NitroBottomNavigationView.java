package com.peridotapps.nitro.ui.view;

import android.content.Context;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.transition.Visibility;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.peridotapps.nitro.R;

public class NitroBottomNavigationView extends BottomNavigationView {
  
  private BottomNavigationMenuView bottomNavMenu;
  
  public NitroBottomNavigationView (Context context) {
    this(context, null);
  }
  
  public NitroBottomNavigationView (Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }
  
  public NitroBottomNavigationView (Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    addNotificationBadgesToTabs();
  }
  
  private void addNotificationBadgesToTabs () {
    for (int pos = 0; pos < getBottomNavigationMenuView().getChildCount(); pos++) {
      if (getBottomNavigationMenuView().getChildAt(pos) instanceof BottomNavigationItemView) {
        BottomNavigationItemView tab = (BottomNavigationItemView) getBottomNavigationMenuView().getChildAt(pos);
        NotificationBadge badge = getBadgeView(tab);
        tab.setTag(badge);
      }
    }
  }
  
  protected NotificationBadge getBadgeView (ViewGroup parent) {
    return new NotificationBadge() {
      View view;
      TextView textView;
      
      @Override
      public View getView () {
        this.view = LayoutInflater.from(getContext())
                                  .inflate(R.layout.notification_badge_view, parent);
        this.textView = this.view.findViewById(R.id.notif_badge_text);
        return view;
      }
      
      @Override
      public void setNotificationCount (int count) {
        if (count > 0) {
          if (count < 10) {
            textView.setText(String.valueOf(count));
          } else {
            textView.setText("9+");
          }
          
          setVisibility(View.VISIBLE);
        } else {
          setVisibility(View.INVISIBLE);
        }
      }
      
      @Override
      public void setVisibility (int mode) {
        getView().setVisibility(mode);
      }
    };
  }
  
  public final void updateNotificationCount (int position, int notificationCount) {
    
    BottomNavigationItemView tab = (BottomNavigationItemView) getBottomNavigationMenuView().getChildAt(position);
    NotificationBadge badge;
    
    if (tab.getTag() == null) {
      badge = getBadgeView(tab);
      tab.setTag(badge);
    } else {
      badge = ((NotificationBadge) tab.getTag());
    }
    
    badge.setNotificationCount(notificationCount);
    
  }
  
  private BottomNavigationMenuView getBottomNavigationMenuView () {
    if (this.bottomNavMenu == null) {
      bottomNavMenu = (BottomNavigationMenuView) getChildAt(0);
    }
    return bottomNavMenu;
  }
  
  public interface NotificationBadge {
    
    View getView ();
    
    void setNotificationCount (int count);
    
    void setVisibility (@Visibility.Mode int mode);
    
  }
}
