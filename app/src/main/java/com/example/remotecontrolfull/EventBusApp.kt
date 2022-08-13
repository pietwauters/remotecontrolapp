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
class CyranoEvent(val EFP_1_1string: String)
class MessageEvent(val message: String)

enum class StatusEventType {
    Score,YellowCards,RedCards,BlackCards,Priority
}
enum class SideOfEvent {
    Left,Right
}
class StatusEvent(val ScoreLeft:String,
                  val ScoreRight: String,
                  )

enum class RS422_FPAMessageType {
    Lights,Timer,CompetitorStatus,ApparatusStatus,CompetitorLeftInformation, CompetitorRightInformation, Unknown
}

class EventBusApp: Application() {

    override fun onCreate() {
        super.onCreate()

        EventBus.builder()
            .installDefaultEventBus()
    }

}