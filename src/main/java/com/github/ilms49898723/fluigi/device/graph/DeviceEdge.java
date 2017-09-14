package com.github.ilms49898723.fluigi.device.graph;

public class DeviceEdge {
    private DeviceComponent mSource;
    private DeviceComponent mTarget;
    private String mChannel;

    public DeviceEdge(DeviceComponent source, DeviceComponent target, String channel) {
        mSource = source;
        mTarget = target;
        mChannel = channel;
    }

    public DeviceComponent getSource() {
        return mSource;
    }

    public DeviceComponent getTarget() {
        return mTarget;
    }

    public String getChannel() {
        return mChannel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DeviceEdge that = (DeviceEdge) o;

        if (getSource() != null ? !getSource().equals(that.getSource()) : that.getSource() != null) {
            return false;
        }
        if (getTarget() != null ? !getTarget().equals(that.getTarget()) : that.getTarget() != null) {
            return false;
        }
        return getChannel() != null ? getChannel().equals(that.getChannel()) : that.getChannel() == null;
    }

    @Override
    public int hashCode() {
        int result = getSource() != null ? getSource().hashCode() : 0;
        result = 31 * result + (getTarget() != null ? getTarget().hashCode() : 0);
        result = 31 * result + (getChannel() != null ? getChannel().hashCode() : 0);
        return result;
    }
}
