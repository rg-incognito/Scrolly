package com.android.mediapipe;

import android.accessibilityservice.AccessibilityService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import java.util.ArrayDeque;
import java.util.Deque;

public class GlobalActionBarService extends AccessibilityService {
    private static GlobalActionBarService sSharedInstance;
    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);

        sSharedInstance = this;
    }




    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {

    }

    @Override
    public void onInterrupt() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            disableSelf();
        }
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        sSharedInstance = this;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        sSharedInstance = null;
        return true;
    }

    public static GlobalActionBarService getSharedInstance() {
        return sSharedInstance;
    }


    private AccessibilityNodeInfo findScorllableNode(AccessibilityNodeInfo root) {
        Deque<AccessibilityNodeInfo> deque = new ArrayDeque<>();
        deque.add(root);
        while (!deque.isEmpty()) {
            AccessibilityNodeInfo node = deque.removeFirst();
            if (node.getActionList().contains(AccessibilityNodeInfo
                    .AccessibilityAction
                    .ACTION_SCROLL_FORWARD)) {
                return node;
            }

            for (int i = 0; i < node.getChildCount(); i++) {
                deque.addLast(node.getChild(i));
            }

        }
        return null;

    }

//    private AccessibilityNodeInfo findDobleTapNode(AccessibilityNodeInfo root) {
//        Deque<AccessibilityNodeInfo> deque = new ArrayDeque<>();
//        deque.add(root);
//        while (!deque.isEmpty()) {
//            AccessibilityNodeInfo node = deque.removeFirst();
//            if (node.getActionList().contains(AccessibilityNodeInfo
//                    .AccessibilityAction
//                    .ACTION_SCROLL_BACKWARD)) {
//                return node;
//            }
//
//            for (int i = 0; i < node.getChildCount(); i++) {
//                deque.addLast(node.getChild(i));
//            }
//
//        }
//        return null;
//
//    }

    public void configureScrollButtonUp() {
        Log.d("CheckScroll1", "configureScrollButton: ");
        AccessibilityNodeInfo scrollable = findScorllableNode(getRootInActiveWindow());
        if (scrollable != null && scrollable.isScrollable()) {
            scrollable
                    .performAction(AccessibilityNodeInfo
                            .AccessibilityAction
                            .ACTION_SCROLL_FORWARD
                            .getId());
        }


    }

    public void configureScrollButtonDown() {
        Log.d("CheckScroll1", "configureScrollButton: ");
        AccessibilityNodeInfo scrollable = findScorllableNode(getRootInActiveWindow());
        if (scrollable != null && scrollable.isScrollable()) {
            scrollable
                    .performAction(AccessibilityNodeInfo
                            .AccessibilityAction
                            .ACTION_SCROLL_BACKWARD
                            .getId());
        }

    }

//
//    public void configureDoubleTap() {
//        Log.d("CheckScroll1", "configureScrollButton: ");
//
//        AccessibilityNodeInfo doubletap = findDobleTapNode(getRootInActiveWindow());
//        if (doubletap != null && doubletap.isScrollable()) {
//            // 1st click
//            doubletap
//                    .performAction(AccessibilityNodeInfo
//                            .AccessibilityAction
//                            .ACTION_CLICK
//                            .getId());
//            // 2nd click
//            doubletap
//                    .performAction(AccessibilityNodeInfo
//                            .AccessibilityAction
//                            .ACTION_CLICK
//                            .getId());
//
//
//        }
//
//
//    }


}
