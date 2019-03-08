package com.movieous.media.mvp.model.entity

class TimeRange {
    var start: Float = 0.toFloat() // second
    var end: Float = 0.toFloat()

    val isValid: Boolean
        get() = this.start >= 0.0f && this.end > this.start

    val startTimeUS: Float
        get() = this.start * 1000000.0f

    val endTimeUS: Float
        get() = this.end * 1000000.0f

    constructor() {}

    constructor(start: Float, end: Float) {
        this.start = start
        this.end = end
    }

    fun duration(): Float {
        return if (!this.isValid) 0.0f else this.end - this.start
    }

    fun durationTimeUS(): Float {
        return this.duration() * 1000000.0f
    }

    operator fun contains(timeRange: TimeRange?): Boolean {
        return if (timeRange != null && timeRange.isValid && this.isValid) timeRange.start >= this.start && timeRange.start < this.end && timeRange.end <= this.end else false
    }

    fun convertTo(timeRange: TimeRange?): TimeRange {
        return if (timeRange != null && timeRange.isValid && this.isValid) TimeRange(timeRange.start + this.start, timeRange.start + this.end) else this
    }

    override fun equals(timeRange: Any?): Boolean {
        if (timeRange == null) {
            return false
        } else if (timeRange !is TimeRange) {
            return false
        } else if (timeRange === this) {
            return true
        } else {
            val range = timeRange as TimeRange?
            return range!!.start == this.start && range.end == this.end
        }
    }

    override fun toString(): String {
        return "Range start = " + this.start + " end = " + this.end
    }
}
