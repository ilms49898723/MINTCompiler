package com.github.ilms49898723.fluigi.device.graph;

public class DeviceComponent {
    private String mIdentifier;
    private int mPortNumber;

    public DeviceComponent(String identifier, int portNumber) {
        mIdentifier = identifier;
        mPortNumber = portNumber;
    }

    public String getIdentifier() {
        return mIdentifier;
    }

    public int getPortNumber() {
        return mPortNumber;
    }

    @Override
    public String toString() {
        return mIdentifier + ":" + mPortNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DeviceComponent that = (DeviceComponent) o;

        if (getPortNumber() != that.getPortNumber()) {
            return false;
        }
        return getIdentifier() != null ? getIdentifier().equals(that.getIdentifier()) : that.getIdentifier() == null;
    }

    @Override
    public int hashCode() {
        int result = getIdentifier() != null ? getIdentifier().hashCode() : 0;
        result = 31 * result + getPortNumber();
        return result;
    }
}
