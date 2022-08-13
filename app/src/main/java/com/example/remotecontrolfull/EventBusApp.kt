package com.example.remotecontrolfull

import android.app.Application
import org.greenrobot.eventbus.EventBus
enum class TimerEventType {
    Running,Stopped,Break,Injury
}
class TimerEvent(val time: String,
                        val Type: TimerEventType = TimerEventType.Stopped)
{
}

class MessageEvent(val message: String)
class EventBusApp: Application() {

    override fun onCreate() {
        super.onCreate()

        EventBus.builder()
            .installDefaultEventBus()
    }

}