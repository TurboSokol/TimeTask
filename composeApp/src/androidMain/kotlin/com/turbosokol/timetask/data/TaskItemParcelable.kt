/***
 *If this code runs it was created by Evgenii Sokol.
 *If it doesn't work, I don't know who was created it.
 ***/

package com.turbosokol.TimeTask.data

import android.os.Parcel
import android.os.Parcelable
import com.turbosokol.TimeTask.screensStates.TaskItem

/**
 * Android-specific Parcelable wrapper for TaskItem
 * Allows TaskItem to be passed in Intents
 */
data class TaskItemParcelable(
    val id: Int,
    val title: String,
    val isActive: Boolean,
    val startTimeStamp: Long,
    val timeSeconds: Long,
    val timeHours: Double,
    val color: String
) : Parcelable {
    
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readByte() != 0.toByte(),
        parcel.readLong(),
        parcel.readLong(),
        parcel.readDouble(),
        parcel.readString() ?: "DEFAULT"
    )
    
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(title)
        parcel.writeByte(if (isActive) 1 else 0)
        parcel.writeLong(startTimeStamp)
        parcel.writeLong(timeSeconds)
        parcel.writeDouble(timeHours)
        parcel.writeString(color)
    }
    
    override fun describeContents(): Int {
        return 0
    }
    
    companion object CREATOR : Parcelable.Creator<TaskItemParcelable> {
        override fun createFromParcel(parcel: Parcel): TaskItemParcelable {
            return TaskItemParcelable(parcel)
        }
        
        override fun newArray(size: Int): Array<TaskItemParcelable?> {
            return arrayOfNulls(size)
        }
    }
    
    fun toTaskItem(): TaskItem {
        return TaskItem(
            id = id,
            title = title,
            isActive = isActive,
            startTimeStamp = startTimeStamp,
            timeSeconds = timeSeconds,
            timeHours = timeHours,
            color = try {
                TaskItem.TaskColor.valueOf(color)
            } catch (e: IllegalArgumentException) {
                TaskItem.TaskColor.DEFAULT
            }
        )
    }
}

fun TaskItem.toParcelable(): TaskItemParcelable {
    return TaskItemParcelable(
        id = id,
        title = title,
        isActive = isActive,
        startTimeStamp = startTimeStamp,
        timeSeconds = timeSeconds,
        timeHours = timeHours,
        color = color.name
    )
}


