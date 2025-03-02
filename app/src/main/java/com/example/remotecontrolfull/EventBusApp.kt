package com.example.remotecontrolfull

import android.app.Application
import com.example.myapp.MyEventBusIndex
import org.greenrobot.eventbus.EventBus


enum class TimerEventType {
    Running,Stopped,Break,Injury
}
public class TimerEvent(val time: String,
                        val Type: TimerEventType = TimerEventType.Stopped)
{
}

enum class StatusEventType {
    Score,YellowCards,RedCards,BlackCards,Priority
}
enum class SideOfEvent {
    Left,Right
}
public class StatusEvent(
    val ScoreLeft: String,
    val ScoreRight: String,
    val YellowCardLeft: String,
    val YellowCardRight: String,
    val RedCardLeft: String,
    val RedCardRight: String,
    val BlackCardLeft: String,
    val BlackCardRight: String,
    val Prio: String,
    val Round: String
)

enum class U2FEventType {
    YellowPCards,RedPCards,BlackPCards
}

public class U2FEvent(
    val YellowPCardLeft: String,
    val YellowPCardRight: String,
    val RedPCardLeft: String,
    val RedPCardRight: String,
    val BlackPCardLeft: String,
    val BlackPCardRight: String,
)

public class CompetitorEvent (
    val side: SideOfEvent,
    val CompetitorName: String
)

public class RemoteControlEvent (
    val Value: String
)

public class ApparatusStatusEvent (
    val Value: String
)
public class ExtraInfoEvent (
    val PistID: String,
    val CyranoStatus: String
)

public class LightsEvent (
    val Red: Boolean,
    val Green: Boolean,
    val WhiteLeft: Boolean,
    val WhiteRight: Boolean

)

enum class RS422_FPAMessageType {
    Lights,Timer,CompetitorStatus,ApparatusStatus,CompetitorLeftInformation, CompetitorRightInformation, CompetitionInformation, UW2F, RemoteControl,ExtraInfo,Unknown
}


class EventBusApp: Application() {

    override fun onCreate() {
        super.onCreate()
/*
        EventBus.builder()
            .installDefaultEventBus()

        */
        val eventBus = EventBus.builder().addIndex(MyEventBusIndex()).build()
        EventBus.builder().addIndex(MyEventBusIndex()).installDefaultEventBus()
        // Now the default instance uses the given index. Use it like this:
        // Now the default instance uses the given index. Use it like this:
        //eventBus = EventBus.getDefault()
    }

}