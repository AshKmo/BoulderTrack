package com.example.bouldertrack

import android.os.Parcel
import android.os.Parcelable

// a class used to keep track of the current state of a bouldering session
// this class is Parcelable so that the states of BoulderTrackState objects can be saved and restored when the device is rotated
class BoulderTrackState() : Parcelable {
    // enum class to represent each of the types of zones that the climber may be in
    enum class ZoneType {
        WARMUP,
        TECHNICAL,
        CRUX
    }

    // fields to keep track of the climber's current state
    // the setters of each of these fields has been made private to disallow external mutation

    var hold: Int = 0 // the number of the hold that the climber last reached
        private set

    var score: Int = 0 // the climber's current total score
        private set

    var streak: Int = 0 // the climber's current bonus streak value
        private set

    var zone: ZoneType = ZoneType.WARMUP // the zone in which the climber is currently climbing
        private set

    var fell: Boolean = false // whether the climber has fallen
        private set

    var chalkedUp: Boolean = false // whether the climber has chalked up

    var onHoldChange: () -> Unit = {} // event handler for when the last reached hold value changes
    var onScoreChange: () -> Unit = {} // event handler for when the score changes
    var onStreakChange: () -> Unit = {} // event handler for when the bonus streak value changes
    var onZoneChange: () -> Unit = {} // event handler for when the zone changes
    var onFellChange: () -> Unit = {} // event handler for when the climber falls or the fall status is reset
    var onChalkUpChange: () -> Unit = {} // event handler for when the climber chalks up or the chalked up status is reset

    fun Byte.toBool() = this != 0.toByte() // extension method to convert bytes to booleans for un-parceling
    fun Boolean.toByte(): Byte = if (this) 1 else 0 // extension method to convert booleans to bytes for parceling

    constructor(`in`: Parcel?) : this() {
        if (`in` == null) return

        hold = `in`.readInt()
        score = `in`.readInt()
        streak = `in`.readInt()
        zone = ZoneType.valueOf(`in`.readString()!!)
        fell = `in`.readByte().toBool()
        chalkedUp = `in`.readByte().toBool()
    }

    fun grip() {
        // do not continue to grip if the current hold is the highest hold
        if (hold == 10 || fell) return

        // add the value of the holds in the current zone, plus the streak bonus (if any), to the current score
        // this also sets the streak bonus value to zero if the streak is at 3, and increments it otherwise
        score += when (zone) {
            ZoneType.WARMUP -> 2
            ZoneType.TECHNICAL -> 3
            ZoneType.CRUX -> 4
        } + if (streak == 3) {streak = 0; 1} else {streak++; 0}

        // increment the current hold
        hold++

        // if the current hold is on a zone boundary, update the current zone and call the event listener
        if (zone == run {
                zone = when (hold) {
                    4 -> ZoneType.TECHNICAL
                    8 -> ZoneType.CRUX
                    else -> zone
                }
                zone
            }) onZoneChange()

        // call the event handlers for when the current hold, score and streak value changes
        onHoldChange()
        onScoreChange()
        onStreakChange()
    }

    fun fall() {
        // no falls can be recorded after the 10th hold is grabbed
        if (hold == 10 || fell) return

        score -= 3

        fell = true

        onFellChange()
    }

    fun chalkUp() {
        if (chalkedUp) return

        chalkedUp = true

        onChalkUpChange()
    }

    fun reset() {
        hold = 0
        score = 0
        streak = 0
        zone = ZoneType.WARMUP
        fell = false
        chalkedUp = false

        onHoldChange()
        onScoreChange()
        onStreakChange()
        onZoneChange()
        onFellChange()
        onChalkUpChange()
    }

    override fun describeContents() = 0

    override fun writeToParcel(out: Parcel, flags: Int) {
        arrayOf(
            hold,
            score,
            streak,
        ).forEach(out::writeInt)

        out.writeString(zone.toString())
        out.writeByte(fell.toByte())
        out.writeByte(chalkedUp.toByte())
    }

    companion object CREATOR : Parcelable.Creator<BoulderTrackState> {
        override fun createFromParcel(`in`: Parcel?): BoulderTrackState {
            return BoulderTrackState(`in`)
        }

        override fun newArray(size: Int): Array<out BoulderTrackState?> {
            return arrayOfNulls(size)
        }
    }
}