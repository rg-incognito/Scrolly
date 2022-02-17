package com.android.mediapipe;

import android.accessibilityservice.AccessibilityService;
import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import java.util.ArrayDeque;
import java.util.Deque;

public class GlobalActionBarService extends AccessibilityService  {
    private static GlobalActionBarService sSharedInstance;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {

    }

    @Override
    public void onInterrupt() {

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

    public void configureScrollButton() {
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
    public void test(){
        Log.d("testt", "test: ");
    }


}
