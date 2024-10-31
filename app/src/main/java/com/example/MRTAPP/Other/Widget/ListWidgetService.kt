package com.example.MRTAPP.Other.Widget

import android.content.Intent
import android.util.Log
import android.widget.RemoteViewsService

class ListWidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        Log.d("listview_service", "ListWidgetService")
        return ListRemoteViewsFactory(this.applicationContext, intent)
    }
}
