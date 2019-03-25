package com.sensetime.stmobile.model;

/**
 * Created by sensetime on 18-5-8.
 */

public class STTriggerEvent {

    private int  triggerType;    // trigger event的类型，有human action，animetion和custom三种
    private long trigger;        // trigger_event的值；
    private int moduleId;       // animation中的module id，仅对animation event有效。如果是custom，或human action，此字段无意义
    private boolean isAppear;

    public int getTriggerType() {
        return triggerType;
    }

    public void setTriggerType(int triggerType) {
        this.triggerType = triggerType;
    }

    public long getTrigger() {
        return trigger;
    }

    public void setTrigger(long trigger) {
        this.trigger = trigger;
    }

    public int getModuleId() {
        return moduleId;
    }

    public void setModuleId(int moduleId) {
        this.moduleId = moduleId;
    }

    public boolean isAppear() {
        return isAppear;
    }

    public void setAppear(boolean appear) {
        isAppear = appear;
    }
}
